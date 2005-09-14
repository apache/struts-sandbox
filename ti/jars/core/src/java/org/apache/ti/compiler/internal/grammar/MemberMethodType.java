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
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;

public class MemberMethodType
        extends AnnotationMemberType {

    private String _requiredMethodAnnotation;
    private String _errorCode;

    public MemberMethodType(String requiredMethodAnnotation, String errorCode, String requiredRuntimeVersion,
                            AnnotationGrammar parentGrammar) {
        super(requiredRuntimeVersion, parentGrammar);
        _requiredMethodAnnotation = requiredMethodAnnotation;
        _errorCode = errorCode;
    }


    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex) {
        //
        // Look through all the methods to see if there is one whose name matches the given value.
        //
        TypeDeclaration outerType = CompilerUtils.getOuterClass(classMember);
        MethodDeclaration[] methods = CompilerUtils.getClassMethods(outerType, null);
        String methodName = (String) value.getValue();

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];

            if (method.getSimpleName().equals(methodName)) {
                if (_requiredMethodAnnotation == null
                        || CompilerUtils.getAnnotation(method, _requiredMethodAnnotation) != null) {
                    checkMethod(method, value, parentAnnotations, classMember);
                    return method;
                }
            }
        }

        addError(value, _errorCode, methodName);
        return null;
    }

    protected MethodDeclaration findMethod(String methodName, TypeDeclaration outerType) {
        MethodDeclaration[] methods = CompilerUtils.getClassMethods(outerType, null);

        for (int i = 0; i < methods.length; i++) {
            MethodDeclaration method = methods[i];

            if (method.getSimpleName().equals(methodName)) {
                if (_requiredMethodAnnotation == null
                        || CompilerUtils.getAnnotation(method, _requiredMethodAnnotation) != null) {
                    return method;
                }
            }
        }

        return null;
    }

    /**
     * Derived classes can plug in here to do additional checks.
     */
    protected void checkMethod(MethodDeclaration method, AnnotationValue member, AnnotationInstance[] parentAnnotations,
                               MemberDeclaration classMember) {
    }
}
