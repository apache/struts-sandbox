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
import org.apache.ti.compiler.internal.FormBeanChecker;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;


public class FormBeanAnnotationProcessor
        extends BaseAnnotationProcessor {

    public FormBeanAnnotationProcessor(AnnotationTypeDeclaration[] annotationTypeDecls, AnnotationProcessorEnvironment env) {
        super(annotationTypeDecls, env);
    }

    protected BaseChecker getChecker(ClassDeclaration decl, Diagnostics diagnostics) {
        if (CompilerUtils.getAnnotation(decl, FORM_BEAN_TAG_NAME, true) != null) {
            return new FormBeanChecker(getAnnotationProcessorEnvironment(), diagnostics);
        }

        return null;
    }

    protected BaseGenerator getGenerator(ClassDeclaration decl, Diagnostics diagnostics) {
        return null;
    }
}
