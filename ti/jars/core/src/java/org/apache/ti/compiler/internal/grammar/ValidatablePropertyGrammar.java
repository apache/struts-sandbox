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

import org.apache.ti.compiler.internal.AnnotationMemberType;
import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.Diagnostics;
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.RuntimeVersionChecker;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;


public class ValidatablePropertyGrammar
        extends ValidationRulesContainerGrammar {

    private static String[][] REQUIRED_ATTRS = {{PROPERTY_NAME_ATTR}};
    private static String[][] MUTUALLY_EXCLUSIVE_ATTRS = {{DISPLAY_NAME_ATTR, DISPLAY_NAME_KEY_ATTR}};


    public ValidatablePropertyGrammar(AnnotationProcessorEnvironment env, Diagnostics diags, RuntimeVersionChecker rvc) {
        super(env, diags, rvc);

        addMemberType(PROPERTY_NAME_ATTR, new AnnotationMemberType(null, this));
        addMemberType(DISPLAY_NAME_ATTR, new AnnotationMemberType(null, this));
        addMemberType(DISPLAY_NAME_KEY_ATTR, new AnnotationMemberType(null, this));
        addMemberArrayGrammar(LOCALE_RULES_ATTR, new LocaleRulesGrammar(env, diags, rvc));
    }

    /**
     * This is overridable by derived classes, which is why it's not simply defined as required in
     * {@link org.apache.ti.pageflow.annotations.Jpf}.
     */
    public String[][] getRequiredAttrs() {
        return REQUIRED_ATTRS;
    }

    public String[][] getMutuallyExclusiveAttrs() {
        return MUTUALLY_EXCLUSIVE_ATTRS;
    }

    protected boolean onBeginCheck(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                   MemberDeclaration classMember)
            throws FatalCompileTimeException {
        if (parentAnnotations == null) return true;

        //
        // Look through all annotation parents for @Jpf.Action or @Jpf.SimpleAction.  If we find one, and there's
        // no validationErrorForward on it, print a warning.
        //
        for (int i = parentAnnotations.length - 1; i >= 0; --i) {
            AnnotationInstance ann = parentAnnotations[i];

            if (CompilerUtils.isJpfAnnotation(ann, ACTION_TAG_NAME)
                    || CompilerUtils.isJpfAnnotation(ann, SIMPLE_ACTION_TAG_NAME)) {
                //
                // Give a warning if there is no validationErrorForward annotation and doValidation isn't set to false.
                //
                if (CompilerUtils.getAnnotationValue(ann, VALIDATION_ERROR_FORWARD_ATTR, true) == null) {
                    Boolean doValidation = CompilerUtils.getBoolean(ann, DO_VALIDATION_ATTR, true);

                    if (doValidation == null || doValidation.booleanValue()) {
                        addWarning(annotation, "warning.validation-annotations-no-forward",
                                ANNOTATION_INTERFACE_PREFIX + ann.getAnnotationType().getDeclaration().getSimpleName(),
                                VALIDATION_ERROR_FORWARD_ATTR);
                    }
                }
            }
        }

        return true;
    }
}
