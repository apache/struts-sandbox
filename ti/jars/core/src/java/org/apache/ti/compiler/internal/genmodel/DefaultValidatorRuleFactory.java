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
package org.apache.ti.compiler.internal.genmodel;

import org.apache.ti.compiler.internal.CompilerUtils;
import org.apache.ti.compiler.internal.JpfLanguageConstants;
import org.apache.ti.compiler.internal.model.validation.ValidatorConstants;
import org.apache.ti.compiler.internal.model.validation.ValidatorRule;
import org.apache.ti.compiler.internal.model.validation.ValidatorRuleRange;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationInstance;
import org.apache.ti.compiler.internal.typesystem.declaration.AnnotationValue;
import org.apache.ti.compiler.internal.typesystem.type.PrimitiveType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DefaultValidatorRuleFactory
        implements ValidatorRuleFactory, ValidatorConstants, JpfLanguageConstants {

    private static final Map VALIDATE_TYPE_RULES =
            new HashMap();

    static {
        VALIDATE_TYPE_RULES.put(PrimitiveType.Kind.INT, RULENAME_INTEGER);
        VALIDATE_TYPE_RULES.put(PrimitiveType.Kind.FLOAT, RULENAME_FLOAT);
        VALIDATE_TYPE_RULES.put(PrimitiveType.Kind.LONG, RULENAME_LONG);
        VALIDATE_TYPE_RULES.put(PrimitiveType.Kind.DOUBLE, RULENAME_DOUBLE);
        VALIDATE_TYPE_RULES.put(PrimitiveType.Kind.BYTE, RULENAME_BYTE);
        VALIDATE_TYPE_RULES.put(PrimitiveType.Kind.SHORT, RULENAME_SHORT);
    }

    public ValidatorRule getFieldRule(String entityName, String propertyName, AnnotationInstance ruleAnnotation) {
        ValidatorRule rule = null;
        String annName = CompilerUtils.getSimpleName(ruleAnnotation);

        if (annName.equals(VALIDATE_REQUIRED_TAG_NAME)) rule = new ValidatorRule(RULENAME_REQUIRED);
        else if (annName.equals(VALIDATE_CREDIT_CARD_TAG_NAME)) rule = new ValidatorRule(RULENAME_CREDIT_CARD);
        else if (annName.equals(VALIDATE_EMAIL_TAG_NAME)) rule = new ValidatorRule(RULENAME_EMAIL);
        else if (annName.equals(VALIDATE_RANGE_TAG_NAME)) {
            Double minFloat = CompilerUtils.getDouble(ruleAnnotation, MIN_FLOAT_ATTR, true);

            if (minFloat != null) {
                Double maxFloat = CompilerUtils.getDouble(ruleAnnotation, MAX_FLOAT_ATTR, true);
                assert maxFloat != null;  // checker should catch this
                rule = new ValidatorRuleRange(minFloat, maxFloat);
            } else {
                Long minLong = CompilerUtils.getLong(ruleAnnotation, MIN_INT_ATTR, true);
                Long maxLong = CompilerUtils.getLong(ruleAnnotation, MAX_INT_ATTR, true);
                assert minLong != null;  // checker should catch this
                assert maxLong != null;  // checker should catch this
                rule = new ValidatorRuleRange(minLong, maxLong);
            }
        } else if (annName.equals(VALIDATE_MIN_LENGTH_TAG_NAME)) {
            Integer nChars = CompilerUtils.getInteger(ruleAnnotation, CHARS_ATTR, true);
            assert nChars != null;
            rule = new ValidatorRule(RULENAME_MINLENGTH);
            rule.setVar(VARNAME_MINLENGTH, nChars.toString());
        } else if (annName.equals(VALIDATE_MAX_LENGTH_TAG_NAME)) {
            Integer nChars = CompilerUtils.getInteger(ruleAnnotation, CHARS_ATTR, true);
            assert nChars != null;
            rule = new ValidatorRule(RULENAME_MAXLENGTH);
            rule.setVar(VARNAME_MAXLENGTH, nChars.toString());
        } else if (annName.equals(VALIDATE_MASK_TAG_NAME)) {
            String regex = CompilerUtils.getString(ruleAnnotation, REGEX_ATTR, true);
            assert regex != null;
            rule = new ValidatorRule(RULENAME_MASK);
            rule.setVar(VARNAME_MASK, regex);
        } else if (annName.equals(VALIDATE_DATE_TAG_NAME)) {
            boolean strict = CompilerUtils.getBoolean(ruleAnnotation, STRICT_ATTR, false).booleanValue();
            String pattern = CompilerUtils.getString(ruleAnnotation, PATTERN_ATTR, true);
            assert pattern != null;
            rule = new ValidatorRule(RULENAME_DATE);
            rule.setVar(strict ? VARNAME_DATE_PATTERN_STRICT : VARNAME_DATE_PATTERN, pattern);
        } else if (annName.equals(VALIDATE_TYPE_TAG_NAME)) {
            AnnotationValue annotationValue = CompilerUtils.getAnnotationValue(ruleAnnotation, TYPE_ATTR, true);
            assert annotationValue != null;
            Object value = annotationValue.getValue();
            assert value instanceof PrimitiveType : value.getClass().getName();  // TODO: checker enforces this
            String typeName = (String) VALIDATE_TYPE_RULES.get(((PrimitiveType) value).getKind());
            assert typeName != null : ((PrimitiveType) value).getKind().toString();  // TODO: checker enforces this
            rule = new ValidatorRule(typeName);
        } else if (annName.equals(VALIDATE_VALID_WHEN_TAG_NAME)) {
            rule = new ValidatorRule(RULENAME_VALID_WHEN);
            rule.setVar(VARNAME_VALID_WHEN, CompilerUtils.getString(ruleAnnotation, CONDITION_ATTR, true));
        } else if (annName.equals(VALIDATE_CUSTOM_RULE_TAG_NAME)) {
            String ruleName = CompilerUtils.getString(ruleAnnotation, RULE_ATTR, false);
            rule = new ValidatorRule(ruleName);
            List ruleVars = CompilerUtils.getAnnotationArray(ruleAnnotation, VARIABLES_ATTR, false);

            for (Iterator ii = ruleVars.iterator(); ii.hasNext();) {
                AnnotationInstance ruleVar = (AnnotationInstance) ii.next();
                rule.setVar(CompilerUtils.getString(ruleVar, NAME_ATTR, false),
                        CompilerUtils.getString(ruleVar, VALUE_ATTR, false));
            }
        }

        return rule;
    }
}
