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
package org.apache.ti.compiler.internal.grammar;

import org.apache.ti.compiler.internal.AnnotationGrammar;
import org.apache.ti.compiler.internal.AnnotationMemberType;
import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.FlowControllerInfo;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Modifier;
import org.apache.ti.compiler.internal.typesystem.declaration.PackageDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * String type that emits a warning if the given path does not exist in the webapp for this pageflow.
 */
public class WebappPathType
        extends AnnotationMemberType {

    private static final String[] CHECKABLE_EXTENSIONS =
            {
                    JSP_FILE_EXTENSION,
                    XJSP_FILE_EXTENSION,
                    JPF_FILE_EXTENSION,
                    "xml",
                    "htm",
                    "html"
            };

    private boolean _pathMustBeRelative = false;
    private FlowControllerInfo _flowControllerInfo;


    public WebappPathType(boolean pathMustBeRelative, String requiredRuntimeVersion, AnnotationGrammar parentGrammar,
                          FlowControllerInfo fcInfo) {
        super(requiredRuntimeVersion, parentGrammar);
        _pathMustBeRelative = pathMustBeRelative;
        _flowControllerInfo = fcInfo;
    }

    private static boolean isCheckableExtension(String filePath) {
        for (int i = 0; i < CHECKABLE_EXTENSIONS.length; ++i) {
            if (filePath.endsWith(CHECKABLE_EXTENSIONS[i])) return true;
        }

        return false;
    }

    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex)
            throws FatalCompileTimeException {
        String filePath = (String) value.getValue();

        //
        // First make sure it's a valid URI.
        //
        try {
            URI uri = new URI(filePath);
            filePath = uri.getPath();   // decodes the path
        }
        catch (URISyntaxException e) {
            addError(value, "error.invalid-uri", e.getLocalizedMessage());
            return null;
        }

        //
        // The path will be null for an 'opaque' URI, like "news:comp.lang.java".
        //
        if (filePath == null || filePath.length() == 0) return null;

        //
        // Make sure it's a filetype that should exist on the filesystem.  If not, ignore it.
        //
        if (! checkAnyExtension() && ! isCheckableExtension(filePath)) return null;

        boolean fileExists = true;
        TypeDeclaration outerClass = CompilerUtils.getOutermostClass(classMember);
        File fileToCheck = null;

        if (filePath.charAt(0) == '/')  // relative to webapp root
        {
            if (_pathMustBeRelative) addError(value, "error.relative-uri");

            if (filePath.endsWith(JPF_FILE_EXTENSION_DOT)) {
                TypeDeclaration type = CompilerUtils.inferTypeFromPath(filePath, getEnv());
                fileToCheck = type != null ? CompilerUtils.getSourceFile(type, false) : null;

                // Note that if we can't infer the file from the type, we'll fall through to the next case, where
                // we actually look for the file in the webapp.
            }

            if (fileToCheck == null) {
                File jpfSourceFile = CompilerUtils.getSourceFile(CompilerUtils.getOuterClass(classMember), false);

                //
                // We don't always have the source file for the classMember's containing class (e.g., when this class
                // extends a class that's on classpath but not sourcepath).  If we don't have the source, just ignore.
                //
                if (jpfSourceFile != null) {
                    fileToCheck = CompilerUtils.getWebappRelativeFile(filePath, allowFileInPageFlowSourceDir(), getEnv());

                    if (fileToCheck != null && ! fileToCheck.exists() && ! (ignoreDirectories() && fileToCheck.isDirectory())) {
                        fileExists = false;
                    }
                }
            }
        }

        //
        // If the class being compiled is abstract, don't print warnings for relative-path files that aren't
        // found.  The derived class might have them.
        //
        else if (filePath.indexOf('/') != 0 && ! outerClass.hasModifier(Modifier.ABSTRACT)) {
            CompilerUtils.Mutable retFileToCheck = new CompilerUtils.Mutable();
            fileExists = checkRelativePath(filePath, outerClass, retFileToCheck, ignoreDirectories(),
                    allowFileInPageFlowSourceDir(), getEnv());
            fileToCheck = (File) retFileToCheck.get();
        }

        if (fileExists) {
            if (fileToCheck != null) runAdditionalChecks(fileToCheck, value);
        } else {
            if (doFatalError()) {
                addError(value, "error.file-not-found", filePath);
            } else {
                addWarning(value, "warning.file-not-found", filePath);
            }
        }

        if (fileToCheck != null) _flowControllerInfo.addReferencedFile(fileToCheck);

        return null;
    }

    public static boolean relativePathExists(String filePath, TypeDeclaration outerClass,
                                             AnnotationProcessorEnvironment env)
            throws FatalCompileTimeException {
        assert filePath.charAt(0) != '/' : filePath + " is not a relative path";
        if (! isCheckableExtension(filePath)) return true;
        return checkRelativePath(filePath, outerClass, null, true, false, env);
    }

    private static boolean checkRelativePath(String filePath, TypeDeclaration outerClass,
                                             CompilerUtils.Mutable retFileToCheck,
                                             boolean ignoreDirectories, boolean allowFileInPageFlowSourceDir,
                                             AnnotationProcessorEnvironment env)
            throws FatalCompileTimeException

    {
        File fileToCheck = null;
        boolean fileExists = true;

        if (filePath.endsWith(JPF_FILE_EXTENSION_DOT)) {
            String className = filePath.substring(0, filePath.length() - JPF_FILE_EXTENSION_DOT.length());
            String pkg = outerClass.getPackage().getQualifiedName();
            while (className.startsWith("../") && className.length() > 3) {
                className = className.substring(3);
                int lastDot = pkg.lastIndexOf('.');
                pkg = lastDot != -1 ? pkg.substring(0, lastDot) : "";
            }
            className = (pkg.length() > 0 ? pkg + '.' : "") + className.replace('/', '.');
            TypeDeclaration type = env.getTypeDeclaration(className);
            fileToCheck = type != null ? CompilerUtils.getSourceFile(type, false) : null;
            if (fileToCheck == null) fileExists = false;
        }
        // In certain error conditions (jpfFile == null), we can't determine the file.  In this case, just ignore.
        else if (CompilerUtils.getSourceFile(outerClass, false) != null) {
            if (allowFileInPageFlowSourceDir) {
                fileToCheck = CompilerUtils.getFileRelativeToSourceFile(outerClass, filePath, env);
            } else {
                // Use the package name to infer the relative path (from web content root) to the page flow directory.
                String[] webContentRoots = CompilerUtils.getWebContentRoots(env);
                String jpfParentRelativePath = "";
                PackageDeclaration jpfPackage = outerClass.getPackage();
                if (jpfPackage != null) jpfParentRelativePath = jpfPackage.getQualifiedName().replace('.', '/');

                for (int i = 0; i < webContentRoots.length; i++) {
                    String webContentRoot = webContentRoots[i];
                    File desiredParentDir = new File(webContentRoot, jpfParentRelativePath);
                    fileToCheck = new File(desiredParentDir, filePath);
                    if (fileToCheck.exists()) break;
                }
            }

            if (fileToCheck != null && ! fileToCheck.exists() && ! (ignoreDirectories && fileToCheck.isDirectory())) {
                fileExists = false;
            }
        }

        if (retFileToCheck != null) retFileToCheck.set(fileToCheck);
        return fileExists;
    }

    protected boolean checkAnyExtension() {
        return false;
    }

    protected boolean doFatalError() {
        return false;
    }

    protected void runAdditionalChecks(File file, AnnotationValue member) {
    }

    protected boolean ignoreDirectories() {
        return true;
    }

    /**
     * Tell whether the file must be in the page flow directory.  If not, it is assumed to live in a webapp-addressable
     * directory whose path corresponds to the page flow's package.  This is here to support page flows in WEB-INF.
     */
    protected boolean allowFileInPageFlowSourceDir() {
        return false;
    }

    protected FlowControllerInfo getFlowControllerInfo() {
        return _flowControllerInfo;
    }
}
