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
import org.apache.ti.compiler.internal.typesystem.declaration.ConstructorDeclaration;
import org.apache.ti.compiler.internal.typesystem.type.ClassType;
import org.apache.ti.compiler.xdoclet.internal.typesystem.impl.WrapperFactory;
import xjavadoc.XClass;
import xjavadoc.XConstructor;
import xjavadoc.XDoc;
import xjavadoc.XJavaDoc;
import xjavadoc.XPackage;
import xjavadoc.XProgramElement;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ClassDeclarationImpl
        extends TypeDeclarationImpl
        implements ClassDeclaration {

    private ConstructorDeclaration[] _constructors;

    public ClassDeclarationImpl(XClass delegate) {
        super(delegate);
    }

    public ClassType getSuperclass() {
        return WrapperFactory.get().getClassType(getDelegateXClass().getSuperclass());
    }

    public ConstructorDeclaration[] getConstructors() {
        if (_constructors == null) {
            Collection delegateCollection = getDelegateXClass().getConstructors();
            ConstructorDeclaration[] array = new ConstructorDeclaration[delegateCollection.size()];
            int j = 0;
            for (Iterator i = delegateCollection.iterator(); i.hasNext();) {
                array[j++] = WrapperFactory.get().getConstructorDeclaration((XConstructor) i.next());
            }

            if (array.length == 0) {
                XConstructor ctor = new DefaultConstructor(getDelegateXClass());
                ConstructorDeclaration decl = WrapperFactory.get().getConstructorDeclaration(ctor);
                _constructors = new ConstructorDeclaration[]{decl};
            } else {
                _constructors = array;
            }
        }

        return _constructors;
    }

    protected XClass getDelegateXClass() {
        return (XClass) super.getDelegate();
    }

    private static class DefaultConstructor
            implements XConstructor {

        private XClass _containingClass;

        public DefaultConstructor(XClass containingClass) {
            _containingClass = containingClass;
        }

        public boolean isNative() {
            return false;
        }

        public boolean isSynchronized() {
            return false;
        }

        public List getParameters() {
            return Collections.EMPTY_LIST;
        }

        public List getThrownExceptions() {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }

        public boolean throwsException(String s) {
            return false;
        }

        public boolean isConstructor() {
            return true;
        }

        public String getSignature(boolean b) {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }

        public String getNameWithSignature(boolean b) {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }

        public String getParameterTypes() {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }

        public XClass getContainingClass() {
            return _containingClass;
        }

        public XPackage getContainingPackage() {
            return _containingClass.getContainingPackage();
        }

        public boolean isFinal() {
            return false;
        }

        public boolean isPackagePrivate() {
            return false;
        }

        public boolean isPrivate() {
            return false;
        }

        public boolean isProtected() {
            return false;
        }

        public boolean isAbstract() {
            return false;
        }

        public boolean isPublic() {
            return true;
        }

        public boolean isStatic() {
            return false;
        }

        public String getModifiers() {
            return "public";
        }

        public int getModifierSpecifier() {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }

        public XDoc getDoc() {
            return null;
        }

        public XProgramElement getSuperElement() {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }

        public List getSuperInterfaceElements() {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }

        public XJavaDoc getXJavaDoc() {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }

        public void updateDoc() {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }

        public int compareTo(Object o) {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }

        public String getName() {
            assert false : "NYI";
            throw new UnsupportedOperationException("NYI");
        }
    }
}
