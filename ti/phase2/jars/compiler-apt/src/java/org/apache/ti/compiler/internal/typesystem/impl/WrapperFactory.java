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
package org.apache.ti.compiler.internal.typesystem.impl;

import org.apache.ti.compiler.internal.typesystem.TypesystemElement;
import org.apache.ti.compiler.internal.typesystem.declaration.*;
import org.apache.ti.compiler.internal.typesystem.impl.declaration.*;
import org.apache.ti.compiler.internal.typesystem.impl.type.AnnotationTypeImpl;
import org.apache.ti.compiler.internal.typesystem.impl.type.ArrayTypeImpl;
import org.apache.ti.compiler.internal.typesystem.impl.type.ClassTypeImpl;
import org.apache.ti.compiler.internal.typesystem.impl.type.ErrorTypeImpl;
import org.apache.ti.compiler.internal.typesystem.impl.type.InterfaceTypeImpl;
import org.apache.ti.compiler.internal.typesystem.impl.type.PrimitiveTypeImpl;
import org.apache.ti.compiler.internal.typesystem.impl.type.TypeVariableImpl;
import org.apache.ti.compiler.internal.typesystem.impl.type.VoidTypeImpl;
import org.apache.ti.compiler.internal.typesystem.type.AnnotationType;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;
import org.apache.ti.compiler.internal.typesystem.type.DeclaredType;
import org.apache.ti.compiler.internal.typesystem.type.InterfaceType;
import org.apache.ti.compiler.internal.typesystem.type.PrimitiveType;
import org.apache.ti.compiler.internal.typesystem.type.ReferenceType;
import org.apache.ti.compiler.internal.typesystem.type.TypeInstance;
import org.apache.ti.compiler.internal.typesystem.type.TypeVariable;
import org.apache.ti.compiler.internal.typesystem.type.VoidType;

import java.util.ArrayList;
import java.util.List;

public class WrapperFactory {

    private static final WrapperFactory INSTANCE = new WrapperFactory();

    private WrapperFactory() {
    }

    public static WrapperFactory get() {
        return INSTANCE;
    }

    public TypeInstance getTypeInstance(com.sun.mirror.type.TypeMirror delegate) {
        if (delegate == null) return null;

        if (delegate instanceof com.sun.mirror.type.ReferenceType) {
            return getReferenceType((com.sun.mirror.type.ReferenceType) delegate);
        } else if (delegate instanceof com.sun.mirror.type.VoidType) {
            return getVoidType((com.sun.mirror.type.VoidType) delegate);
        } else {
            assert delegate instanceof com.sun.mirror.type.PrimitiveType : delegate.getClass().getName();
            return getPrimitiveType((com.sun.mirror.type.PrimitiveType) delegate);
        }
    }

    public VoidType getVoidType(com.sun.mirror.type.VoidType delegate) {
        if (delegate == null) return null;
        return new VoidTypeImpl(delegate);
    }

    public ReferenceType getReferenceType(com.sun.mirror.type.ReferenceType delegate) {
        if (delegate == null) return null;

        if (delegate instanceof com.sun.mirror.type.DeclaredType) {
            return getDeclaredType((com.sun.mirror.type.DeclaredType) delegate);
        } else if (delegate instanceof com.sun.mirror.type.ArrayType) {
            return new ArrayTypeImpl((com.sun.mirror.type.ArrayType) delegate);
        } else {
            assert delegate instanceof com.sun.mirror.type.TypeVariable : delegate.getClass().getName();
            return getTypeVariable((com.sun.mirror.type.TypeVariable) delegate);
        }
    }


    public PrimitiveType getPrimitiveType(com.sun.mirror.type.PrimitiveType delegate) {
        if (delegate == null) return null;

        return new PrimitiveTypeImpl(delegate);
    }

    public TypeVariable getTypeVariable(com.sun.mirror.type.TypeVariable delegate) {
        if (delegate == null) return null;

        return new TypeVariableImpl(delegate);
    }

    public DeclaredType getDeclaredType(com.sun.mirror.type.DeclaredType delegate) {
        if (delegate == null) return null;

        if (delegate instanceof com.sun.mirror.type.ClassType) {
            return getClassType((com.sun.mirror.type.ClassType) delegate);
        } else if (delegate instanceof com.sun.mirror.type.InterfaceType) {
            return getInterfaceType((com.sun.mirror.type.InterfaceType) delegate);
        }

        //
        // This must be an error type, which is indicated by a DeclaredType with no type declaration.
        //
        assert delegate.getDeclaration() == null :
                "expected error type, got " + delegate.toString() + " with declaration " + delegate.getDeclaration();
        return getErrorType(delegate);
    }

