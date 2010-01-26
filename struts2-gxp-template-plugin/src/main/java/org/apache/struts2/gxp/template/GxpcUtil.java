package org.apache.struts2.gxp.template;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import com.google.gxp.base.GxpContext;
import com.google.gxp.base.dynamic.GxpCompilationException;
import com.google.gxp.base.dynamic.StubGxpTemplate;
import com.google.gxp.com.google.common.base.Charsets;
import com.google.gxp.com.google.common.collect.ImmutableList;
import com.google.gxp.com.google.common.collect.Lists;
import com.google.gxp.com.google.common.io.Bytes;
import com.google.gxp.compiler.Compiler;
import com.google.gxp.compiler.InvalidConfigException;
import com.google.gxp.compiler.alerts.AlertSink;
import com.google.gxp.compiler.alerts.ConfigurableAlertPolicy;
import com.google.gxp.compiler.alerts.PrintingAlertSink;
import com.google.gxp.compiler.cli.Gxpc;
import com.google.gxp.compiler.fs.FileRef;
import com.google.gxp.compiler.fs.JavaFileManagerImpl;
import com.google.gxp.compiler.fs.JavaFileRef;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public abstract class GxpcUtil {
    private static final Logger LOG = LoggerFactory.getLogger(GxpcUtil.class);

    private static final Method DEFINE_CLASS = AccessController.doPrivileged(new PrivilegedAction<Method>() {
        public Method run() {
            try {
                Class<ClassLoader> loader = ClassLoader.class;
                Method m = loader.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class,
                        Integer.TYPE, Integer.TYPE, ProtectionDomain.class });
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException();
            }
        }
    });

    public static void buildAndExec(String srcPath, String file, boolean dynamic, Appendable appendable,
            GxpContext context) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, InstantiationException, InvalidConfigException {
        // create instance of template
        String className = toJavaFileName(file);
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (Exception e) {
            // ok ok, lets compile it
        }

        if (clazz == null)
            clazz = build(srcPath, file, dynamic);

        Method method = getWriteMethod(clazz);
        method.invoke(clazz.newInstance(), appendable, context);
    }

    private static Method getWriteMethod(Class clazz) {
        try {
            return clazz.getMethod("write", Appendable.class, GxpContext.class);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static Class build(String srcPath, String file, boolean dynamic) throws InvalidConfigException {
        ConfigurableAlertPolicy alertPolicy = new ConfigurableAlertPolicy();
        alertPolicy.setTreatWarningsAsErrors(false);

        AlertSink alertSink = new PrintingAlertSink(alertPolicy, true, System.err);

        GxpcConfiguration.Builder builder = new GxpcConfiguration.Builder();
        builder.setSrcpaths(srcPath);
        builder.setDynamic(dynamic);
        builder.setAlertPolicy(alertPolicy);

        List<String> fullPathFiles = new ArrayList<String>();
        fullPathFiles.add(srcPath + "/" + file);

        GxpcConfiguration configuration = builder.build(fullPathFiles);
        Compiler compiler = new Compiler(configuration);
        compiler.call(alertSink);

        // compile java files
        return compileJava(file.substring(0, file.indexOf(".")), configuration);

    }

    private static String toJavaFileName(String file) {
        return file.substring(0, file.indexOf(".")).replace("/", ".");
    }

    private static Class compileJava(String javaFile, GxpcConfiguration configuration) {
        // compile java
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();

        JavaFileManager javaFileManager = new JavaFileManagerImpl(javaCompiler.getStandardFileManager(
                diagnosticCollector, Locale.US, Charsets.US_ASCII), configuration.getMemoryFileSystem());

        try {
            String className = javaFile.replace("/", ".");
            JavaFileObject compilationUnit = javaFileManager.getJavaFileForInput(StandardLocation.SOURCE_PATH,
                    javaFile, JavaFileObject.Kind.SOURCE);

            Iterable<JavaFileObject> compilationUnits = ImmutableList.of(compilationUnit);

            // find the GXP jar file and add it to the classpath
            ProtectionDomain protectionDomain = Gxpc.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            File gxpJar = new File(codeSource.getLocation().getFile());

            List<String> optionList = new ArrayList<String>();
            optionList.addAll(Arrays.asList("-classpath", gxpJar.getAbsolutePath()));

            javaCompiler.getTask(null, javaFileManager, diagnosticCollector, optionList, null, compilationUnits)
                    .call();

            List<Diagnostic<? extends JavaFileObject>> diagnostics = filterErrors(diagnosticCollector
                    .getDiagnostics());

            if (!diagnostics.isEmpty()) {
                throw new GxpCompilationException.Java(diagnostics);
            }

            List<byte[]> classFiles = Lists.newArrayList();
            for (FileRef fileRef : configuration.getMemoryFileSystem().getManifest()) {
                if (fileRef.getKind().equals(JavaFileObject.Kind.CLASS)) {
                    String outputClassName = javaFileManager.inferBinaryName(StandardLocation.CLASS_OUTPUT,
                            new JavaFileRef(fileRef));
                    if (outputClassName.equals(className) || outputClassName.startsWith(className + "$")) {
                        classFiles.add(Bytes.toByteArray(fileRef.openInputStream()));
                    }
                }
            }

            // A single java compile can generate many .class files due to inner
            // classes, and it
            // is difficult to know what order to load them in to avoid
            // NoClassDefFoundErrors,
            // so what we do is go through the whole list attempting to load
            // them all, keeping
            // track of which ones file with NoClassDefFoundError. Then we loop
            // and try again.
            // This should eventually work no matter what order the files come
            // in.
            //
            // We have an additional check to make sure that at least one file
            // is loaded each
            // time through the loop to prevent infinite looping.
            //
            // I'm not entirely happy with this schema, but it's the best I can
            // come up with
            // for now.
            int oldCount, newCount;
            do {
                oldCount = classFiles.size();
                classFiles = defineClasses(classFiles);
                newCount = classFiles.size();
            } while (newCount != 0 && newCount != oldCount);

            // get the main class generated durring this compile
            Class c = Class.forName(className);

            return c;
        } catch (GxpCompilationException e) {
            throw e;
        } catch (Throwable e) {
            throw new GxpCompilationException.Throw(e);
        }
    }

    private static <T> List<Diagnostic<? extends T>> filterErrors(List<Diagnostic<? extends T>> diagnostics) {
        List<Diagnostic<? extends T>> newList = Lists.newArrayList();
        for (Diagnostic<? extends T> diagnostic : diagnostics) {
            if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR)) {
                newList.add(diagnostic);
            }
        }
        return Collections.unmodifiableList(newList);
    }

    private static List<byte[]> defineClasses(List<byte[]> classFiles) throws Throwable {
        List<byte[]> failures = Lists.newArrayList();
        for (byte[] classFile : classFiles) {
            try {
                Class clazz = defineClass(classFile);
                LOG.debug("Template class [#0] loaded", clazz.getName());
            } catch (NoClassDefFoundError e) {
                failures.add(classFile);
            }
        }
        return failures;
    }

    /**
     * Define a class using the SystemClassLoader so that the class has access
     * to package private items in its java package.
     */
    private static Class defineClass(byte[] classFile) throws Throwable {
        ProtectionDomain PROTECTION_DOMAIN = StubGxpTemplate.class.getProtectionDomain();
        Object[] args = new Object[] { null, classFile, new Integer(0), new Integer(classFile.length),
                PROTECTION_DOMAIN };
        try {
            return (Class) DEFINE_CLASS.invoke(Thread.currentThread().getContextClassLoader(), args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
