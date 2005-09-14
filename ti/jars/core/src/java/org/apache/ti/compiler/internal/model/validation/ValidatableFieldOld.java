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

import org.apache.ti.schema.validator11.Arg0Document;
import org.apache.ti.schema.validator11.FieldDocument;
import org.apache.ti.schema.validator11.MsgDocument;

import java.lang.reflect.InvocationTargetException;

public class ValidatableFieldOld extends ValidatableField {

    public ValidatableFieldOld(String propertyName, String displayName, String displayNameKey) {
        super(propertyName, displayName, displayNameKey);
    }

    //
    // Support for validator version 1.0 arg0,...,arg3 elements
    //

    void setDefaultArg0Element(String displayName, boolean displayNameIsResource, FieldDocument.Field fieldElement) {
        Arg0Document.Arg0[] arg0Array = fieldElement.getArg0Array();
        Arg0Document.Arg0 defaultArg0Element = null;

        for (int i = 0; i < arg0Array.length; i++) {
            Arg0Document.Arg0 arg0 = arg0Array[i];
            if (arg0.getName() == null) {
                defaultArg0Element = arg0;
                break;
            }
        }

        if (defaultArg0Element == null && _rules.size() > 0) {
            defaultArg0Element = fieldElement.addNewArg0();
        }

        if (defaultArg0Element != null) {
            defaultArg0Element.setKey(displayName);
            defaultArg0Element.setResource(Boolean.toString(displayNameIsResource));
        }
    }

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

    //
    // Support for validator version 1.0 and the deprecated ,..., elements,
    // rather than the general <arg position="N"> element of validator version 1.1.
    //

    void setRuleArg(ValidatorRule rule, int argNum, FieldDocument.Field fieldElement, String altMessageVar) {
        try {
            Class fieldElementClass = fieldElement.getClass();
            ValidatorRule.MessageArg arg = rule.getArg(new Integer(argNum));

            String ruleName = rule.getRuleName();
            Object argElementToUse = null;
            Object[] existingArgElements =
                    (Object[]) fieldElementClass.getMethod("getArg" + argNum + "Array", null).invoke(fieldElement, null);

            for (int i = 0; i < existingArgElements.length; i++) {
                Object existingElement = existingArgElements[i];
                if (ruleName.equals(existingElement.getClass().getMethod("getName", null).invoke(existingElement, null))) {
                    argElementToUse = existingElement;
                    break;
                }
            }

            if (arg != null || altMessageVar != null) {
                if (argElementToUse == null) {
                    argElementToUse = fieldElementClass.getMethod("addNewArg" + argNum, null).invoke(fieldElement, null);
                }

                Class argElementToUseClass = argElementToUse.getClass();

                if (arg != null) {
                    String argMessage = arg.getMessage();
                    String key = arg.isKey() ? argMessage : ValidatorConstants.EXPRESSION_KEY_PREFIX + argMessage;
                    argElementToUseClass.getMethod("setKey", new Class[]{String.class}).invoke(argElementToUse, new Object[]{key});
                    String isResource = Boolean.TRUE.toString();
                    argElementToUseClass.getMethod("setResource", new Class[]{String.class}).invoke(argElementToUse, new Object[]{isResource});
                    argElementToUseClass.getMethod("setName", new Class[]{String.class}).invoke(argElementToUse, new Object[]{ruleName});
                } else {
                    altMessageVar = "${var:" + altMessageVar + '}';
                    argElementToUseClass.getMethod("setKey", new Class[]{String.class}).invoke(argElementToUse, new Object[]{altMessageVar});
                    argElementToUseClass.getMethod("setResource", new Class[]{String.class}).invoke(argElementToUse, new Object[]{"false"});
                    argElementToUseClass.getMethod("setName", new Class[]{String.class}).invoke(argElementToUse, new Object[]{ruleName});
                }
            }
        }
        catch (NoSuchMethodException e) {
            assert false : e;   // this shouldn't ever happen -- we know what the compiled types look like.
        }
        catch (IllegalAccessException e) {
            assert false : e;   // this shouldn't ever happen -- we know what the compiled types look like.
        }
        catch (InvocationTargetException e) {
            assert false : e;   // this shouldn't ever happen -- we know what the compiled types look like.
        }
    }
}
