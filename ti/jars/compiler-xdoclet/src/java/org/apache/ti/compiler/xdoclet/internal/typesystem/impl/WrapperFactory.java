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
package org.apache.ti.compiler.xdoclet.internal.typesystem.impl;

import org.apache.ti.compiler.internal.typesystem.declaration.*;
import org.apache.ti.compiler.internal.typesystem.type.ArrayType;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.InterfaceType;
import org.apache.ti.compiler.internal.typesystem.type.PrimitiveType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;
import org.apache.ti.compiler.internal.typesystem.type.VoidType;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration.ClassDeclarationImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration.ConstructorDeclarationImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration.FieldDeclarationImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration.InterfaceDeclarationImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration.MethodDeclarationImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration.PackageDeclarationImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.declaration.ParameterDeclarationImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.type.ArrayTypeImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.type.ClassTypeImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.type.InterfaceTypeImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.type.PrimitiveTypeImpl;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.type.VoidTypeImpl;
import xjavadoc.*;

import java.util.HashSet;

public class WrapperFactory {

    private static final WrapperFactory INSTANCE = new WrapperFactory();
    private static final HashSet PRIMITIVE_TYPES = new HashSet();

    static {
        PRIMITIVE_TYPES.add("boolean");
        PRIMITIVE_TYPES.add("byte");
        PRIMITIVE_TYPES.add("short");
        PRIMITIVE_TYPES.add("int");
        PRIMITIVE_TYPES.add("long");
        PRIMITIVE_TYPES.add("char");
        PRIMITIVE_TYPES.add("float");
        PRIMITIVE_TYPES.add("double");
    }

    private WrapperFactory() {
    }

    public static WrapperFactory get() {
        return INSTANCE;
    }

    public TypeInstance getTypeInstance(Type delegate) {
        if (delegate == null) return null;

        if (delegate.getDimension() > 0) {
            return getArrayType(delegate);
        } else {
            return getTypeInstance(delegate.getType());
        }
    }

    public TypeInstance getTypeInstance(XClass delegate, int dimension) {
        if (delegate == null) return null;

        if (dimension > 0) {
            return getArrayType(new SynthesizedXJavaDocArrayType(delegate, dimension));
        } else {
            return getTypeInstance(delegate);
        }
    }

    private static class SynthesizedXJavaDocArrayType
            implements Type {

        private XClass _baseType;
        private int _dimension;

        public SynthesizedXJavaDocArrayType(XClass type, int dimension) {
            _baseType = type;
            _dimension = dimension;
        }

        public int getDimension() {
            return _dimension;
        }

        public String getDimensionAsString() {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < _dimension; ++i) {
                buf.append("[]");
            }
            return buf.toString();
        }

        public XClass getType() {
            return _baseType;
        }
    }

    public TypeInstance getTypeInstance(XClass delegate) {
        if (delegate == null) return null;

        String name = delegate.getName();

        if (name.equals("void")) {
            return getVoidType(delegate);
        } else if (PRIMITIVE_TYPES.contains(name)) {
            return getPrimitiveType(delegate);
        } else {
            return getDeclaredType(delegate);
        }
    }

    public ArrayType getArrayType(Type delegate) {
        return new ArrayTypeImpl(delegate);
    }

    public VoidType getVoidType(XClass delegate) {
        if (delegate == null) return null;
        return new VoidTypeImpl(delegate);
    }

    public PrimitiveType getPrimitiveType(XClass delegate) {
        if (delegate == null) return null;
        return new PrimitiveTypeImpl(delegate);
    }

    public DeclaredType getDeclaredType(XClass delegate) {
        if (delegate == null) return null;
        return isInterface(delegate) ? (DeclaredType) getInterfaceType(delegate) : getClassType(delegate);
    }

    private static boolean isInterface(XClass xClass) {
        // There's a bug where some returned XClass objects won't think they're interfaces, even when they are.
        // In these cases, the word "interface" appears in the list of Modifiers.
        if (xClass.isInterface()) return true;
        return xClass.getModifiers().indexOf("interface") != -1;
    }

    public ClassType getClassType(XClass delegate) {
        if (delegate == null) return null;
        return new ClassTypeImpl(delegate);
    }

    public InterfaceType getInterfaceType(XClass delegate) {
        if (delegate == null) return null;
        return new InterfaceTypeImpl(delegate);
    }

    public Declaration getDeclaration(XProgramElement delegate) {
        if (delegate == null) return null;

        if (delegate instanceof XMember) {
            return getMemberDeclaration((XMember) delegate);
        } else {
            assert delegate instanceof XType : delegate.getClass().getName();
            return getTypeDeclaration((XType) delegate);
        }
    }

    public MemberDeclaration getMemberDeclaration(XMember delegate) {
        if (delegate == null) return null;

        else if (delegate instanceof XExecutableMember) {
            return getExecutableDeclaration((XExecutableMember) delegate);
        } else {
            assert delegate instanceof XField : delegate.getClass().getName();
            return getFieldDeclaration((XField) delegate);
        }
    }

    public TypeDeclaration getTypeDeclaration(XType delegate) {
        if (delegate == null) return null;
        assert delegate instanceof XClass : delegate.getClass().getName();
        XClass xclass = (XClass) delegate;
        return isInterface(xclass) ? (TypeDeclaration) getInterfaceDeclaration(xclass) : getClassDeclaration(xclass);
    }

    public ClassDeclaration getClassDeclaration(XClass delegate) {
        if (delegate == null) return null;

        String qualifiedName = delegate.getQualifiedName();
        //ClassDeclaration decl = ( ClassDeclaration ) _classDeclarations.get( qualifiedName );
//        if ( decl != null ) return decl;

        return new ClassDeclarationImpl(delegate);
//        _classDeclarations.put( qualifiedName, decl );
//        return decl;
    }

    public InterfaceDeclaration getInterfaceDeclaration(XClass delegate) {
        if (delegate == null) return null;
        return new InterfaceDeclarationImpl(delegate);
    }

    public ExecutableDeclaration getExecutableDeclaration(XExecutableMember delegate) {
        if (delegate == null) return null;

        if (delegate instanceof XMethod) {
            return getMethodDeclaration((XMethod) delegate);
        }

        assert delegate instanceof XConstructor : delegate.getClass().getName();
        return getConstructorDeclaration((XConstructor) delegate);
    }

    public ParameterDeclaration getParameterDeclaration(XParameter delegate) {
        if (delegate == null) return null;
        return new ParameterDeclarationImpl(delegate);
    }

    public PackageDeclaration getPackageDeclaration(XPackage delegate) {
        if (delegate == null) return null;
        return new PackageDeclarationImpl(delegate);
    }

    public ConstructorDeclaration getConstructorDeclaration(XConstructor delegate) {
        if (delegate == null) return null;
        return new ConstructorDeclarationImpl(delegate);
    }

    public MethodDeclaration getMethodDeclaration(XMethod delegate) {
        if (delegate == null) return null;
        return new MethodDeclarationImpl(delegate);
    }

    public FieldDeclaration getFieldDeclaration(XField delegate) {
        if (delegate == null) return null;
        return new FieldDeclarationImpl(delegate);
    }
}