    public DeclaredType getErrorType(com.sun.mirror.type.DeclaredType delegate) {
        if (delegate == null) return null;
        return new ErrorTypeImpl(delegate);
    }


    public ClassType getClassType(com.sun.mirror.type.ClassType delegate) {
        if (delegate == null) return null;

        return new ClassTypeImpl(delegate);
    }

    public InterfaceType getInterfaceType(com.sun.mirror.type.InterfaceType delegate) {
        if (delegate == null) return null;

        if (delegate instanceof com.sun.mirror.type.AnnotationType) {
            return getAnnotationType((com.sun.mirror.type.AnnotationType) delegate);
        }

        return new InterfaceTypeImpl(delegate);
    }

    public AnnotationType getAnnotationType(com.sun.mirror.type.AnnotationType delegate) {
        if (delegate == null) return null;
        return new AnnotationTypeImpl(delegate);
    }

    public AnnotationInstance getAnnotationInstance(com.sun.mirror.declaration.AnnotationMirror delegate,
                                                    TypesystemElement containingElement) {
        if (delegate == null) return null;
        return new AnnotationInstanceImpl(delegate, containingElement);
    }

    public AnnotationValue getAnnotationValue(com.sun.mirror.declaration.AnnotationValue delegate,
                                              AnnotationInstance containingAnnotation) {
        if (delegate == null) return null;
        return new AnnotationValueImpl(delegate, containingAnnotation);
    }

    public Declaration getDeclaration(com.sun.mirror.declaration.Declaration delegate) {
        if (delegate == null) return null;

        if (delegate instanceof com.sun.mirror.declaration.MemberDeclaration) {
            return getMemberDeclaration((com.sun.mirror.declaration.MemberDeclaration) delegate);
        } else if (delegate instanceof com.sun.mirror.declaration.ParameterDeclaration) {
            return getParameterDeclaration((com.sun.mirror.declaration.ParameterDeclaration) delegate);
        } else {
            assert delegate instanceof com.sun.mirror.declaration.TypeParameterDeclaration : delegate.getClass().getName();
            return getTypeParameterDeclaration((com.sun.mirror.declaration.TypeParameterDeclaration) delegate);
        }
    }

    public MemberDeclaration getMemberDeclaration(com.sun.mirror.declaration.MemberDeclaration delegate) {
        if (delegate == null) return null;

        if (delegate instanceof com.sun.mirror.declaration.TypeDeclaration) {
            return getTypeDeclaration((com.sun.mirror.declaration.TypeDeclaration) delegate);
        } else if (delegate instanceof com.sun.mirror.declaration.ExecutableDeclaration) {
            return getExecutableDeclaration((com.sun.mirror.declaration.ExecutableDeclaration) delegate);
        } else {
            assert delegate instanceof com.sun.mirror.declaration.FieldDeclaration : delegate.getClass().getName();
            return getFieldDeclaration((com.sun.mirror.declaration.FieldDeclaration) delegate);
        }
    }

    public TypeDeclaration getTypeDeclaration(com.sun.mirror.declaration.TypeDeclaration delegate) {
        if (delegate == null) return null;

        if (delegate instanceof com.sun.mirror.declaration.ClassDeclaration) {
            return getClassDeclaration((com.sun.mirror.declaration.ClassDeclaration) delegate);
        } else {
            assert delegate instanceof com.sun.mirror.declaration.InterfaceDeclaration : delegate.getClass().getName();
            return getInterfaceDeclaration((com.sun.mirror.declaration.InterfaceDeclaration) delegate);
        }
    }

    public ClassDeclaration getClassDeclaration(com.sun.mirror.declaration.ClassDeclaration delegate) {
        if (delegate == null) return null;

        return new ClassDeclarationImpl(delegate);
    }

    public InterfaceDeclaration getInterfaceDeclaration(com.sun.mirror.declaration.InterfaceDeclaration delegate) {
        if (delegate == null) return null;

        if (delegate instanceof com.sun.mirror.declaration.AnnotationTypeDeclaration) {
            return getAnnotationTypeDeclaration((com.sun.mirror.declaration.AnnotationTypeDeclaration) delegate);
        }

        return new InterfaceDeclarationImpl(delegate);
    }

