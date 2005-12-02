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

import org.apache.ti.compiler.internal.typesystem.declaration.ExecutableDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.ParameterDeclaration;
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;

import java.util.Collection;

public class ExecutableDeclarationImpl
        extends MemberDeclarationImpl
        implements ExecutableDeclaration {

    private ParameterDeclaration[] _parameters;

    public ExecutableDeclarationImpl(com.sun.mirror.declaration.ExecutableDeclaration delegate) {
        super(delegate);
    }

    public boolean isVarArgs() {
        return getDelegate().isVarArgs();
    }

    /*
    public TypeParameterDeclaration[] getFormalTypeParameters()
    {
    }
    */

    public ParameterDeclaration[] getParameters() {
        if (_parameters == null) {
            Collection<com.sun.mirror.declaration.ParameterDeclaration> delegateCollection = getDelegate().getParameters();
            ParameterDeclaration[] array = new ParameterDeclaration[delegateCollection.size()];
            int j = 0;
            for (com.sun.mirror.declaration.ParameterDeclaration i : delegateCollection) {
                array[j++] = WrapperFactory.get().getParameterDeclaration(i);
            }
            _parameters = array;
        }

        return _parameters;
    }

    /*
    public ReferenceType[] getThrownTypes()
    {
    }
    */

    protected com.sun.mirror.declaration.ExecutableDeclaration getDelegate() {
        return (com.sun.mirror.declaration.ExecutableDeclaration) super.getDelegate();
    }
}
