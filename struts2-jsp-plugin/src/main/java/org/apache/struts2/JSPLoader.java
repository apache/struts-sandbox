/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterfaceDelegate;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspPage;
import javax.tools.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import org.apache.struts2.jasper.JasperException;
import org.apache.struts2.jasper.JspC;
import org.apache.struts2.jasper.compiler.JspUtil;
import org.apache.struts2.compiler.MemoryClassLoader;
import org.apache.struts2.compiler.MemoryJavaFileObject;
import org.apache.commons.lang.xwork.StringUtils;

/**
 * Uses jasper to extract a JSP from the classpath to a file and compile it
 */
public class JSPLoader {
    private static final Logger LOG = LoggerFactory.getLogger(JSPLoader.class);
    public static final File JSP_DIR = new File(System.getProperty("java.io.tmpdir"), "struts_jsps");

    private static MemoryClassLoader classLoader = new MemoryClassLoader();
    private static final String DEFAULT_PACKAGE = "org.apache.struts2.jsp";

    public Servlet load(String location) throws Exception {
        location = StringUtils.substringBeforeLast(location, "?");
        
        String source = compileJSP(location);

//        System.out.print(source);

        String className = toClassName(location);
        compileJava(className, source);

        Class clazz = Class.forName(className, false, classLoader);
        return createServlet(clazz);
    }

    private String toClassName(String location) {
        String className = StringUtils.substringBeforeLast(location, ".jsp");
        className = JspUtil.makeJavaPackage(className);
        return DEFAULT_PACKAGE + "." + className + "_jsp";
    }

    /**
     * Creates and inits a servlet
     */
    private Servlet createServlet(Class clazz) throws IllegalAccessException, InstantiationException, ServletException {
        Servlet servlet = (Servlet) clazz.newInstance();
        JSPServletConfig config = new JSPServletConfig(ServletActionContext.getServletContext());
        servlet.init(config);

        return servlet;
    }

    private void compileJava(String className, final String source) {
        JavaCompiler compiler =
                ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<JavaFileObject>();

        JavaFileManager jfm = new
                ForwardingJavaFileManager<StandardJavaFileManager>(
                        compiler.getStandardFileManager(diagnostics, null, null)) {

                    @Override
                    public JavaFileObject getJavaFileForOutput(Location location,
                                                               String name,
                                                               JavaFileObject.Kind kind,
                                                               FileObject sibling) throws IOException {
                        MemoryJavaFileObject fileObject = new MemoryJavaFileObject(name, kind);
                        classLoader.addMemoryJavaFileObject(name, fileObject);
                        return fileObject;
                    }

                };

        String fileName = className.replace('.', '/') + ".java";
        SimpleJavaFileObject sourceCodeObject = new SimpleJavaFileObject(toURI(fileName), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean
                    ignoreEncodingErrors)
                    throws IOException, IllegalStateException,
                    UnsupportedOperationException {
                return source;
            }

        };

        //build classpath
        List<String> optionList = new ArrayList<String>();
        StringBuilder classPath = new StringBuilder();
        //this jar
        classPath.append(getJarUrl(EmbeddedJSPResult.class));
        classPath.append(";");
        //servlet api
        classPath.append(getJarUrl(Servlet.class));
        classPath.append(";");
        //jsp api
        classPath.append(getJarUrl(JspPage.class));

        optionList.addAll(Arrays.asList("-classpath", classPath.toString()));


        JavaCompiler.CompilationTask task = compiler.getTask(
                null, jfm, diagnostics, optionList, null,
                Arrays.asList(sourceCodeObject));

        if (!task.call()) {
            throw new RuntimeException("Compilation failed:" + diagnostics.getDiagnostics().get(0).toString());
        }
    }

    protected String getJarUrl(Class clazz) {
        ProtectionDomain protectionDomain = clazz.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL loc = codeSource.getLocation();
        try {
            File file = new File(loc.toURI());
            return file.getAbsolutePath();
        } catch (URISyntaxException e) {
            //can this ever happen?
            LOG.error("Unable to get the jar file for class", e, clazz.getName());
        }
        return loc.toExternalForm();
    }

    private String compileJSP(String location) throws JasperException {
        JspC jspC = new JspC();
        //TODO: get this from context so OSGI works
        jspC.setClassLoaderInterface(new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()));
        jspC.setCompile(false);
        jspC.setJspFiles(location);
        jspC.setPackage(DEFAULT_PACKAGE);
        jspC.execute();
        return jspC.getSourceCode();
    }

    private static URI toURI(String name) {
        try {
            return new URI(name);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
