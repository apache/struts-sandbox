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
package org.apache.ti.compiler.internal.typesystem.impl.declaration;

import org.apache.ti.compiler.internal.typesystem.declaration.FieldDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.PackageDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;
import org.apache.ti.compiler.internal.typesystem.type.InterfaceType;

import java.util.Collection;

public class TypeDeclarationImpl
        extends MemberDeclarationImpl
        implements TypeDeclaration {

    private InterfaceType[] _superInterfaces;
    private MethodDeclaration[] _methods;
    private TypeDeclaration[] _nestedTypes;
    private FieldDeclaration[] _fields;

    public TypeDeclarationImpl(com.sun.mirror.declaration.TypeDeclaration delegate) {
        super(delegate);
    }

    public PackageDeclaration getPackage() {
        return WrapperFactory.get().getPackageDeclaration(getDelegate().getPackage());
    }

    public String getQualifiedName() {
        return getDelegate().getQualifiedName();
    }

    /*
    public TypeParameterDeclaration[] getFormalTypeParameters()
    {
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

    public FieldDeclaration[] getFields() {
        if (_fields == null) {
            Collection<com.sun.mirror.declaration.FieldDeclaration> delegateCollection = getDelegate().getFields();
            FieldDeclaration[] array = new FieldDeclaration[delegateCollection.size()];
            int j = 0;
            for (com.sun.mirror.declaration.FieldDeclaration i : delegateCollection) {
                array[j++] = WrapperFactory.get().getFieldDeclaration(i);
            }
            _fields = array;
        }

        return _fields;
    }

    public MethodDeclaration[] getMethods() {
        if (_methods == null) {
            Collection<? extends com.sun.mirror.declaration.MethodDeclaration> delegateCollection =
                    getDelegate().getMethods();
            MethodDeclaration[] array = new MethodDeclaration[delegateCollection.size()];
            int j = 0;
            for (com.sun.mirror.declaration.MethodDeclaration i : delegateCollection) {
                array[j++] = WrapperFactory.get().getMethodDeclaration(i);
            }
            _methods = array;
        }

        return _methods;
    }

    public TypeDeclaration[] getNestedTypes() {
        if (_nestedTypes == null) {
            Collection<com.sun.mirror.declaration.TypeDeclaration> delegateCollection = getDelegate().getNestedTypes();
            TypeDeclaration[] array = new TypeDeclaration[delegateCollection.size()];
            int j = 0;
            for (com.sun.mirror.declaration.TypeDeclaration i : delegateCollection) {
                array[j++] = WrapperFactory.get().getTypeDeclaration(i);
            }
            _nestedTypes = array;
        }

        return _nestedTypes;
    }

    protected com.sun.mirror.declaration.TypeDeclaration getDelegate() {
        return (com.sun.mirror.declaration.TypeDeclaration) super.getDelegate();
    }
}
