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

import java.util.LinkedHashMap;
import java.util.Map;

public class ValidatorRule {

    private Map _vars;
    private String _ruleName;
    private String _messageKey;
    private String _message;
    private String _bundle;
    private Map _args;


    public static class MessageArg {

        private String _message;
        private boolean _isKey;
        private String _bundle;
        private Integer _position;

        public MessageArg(String message, boolean isKey, String bundle, Integer position) {
            _message = message;
            _isKey = isKey;
            _bundle = bundle;
            _position = position;
        }

        public String getMessage() {
            return _message;
        }

        public boolean isKey() {
            return _isKey;
        }

        public String getBundle() {
            return _bundle;
        }

        public Integer getPosition() {
            return _position;
        }
    }

    public ValidatorRule(String ruleName) {
        assert ruleName != null;
        _ruleName = ruleName;
    }

    public void setVar(String name, String val) {
        if (_vars == null) _vars = new LinkedHashMap();
        _vars.put(name, val);
    }

    public Map getVars() {
        return _vars;
    }

    public String getRuleName() {
        return _ruleName;
    }

    public String getMessageKey() {
        return _messageKey;
    }

    public void setMessageKey(String messageKey) {
        _messageKey = messageKey;
    }

    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        assert _messageKey == null;
        _message = message;
    }

    public String getBundle() {
        return _bundle;
    }

    public void setBundle(String bundle) {
        _bundle = bundle;
    }

    public void setArg(String message, boolean isKey, String bundle, Integer position) {
        if (_args == null) {
            _args = new LinkedHashMap();
        }
        _args.put(position, new MessageArg(message, isKey, bundle, position));
    }

    public MessageArg getArg(Integer position) {
        if (_args == null) {
            return null;
        }
        return (MessageArg) _args.get(position);
    }
}
