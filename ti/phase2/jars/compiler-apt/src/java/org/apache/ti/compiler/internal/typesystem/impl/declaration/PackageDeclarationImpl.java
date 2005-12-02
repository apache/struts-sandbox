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

import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.PackageDeclaration;
import org.apache.ti.compiler.internal.typesystem.impl.DelegatingImpl;
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;

import java.util.Collection;

public class PackageDeclarationImpl
        extends DelegatingImpl
        implements PackageDeclaration {

    private ClassDeclaration[] _classes;

    public PackageDeclarationImpl(com.sun.mirror.declaration.PackageDeclaration delegate) {
        super(delegate);
    }

    public String getQualifiedName() {
        return getDelegate().getQualifiedName();
    }

    public ClassDeclaration[] getClasses() {
        if (_classes == null) {
            Collection<com.sun.mirror.declaration.ClassDeclaration> delegateCollection = getDelegate().getClasses();
            ClassDeclaration[] array = new ClassDeclaration[delegateCollection.size()];
            int j = 0;
            for (com.sun.mirror.declaration.ClassDeclaration i : delegateCollection) {
                array[j++] = WrapperFactory.get().getClassDeclaration(i);
            }
            _classes = array;
        }

        return _classes;
    }

    protected com.sun.mirror.declaration.PackageDeclaration getDelegate() {
        return (com.sun.mirror.declaration.PackageDeclaration) super.getDelegate();
    }
}
