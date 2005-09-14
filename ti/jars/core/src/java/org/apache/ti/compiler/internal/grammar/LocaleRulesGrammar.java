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
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.RuntimeVersionChecker;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;


public class LocaleRulesGrammar
        extends ValidationRulesContainerGrammar
        implements JpfLanguageConstants {

    private static final String[][] REQUIRED_ATTRS =
            {
                    {APPLY_TO_UNHANDLED_LOCALES_ATTR, LANGUAGE_ATTR},
            };

    private static final String[][] ATTR_DEPENDENCIES =
            {
                    {COUNTRY_ATTR, LANGUAGE_ATTR},
                    {VARIANT_ATTR, LANGUAGE_ATTR}
            };

    public LocaleRulesGrammar(AnnotationProcessorEnvironment env, Diagnostics diags,
                              RuntimeVersionChecker rvc) {
        super(env, diags, rvc);

        addMemberType(LANGUAGE_ATTR, new AnnotationMemberType(null, this));
        addMemberType(COUNTRY_ATTR, new AnnotationMemberType(null, this));
        addMemberType(VARIANT_ATTR, new AnnotationMemberType(null, this));
        addMemberType(APPLY_TO_UNHANDLED_LOCALES_ATTR, new ApplyToUnhandledLocalesType());
    }

    public String[][] getRequiredAttrs() {
        return REQUIRED_ATTRS;
    }

    public String[][] getAttrDependencies() {
        return ATTR_DEPENDENCIES;
    }

    private class ApplyToUnhandledLocalesType
            extends AnnotationMemberType {

        public ApplyToUnhandledLocalesType() {
            super(null, LocaleRulesGrammar.this);
        }


        public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue member,
                              AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                              int annotationArrayIndex) {
            AnnotationInstance parentAnnotation = parentAnnotations[parentAnnotations.length - 1];
            String language = CompilerUtils.getString(parentAnnotation, LANGUAGE_ATTR, true);

            if (((Boolean) member.getValue()).booleanValue()) {
                if (language != null) {
                    addError(member, "error.incompatible-locale-annotations", LANGUAGE_ATTR,
                            APPLY_TO_UNHANDLED_LOCALES_ATTR);
                }
            } else {
                if (language == null || language.length() == 0) {
                    addError(member, "error.missing-locale-annotations", LANGUAGE_ATTR,
                            APPLY_TO_UNHANDLED_LOCALES_ATTR);
                }
            }

            return null;
        }
    }
}
