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
import org.apache.ti.compiler.internal.processor.PageFlowAnnotationProcessor;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessor;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;


public class PageFlowAnnotationProcessorFactory
        extends BaseAnnotationProcessorFactory
        implements JpfLanguageConstants {

    private static final HashSet PAGEFLOW_ANNOTATIONS = new HashSet();
    private static final HashSet FACES_BACKING_ANNOTATIONS = new HashSet();

    private static final ArrayList SUPPORTED_ANNOTATION_TYPES = new ArrayList();

    static {
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + ACTION_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + ACTION_OUTPUT_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + CATCH_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + CONDITIONAL_FORWARD_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + CONTROLLER_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + EXCEPTION_HANDLER_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + FORM_BEAN_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + FORWARD_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + MESSAGE_ARG_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + MESSAGE_BUNDLE_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + SIMPLE_ACTION_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATABLE_BEAN_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATABLE_PROPERTY_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_CREDIT_CARD_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_CUSTOM_RULE_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_CUSTOM_VARIABLE_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_DATE_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_EMAIL_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_MASK_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_MAX_LENGTH_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_MIN_LENGTH_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_RANGE_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_REQUIRED_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_TYPE_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATE_VALID_WHEN_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VALIDATION_LOCALE_RULES_TAG_NAME);
        PAGEFLOW_ANNOTATIONS.add(ANNOTATION_QUALIFIER + VIEW_PROPERTIES_TAG_NAME);

        FACES_BACKING_ANNOTATIONS.add(ANNOTATION_QUALIFIER + COMMAND_HANDLER_TAG_NAME);
        FACES_BACKING_ANNOTATIONS.add(ANNOTATION_QUALIFIER + FACES_BACKING_TAG_NAME);
        FACES_BACKING_ANNOTATIONS.add(ANNOTATION_QUALIFIER + PAGE_FLOW_FIELD_TAG_NAME);
        FACES_BACKING_ANNOTATIONS.add(ANNOTATION_QUALIFIER + RAISE_ACTION_TAG_NAME);

        SUPPORTED_ANNOTATION_TYPES.addAll(PAGEFLOW_ANNOTATIONS);
        SUPPORTED_ANNOTATION_TYPES.addAll(FACES_BACKING_ANNOTATIONS);
        SUPPORTED_ANNOTATION_TYPES.add(ANNOTATION_QUALIFIER + SHARED_FLOW_FIELD_TAG_NAME);
        SUPPORTED_ANNOTATION_TYPES.add(ANNOTATION_QUALIFIER + SHARED_FLOW_REF_TAG_NAME);
    }

    public Collection supportedAnnotationTypes() {
        return SUPPORTED_ANNOTATION_TYPES;
    }

    public Collection supportedOptions() {
        return Collections.EMPTY_LIST;
    }

    public AnnotationProcessor getProcessorFor(AnnotationTypeDeclaration[] atds, AnnotationProcessorEnvironment env) {
        return new PageFlowAnnotationProcessor(atds, env);
    }
}
