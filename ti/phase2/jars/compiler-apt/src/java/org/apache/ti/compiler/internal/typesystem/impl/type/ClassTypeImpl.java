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

import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;

public class ClassTypeImpl
        extends DeclaredTypeImpl
        implements ClassType {

    public ClassTypeImpl(com.sun.mirror.type.ClassType delegate) {
        super(delegate);
    }

    public ClassType getSuperclass() {
        return WrapperFactory.get().getClassType(getDelegate().getSuperclass());
    }


    public ClassDeclaration getClassTypeDeclaration() {
        return WrapperFactory.get().getClassDeclaration(getDelegate().getDeclaration());
    }

    protected com.sun.mirror.type.ClassType getDelegate() {
        return (com.sun.mirror.type.ClassType) super.getDelegate();
    }
}
