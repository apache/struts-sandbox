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
package org.apache.ti.compiler.apt;

import com.sun.mirror.apt.AnnotationProcessorFactory;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationTypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessor;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;
import org.apache.ti.compiler.internal.typesystem.impl.DelegatingImpl;
import org.apache.ti.compiler.internal.typesystem.impl.WrapperFactory;
import org.apache.ti.compiler.internal.typesystem.impl.env.AnnotationProcessorEnvironmentImpl;

import java.util.Iterator;
import java.util.Set;

public abstract class BaseAnnotationProcessorFactory
        implements AnnotationProcessorFactory {

    public final com.sun.mirror.apt.AnnotationProcessor
            getProcessorFor(Set annotationTypeDeclarations, com.sun.mirror.apt.AnnotationProcessorEnvironment aptEnv) {

        AnnotationProcessorEnvironment env = AnnotationProcessorEnvironmentImpl.get(aptEnv);
        AnnotationTypeDeclaration[] atds = new AnnotationTypeDeclaration[ annotationTypeDeclarations.size() ];
        int j = 0;
        for (Iterator i = annotationTypeDeclarations.iterator(); i.hasNext();) {
            com.sun.mirror.declaration.AnnotationTypeDeclaration decl =
                    (com.sun.mirror.declaration.AnnotationTypeDeclaration) i.next();
            atds[j++] = WrapperFactory.get().getAnnotationTypeDeclaration(decl);
        }

        AnnotationProcessor ap = getProcessorFor(atds, env);
        return ap != null ? new DelegatingAnnotationProcessor(ap) : null;
    }

    private static class DelegatingAnnotationProcessor
            extends DelegatingImpl
            implements com.sun.mirror.apt.AnnotationProcessor {

        public DelegatingAnnotationProcessor(AnnotationProcessor delegate) {
            super(delegate);
        }

        public void process() {
            ((AnnotationProcessor) getDelegate()).process();
        }
    }

    protected abstract AnnotationProcessor getProcessorFor(AnnotationTypeDeclaration[] annotationTypeDeclarations,
                                                           AnnotationProcessorEnvironment env);
}
