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
package org.apache.ti.compiler.xdoclet.internal.typesystem.impl.type;

import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.InterfaceDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.AnnotationType;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.InterfaceType;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.DelegatingImpl;

public class AnnotationTypeImpl
        extends DelegatingImpl
        implements AnnotationType {

    public AnnotationTypeImpl(AnnotationTypeDeclaration decl) {
        super(decl);
    }

    public AnnotationTypeDeclaration getAnnotationTypeDeclaration() {
        return (AnnotationTypeDeclaration) getDelegate();
    }

    public InterfaceDeclaration getInterfaceTypeDeclaration() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public DeclaredType getContainingType() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public InterfaceType[] getSuperinterfaces() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public TypeDeclaration getDeclaration() {
        return (TypeDeclaration) getDelegate();
    }
}
