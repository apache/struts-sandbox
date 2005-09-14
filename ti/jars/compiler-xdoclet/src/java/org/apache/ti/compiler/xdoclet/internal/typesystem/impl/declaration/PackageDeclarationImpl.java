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

import org.apache.ti.compiler.internal.typesystem.declaration.ClassDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.PackageDeclaration;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.DelegatingImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.WrapperFactory;
import xjavadoc.XClass;
import xjavadoc.XPackage;

import java.util.Collection;
import java.util.Iterator;

public class PackageDeclarationImpl
        extends DelegatingImpl
        implements PackageDeclaration {

    private ClassDeclaration[] _classes;

    public PackageDeclarationImpl(XPackage delegate) {
        super(delegate);
    }

    public String getQualifiedName() {
        return getDelegateXPackage().getName();
    }

    public ClassDeclaration[] getClasses() {
        if (_classes == null) {
            Collection delegateCollection = getDelegateXPackage().getClasses();
            ClassDeclaration[] array = new ClassDeclaration[delegateCollection.size()];
            int j = 0;
            for (Iterator i = delegateCollection.iterator(); i.hasNext();) {
                array[j++] = WrapperFactory.get().getClassDeclaration((XClass) i.next());
            }
            _classes = array;
        }

        return _classes;
    }

    public XPackage getDelegateXPackage() {
        return (XPackage) super.getDelegate();
    }
}
