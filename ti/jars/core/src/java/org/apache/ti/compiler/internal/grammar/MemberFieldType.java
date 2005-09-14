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
import org.apache.ti.compiler.internal.typesystem.declaration.FieldDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

import java.util.Collection;
import java.util.Iterator;


public class MemberFieldType
        extends AnnotationMemberType {

    private String _requiredSuperclassName;


    public MemberFieldType(String requiredSuperclassName, String requiredRuntimeVersion,
                           AnnotationGrammar parentGrammar) {
        super(requiredRuntimeVersion, parentGrammar);
        _requiredSuperclassName = requiredSuperclassName;
    }


    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue member,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex) {
        String fieldName = (String) member.getValue();
        Collection fields =
                CompilerUtils.getClassFields(CompilerUtils.getOuterClass(classMember));

        for (Iterator ii = fields.iterator(); ii.hasNext();) {
            FieldDeclaration field = (FieldDeclaration) ii.next();
            if (field.getSimpleName().equals(fieldName)) {
                TypeInstance fieldType = CompilerUtils.getGenericBoundsType(field.getType());

                if (_requiredSuperclassName != null
                        && ! CompilerUtils.isAssignableFrom(_requiredSuperclassName, fieldType, getEnv())) {
                    addError(member, "error.wrong-field-type", new Object[]{fieldName, _requiredSuperclassName});
                    return null;
                }

                return field;
            }
        }

        addError(member, "error.unresolved-field", fieldName);
        return null;
    }
}
