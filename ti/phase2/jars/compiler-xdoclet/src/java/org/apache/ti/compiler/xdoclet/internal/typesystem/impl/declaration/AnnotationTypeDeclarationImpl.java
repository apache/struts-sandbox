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

import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeElementDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.FieldDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.MethodDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.Modifier;
import org.apache.ti.compiler.internal.typesystem.declaration.PackageDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.InterfaceType;
import org.apache.ti.compiler.internal.typesystem.util.SourcePosition;

import java.util.HashMap;
import java.util.Set;

public class AnnotationTypeDeclarationImpl
        implements AnnotationTypeDeclaration, JpfLanguageConstants {

    private String _annotationName;
    private String _packageName;
    private String _intermediateName;
    private AnnotationTypeElementDeclaration[] _members;
    private HashMap _membersByName;

    public AnnotationTypeDeclarationImpl(String annotationName, String interfaceQualifier,
                                         String packageName, AnnotationTypeElementDeclaration[] members) {
        _annotationName = annotationName;
        _intermediateName = interfaceQualifier != null ? interfaceQualifier + annotationName : annotationName;
        _packageName = packageName;
        _members = members;

        _membersByName = new HashMap();
        for (int i = 0; i < members.length; i++) {
            AnnotationTypeElementDeclaration member = members[i];
            _membersByName.put(member.getSimpleName(), member);
        }
    }

    public AnnotationTypeDeclarationImpl(AnnotationTypeDeclarationImpl source, String annotationName,
                                         String interfaceQualifier) {
        _annotationName = annotationName;
        _intermediateName = interfaceQualifier != null ? interfaceQualifier + annotationName : annotationName;
        _packageName = source._packageName;
        _members = source._members;
        _membersByName = source._membersByName;
    }

    public AnnotationTypeElementDeclaration[] getAnnotationMembers() {
        return _members;
    }

    public AnnotationTypeElementDeclaration getMember(String name) {
        return (AnnotationTypeElementDeclaration) _membersByName.get(name);
    }

    public PackageDeclaration getPackage() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public String getQualifiedName() {
        return _packageName + '.' + _intermediateName;
    }

    public InterfaceType[] getSuperinterfaces() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public FieldDeclaration[] getFields() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public MethodDeclaration[] getMethods() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public TypeDeclaration[] getNestedTypes() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public TypeDeclaration getDeclaringType() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public String getDocComment() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public AnnotationInstance[] getAnnotationInstances() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public Set getModifiers() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public String getSimpleName() {
        return _annotationName;
    }

    public String getIntermediateName() {
        return _intermediateName;
    }

    public SourcePosition getPosition() {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public boolean hasModifier(Modifier modifier) {
        assert false : "NYI";
        throw new UnsupportedOperationException("NYI");
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (! (o instanceof AnnotationTypeDeclarationImpl)) return false;
        return getQualifiedName().equals(((AnnotationTypeDeclarationImpl) o).getQualifiedName());
    }
}
