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
package org.apache.ti.compiler.internal.processor;

import org.apache.ti.compiler.internal.BaseChecker;
import org.apache.ti.compiler.internal.BaseGenerator;
import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.Diagnostics;
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.SourceFileInfo;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Declaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Modifier;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.ResourceBundle;

public abstract class BaseAnnotationProcessor
        extends TwoPhaseAnnotationProcessor
        implements JpfLanguageConstants {

    private HashMap _sourceFileInfo;
    private SourceFileInfo _singleSourceFileInfo = null;
    private ResourceBundle _messages;


    protected BaseAnnotationProcessor(AnnotationTypeDeclaration[] annotationTypeDecls,
                                      AnnotationProcessorEnvironment env) {
        super(annotationTypeDecls, env);
        _messages = ResourceBundle.getBundle(TI_PACKAGE + ".compiler.internal.diagnostics");
    }

    public void check(Declaration decl)
            throws FatalCompileTimeException {
        assert _sourceFileInfo != null;     // process() should guarantee this.

        if (decl instanceof ClassDeclaration) {
            ClassDeclaration classDecl = (ClassDeclaration) decl;
            BaseChecker checker = getChecker(classDecl, this);

            if (checker != null) {
                checker.check(classDecl);

                //
                // Also do a silent check on all base classes.  We don't want to generate if there were errors
                // in the base class.
                //
                SilentDiagnostics silentDiagnostics = new SilentDiagnostics();

                for (ClassType i = classDecl.getSuperclass(); i != null; i = i.getSuperclass()) {
                    ClassDeclaration baseDecl = i.getClassTypeDeclaration();

                    if (CompilerUtils.getSourceFile(baseDecl, false) != null) {
                        BaseChecker silentChecker = getChecker(baseDecl, silentDiagnostics);
                        if (silentChecker != null) silentChecker.check(baseDecl);
                    }
                }

                if (silentDiagnostics.hasErrors()) setHasErrors(true);
            }
        }
    }

    public void generate(Declaration decl)
            throws FatalCompileTimeException {
        assert _sourceFileInfo != null;     // process() should guarantee this.

        if (decl instanceof ClassDeclaration) {
            ClassDeclaration classDecl = (ClassDeclaration) decl;
            BaseGenerator generator = getGenerator(classDecl, this);
            if (generator != null) generator.generate(classDecl);
        }
    }

    public void process() {
        _sourceFileInfo = new HashMap();
        super.process();
        _sourceFileInfo = null;
    }

    protected abstract BaseChecker getChecker(ClassDeclaration decl, Diagnostics diagnostics);

    protected abstract BaseGenerator getGenerator(ClassDeclaration decl, Diagnostics diagnostics);

    protected SourceFileInfo getSourceFileInfo(ClassDeclaration decl) {
        assert _sourceFileInfo != null || _singleSourceFileInfo != null;
        assert _sourceFileInfo == null || _singleSourceFileInfo == null;
        return _singleSourceFileInfo != null ? _singleSourceFileInfo : (SourceFileInfo) _sourceFileInfo.get(decl.getQualifiedName());
    }

    protected void setSourceFileInfo(ClassDeclaration decl, SourceFileInfo sourceFileInfo) {
        assert _sourceFileInfo != null || _singleSourceFileInfo == null;

        if (_sourceFileInfo != null) {
            _sourceFileInfo.put(decl.getQualifiedName(), sourceFileInfo);
        } else {
            _singleSourceFileInfo = sourceFileInfo;
        }
    }

    protected static boolean expectAnnotation(ClassDeclaration classDecl, String annotationBaseName,
                                              String fileExtensionRequiresAnnotation, String baseClass,
                                              Diagnostics diagnostics) {
        if (CompilerUtils.getAnnotation(classDecl, annotationBaseName) != null) return true;

        String fileName = classDecl.getPosition().file().getName();

        if (fileExtensionRequiresAnnotation != null && fileName.endsWith(fileExtensionRequiresAnnotation)) {
            diagnostics.addError(classDecl, "error.annotation-required",
                    fileExtensionRequiresAnnotation, ANNOTATIONS_CLASSNAME + '.' + annotationBaseName);
        } else if (! classDecl.hasModifier(Modifier.ABSTRACT)) {
            diagnostics.addWarning(classDecl, "warning.missing-annotation", baseClass,
                    ANNOTATIONS_CLASSNAME + '.' + annotationBaseName);
        }

        return false;
    }

    protected String getResourceString(String key, Object[] args) {
        String message = _messages.getString(key);
        return MessageFormat.format(message, args);
    }
}
