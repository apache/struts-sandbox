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
import org.apache.ti.compiler.internal.FlowControllerInfo;
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.RuntimeVersionChecker;
import org.apache.ti.compiler.internal.typesystem.declaration.*;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;


public class ActionGrammar
        extends BaseFlowControllerGrammar
        implements JpfLanguageConstants {

    public ActionGrammar(AnnotationProcessorEnvironment env, Diagnostics diags,
                         RuntimeVersionChecker rvc, FlowControllerInfo fcInfo) {
        super(env, diags, null, rvc, fcInfo);

        addMemberType(LOGIN_REQUIRED_ATTR, new AnnotationMemberType(null, this));
        addMemberType(ROLES_ALLOWED_ATTR, new RolesAllowedType(this));
        addMemberType(READONLY_ATTR, new AnnotationMemberType(VERSION_8_SP2_STRING, this));
        addMemberType(USE_FORM_BEAN_ATTR, new UseFormBeanType());
        addMemberType(PREVENT_DOUBLE_SUBMIT_ATTR, new AnnotationMemberType(null, this));
        addMemberType(DO_VALIDATION_ATTR, new DoValidateType());

        addMemberArrayGrammar(FORWARDS_ATTR, new ForwardGrammar(env, diags, null, rvc, fcInfo));
        addMemberArrayGrammar(CATCHES_ATTR, new CatchGrammar(env, diags, null, rvc, ACTION_TAG_NAME, fcInfo));
        addMemberArrayGrammar(VALIDATABLE_PROPERTIES_ATTR, new ValidatablePropertyGrammar(env, diags, rvc));
        addMemberGrammar(VALIDATION_ERROR_FORWARD_ATTR, new ActionForwardGrammar());
    }

    public String[][] getMutuallyExclusiveAttrs() {
        return null;
    }

    public String[][] getRequiredAttrs() {
        return null;
    }

    protected boolean onBeginCheck(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                   MemberDeclaration classMember)
            throws FatalCompileTimeException {
        //
        // First check the form bean type.
        //
        TypeInstance argType = getFormBeanType(annotation, classMember);
        TypeDeclaration argTypeDecl = null;

        if (! (argType instanceof DeclaredType)) {
            if (argType != null) {
                getDiagnostics().addError(annotation, "error.action-invalid-form-bean-type", argType.toString());
                argType = null;
            }
        } else {
            argTypeDecl = CompilerUtils.getDeclaration((DeclaredType) argType);
            boolean isClass = argTypeDecl instanceof ClassDeclaration;

            if (isClass && ! CompilerUtils.hasDefaultConstructor(argTypeDecl)) {
                getDiagnostics().addError(annotation, "error.action-form-bean-no-default-constructor",
                        argTypeDecl.getQualifiedName());
            }

            if (! argTypeDecl.hasModifier(Modifier.PUBLIC)) {
                getDiagnostics().addError(annotation, "error.action-form-bean-not-public",
                        argTypeDecl.getQualifiedName());
            }

            if (isClass && argTypeDecl.getDeclaringType() != null && ! argTypeDecl.hasModifier(Modifier.STATIC)) {
                getDiagnostics().addError(annotation, "error.action-form-bean-not-static",
                        argTypeDecl.getQualifiedName());
            }

            //
            // Give a warning if there is no validationErrorForward annotation and doValidation isn't set to false.
            //
            if (CompilerUtils.getAnnotationValue(annotation, VALIDATION_ERROR_FORWARD_ATTR, true) == null
                    && hasValidationAnnotations(argTypeDecl)) {
                Boolean doValidation = CompilerUtils.getBoolean(annotation, DO_VALIDATION_ATTR, true);

                if (doValidation == null || doValidation.booleanValue()) {
                    getDiagnostics().addWarning(
                            annotation, "warning.validatable-formbean-no-forward",
                            ANNOTATION_INTERFACE_PREFIX + annotation.getAnnotationType().getDeclaration().getSimpleName(),
                            VALIDATION_ERROR_FORWARD_ATTR, argTypeDecl.getQualifiedName());
                }
            }
        }

        //
        // Add this action to the FlowControllerInfo.
        //
        getFlowControllerInfo().addAction(getActionName(annotation, classMember),
                argTypeDecl != null ? argTypeDecl.getQualifiedName() : null);

        //
        // Check to make sure the 'useFormBean' attribute (reference to a member variable) matches the form declared as
        // an argument to the action method.
        //
        TypeInstance useFormBeanType = getUseFormBeanType(annotation, classMember);

        if (useFormBeanType != null && useFormBeanType instanceof DeclaredType) {
            if (argType == null) {
                String memberFormTypeName = CompilerUtils.getDeclaration((DeclaredType) useFormBeanType).getQualifiedName();
                getDiagnostics().addError(annotation, "error.action-mismatched-form", USE_FORM_BEAN_ATTR,
                        memberFormTypeName);
            } else if (! CompilerUtils.isAssignableFrom(argTypeDecl, useFormBeanType)) {
                String memberFormTypeName = CompilerUtils.getDeclaration((DeclaredType) useFormBeanType).getQualifiedName();
                getDiagnostics().addError(annotation, "error.action-mismatched-form", USE_FORM_BEAN_ATTR,
                        memberFormTypeName);
            }
        }

        return true;
    }

    protected String getActionName(AnnotationInstance annotation, MemberDeclaration classMember) {
        assert classMember instanceof MethodDeclaration : classMember.getClass().getName();
        return classMember.getSimpleName();
    }

    private static boolean hasValidationAnnotations(TypeDeclaration type) {
        // Could cache this if it's a performance problem.

        MethodDeclaration[] methods = type.getMethods();

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];
            AnnotationInstance[] annotations = method.getAnnotationInstances();

            for (int j = 0; j < annotations.length; j++) {
                AnnotationInstance ann = annotations[j];
                String annotationName = CompilerUtils.getDeclaration(ann.getAnnotationType()).getQualifiedName();
                int pos = annotationName.indexOf(ANNOTATION_QUALIFIER);

                if (pos != -1) {
                    if (annotationName.substring(pos + ANNOTATION_QUALIFIER.length()).startsWith("Validat")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected static TypeInstance getUseFormBeanType(AnnotationInstance annotation, MemberDeclaration classMember) {
        String formBeanFieldName = CompilerUtils.getString(annotation, USE_FORM_BEAN_ATTR, true);

        if (formBeanFieldName != null) {
            FieldDeclaration formBeanField =
                    CompilerUtils.findField(CompilerUtils.getOutermostClass(classMember), formBeanFieldName);

            if (formBeanField != null) {
                return CompilerUtils.getGenericBoundsType(formBeanField.getType());
            }
        }

        return null;
    }

    protected TypeInstance getFormBeanType(AnnotationInstance annotation, MemberDeclaration classMember) {
        assert classMember instanceof MethodDeclaration : classMember.getClass().getName();
        MethodDeclaration method = (MethodDeclaration) classMember;
        ParameterDeclaration[] parameters = method.getParameters();
        int nParameters = parameters.length;

        if (nParameters > 1) getDiagnostics().addError(method, "error.action-method-wrong-arg");
        if (nParameters > 0) return CompilerUtils.getGenericBoundsType(parameters[0].getType());

        return null;
    }

    private class DoValidateType
            extends AnnotationMemberType {

        public DoValidateType() {
            super(null, ActionGrammar.this);
        }


        public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue member,
                              AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                              int annotationArrayIndex) {
            //
            // If this value is set to true, there must be a value for validationErrorForward.
            //
            if (((Boolean) member.getValue()).booleanValue()) {
                AnnotationInstance parentAnnotation = parentAnnotations[parentAnnotations.length - 1];

                if (CompilerUtils.getAnnotation(parentAnnotation, VALIDATION_ERROR_FORWARD_ATTR, true) == null) {
                    addError(member, "error.validate-with-no-validation-error-forward", DO_VALIDATION_ATTR,
                            VALIDATION_ERROR_FORWARD_ATTR);
                }
            }

            return null;
        }
    }

    private class ActionForwardGrammar
            extends ForwardGrammar {

        public ActionForwardGrammar() {
            super(ActionGrammar.this.getEnv(), ActionGrammar.this.getDiagnostics(), null,
                    ActionGrammar.this.getRuntimeVersionChecker(), ActionGrammar.this.getFlowControllerInfo());
            ExternalPathOrActionType baseForwardType =
                    new ExternalPathOrActionType(false, null, this, ActionGrammar.this.getFlowControllerInfo());
            addMemberType(PATH_ATTR, new ForwardToExternalPathType(baseForwardType, null, ActionGrammar.this));
        }
    }

    private class UseFormBeanType
            extends WritableFieldType {

        public UseFormBeanType() {
            super(null, USE_FORM_BEAN_ATTR, null, ActionGrammar.this);
        }


        public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                              AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                              int annotationArrayIndex) {
            FieldDeclaration memberField =
                    (FieldDeclaration) super.onCheck(valueDecl, value, parentAnnotations, classMember,
                            annotationArrayIndex);

            if (memberField != null) {
                //
                // If this action is marked 'readOnly', print a warning about the 'useFormBean' attribute implicitly
                // modifying member data.
                //
                AnnotationInstance parentAnnotation = parentAnnotations[parentAnnotations.length - 1];
                if (CompilerUtils.getBoolean(parentAnnotation, READONLY_ATTR, false).booleanValue()) {
                    addWarning(value, "warning.use-form-bean-on-readonly-action", READONLY_ATTR, USE_FORM_BEAN_ATTR,
                            memberField.getSimpleName());
                }
            }

            return memberField;
        }
    }
}
