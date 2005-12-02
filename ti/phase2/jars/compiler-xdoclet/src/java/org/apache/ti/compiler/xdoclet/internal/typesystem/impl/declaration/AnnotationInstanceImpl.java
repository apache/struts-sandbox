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

import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.AnnotationType;
import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;
import org.apache.ti.compiler.xdoclet.XDocletCompilerUtils;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.DelegatingImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.WrapperFactory;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.env.SourcePositionImpl;
import xjavadoc.XMember;
import xjavadoc.XProgramElement;
import xjavadoc.XTag;
import xjavadoc.XType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationInstanceImpl
        extends DelegatingImpl
        implements AnnotationInstance {

    private AnnotationType _type;
    private SourcePosition _sourcePosition;
    private XProgramElement _containingElement;

    // Map<AnnotationTypeElementDeclaration, AnnotationValue> getElementValues();
    private HashMap _elementValues;

    public AnnotationInstanceImpl(XTag tag, XProgramElement element, AnnotationType type, HashMap elementValues) {
        super(tag);
        _sourcePosition = SourcePositionImpl.get(tag, element);
        _type = type;
        _elementValues = elementValues;
        assert element != null;
        _containingElement = element;
    }

    public AnnotationType getAnnotationType() {
        return _type;
    }

    public SourcePosition getPosition() {
        return _sourcePosition;
    }

    public void addElementValue(String memberName, boolean memberIsArray, Object value, SourcePosition sourcePosition) {
        AnnotationTypeElementDeclaration elementDecl = _type.getAnnotationTypeDeclaration().getMember(memberName);

        if (elementDecl == null) {
            XDocletCompilerUtils.addError(getPosition(), "error.no-such-member",
                    new String[]{memberName, _type.getAnnotationTypeDeclaration().getQualifiedName()});
        } else if (memberIsArray) {
            AnnotationValueImpl av = (AnnotationValueImpl) _elementValues.get(elementDecl);
            List list;

            if (av == null) {
                list = new ArrayList();
                av = new AnnotationValueImpl(list, sourcePosition, elementDecl);
                _elementValues.put(elementDecl, av);
            } else {
                list = (List) av.getValue();
            }

            list.add(new AnnotationValueImpl(value, sourcePosition, null));
        } else {
            _elementValues.put(elementDecl, new AnnotationValueImpl(value, sourcePosition, elementDecl));
        }
    }

    // Map<AnnotationTypeElementDeclaration, AnnotationValue> getElementValues();
    public Map getElementValues() {
        return _elementValues;
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (! (o instanceof AnnotationInstanceImpl)) return false;
        assert false : "didn't finish equals()";
        return false;
    }

    public String toString() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public XTag getDelegateXTag() {
        return (XTag) super.getDelegate();
    }

    public TypeDeclaration getContainingType() {
        if (_containingElement instanceof XType) {
            return WrapperFactory.get().getTypeDeclaration((XType) _containingElement);
        }
        assert _containingElement instanceof XMember : _containingElement.getClass().getName();
        MemberDeclaration member = WrapperFactory.get().getMemberDeclaration((XMember) _containingElement);
        return CompilerUtils.getOuterClass(member);
    }
}
