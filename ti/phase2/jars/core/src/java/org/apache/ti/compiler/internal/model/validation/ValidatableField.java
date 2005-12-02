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

import org.apache.ti.compiler.internal.model.XmlElementSupport;
import org.apache.ti.compiler.internal.model.XmlModelWriter;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ValidatableField
        extends XmlElementSupport {
    private String _propertyName;
    private String _displayName;
    private String _displayNameKey;
    private boolean _isValidatorOneOne;
    List _rules = new ArrayList();

    public ValidatableField(String propertyName, String displayName, String displayNameKey, boolean isValidatorOneOne) {
        _propertyName = propertyName;
        _displayName = displayName;
        _displayNameKey = displayNameKey;
        _isValidatorOneOne = isValidatorOneOne;
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
        return (ValidatorRule[]) _rules.toArray(new ValidatorRule[_rules.size()]);
    }

    /**
     * Merge the rule names with the list in the field element's depends attribute.
     */
    void mergeDependsList(Element element) {
        String depends = getElementAttribute(element, "depends");
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

            if (depends.indexOf(name) == -1) {
                rules.add(name);
            }
        }

        Collections.sort(rules);

        for (Iterator i = rules.iterator(); i.hasNext();) {
            if (updatedDepends.length() > 0) {
                updatedDepends.append(',');
            }

            updatedDepends.append((String) i.next());
        }

        if (updatedDepends.length() != 0) {
            element.setAttribute("depends", updatedDepends.toString());
        }
    }

    public void writeToElement(XmlModelWriter xw, Element element) {
        assert _propertyName.equals(getElementAttribute(element, "property")) : _propertyName + ", " +
        getElementAttribute(element, "property");

        mergeDependsList(element);

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

        setDefaultArg0Element(xw, displayName, displayNameIsResource, element);

        //
        // Go through the rules, and add each one.  Each rule can spray into...
        // 1) an entry in the comma-separated rules dependencies list (handled above with mergeDependsList(),
        // 2) a set of  elements,
        // 3) a set of  elements.
        //
        for (Iterator ii = _rules.iterator(); ii.hasNext();) {
            ValidatorRule rule = (ValidatorRule) ii.next();

            // Add the message from the rule.
            setRuleMessage(xw, rule, element);

            // Add vars from the rule.
            Map ruleVars = rule.getVars();

            if (ruleVars != null) {
                for (Iterator j = ruleVars.entrySet().iterator(); j.hasNext();) {
                    Map.Entry entry = (Map.Entry) j.next();
                    String varName = (String) entry.getKey();
                    Element varElementToUse = findChildElementWithChildText(xw, element, "var", "var-name", varName, true);
                    xw.addElementWithText(varElementToUse, "var-value", (String) entry.getValue());
                }
            }

            //
            // Add message arguments from the rule.  If the user didn't specify an args, fill it in with a variable
            // value from the rule.
            //
            Iterator j = (ruleVars != null) ? ruleVars.keySet().iterator() : null;
            setRuleArg(xw, rule, 0, element, null);
            setRuleArg(xw, rule, 1, element, ((j != null) && j.hasNext()) ? (String) j.next() : null);
            setRuleArg(xw, rule, 2, element, ((j != null) && j.hasNext()) ? (String) j.next() : null);
            setRuleArg(xw, rule, 3, element, ((j != null) && j.hasNext()) ? (String) j.next() : null);
        }
    }

    private Element getArgElement(XmlModelWriter xw, Element element, int argNum, String forRuleName, boolean create) {
        if (_isValidatorOneOne) {
            String strNum = new Integer(argNum).toString();
            Element[] argArray = getChildElements(element, "arg");

            for (int i = 0; i < argArray.length; i++) {
                Element arg = argArray[i];
                String argRuleName = getElementAttribute(arg, "name");

                if (((forRuleName == null) && (argRuleName == null)) ||
                        ((forRuleName != null) && forRuleName.equals(argRuleName))) {
                    if (strNum.equals(getElementAttribute(arg, "position"))) {
                        return arg;
                    }
                }
            }

            Element retVal = null;

            if (create) {
                retVal = xw.addElement(element, "arg");
                setElementAttribute(retVal, "position", strNum);
            }

            return retVal;
        } else {
            return findChildElement(xw, element, "arg" + argNum, "name", forRuleName, create);
        }
    }

    /**
     * Find or create a default arg 0 element not associated with a specific rule and set it to the display name.
     */
    void setDefaultArg0Element(XmlModelWriter xw, String displayName, boolean displayNameIsResource, Element element) {
        Element defaultArg0Element = getArgElement(xw, element, 0, null, _rules.size() > 0);

        if (defaultArg0Element != null) {
            setElementAttribute(defaultArg0Element, "key", displayName);
            setElementAttribute(defaultArg0Element, "resource", Boolean.toString(displayNameIsResource));
            defaultArg0Element.removeAttribute("bundle");
        }
    }

    /**
     * Set up the desired &lt;msg&gt; element and attributes for the given rule.
     *
     * @param rule the rule with the message to use
     */
    void setRuleMessage(XmlModelWriter xw, ValidatorRule rule, Element element) {
        String messageKey = rule.getMessageKey();
        String message = rule.getMessage();

        if ((messageKey != null) || (message != null)) {
            Element msgElementToUse = findChildElement(xw, element, "msg", "name", rule.getRuleName(), true);
            setElementAttribute(msgElementToUse, "resource", true);

            if (messageKey != null) {
                setElementAttribute(msgElementToUse, "key", messageKey);

                if (_isValidatorOneOne) {
                    setElementAttribute(msgElementToUse, "bundle", rule.getBundle());
                }
            } else // message != null (it's a hardcoded message)
             {
                // Add our special constant as the message key, append the hardcoded message to it.
                setElementAttribute(msgElementToUse, "key", ValidatorConstants.EXPRESSION_KEY_PREFIX + message);
            }
        }
    }

    /**
     * Set up the desired &lt;arg&gt; element and attributes for the given rule.
     *
     * @param rule the rule with the message and arg information to use
     * @param argNum the position of the arg in the message
     * @param element a <code>&lt;field&gt;</code> element in the validation XML to update
     * @param altMessageVar alternative message var
     */
    void setRuleArg(XmlModelWriter xw, ValidatorRule rule, int argNum, Element element, String altMessageVar) {
        Integer argPosition = new Integer(argNum);
        ValidatorRule.MessageArg arg = rule.getArg(argPosition);

        if ((arg != null) || (altMessageVar != null)) {
            String ruleName = rule.getRuleName();
            Element argElementToUse = getArgElement(xw, element, argNum, ruleName, true);

            if (arg != null) {
                String argMessage = arg.getMessage();
                String key = arg.isKey() ? argMessage : (ValidatorConstants.EXPRESSION_KEY_PREFIX + argMessage);
                setElementAttribute(argElementToUse, "key", key);
                setElementAttribute(argElementToUse, "resource", true);

                if (arg.isKey() && _isValidatorOneOne) {
                    setElementAttribute(argElementToUse, "bundle", rule.getBundle());
                }
            } else {
                altMessageVar = "${var:" + altMessageVar + '}';
                setElementAttribute(argElementToUse, "key", altMessageVar);
                setElementAttribute(argElementToUse, "resource", "false");
            }

            setElementAttribute(argElementToUse, "name", ruleName);
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
