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
package org.apache.ti.script.el.tokens;

import org.apache.ti.util.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class IdentifierToken
        extends ExpressionToken {

    private static final Logger LOGGER = Logger.getInstance(IdentifierToken.class);
    private static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();

    private String _identifier = null;

    public IdentifierToken(String identifier) {
        _identifier = identifier;
    }

    public Object evaluate(Object value) {
        /* todo: error handling */
        if (value == null) {
            String msg = "Can not evaluate the identifier \"" + _identifier + "\" on a null value object.";
            if (LOGGER.isErrorEnabled())
                LOGGER.error(msg);
            throw new RuntimeException(msg);
        }

        if (DEBUG_ENABLED) {
            LOGGER.debug("evaluate: " + _identifier);
            LOGGER.debug("value type: " + value.getClass().getName());
        }

        if (value instanceof Map) {
            return mapLookup((Map) value, _identifier);
        } else if (value instanceof List) {
            int i = parseIndex(_identifier);
            return listLookup((List) value, i);
        } else if (value.getClass().isArray()) {
            int i = parseIndex(_identifier);
            return arrayLookup(value, i);
        } else
            return beanLookup(value, _identifier);
    }

    public void update(Object root, Object newValue) {
        /* todo: error handling */
        if (root == null) {
            String msg = "Can not update the identifier \"" + _identifier + "\" on a null value object.";
            if (LOGGER.isErrorEnabled())
                LOGGER.error(msg);
            throw new RuntimeException(msg);
        }

        if (DEBUG_ENABLED)
            LOGGER.debug("Update _identifier \"" + _identifier + "\" on object of type: \"" + root.getClass().getName() + "\"");

        if (root instanceof Map)
            mapUpdate((Map) root, _identifier, newValue);
        else if (root instanceof List) {
            int i = parseIndex(_identifier);
            listUpdate((List) root, i, newValue);
        } else if (root.getClass().isArray()) {
            int i = parseIndex(_identifier);
            arrayUpdate(root, i, newValue);
        } else
            beanUpdate(root, _identifier, newValue);
    }

    public String getTokenString() {
        return "." + _identifier;
    }

    public String toString() {
        return _identifier;
    }
}
