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
import org.apache.ti.compiler.internal.Diagnostics;
import org.apache.ti.compiler.internal.RuntimeVersionChecker;
import org.apache.ti.compiler.internal.typesystem.env.AnnotationProcessorEnvironment;

public class ActionOutputGrammar
        extends AnnotationGrammar {

    private static final String[][] REQUIRED_ATTRS = {{NAME_ATTR}, {TYPE_ATTR}};

    public ActionOutputGrammar(AnnotationProcessorEnvironment env, Diagnostics diags, RuntimeVersionChecker rvc) {
        super(env, diags, null, rvc);

        addMemberType(NAME_ATTR, new UniqueValueType(ACTION_OUTPUTS_ATTR, false, false, null, this));
        addMemberType(TYPE_ATTR, new TypeNameType(null, true, null, this));
    }

    public String[][] getRequiredAttrs() {
        return REQUIRED_ATTRS;
    }
}
