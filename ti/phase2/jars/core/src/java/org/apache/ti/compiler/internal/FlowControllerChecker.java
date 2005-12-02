/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.compiler.internal;

import org.apache.ti.compiler.internal.genmodel.GenXWorkModuleConfigModel;
import org.apache.ti.compiler.internal.grammar.ActionGrammar;
import org.apache.ti.compiler.internal.grammar.ExceptionHandlerGrammar;
import org.apache.ti.compiler.internal.grammar.WebappPathType;
import org.apache.ti.compiler.internal.processor.SilentDiagnostics;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.FieldDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Modifier;
import org.apache.ti.compiler.internal.typesystem.declaration.PackageDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.ParameterDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FlowControllerChecker
        extends BaseChecker
        implements JpfLanguageConstants {
    private AnnotationGrammar _controllerGrammar;
    private AnnotationGrammar _actionGrammar;
    private AnnotationGrammar _exceptionHandlerGrammar;
    private AnnotationGrammar _actionGrammarSilentDiagnostics;
    private AnnotationGrammar _exceptionHandlerGrammarSilentDiagnostics;
    private FormBeanChecker _formBeanChecker;
    private Map _checkResultMap;

    protected FlowControllerChecker(AnnotationProcessorEnvironment env, FlowControllerInfo fcInfo, Diagnostics diags) {
        super(env, fcInfo, diags);
    }

    protected void doAdditionalClassChecks(ClassDeclaration jpfClass) {
    }

    protected Map getCheckResultMap() {
        return _checkResultMap;
    }

    protected abstract String getDesiredBaseClass(ClassDeclaration jclass);

    protected abstract AnnotationGrammar getControllerGrammar();

    public Map onCheck(ClassDeclaration jclass) throws FatalCompileTimeException {
        FlowControllerInfo fcInfo = getFCSourceFileInfo();

        _checkResultMap = new HashMap();
        _controllerGrammar = getControllerGrammar();
        _actionGrammar = new ActionGrammar(getEnv(), getDiagnostics(), getRuntimeVersionChecker(), fcInfo);
        _exceptionHandlerGrammar = new ExceptionHandlerGrammar(getEnv(), getDiagnostics(), getRuntimeVersionChecker(), fcInfo);
        _formBeanChecker = new FormBeanChecker(getEnv(), getDiagnostics());

        SilentDiagnostics silentDiagnostics = new SilentDiagnostics();
        _actionGrammarSilentDiagnostics = new ActionGrammar(getEnv(), silentDiagnostics, getRuntimeVersionChecker(), fcInfo);
        _exceptionHandlerGrammarSilentDiagnostics = new ExceptionHandlerGrammar(getEnv(), silentDiagnostics,
                                                                                getRuntimeVersionChecker(), fcInfo);

        fcInfo.startBuild(getEnv(), jclass);

        try {
            return onCheckInternal(jclass);
        } finally {
            fcInfo.endBuild();
        }
    }

    private Map onCheckInternal(ClassDeclaration jclass)
            throws FatalCompileTimeException {
        FlowControllerInfo fcInfo = getFCSourceFileInfo();

        //
        // Check the base class.
        //
        String desiredBaseClass = getDesiredBaseClass(jclass);

        if ((desiredBaseClass != null) && !CompilerUtils.isAssignableFrom(desiredBaseClass, jclass, getEnv())) {
            getDiagnostics().addError(jclass, "error.does-not-extend-base", desiredBaseClass);
        }

        //
        // Check the annotations on the class.
        //
        startCheckClass(jclass);

        //
        // Check the fields.  Note that we're checking public and protected inherited fields, too.
        //
        Collection fields = CompilerUtils.getClassFields(jclass);

        for (Iterator ii = fields.iterator(); ii.hasNext();) {
            FieldDeclaration field = (FieldDeclaration) ii.next();
            checkField(field, jclass);
        }

        //
        // Check the methods.  Note that we're checking public and protected inherited methods, too.
        //
        MethodDeclaration[] methods = CompilerUtils.getClassMethods(jclass, null);

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];
            TypeDeclaration declaringType = method.getDeclaringType();

            //
            // Only add diagnostics if the method is in this class, or if it's inherited from a class that's *not* on
            // sourcepath (i.e., its SourcePosition is null).
            //
            if (declaringType.equals(jclass) || (declaringType.getPosition() == null)) {
                checkMethod(method, jclass, _actionGrammar, _exceptionHandlerGrammar);
            } else {
                //
                // We still want to run the checks, which aggregate information into the FlowControllerInfo.  We just
                // don't want diagnostics to be printed.
                //
                checkMethod(method, jclass, _actionGrammarSilentDiagnostics, _exceptionHandlerGrammarSilentDiagnostics);
            }
        }

        //
        // Check the inner classes.
        //
        Collection innerTypes = CompilerUtils.getClassNestedTypes(jclass);

        for (Iterator ii = innerTypes.iterator(); ii.hasNext();) {
            TypeDeclaration innerType = (TypeDeclaration) ii.next();

            if (innerType instanceof ClassDeclaration) {
                checkInnerClass((ClassDeclaration) innerType);
            }
        }

        //
        // Run additional .jpf- or .app-specific checks.
        //
        doAdditionalClassChecks(jclass);

        //
        // Runtime performance enhancement: enable saving of previous-page and previous-action information based on
        // whether there were Forwards that contained navigateTo attributes.
        //
        enableNavigateTo(jclass, fcInfo.getMergedControllerAnnotation(), fcInfo);

        Map sharedFlowTypes = fcInfo.getSharedFlowTypes();

        if (sharedFlowTypes != null) {
            for (Iterator ii = sharedFlowTypes.values().iterator(); ii.hasNext();) {
                TypeDeclaration sharedFlowType = (TypeDeclaration) ii.next();

                //
                // Saving of previous-page/previous-action info must be enabled if any of the referenced shared flows
                // use this feature.
                //
                enableNavigateTo(sharedFlowType, new MergedControllerAnnotation(sharedFlowType), fcInfo);
            }
        }

        endCheckClass(jclass);
        _checkResultMap.put(JpfLanguageConstants.ExtraInfoKeys.flowControllerInfo, fcInfo);

        return _checkResultMap;
    }

    private static void enableNavigateTo(TypeDeclaration flowControllerClass, MergedControllerAnnotation controllerAnn,
                                         FlowControllerInfo fcInfo) {
        //
        // Look through Forwards and SimpleActions in the Controller annotation.
        //
        enableNavigateTo(controllerAnn.getForwards(), fcInfo);
        enableNavigateTo(controllerAnn.getSimpleActions(), fcInfo);

        //
        // Look through Forwards on Action and ExceptionHandler methods.
        //
        MethodDeclaration[] methods = CompilerUtils.getClassMethods(flowControllerClass, null);

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];
            AnnotationInstance ann = CompilerUtils.getAnnotation(method, ACTION_TAG_NAME);

            if (ann != null) {
                enableNavigateTo(CompilerUtils.getAnnotation(ann, VALIDATION_ERROR_FORWARD_ATTR, true), fcInfo);
            }

            if (ann == null) {
                ann = CompilerUtils.getAnnotation(method, EXCEPTION_HANDLER_TAG_NAME);
            }

            if (ann != null) {
                enableNavigateTo(CompilerUtils.getAnnotationArray(ann, FORWARDS_ATTR, true), fcInfo);
            }
        }
    }

    private static void enableNavigateTo(Collection childAnnotations, FlowControllerInfo fcInfo) {
        if (childAnnotations != null) {
            for (Iterator ii = childAnnotations.iterator(); ii.hasNext();) {
                AnnotationInstance childAnnotation = (AnnotationInstance) ii.next();
                enableNavigateTo(childAnnotation, fcInfo);
            }
        }
    }

    private static void enableNavigateTo(AnnotationInstance ann, FlowControllerInfo fcInfo) {
        if (ann == null) {
            return;
        }

        String val = CompilerUtils.getEnumFieldName(ann, NAVIGATE_TO_ATTR, true);

        if (val != null) {
            if (val.equals(NAVIGATE_TO_CURRENT_PAGE_STR) || val.equals(NAVIGATE_TO_PREVIOUS_PAGE_STR)) {
                fcInfo.enableNavigateToPage();
            } else if (val.equals(NAVIGATE_TO_PREVIOUS_ACTION_STR)) {
                fcInfo.enableNavigateToAction();
            }
        }
    }

    protected void endCheckClass(ClassDeclaration jclass) {
    }

    protected abstract GenXWorkModuleConfigModel createStrutsApp(ClassDeclaration jclass)
            throws IOException, FatalCompileTimeException;

    protected void startCheckClass(ClassDeclaration jclass)
            throws FatalCompileTimeException {
        //
        // Check for basic things like writability of the struts-config file.
        //
        GenXWorkModuleConfigModel strutsApp = null;
        File strutsConfigFile = null;

        //
        // Make sure we can write to the struts-config XML file.
        //
        try {
            strutsApp = createStrutsApp(jclass);
            strutsConfigFile = strutsApp.getStrutsConfigFile();
        } catch (IOException e) {
            // will be reported at generate time
        }

        if (strutsConfigFile != null) {
            File parentDir = strutsConfigFile.getParentFile();

            getFCSourceFileInfo().addReferencedFile(strutsConfigFile);

            if (strutsConfigFile.exists() && (strutsApp != null) && !strutsApp.canWrite()) {
                getDiagnostics().addError(jclass, "error.struts-config-not-writable", strutsConfigFile);
            }
        }

        getRuntimeVersionChecker().checkRuntimeVersion(VERSION_8_SP2_STRING, jclass, getDiagnostics(), "warning.runtime-version",
                                                       null);

        //
        // Check the Jpf.Controller annotation on this class.
        //
        AnnotationInstance controllerAnnotation = CompilerUtils.getAnnotation(jclass, CONTROLLER_TAG_NAME);

        if (controllerAnnotation != null) {
            _controllerGrammar.check(controllerAnnotation, null, jclass);
        }

        //
        // Check relative paths on Jpf.Catch, Jpf.Forward, and Jpf.SimpleAction annotations on superclasses.
        // If inheritLocalPaths is set to true on @Jpf.Controller, then we don't need to do this check, since
        // inherited paths will always resolve.
        //
        if (!getFCSourceFileInfo().getMergedControllerAnnotation().isInheritLocalPaths()) {
            checkInheritedRelativePaths(jclass);
        }
    }

    /**
     * Check relative paths in annotations inherited from a base class.
     */
    private void checkInheritedRelativePaths(ClassDeclaration jclass)
            throws FatalCompileTimeException {
        for (ClassType type = jclass.getSuperclass();
                 (type != null) && CompilerUtils.isAssignableFrom(FLOWCONTROLLER_BASE_CLASS, type, getEnv());
                 type = type.getSuperclass()) {
            TypeDeclaration decl = CompilerUtils.getDeclaration(type);

            //
            // Check simple actions in the Controller annotation.
            //
            List simpleActions = CompilerUtils.getAnnotationArrayValue(decl, CONTROLLER_TAG_NAME, SIMPLE_ACTIONS_ATTR, true);

            if (simpleActions != null) {
                for (Iterator j = simpleActions.iterator(); j.hasNext();) {
                    AnnotationInstance i = (AnnotationInstance) j.next();
                    checkRelativePath(i, PATH_ATTR, jclass, decl, false);

                    List conditionalForwards = CompilerUtils.getAnnotationArray(i, CONDITIONAL_FORWARDS_ATTR, true);

                    if (conditionalForwards != null) {
                        for (Iterator k = conditionalForwards.iterator(); k.hasNext();) {
                            AnnotationInstance ann = (AnnotationInstance) k.next();
                            checkRelativePath(ann, PATH_ATTR, jclass, decl, false);
                        }
                    }
                }
            }

            //
            // Check Forwards in the Controller annotation.
            //
            List forwards = CompilerUtils.getAnnotationArrayValue(decl, CONTROLLER_TAG_NAME, FORWARDS_ATTR, true);

            if (forwards != null) {
                for (Iterator ii = forwards.iterator(); ii.hasNext();) {
                    AnnotationInstance i = (AnnotationInstance) ii.next();
                    checkRelativePath(i, PATH_ATTR, jclass, decl, false);
                }
            }

            //
            // Check Catches in the Controller annotation.
            //
            List catches = CompilerUtils.getAnnotationArrayValue(decl, CONTROLLER_TAG_NAME, CATCHES_ATTR, true);

            if (catches != null) {
                for (Iterator j = catches.iterator(); j.hasNext();) {
                    AnnotationInstance i = (AnnotationInstance) j.next();
                    checkRelativePath(i, PATH_ATTR, jclass, decl, false);
                }
            }

            //
            // Check strutsMerge and validatorMerge in the Controller annotation.
            //
            AnnotationInstance controllerAnnotation = CompilerUtils.getAnnotation(decl, CONTROLLER_TAG_NAME);

            if (controllerAnnotation != null) {
                checkRelativePath(controllerAnnotation, VALIDATOR_MERGE_ATTR, jclass, decl, true);
            }

            //
            // Check Forwards and Catches on action methods and exception-handler methods.
            //
            MethodDeclaration[] methods = decl.getMethods();

            for (int i = 0; i < methods.length; i++) {
                MethodDeclaration method = methods[i];
                AnnotationInstance ann = CompilerUtils.getAnnotation(method, ACTION_TAG_NAME);

                if (ann == null) {
                    ann = CompilerUtils.getAnnotation(method, EXCEPTION_HANDLER_TAG_NAME);
                }

                if (ann != null) {
                    List methodForwards = CompilerUtils.getAnnotationArray(ann, FORWARDS_ATTR, true);
                    String methodName = method.getSimpleName();

                    if (methodForwards != null) {
                        for (Iterator j = methodForwards.iterator(); j.hasNext();) {
                            AnnotationInstance methodForward = (AnnotationInstance) j.next();
                            checkRelativePath(methodName, methodForward, PATH_ATTR, jclass, decl, false);
                        }
                    }

                    List methodCatches = CompilerUtils.getAnnotationArray(ann, CATCHES_ATTR, true);

                    if (methodCatches != null) {
                        for (Iterator j = methodCatches.iterator(); j.hasNext();) {
                            AnnotationInstance methodCatch = (AnnotationInstance) j.next();
                            checkRelativePath(methodName, methodCatch, PATH_ATTR, jclass, decl, false);
                        }
                    }
                }
            }
        }
    }

    private void checkRelativePath(AnnotationInstance ann, String memberName, TypeDeclaration jclass, TypeDeclaration baseType,
                                   boolean isError) throws FatalCompileTimeException {
        if (ann != null) {
            AnnotationValue pathVal = CompilerUtils.getAnnotationValue(ann, memberName, true);

            if (pathVal != null) {
                String path = (String) pathVal.getValue();

                if ((path.charAt(0) != '/') && !WebappPathType.relativePathExists(path, jclass, getEnv())) {
                    String[] args = { path, ANNOTATION_INTERFACE_PREFIX +
                                    ann.getAnnotationType().getDeclaration().getSimpleName(), baseType.getQualifiedName() };

                    if (isError) {
                        getDiagnostics().addErrorArrayArgs(ann, "message.inherited-file-not-found", args);
                    } else {
                        getDiagnostics().addWarningArrayArgs(ann, "message.inherited-file-not-found", args);
                    }
                }
            }
        }
    }

    private void checkRelativePath(String methodName, AnnotationInstance ann, String memberName, TypeDeclaration jclass,
                                   TypeDeclaration baseType, boolean isError)
            throws FatalCompileTimeException {
        if (ann != null) {
            AnnotationValue pathVal = CompilerUtils.getAnnotationValue(ann, memberName, true);

            if (pathVal != null) {
                String path = (String) pathVal.getValue();

                if ((path.charAt(0) != '/') && !WebappPathType.relativePathExists(path, jclass, getEnv())) {
                    String[] args = { path, ANNOTATION_INTERFACE_PREFIX +
                                    ann.getAnnotationType().getDeclaration().getSimpleName(), methodName, baseType.getQualifiedName() };

                    if (isError) {
                        getDiagnostics().addErrorArrayArgs(jclass, "message.method-inherited-file-not-found", args);
                    } else {
                        getDiagnostics().addWarningArrayArgs(jclass, "message.method-inherited-file-not-found", args);
                    }
                }
            }
        }
    }

    protected void checkField(FieldDeclaration field, TypeDeclaration jclass) {
        //
        // Only warn about nonserializable member data that's defined in this particular class.
        //
        if (CompilerUtils.typesAreEqual(jclass, field.getDeclaringType())) {
            TypeInstance type = field.getType();

            if (!field.hasModifier(Modifier.TRANSIENT) && !field.hasModifier(Modifier.STATIC) && type instanceof ClassType &&
                    !CompilerUtils.isAssignableFrom(SERIALIZABLE_CLASS_NAME, type, getEnv())) {
                getDiagnostics().addWarning(field, "warning.nonserializable-member-data");
            }
        }
    }

    protected void checkMethod(MethodDeclaration method, ClassDeclaration jclass, AnnotationGrammar actionGrammar,
                               AnnotationGrammar exceptionHandlerGrammar)
            throws FatalCompileTimeException {
        AnnotationInstance[] annotations = method.getAnnotationInstances();

        for (int i = 0; i < annotations.length; i++) {
            AnnotationInstance annotation = annotations[i];
            String annotationName = CompilerUtils.getDeclaration(annotation.getAnnotationType()).getSimpleName();

            if (annotationName.equals(ACTION_TAG_NAME)) {
                actionGrammar.check(annotation, null, method);

                if (!CompilerUtils.isAssignableFrom(FORWARD_CLASS_NAME, method.getReturnType(), getEnv()) &&
                        !CompilerUtils.isAssignableFrom(STRING_CLASS_NAME, method.getReturnType(), getEnv())) {
                    getDiagnostics().addError(method, "error.method-wrong-return-type", FORWARD_CLASS_NAME, STRING_CLASS_NAME);
                }
            } else if (annotationName.equals(EXCEPTION_HANDLER_TAG_NAME)) {
                exceptionHandlerGrammar.check(annotation, null, method);
                checkExceptionHandlerMethod(method);
            }
        }
    }

    protected void checkInnerClass(ClassDeclaration innerClass)
            throws FatalCompileTimeException {
        _formBeanChecker.check(innerClass);
    }

    private void checkExceptionHandlerMethod(MethodDeclaration method) {
        if (!CompilerUtils.isAssignableFrom(FORWARD_CLASS_NAME, method.getReturnType(), getEnv()) &&
                !CompilerUtils.isAssignableFrom(STRING_CLASS_NAME, method.getReturnType(), getEnv())) {
            getDiagnostics().addError(method, "error.method-wrong-return-type", FORWARD_CLASS_NAME, STRING_CLASS_NAME);
        }

        ParameterDeclaration[] parameters = method.getParameters();

        if (parameters.length == 2) {
            if (!CompilerUtils.isAssignableFrom(THROWABLE_CLASS_NAME, parameters[0].getType(), getEnv())) {
                getDiagnostics().addError(method, "error.exception-method-wrong-exception-arg", THROWABLE_CLASS_NAME);
            }

            checkExceptionHandlerArgType(method, parameters, 1, STRING_CLASS_NAME);
        } else {
            getDiagnostics().addError(method, "error.exception-method-wrong-arg-count", new Integer(2));
        }
    }

    private void checkExceptionHandlerArgType(MethodDeclaration method, ParameterDeclaration[] parameters, int index,
                                              String className) {
        if (!CompilerUtils.isOfClass(parameters[index].getType(), className, getEnv())) {
            getDiagnostics().addError(method, "error.exception-method-wrong-arg-type", new Integer(index + 1), className);
        }
    }

    protected void checkForOverlappingClasses(ClassDeclaration jpfClass, String baseClass, String fileExtension, String errorKey) {
        File jpfFile = CompilerUtils.getSourceFile(jpfClass, true);
        File parentDir = jpfFile.getParentFile();
        PackageDeclaration pkg = jpfClass.getPackage();
        ClassDeclaration[] packageClasses = pkg.getClasses();
        Set overlapping = new HashSet();
        List overlappingFiles = new ArrayList();

        //
        // First go through the other classes in this package to look for other classes of this type.  Only one per
        // directory is allowed.
        //
        for (int i = 0; i < packageClasses.length; i++) {
            ClassDeclaration classDecl = packageClasses[i];

            if ((CompilerUtils.getAnnotation(classDecl, CONTROLLER_TAG_NAME) != null) &&
                    CompilerUtils.isAssignableFrom(baseClass, classDecl, getEnv())) {
                File file = CompilerUtils.getSourceFile(classDecl, false);

                //
                // Add the dependency if it's a different file and if the file exists (it may have been deleted
                // sometime after the list of classes in this package got built.
                //
                if (!jpfFile.equals(file) && (file != null) && file.exists()) {
                    overlapping.add(file.getName());
                    overlappingFiles.add(file);
                }
            }
        }

        //
        // Additionally, we'll go through the parent directory to make sure there are no other files of this type. 
        // This is a double-check for the case where duplicate files have the same class names inside them, which means
        // that iterating through the list of package classes is hit or miss (only one of them will show up, and it may
        // be this class or the duplicate class).
        //
        File[] peers = parentDir.listFiles(new ExtensionFileFilter(fileExtension));

        if (peers != null) // make sure the directory hasn't been deleted while we're running
         {
            for (int i = 0; i < peers.length; i++) {
                File peer = peers[i];

                if (!peer.equals(jpfFile)) {
                    String name = peer.getName();

                    if (!overlapping.contains(name)) {
                        overlapping.add(name);
                        overlappingFiles.add(peer);
                    }
                }
            }
        }

        int len = overlapping.size();

        if (len > 0) {
            if (len > 3) {
                getDiagnostics().addErrorArrayArgs(jpfClass, errorKey, overlapping.toArray());
            } else {
                getDiagnostics().addErrorArrayArgs(jpfClass, errorKey + len, overlapping.toArray());
            }
        }

        getCheckResultMap().put(JpfLanguageConstants.ExtraInfoKeys.overlappingPageFlowFiles, overlappingFiles);
    }

    private static class ExtensionFileFilter
            implements FilenameFilter {
        private String _extension;

        public ExtensionFileFilter(String extension) {
            _extension = extension;
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(_extension);
        }
    }

    protected FlowControllerInfo getFCSourceFileInfo() {
        return (FlowControllerInfo) super.getSourceFileInfo();
    }
}
