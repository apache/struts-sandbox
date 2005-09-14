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
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;


public class BundleNameType
        extends AnnotationMemberType {

    public BundleNameType(String requiredRuntimeVersion, AnnotationGrammar parentGrammar) {
        super(requiredRuntimeVersion, parentGrammar);
    }


    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex) {
        //
        // Check that the bundle attribute isn't used with commons-validator v1.0
        //
        String bundle = (String) value.getValue();

        if (bundle != null && !bundlesSupported(parentAnnotations, classMember)) {
            AnnotationInstance annotation = parentAnnotations[parentAnnotations.length - 1];
            addError(value, "error.validation-bundle-support", BUNDLE_NAME_ATTR, annotation);
        }

        return null;
    }

    protected static boolean bundlesSupported(AnnotationInstance[] parentAnnotations, MemberDeclaration classMember) {
        //
        // Find the validator version attribute from the controller.
        // Look at the annotation parent root for @Jpf.Controller,
        // @Jpf.Action or @Jpf.ValidatableProperty. If the root is
        // the Controller then just get the required attribute.
        // Otherwise, get the controller class declaration then the
        // attribute.
        //
        AnnotationInstance ann = parentAnnotations[0];
        String validatorVersion = null;

        if (CompilerUtils.isJpfAnnotation(ann, CONTROLLER_TAG_NAME)) {
            validatorVersion = CompilerUtils.getEnumFieldName(ann, VALIDATOR_VERSION_ATTR, true);
        } else {
            TypeDeclaration outerType = null;

            if (CompilerUtils.isJpfAnnotation(ann, ACTION_TAG_NAME)) {
                outerType = CompilerUtils.getOuterClass(classMember);
            } else if (CompilerUtils.isJpfAnnotation(ann, VALIDATABLE_PROPERTY_TAG_NAME)) {
                outerType = CompilerUtils.getOutermostClass(classMember);
            } else {
                // Should not hit this condition
                assert false;
            }

            if (outerType instanceof ClassDeclaration) {
                ann = CompilerUtils.getAnnotation(outerType, CONTROLLER_TAG_NAME);

                if (ann != null) {
                    validatorVersion = CompilerUtils.getEnumFieldName(ann, VALIDATOR_VERSION_ATTR, true);
                }
            }
        }

        //
        // Default is commons-validator v1.0 unless otherwise declared with the @Jpf.Controller
        // validator version attribute.
        if (validatorVersion == null || validatorVersion.equals(VALIDATOR_VERSION_ONE_ZERO_STR)) {
            return false;
        }

        return true;
    }
}
