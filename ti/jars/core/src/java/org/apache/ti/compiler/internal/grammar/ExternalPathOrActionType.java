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
import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.FlowControllerInfo;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;

public class ExternalPathOrActionType extends WebappPathOrActionType {

    public ExternalPathOrActionType(boolean pathMustBeRelative, String requiredRuntimeVersion,
                                    AnnotationGrammar parentGrammar, FlowControllerInfo fcInfo) {
        super(pathMustBeRelative, requiredRuntimeVersion, parentGrammar, fcInfo);
    }


    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex)
            throws FatalCompileTimeException {
        AnnotationInstance parentAnnotation = parentAnnotations[parentAnnotations.length - 1];

        // if we have an external redirect just bail
        if (CompilerUtils.getBoolean(parentAnnotation, EXTERNAL_REDIRECT_ATTR, false).booleanValue()) {
            return null;
        }

        return super.onCheck(valueDecl, value, parentAnnotations, classMember, annotationArrayIndex);
    }
}
