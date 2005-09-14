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

import org.apache.ti.schema.validator11.ArgDocument;
import org.apache.ti.schema.validator11.FieldDocument;
import org.apache.ti.schema.validator11.MsgDocument;
import org.apache.ti.schema.validator11.VarDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ValidatableField {

    private String _propertyName;
    private String _displayName;
    private String _displayNameKey;
    List _rules = new ArrayList();


    public ValidatableField(String propertyName, String displayName, String displayNameKey) {
        _propertyName = propertyName;
        _displayName = displayName;
        _displayNameKey = displayNameKey;
    }

    public String getPropertyName() {
        return _propertyName;
    }

    protected boolean hasRule(ValidatorRule rule) {
        assert rule != null;

        String name = rule.getRuleName();
        for (Iterator ii = _rules.iterator(); ii.hasNext();) {
            ValidatorRule existingRule = (ValidatorRule) ii.next();
            if (existingRule.getRuleName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addRule(ValidatorRule rule) {
        assert rule != null;

        _rules.add(rule);
    }

    public ValidatorRule[] getRules() {
        return (ValidatorRule[]) _rules.toArray(new ValidatorRule[ _rules.size() ]);
    }

    /**
     * Merge the rule names with the list in the field element's depends attribute.
     *
     * @param fieldElement the XMLBean field element in the validation XML to update
     */
    void mergeDependsList(FieldDocument.Field fieldElement) {
        String depends = fieldElement.getDepends();
        StringBuffer updatedDepends = new StringBuffer();

        if (depends != null) {
            updatedDepends.append(depends);
        } else {
            depends = "";
        }

        ArrayList rules = new ArrayList();
        for (Iterator i = _rules.iterator(); i.hasNext();) {
            ValidatorRule rule = (ValidatorRule) i.next();
            String name = rule.getRuleName();

            if (depends.indexOf(name) == -1) rules.add(name);
        }
        Collections.sort(rules);

        for (Iterator i = rules.iterator(); i.hasNext();) {
            if (updatedDepends.length() > 0) updatedDepends.append(',');
            updatedDepends.append((String) i.next());
        }

        if (updatedDepends.length() != 0) {
            fieldElement.setDepends(updatedDepends.toString());
        }
    }

    public void writeToXMLBean(FieldDocument.Field fieldElement) {
        assert fieldElement.getProperty().equals(_propertyName);

        mergeDependsList(fieldElement);

        //
        // Add the display name as the default first argument (can be overridden by individual rules).
        //
        String displayName;
        boolean displayNameIsResource = false;

        if (_displayName != null) {
            displayName = ValidatorConstants.EXPRESSION_KEY_PREFIX + _displayName;
            displayNameIsResource = true;
        } else if (_displayNameKey != null) {
            displayName = _displayNameKey;
            displayNameIsResource = true;
        } else {
            displayName = Character.toUpperCase(_propertyName.charAt(0)) + _propertyName.substring(1);
        }

        setDefaultArg0Element(displayName, displayNameIsResource, fieldElement);

        //
        // Go through the rules, and add each one.  Each rule can spray into...
        // 1) an entry in the comma-separated rules dependencies list (handled above with mergeDependsList(),
        // 2) a set of  elements,
        // 3) a set of  elements.
        //
        for (Iterator ii = _rules.iterator(); ii.hasNext();) {
            ValidatorRule rule = (ValidatorRule) ii.next();
            //
            // Add the message from the rule.
            //
            setRuleMessage(rule, fieldElement);

            //
            // Add vars from the rule.
            //
            VarDocument.Var[] existingVars = fieldElement.getVarArray();
            Map ruleVars = rule.getVars();

            if (ruleVars != null) {
                for (Iterator j = ruleVars.entrySet().iterator(); j.hasNext();) {
                    Map.Entry entry = (Map.Entry) j.next();
                    String varName = (String) entry.getKey();

                    //
                    // Look for an existing var entry to update, or create one if there's none with the right name.
                    //
                    VarDocument.Var varElementToUse = null;
                    for (int k = 0; k < existingVars.length; k++) {
                        VarDocument.Var existingVar = existingVars[k];

                        if (varName.equals(existingVar.getVarName())) {
                            varElementToUse = existingVar;
                            break;
                        }
                    }

                    if (varElementToUse == null) {
                        varElementToUse = fieldElement.addNewVar();
                        varElementToUse.setVarName(varName);
                    }

                    if (varElementToUse.getVarValue() == null) {
                        varElementToUse.setVarValue((String) entry.getValue());
                    }
                }
            }

            //
            // Add message arguments from the rule.  If the user didn't specify an args, fill it in with a variable
            // value from the rule.
            //
            Iterator j = ruleVars != null ? ruleVars.keySet().iterator() : null;
            setRuleArg(rule, 0, fieldElement, null);
            setRuleArg(rule, 1, fieldElement, j != null && j.hasNext() ? (String) j.next() : null);
            setRuleArg(rule, 2, fieldElement, j != null && j.hasNext() ? (String) j.next() : null);
            setRuleArg(rule, 3, fieldElement, j != null && j.hasNext() ? (String) j.next() : null);
        }
    }

    /**
     * Find or create a default arg 0 element not associated with a
     * specific rule and set it to the display name.
     */
    void setDefaultArg0Element(String displayName, boolean displayNameIsResource, FieldDocument.Field fieldElement) {
        ArgDocument.Arg[] argArray = fieldElement.getArgArray();
        ArgDocument.Arg defaultArg0Element = null;

        for (int i = 0; i < argArray.length; i++) {
            ArgDocument.Arg arg = argArray[i];
            if (arg.getName() == null && "0".equals(arg.getPosition())) {
                defaultArg0Element = arg;
                break;
            }
        }

        if (defaultArg0Element == null && _rules.size() > 0) {
            defaultArg0Element = fieldElement.addNewArg();
            defaultArg0Element.setPosition("0");
        }

        if (defaultArg0Element != null) {
            defaultArg0Element.setKey(displayName);
            defaultArg0Element.setResource(Boolean.toString(displayNameIsResource));
            if (defaultArg0Element.getBundle() != null) {
                defaultArg0Element.setBundle(null);
            }
        }
    }

    /**
     * Set up the desired &lt;msg&gt; element and attributes for the given rule.
     *
     * @param rule         the rule with the message to use
     * @param fieldElement an XMLBean field element in the validation XML to update
     */
    void setRuleMessage(ValidatorRule rule, FieldDocument.Field fieldElement) {
        String messageKey = rule.getMessageKey();
        String message = rule.getMessage();

        if (messageKey != null || message != null) {
            MsgDocument.Msg[] existingMsgElements = fieldElement.getMsgArray();
            MsgDocument.Msg msgElementToUse = null;

            for (int j = 0; j < existingMsgElements.length; j++) {
                MsgDocument.Msg existingMsgElement = existingMsgElements[j];
                if (rule.getRuleName().equals(existingMsgElement.getName())) {
                    msgElementToUse = existingMsgElement;
                    break;
                }
            }

            if (msgElementToUse == null) {
                msgElementToUse = fieldElement.addNewMsg();
                msgElementToUse.setName(rule.getRuleName());
            }

            if (messageKey != null) {
                msgElementToUse.setKey(messageKey);
                msgElementToUse.setResource(Boolean.TRUE.toString());
                String bundle = rule.getBundle();
                if (bundle != null && bundle.length() > 0) {
                    msgElementToUse.setBundle(bundle);
                }
            } else // message != null (it's a hardcoded message)
            {
                //
                // Add our special constant as the message key, append the hardcoded message to it.
                //
                msgElementToUse.setKey(ValidatorConstants.EXPRESSION_KEY_PREFIX + message);
                msgElementToUse.setResource(Boolean.TRUE.toString());
            }
        }
    }

    /**
     * Set up the desired &lt;arg&gt; element and attributes for the given rule.
     *
     * @param rule          the rule with the message and arg information to use
     * @param argNum        the position of the arg in the message
     * @param fieldElement  an XMLBean field element in the validation XML to update
     * @param altMessageVar alternative message var
     */
    void setRuleArg(ValidatorRule rule, int argNum, FieldDocument.Field fieldElement, String altMessageVar) {
        Integer argPosition = new Integer(argNum);
        String position = argPosition.toString();
        ValidatorRule.MessageArg arg = rule.getArg(argPosition);

        String ruleName = rule.getRuleName();
        ArgDocument.Arg[] argArray = fieldElement.getArgArray();
        ArgDocument.Arg argElementToUse = null;

        for (int i = 0; i < argArray.length; i++) {
            if (ruleName.equals(argArray[i].getName()) && position.equals(argArray[i].getPosition())) {
                argElementToUse = argArray[i];
                break;
            }
        }

        if (arg != null || altMessageVar != null) {
            if (argElementToUse == null) {
                argElementToUse = fieldElement.addNewArg();
            }

            if (arg != null) {
                String argMessage = arg.getMessage();
                String key = arg.isKey() ? argMessage : ValidatorConstants.EXPRESSION_KEY_PREFIX + argMessage;
                argElementToUse.setKey(key);
                argElementToUse.setResource(Boolean.TRUE.toString());
                String bundle = rule.getBundle();
                if (arg.isKey() && bundle != null && bundle.length() > 0) {
                    argElementToUse.setBundle(bundle);
                }
            } else {
                altMessageVar = "${var:" + altMessageVar + '}';
                argElementToUse.setKey(altMessageVar);
                argElementToUse.setResource("false");
            }

            argElementToUse.setPosition(position);
            argElementToUse.setName(ruleName);
        }
    }

    public String getDisplayName() {
        return _displayName;
    }

    public void setDisplayName(String displayName) {
        _displayName = displayName;
    }

    public String getDisplayNameKey() {
        return _displayNameKey;
    }

    public void setDisplayNameKey(String displayNameKey) {
        _displayNameKey = displayNameKey;
    }
}
