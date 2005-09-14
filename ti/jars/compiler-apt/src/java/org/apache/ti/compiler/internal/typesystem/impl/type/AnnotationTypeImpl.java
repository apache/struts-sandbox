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

import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;
import org.apache.ti.compiler.internal.typesystem.type.AnnotationType;

public class AnnotationTypeImpl
        extends InterfaceTypeImpl
        implements AnnotationType {

    public AnnotationTypeImpl(com.sun.mirror.type.AnnotationType delegate) {
        super(delegate);
    }

    public AnnotationTypeDeclaration getAnnotationTypeDeclaration() {
        return WrapperFactory.get().getAnnotationTypeDeclaration(((com.sun.mirror.type.AnnotationType) getDelegate()).getDeclaration());
    }
}
