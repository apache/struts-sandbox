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

import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;

import java.util.Collection;
import java.util.HashMap;

public class AnnotationTypeDeclarationImpl
        extends InterfaceDeclarationImpl
        implements AnnotationTypeDeclaration {

    private AnnotationTypeElementDeclaration[] _members;
    private HashMap<String, AnnotationTypeElementDeclaration> _membersByName =
            new HashMap<String, AnnotationTypeElementDeclaration>();

    public AnnotationTypeDeclarationImpl(com.sun.mirror.declaration.AnnotationTypeDeclaration delegate) {
        super(delegate);
    }

    public AnnotationTypeElementDeclaration[] getAnnotationMembers() {
        if (_members == null) {
            Collection<com.sun.mirror.declaration.AnnotationTypeElementDeclaration> delegateCollection =
                    getDelegate().getMethods();
            AnnotationTypeElementDeclaration[] array = new AnnotationTypeElementDeclaration[ delegateCollection.size() ];
            int j = 0;
            for (com.sun.mirror.declaration.AnnotationTypeElementDeclaration i : delegateCollection) {
                AnnotationTypeElementDeclaration decl = WrapperFactory.get().getAnnotationTypeElementDeclaration(i);
                array[j++] = decl;
                _membersByName.put(decl.getSimpleName(), decl);
            }
            _members = array;
        }

        return _members;
    }

    public com.sun.mirror.declaration.AnnotationTypeDeclaration getDelegate() {
        return (com.sun.mirror.declaration.AnnotationTypeDeclaration) super.getDelegate();
    }

    public AnnotationTypeElementDeclaration getMember(String name) {
        return _membersByName.get(name);
    }
}
