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
package org.apache.ti.compiler.internal.grammar;

import org.apache.ti.compiler.internal.AnnotationGrammar;
import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.Diagnostics;
import org.apache.ti.compiler.internal.FatalCompileTimeException;
import org.apache.ti.compiler.internal.RuntimeVersionChecker;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.MemberDeclaration;
import org.apache.ti.compiler.internal.typesystem.declaration.TypeDeclaration;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

public class SharedFlowRefGrammar
        extends AnnotationGrammar {

    private static final String[][] REQUIRED_ATTRS = {{NAME_ATTR}, {TYPE_ATTR}};

    public SharedFlowRefGrammar(AnnotationProcessorEnvironment env, Diagnostics diags, RuntimeVersionChecker rvc) {
        super(env, diags, null, rvc);

        // NAME_ATTR does not need a custom type
        addMemberType(TYPE_ATTR, new TypeNameType(SHARED_FLOW_BASE_CLASS, false, null, this));
    }

    protected boolean onBeginCheck(AnnotationInstance annotation, AnnotationInstance[] parentAnnotations,
                                   MemberDeclaration classMember)
            throws FatalCompileTimeException {
        assert classMember instanceof TypeDeclaration : classMember.getClass().getName();   // enforced in Jpf.java

        if (! CompilerUtils.isAssignableFrom(JPF_BASE_CLASS, (TypeDeclaration) classMember, getEnv())) {
            addError(annotation, "error.annotation-invalid-base-class", SHARED_FLOW_REF_TAG_NAME, JPF_BASE_CLASS);
            return false;
        }

        return super.onBeginCheck(annotation, parentAnnotations, classMember);
    }

    public String[][] getRequiredAttrs() {
        return REQUIRED_ATTRS;
    }
}
