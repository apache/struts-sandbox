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
package org.apache.ti.compiler.internal.model.validation;

public class ValidatorRuleRange
        extends ValidatorRule
        implements ValidatorConstants {

    public ValidatorRuleRange(Double min, Double max) {
        super(RULENAME_FLOAT_RANGE);
        setVar(VARNAME_MIN, min.toString());
        setVar(VARNAME_MAX, max.toString());
    }

    public ValidatorRuleRange(Long min, Long max) {
        super(RULENAME_INT_RANGE);
        setVar(VARNAME_MIN, min.toString());
        setVar(VARNAME_MAX, max.toString());
    }
}
