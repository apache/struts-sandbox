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

import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.typesystem.TypesystemElement;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.impl.DelegatingImpl;
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;
import org.apache.ti.compiler.internal.typesystem.impl.env.SourcePositionImpl;
import org.apache.ti.compiler.internal.typesystem.type.AnnotationType;
import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotationInstanceImpl
        extends DelegatingImpl
        implements AnnotationInstance {

    private Map<AnnotationTypeElementDeclaration, AnnotationValue> _elementValues;
    private TypesystemElement _containingElement;

    public AnnotationInstanceImpl(com.sun.mirror.declaration.AnnotationMirror delegate,
                                  TypesystemElement containingElement) {
        super(delegate);

        assert containingElement != null;
        assert containingElement instanceof AnnotationInstance || containingElement instanceof MemberDeclaration
                : containingElement.getClass().getName();
        _containingElement = containingElement;
    }

    public AnnotationType getAnnotationType() {
        return WrapperFactory.get().getAnnotationType(getDelegate().getAnnotationType());
    }

    public SourcePosition getPosition() {
        return SourcePositionImpl.get(getDelegate().getPosition());
    }

    // Map<AnnotationTypeElementDeclaration, AnnotationValue> getElementValues();
    public Map getElementValues() {
        if (_elementValues == null) {
            Set<Map.Entry<com.sun.mirror.declaration.AnnotationTypeElementDeclaration, com.sun.mirror.declaration.AnnotationValue>>
                    entries = getDelegate().getElementValues().entrySet();
            Map<AnnotationTypeElementDeclaration, AnnotationValue> elementValues =
                    new HashMap<AnnotationTypeElementDeclaration, AnnotationValue>();
            for (Map.Entry<com.sun.mirror.declaration.AnnotationTypeElementDeclaration, com.sun.mirror.declaration.AnnotationValue> i : entries) {
                elementValues.put(
                        WrapperFactory.get().getAnnotationTypeElementDeclaration(i.getKey()),
                        WrapperFactory.get().getAnnotationValue(i.getValue(), this));
            }
            _elementValues = elementValues;
        }

        return _elementValues;
    }

    protected com.sun.mirror.declaration.AnnotationMirror getDelegate() {
        return (com.sun.mirror.declaration.AnnotationMirror) super.getDelegate();
    }

    public TypeDeclaration getContainingType() {
        TypesystemElement containingDeclaration = _containingElement;

        while (containingDeclaration instanceof AnnotationInstance) {
            containingDeclaration = ((AnnotationInstance) containingDeclaration).getContainingType();
        }

        assert containingDeclaration != null;
        assert containingDeclaration instanceof MemberDeclaration : containingDeclaration.getClass().getName();
        return CompilerUtils.getOuterClass((MemberDeclaration) containingDeclaration);
    }
}
