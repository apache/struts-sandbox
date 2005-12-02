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
package org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration;

import org.apache.ti.compiler.internal.typesystem.declaration.ExecutableDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.ParameterDeclaration;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.WrapperFactory;
import xjavadoc.XExecutableMember;
import xjavadoc.XParameter;

import java.util.Collection;
import java.util.Iterator;

public class ExecutableDeclarationImpl
        extends MemberDeclarationImpl
        implements ExecutableDeclaration {

    private ParameterDeclaration[] _parameters;

    public ExecutableDeclarationImpl(XExecutableMember delegate) {
        super(delegate);
    }

    public ParameterDeclaration[] getParameters() {
        if (_parameters == null) {
            Collection delegateCollection = getDelegateXExecutableMember().getParameters();
            ParameterDeclaration[] array = new ParameterDeclaration[delegateCollection.size()];
            int j = 0;
            for (Iterator i = delegateCollection.iterator(); i.hasNext();) {
                array[j++] = WrapperFactory.get().getParameterDeclaration((XParameter) i.next());
            }
            _parameters = array;
        }

        return _parameters;
    }

    protected XExecutableMember getDelegateXExecutableMember() {
        return (XExecutableMember) super.getDelegate();
    }
}