    public ExecutableDeclaration getExecutableDeclaration(com.sun.mirror.declaration.ExecutableDeclaration delegate) {
        if (delegate == null) return null;

        if (delegate instanceof com.sun.mirror.declaration.MethodDeclaration) {
            return getMethodDeclaration((com.sun.mirror.declaration.MethodDeclaration) delegate);
        }

        assert delegate instanceof com.sun.mirror.declaration.ConstructorDeclaration : delegate.getClass().getName();
        return getConstructorDeclaration((com.sun.mirror.declaration.ConstructorDeclaration) delegate);
    }

    public ParameterDeclaration getParameterDeclaration(com.sun.mirror.declaration.ParameterDeclaration delegate) {
        if (delegate == null) return null;

        return new ParameterDeclarationImpl(delegate);
    }

    public PackageDeclaration getPackageDeclaration(com.sun.mirror.declaration.PackageDeclaration delegate) {
        if (delegate == null) return null;

        return new PackageDeclarationImpl(delegate);
    }

    public ConstructorDeclaration getConstructorDeclaration(com.sun.mirror.declaration.ConstructorDeclaration delegate) {
        if (delegate == null) return null;

        return new ConstructorDeclarationImpl(delegate);
    }

    public MethodDeclaration getMethodDeclaration(com.sun.mirror.declaration.MethodDeclaration delegate) {
        if (delegate == null) return null;

        if (delegate instanceof com.sun.mirror.declaration.AnnotationTypeElementDeclaration) {
            return getAnnotationTypeElementDeclaration((com.sun.mirror.declaration.AnnotationTypeElementDeclaration) delegate);
        }

        return new MethodDeclarationImpl(delegate);
    }

    public AnnotationTypeDeclaration getAnnotationTypeDeclaration(com.sun.mirror.declaration.AnnotationTypeDeclaration delegate) {
        if (delegate == null) return null;

        return new AnnotationTypeDeclarationImpl(delegate);
    }

    public AnnotationTypeElementDeclaration getAnnotationTypeElementDeclaration(com.sun.mirror.declaration.AnnotationTypeElementDeclaration delegate) {
        if (delegate == null) return null;

        return new AnnotationTypeElementDeclarationImpl(delegate);
    }

    public FieldDeclaration getFieldDeclaration(com.sun.mirror.declaration.FieldDeclaration delegate) {
        if (delegate == null) return null;

        return new FieldDeclarationImpl(delegate);
    }

    public TypeParameterDeclaration getTypeParameterDeclaration(com.sun.mirror.declaration.TypeParameterDeclaration delegate) {
        if (delegate == null) return null;

        return new TypeParameterDeclarationImpl(delegate);
    }

    public Object getWrapper(Object o, TypesystemElement containingElement) {
        if (o == null) return null;

        if (o instanceof com.sun.mirror.type.TypeMirror) {
            return getTypeInstance((com.sun.mirror.type.TypeMirror) o);
        } else if (o instanceof com.sun.mirror.declaration.Declaration) {
            return getDeclaration((com.sun.mirror.declaration.Declaration) o);
        } else if (o instanceof com.sun.mirror.declaration.AnnotationMirror) {
            assert containingElement != null;
            return getAnnotationInstance((com.sun.mirror.declaration.AnnotationMirror) o, containingElement);
        } else if (o instanceof com.sun.mirror.declaration.AnnotationValue) {
            assert containingElement != null;
            assert containingElement instanceof AnnotationInstance : containingElement.getClass().getName();
            AnnotationInstance containingAnnotation = (AnnotationInstance) containingElement;
            return getAnnotationValue((com.sun.mirror.declaration.AnnotationValue) o, containingAnnotation);
        } else if (o instanceof com.sun.mirror.declaration.PackageDeclaration) {
            return getPackageDeclaration((com.sun.mirror.declaration.PackageDeclaration) o);
        } else if (o instanceof List) {
            List list = (List) o;
            ArrayList ret = new ArrayList(list.size());

            for (Object i : list) {
                ret.add(getWrapper(i, containingElement));
            }

            return ret;
        }

        return o;
    }
}
