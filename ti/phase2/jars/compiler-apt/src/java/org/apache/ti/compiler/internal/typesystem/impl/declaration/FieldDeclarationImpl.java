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
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;

public class FieldDeclarationImpl
        extends MemberDeclarationImpl
        implements FieldDeclaration {

    public FieldDeclarationImpl(com.sun.mirror.declaration.FieldDeclaration delegate) {
        super(delegate);
    }

    public TypeInstance getType() {
        return WrapperFactory.get().getTypeInstance(getDelegate().getType());
    }

    public Object getConstantValue() {
        return getDelegate().getConstantValue();
    }

    public String getConstantExpression() {
        return getDelegate().getConstantExpression();
    }

    protected com.sun.mirror.declaration.FieldDeclaration getDelegate() {
        return (com.sun.mirror.declaration.FieldDeclaration) super.getDelegate();
    }
}
