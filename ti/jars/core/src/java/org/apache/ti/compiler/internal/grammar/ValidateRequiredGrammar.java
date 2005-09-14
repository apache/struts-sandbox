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

import org.apache.ti.compiler.internal.Diagnostics;
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.RuntimeVersionChecker;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.type.PrimitiveType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;


public class ValidateRequiredGrammar
        extends BaseValidationRuleGrammar {

    public ValidateRequiredGrammar(AnnotationProcessorEnvironment env, Diagnostics diags, RuntimeVersionChecker rvc) {
        super(env, diags, rvc);
    }

    protected boolean onBeginCheck(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                   MemberDeclaration classMember)
            throws FatalCompileTimeException {
        //
        // Add a warning when this annotation is on a getter method which returns a primitive type.
        // In that case, it will never be null.
        //
        if (classMember instanceof MethodDeclaration) {
            TypeInstance returnType = ((MethodDeclaration) classMember).getReturnType();

            if (returnType instanceof PrimitiveType) {
                addWarning(annotation, "warning.validate-required-on-primitive-type",
                        ANNOTATION_INTERFACE_PREFIX + VALIDATE_REQUIRED_TAG_NAME);
            }
        }

        return super.onBeginCheck(annotation, parentAnnotations, classMember);
    }
}
