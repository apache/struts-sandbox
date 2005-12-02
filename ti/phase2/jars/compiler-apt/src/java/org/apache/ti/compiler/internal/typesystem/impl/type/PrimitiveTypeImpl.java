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
package org.apache.ti.compiler.internal.typesystem.impl.type;

import org.apache.ti.compiler.internal.typesystem.type.PrimitiveType;

public class PrimitiveTypeImpl
        extends TypeInstanceImpl
        implements PrimitiveType {

    public PrimitiveTypeImpl(com.sun.mirror.type.PrimitiveType delegate) {
        super(delegate);
    }

    public Kind getKind() {
        switch (((com.sun.mirror.type.PrimitiveType) getDelegate()).getKind()) {
            case BOOLEAN:
                return Kind.BOOLEAN;
            case BYTE:
                return Kind.BYTE;
            case SHORT:
                return Kind.SHORT;
            case INT:
                return Kind.INT;
            case LONG:
                return Kind.LONG;
            case CHAR:
                return Kind.CHAR;
            case FLOAT:
                return Kind.FLOAT;
            case DOUBLE:
                return Kind.DOUBLE;
        }

        assert false : "unknown Kind";
        return Kind.INT;
    }
}
