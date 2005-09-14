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
package org.apache.ti.compiler.apt;

import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.processor.FormBeanAnnotationProcessor;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessor;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


public class FormBeanAnnotationProcessorFactory
        extends BaseAnnotationProcessorFactory
        implements JpfLanguageConstants {

    public Collection supportedAnnotationTypes() {
        return Collections.unmodifiableCollection(
                Arrays.asList(new String[]{ANNOTATION_QUALIFIER + FORM_BEAN_TAG_NAME}));
    }

    public Collection supportedOptions() {
        return new ArrayList();
    }

    public AnnotationProcessor getProcessorFor(AnnotationTypeDeclaration[] atds, AnnotationProcessorEnvironment env) {
        return new FormBeanAnnotationProcessor(atds, env);
    }
}
