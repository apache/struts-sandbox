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

public class ValidateCustomGrammar
        extends BaseValidationRuleGrammar {

    private static final String[][] REQUIRED_ATTRS = {{RULE_ATTR}, {MESSAGE_ATTR, MESSAGE_KEY_ATTR}};
    private static final String[][] VARIABLE_REQUIRED_ATTRS = {{NAME_ATTR}, {VALUE_ATTR}};

    public ValidateCustomGrammar(AnnotationProcessorEnvironment env, Diagnostics diagnostics,
                                 RuntimeVersionChecker rvc) {
        super(env, diagnostics, rvc);
        addMemberArrayGrammar(VARIABLES_ATTR, new ValidateCustomVariableGrammar());
    }

    public String[][] getRequiredAttrs() {
        return REQUIRED_ATTRS;
    }

    private class ValidateCustomVariableGrammar
            extends AnnotationGrammar {

        public ValidateCustomVariableGrammar() {
            super(ValidateCustomGrammar.this.getEnv(), ValidateCustomGrammar.this.getDiagnostics(),
                    null, ValidateCustomGrammar.this.getRuntimeVersionChecker());
        }

        public String[][] getRequiredAttrs() {
            return VARIABLE_REQUIRED_ATTRS;
        }
    }
}


