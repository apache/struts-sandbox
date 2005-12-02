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
package org.apache.ti.compiler.internal.genmodel;

import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.model.ActionOutputModel;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.ArrayType;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.PrimitiveType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;


public class GenActionOutputModel
        extends ActionOutputModel
        implements JpfLanguageConstants {

    public GenActionOutputModel(AnnotationInstance annotation, ClassDeclaration jclass) {
        setName(CompilerUtils.getString(annotation, NAME_ATTR, true));

        Boolean required = CompilerUtils.getBoolean(annotation, REQUIRED_ATTR, false);
        boolean nullable = ! required.booleanValue();
        setNullable(nullable);

        //
        // Get the base type, and add "[]" to it for arrays.
        //
        TypeInstance baseType = CompilerUtils.getReferenceType(annotation, TYPE_ATTR, true);
        StringBuffer arrayDimensions = new StringBuffer();
        while (baseType instanceof ArrayType) {
            arrayDimensions.append(ARRAY_TYPE_SUFFIX);
            baseType = ((ArrayType) baseType).getComponentType();
        }

        String baseTypeName;
        if (baseType instanceof PrimitiveType) {
            baseTypeName = ((PrimitiveType) baseType).getKind().toString().toLowerCase();
        } else {
            assert baseType instanceof DeclaredType : baseType.getClass().getName();   // checker should enforce this
            baseTypeName = CompilerUtils.getLoadableName((DeclaredType) baseType);
        }

        setType(baseTypeName + arrayDimensions.toString());
    }
}
