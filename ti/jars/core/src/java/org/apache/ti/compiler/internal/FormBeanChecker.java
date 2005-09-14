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

import org.apache.ti.compiler.internal.grammar.ValidatablePropertyGrammar;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Modifier;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

import java.util.Map;


public class FormBeanChecker
        extends BaseChecker
        implements JpfLanguageConstants {

    public FormBeanChecker(AnnotationProcessorEnvironment env, Diagnostics diags) {
        super(env, null, diags);
    }

    public Map onCheck(ClassDeclaration jclass)
            throws FatalCompileTimeException {
        GetterValidatablePropertyGrammar validatablePropertyGrammar = new GetterValidatablePropertyGrammar();
        boolean isFormBeanClass = CompilerUtils.getAnnotation(jclass, FORM_BEAN_TAG_NAME, true) != null;

        //
        // Look for ValidationField annotations on the methods; if there are some present, then we consider this
        // a form bean class, even if it's not annotated as such.
        //
        MethodDeclaration[] methods = CompilerUtils.getClassMethods(jclass, null);

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];
            isFormBeanClass |=
                    checkValidationAnnotation(method, VALIDATABLE_PROPERTY_TAG_NAME, validatablePropertyGrammar);
            // We don't currently support validation rule annotations directly on getter methods.
            /*
            hasOne |= checkValidationAnnotation( method, LOCALE_RULES_ATTR, _validationLocaleRulesGrammar );
            hasOne |= checkValidationAnnotation( method, VALIDATE_REQUIRED_TAG_NAME, _baseValidationRuleGrammar );
            hasOne |= checkValidationAnnotation( method, VALIDATE_RANGE_TAG_NAME, _validateRangeGrammar );
            hasOne |= checkValidationAnnotation( method, VALIDATE_MIN_LENGTH_TAG_NAME, _baseValidationRuleGrammar );
            hasOne |= checkValidationAnnotation( method, VALIDATE_MAX_LENGTH_TAG_NAME, _baseValidationRuleGrammar );
            hasOne |= checkValidationAnnotation( method, VALIDATE_CREDIT_CARD_TAG_NAME, _baseValidationRuleGrammar );
            hasOne |= checkValidationAnnotation( method, VALIDATE_EMAIL_TAG_NAME, _baseValidationRuleGrammar );
            hasOne |= checkValidationAnnotation( method, VALIDATE_MASK_TAG_NAME, _baseValidationRuleGrammar );
            hasOne |= checkValidationAnnotation( method, VALIDATE_DATE_TAG_NAME, _baseValidationRuleGrammar );
            hasOne |= checkValidationAnnotation( method, VALIDATE_TYPE_TAG_NAME, _validateTypeGrammar );
            */
        }

        //
        // Make sure form bean subclasses are public static, and that they have default constructors.
        //
        if (isFormBeanClass) {
            if (jclass.getDeclaringType() != null && ! jclass.hasModifier(Modifier.STATIC)) {
                getDiagnostics().addError(jclass, "error.form-not-static");
            }

            if (! jclass.hasModifier(Modifier.PUBLIC)) {
                getDiagnostics().addError(jclass, "error.form-not-public");
            }

            if (! CompilerUtils.hasDefaultConstructor(jclass)) {
                getDiagnostics().addError(jclass, "error.form-no-default-constructor");
            }
        }

        return null;
    }

    private boolean checkValidationAnnotation(MethodDeclaration method, String annotationTagName,
                                              AnnotationGrammar grammar)
            throws FatalCompileTimeException {
        AnnotationInstance annotation = CompilerUtils.getAnnotation(method, annotationTagName);

        if (annotation != null) {
            if (CompilerUtils.getBeanProperty(method) == null) {
                getDiagnostics().addError(annotation, "error.validation-field-on-non-getter");
            }

            grammar.check(annotation, null, method);

            return true;
        }

        return false;
    }

    private class GetterValidatablePropertyGrammar
            extends ValidatablePropertyGrammar {

        public GetterValidatablePropertyGrammar() {
            super(FormBeanChecker.this.getEnv(), FormBeanChecker.this.getDiagnostics(),
                    FormBeanChecker.this.getRuntimeVersionChecker());
        }

        public String[][] getRequiredAttrs() {
            return null;  // This override causes the 'propertyName' attribute *not* to be required
        }

        protected void onCheckMember(AnnotationTypeElementDeclaration memberDecl, AnnotationValue member,
                                     AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                     MemberDeclaration classMember) {
            if (memberDecl.getSimpleName().equals(PROPERTY_NAME_ATTR)) {
                addError(member, "error.validatable-field-property-name-not-allowed", PROPERTY_NAME_ATTR);
            }
        }
    }
}
