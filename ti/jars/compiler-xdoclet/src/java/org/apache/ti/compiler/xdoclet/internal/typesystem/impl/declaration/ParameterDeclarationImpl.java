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

import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.Modifier;
import org.apache.ti.compiler.internal.typesystem.declaration.ParameterDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;
import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.DelegatingImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.WrapperFactory;
import xjavadoc.XParameter;

import java.util.Set;

public class ParameterDeclarationImpl
        extends DelegatingImpl
        implements ParameterDeclaration {

    public ParameterDeclarationImpl(XParameter delegate) {
        super(delegate);
    }

    public TypeInstance getType() {
        return WrapperFactory.get().getTypeInstance(getDelegateXParameter().getType());
    }

    public AnnotationInstance[] getAnnotationInstances() {
        throw new UnsupportedOperationException(ParameterDeclaration.class.getName() + " cannot have annotations");
    }

    public Set getModifiers() {
        throw new UnsupportedOperationException(ParameterDeclaration.class.getName() + " does not support modifiers");
    }

    public String getSimpleName() {
        return getDelegateXParameter().getName();
    }

    public SourcePosition getPosition() {
        throw new UnsupportedOperationException(ParameterDeclaration.class.getName() + " does not have SourcePosition");
    }

    public boolean hasModifier(Modifier modifier) {
        throw new UnsupportedOperationException(ParameterDeclaration.class.getName() + " does not support modifiers");
    }

    public XParameter getDelegateXParameter() {
        return (XParameter) super.getDelegate();
    }
}
