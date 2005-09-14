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
import org.apache.ti.compiler.internal.typesystem.type.ArrayType;
import org.apache.ti.compiler.internal.typesystem.type.PrimitiveType;
import org.apache.ti.compiler.internal.typesystem.type.ReferenceType;
import org.apache.ti.compiler.internal.typesystem.type.VoidType;

public class TypeNameType
        extends AnnotationMemberType {

    private String _requiredSuperclassName;
    private boolean _allowArrayType;


    public TypeNameType(String requiredSuperclassName, boolean allowArrayType, String requiredRuntimeVersion,
                        AnnotationGrammar parentGrammar) {
        super(requiredRuntimeVersion, parentGrammar);
        _requiredSuperclassName = requiredSuperclassName;
        _allowArrayType = allowArrayType;
    }

    /**
     * @return the fully-qualified type (ClassDeclaration)
     */

    public Object onCheck(AnnotationTypeElementDeclaration valueDecl, AnnotationValue value,
                          AnnotationInstance[] parentAnnotations, MemberDeclaration classMember,
                          int annotationArrayIndex) {
        Object val = value.getValue();

        if (CompilerUtils.isErrorString(val)) {
            // This means that there is already an error related to the type itself.
            return null;
        }

        if (val instanceof PrimitiveType) {
            addError(value, "error.primitive-type-not-allowed");
            return null;
        } else if (val instanceof VoidType) {
            addError(value, "error.void-type-not-allowed");
            return null;
        }

        if (val instanceof String) {
            assert CompilerUtils.isErrorString(val) : val;
            return null;
        }

        assert val instanceof ReferenceType : val.getClass().getName();
        ReferenceType type = (ReferenceType) val;

        if (! _allowArrayType && type instanceof ArrayType) {
            addError(value, "error.array-type-not-allowed");
            return null;
        }

        if (_requiredSuperclassName != null) {
            if (! CompilerUtils.isAssignableFrom(_requiredSuperclassName, type, getEnv())) {
                addError(value, "error.does-not-extend-base", new Object[]{_requiredSuperclassName});
                return null;
            }
        }

        checkType(type, value);
        return type;
    }

    /**
     * Derived classes can plug in here to do further checks on the type.
     */
    protected void checkType(ReferenceType type, AnnotationValue member) {
    }
}
