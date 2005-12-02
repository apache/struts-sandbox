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

import org.apache.ti.compiler.internal.*;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

import java.io.File;


public class PageFlowAnnotationProcessor
        extends BaseAnnotationProcessor
        implements JpfLanguageConstants {

    public PageFlowAnnotationProcessor(AnnotationTypeDeclaration[] annotationTypeDecls,
                                       AnnotationProcessorEnvironment env) {
        super(annotationTypeDecls, env);
    }

    public BaseChecker getChecker(ClassDeclaration classDecl, Diagnostics diagnostics) {
        AnnotationProcessorEnvironment env = getAnnotationProcessorEnvironment();

        if (CompilerUtils.isAssignableFrom(JPF_BASE_CLASS, classDecl, env)) {
            if (expectAnnotation(classDecl, CONTROLLER_TAG_NAME, JPF_FILE_EXTENSION_DOT, JPF_BASE_CLASS, diagnostics)) {
                FlowControllerInfo fcInfo = new FlowControllerInfo(classDecl);
                setSourceFileInfo(classDecl, fcInfo);

                return new PageFlowChecker(env, diagnostics, fcInfo);
            }
        } else if (CompilerUtils.isAssignableFrom(SHARED_FLOW_BASE_CLASS, classDecl, env)) {
            if (expectAnnotation(classDecl, CONTROLLER_TAG_NAME, SHARED_FLOW_FILE_EXTENSION_DOT,
                    SHARED_FLOW_BASE_CLASS, diagnostics)) {
                FlowControllerInfo fcInfo = new FlowControllerInfo(classDecl);
                setSourceFileInfo(classDecl, fcInfo);

                return new SharedFlowChecker(env, fcInfo, diagnostics);
            }
        } else if (CompilerUtils.isAssignableFrom(FACES_BACKING_BEAN_CLASS, classDecl, env)) {
            if (expectAnnotation(classDecl, FACES_BACKING_TAG_NAME, JPF_FILE_EXTENSION_DOT, JPF_BASE_CLASS, diagnostics)) {
                File originalFile = CompilerUtils.getSourceFile(classDecl, true);
                FacesBackingInfo fbInfo = new FacesBackingInfo(originalFile, classDecl.getQualifiedName());
                setSourceFileInfo(classDecl, fbInfo);
                return new FacesBackingChecker(env, fbInfo, diagnostics);
            }
        } else {
            AnnotationInstance ann = CompilerUtils.getAnnotation(classDecl, CONTROLLER_TAG_NAME);

            if (ann != null) {
                diagnostics.addError(ann, "error.annotation-invalid-base-class2",
                        CONTROLLER_TAG_NAME, JPF_BASE_CLASS, SHARED_FLOW_BASE_CLASS);
            }

            ann = CompilerUtils.getAnnotation(classDecl, FACES_BACKING_TAG_NAME);

            if (ann != null) {
                diagnostics.addError(ann, "error.annotation-invalid-base-class",
                        FACES_BACKING_TAG_NAME, FACES_BACKING_BEAN_CLASS);
            }
        }

        return null;
    }

    public BaseGenerator getGenerator(ClassDeclaration classDecl, Diagnostics diags) {
        AnnotationProcessorEnvironment env = getAnnotationProcessorEnvironment();
        SourceFileInfo sourceFileInfo = getSourceFileInfo(classDecl);

        if (CompilerUtils.isAssignableFrom(JPF_BASE_CLASS, classDecl, env)) {
            assert sourceFileInfo != null : classDecl.getQualifiedName();
            assert sourceFileInfo instanceof FlowControllerInfo : sourceFileInfo.getClass().getName();
            return new PageFlowGenerator(env, (FlowControllerInfo) sourceFileInfo, diags);
        } else if (CompilerUtils.isAssignableFrom(SHARED_FLOW_BASE_CLASS, classDecl, env)) {
            assert sourceFileInfo != null : classDecl.getQualifiedName();
            assert sourceFileInfo instanceof FlowControllerInfo : sourceFileInfo.getClass().getName();
            return new SharedFlowGenerator(env, (FlowControllerInfo) sourceFileInfo, diags);
        } else if (CompilerUtils.isAssignableFrom(FACES_BACKING_BEAN_CLASS, classDecl, env)) {
            assert sourceFileInfo != null : classDecl.getQualifiedName();
            assert sourceFileInfo instanceof FacesBackingInfo : sourceFileInfo.getClass().getName();
            return new FacesBackingGenerator(env, sourceFileInfo, diags);
        }

        return null;
    }
}
