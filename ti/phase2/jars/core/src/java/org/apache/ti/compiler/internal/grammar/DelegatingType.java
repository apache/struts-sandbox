/*
 * B E A   S Y S T E M S
 * Copyright 2002-2004  BEA Systems, Inc.
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
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;

public class DelegatingType
        extends AnnotationMemberType {

    private AnnotationMemberType _baseType;

    public DelegatingType(AnnotationMemberType baseType, String requiredRuntimeVersion,
                          AnnotationGrammar parentGrammar) {
        super(requiredRuntimeVersion, parentGrammar);
        _baseType = baseType;
    }


    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue member,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex)
            throws FatalCompileTimeException {
        return _baseType.onCheck(valueDecl, member, parentAnnotations, classMember, annotationArrayIndex);
    }
}
