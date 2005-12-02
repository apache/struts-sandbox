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
package org.apache.ti.compiler.internal.typesystem.declaration;

import org.apache.ti.compiler.internal.typesystem.TypesystemElement;

import java.util.Set;

public interface Declaration
        extends TypesystemElement {

    boolean equals(Object o);

    // --Commented out by Inspection (3/13/05 4:42 PM): String getDocComment();

    AnnotationInstance[] getAnnotationInstances();

    //<A extends Annotation> A getAnnotation( Class<A> aClass );

    /**
     * Set of Modifier
     */
    Set getModifiers();

    String getSimpleName();

    org.apache.ti.compiler.internal.typesystem.util.SourcePosition getPosition();

    //void accept( DeclarationVisitor declarationVisitor );

    boolean hasModifier(Modifier modifier);
}
