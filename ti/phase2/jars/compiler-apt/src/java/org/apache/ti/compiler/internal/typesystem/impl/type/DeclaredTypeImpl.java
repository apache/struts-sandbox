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

import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.InterfaceType;

import java.util.Collection;

public class DeclaredTypeImpl
        extends ReferenceTypeImpl
        implements DeclaredType {

    private InterfaceType[] _superInterfaces;

    public DeclaredTypeImpl(com.sun.mirror.type.DeclaredType delegate) {
        super(delegate);
    }

    public TypeDeclaration getDeclaration() {
        return WrapperFactory.get().getTypeDeclaration(getDelegate().getDeclaration());
    }

    public DeclaredType getContainingType() {
        return WrapperFactory.get().getDeclaredType(getDelegate().getContainingType());
    }

    /*
    public TypeInstance[] getActualTypeArguments()
    {
        return new TypeInstance[0];
    }
    */

    public InterfaceType[] getSuperinterfaces() {
        if (_superInterfaces == null) {
            Collection<com.sun.mirror.type.InterfaceType> delegateCollection = getDelegate().getSuperinterfaces();
            InterfaceType[] array = new InterfaceType[delegateCollection.size()];
            int j = 0;
            for (com.sun.mirror.type.InterfaceType i : delegateCollection) {
                array[j++] = WrapperFactory.get().getInterfaceType(i);
            }
            _superInterfaces = array;
        }

        return _superInterfaces;
    }

    protected com.sun.mirror.type.DeclaredType getDelegate() {
        return (com.sun.mirror.type.DeclaredType) super.getDelegate();
    }
}
