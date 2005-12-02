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
import org.apache.ti.compiler.internal.typesystem.declaration.ConstructorDeclaration;
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;

import java.util.Collection;

public class ClassDeclarationImpl
        extends TypeDeclarationImpl
        implements ClassDeclaration {

    private ConstructorDeclaration[] _constructors;

    public ClassDeclarationImpl(com.sun.mirror.declaration.ClassDeclaration delegate) {
        super(delegate);
    }

    public ClassType getSuperclass() {
        return WrapperFactory.get().getClassType(getDelegate().getSuperclass());
    }

    public ConstructorDeclaration[] getConstructors() {
        if (_constructors == null) {
            Collection<com.sun.mirror.declaration.ConstructorDeclaration> delegateCollection = getDelegate().getConstructors();
            ConstructorDeclaration[] array = new ConstructorDeclaration[delegateCollection.size()];
            int j = 0;
            for (com.sun.mirror.declaration.ConstructorDeclaration i : delegateCollection) {
                array[j++] = WrapperFactory.get().getConstructorDeclaration(i);
            }
            _constructors = array;
        }

        return _constructors;
    }

    protected com.sun.mirror.declaration.ClassDeclaration getDelegate() {
        return (com.sun.mirror.declaration.ClassDeclaration) super.getDelegate();
    }
}
