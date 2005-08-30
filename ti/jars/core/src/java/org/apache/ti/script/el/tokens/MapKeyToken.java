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

import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import java.util.Map;

/**
 *
 */
public class MapKeyToken
        extends ExpressionToken {

    private static final Logger LOGGER = Logger.getInstance(MapKeyToken.class);

    private String _identifier = null;
    private boolean _dblQuote = false;

    public MapKeyToken(String identifier) {
        this._identifier = identifier;

        if (identifier.startsWith("\""))
            _dblQuote = true;

        // convert the Java string to an EcmaScript string.  Strip the quotes that exist because they're
        // always there for this token.
        this._identifier = convertToEcmaScriptString(this._identifier.substring(1, identifier.length() - 1));
    }

    /**
     * Given a Java String, this value needs to be converted into a JavaScript compliant String.
     * See JavaScript: The Definitive Guide for how to do this
     */
    private final String convertToEcmaScriptString(String string) {
        CharSequence cs = string;

        int len = cs.length();
        InternalStringBuilder buf = new InternalStringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = cs.charAt(i);
            // skip the \\ and consume the next character either appending it or turning it back into the single character
            // that it should have been in the first place.
            // 
            // if slash and not at the last character...
            if (c == '\\' && i + 1 < len) {
                i++;

                // skip the slash
                c = cs.charAt(i);

                if (c == 'b')
                    c = '\b';
                else if (c == 't')
                    c = '\t';
                else if (c == 'n')
                    c = '\n';
                //else if(c == 'v') c = '\v';
                else if (c == 'f')
                    c = '\f';
                else if (c == 'r') c = '\r';
                // @TODO: unicode escaping...
            }

            buf.append(c);
        }

        if (LOGGER.isDebugEnabled()) LOGGER.debug("new _identifier: " + buf.toString());

        return buf.toString();
    }

    public void update(Object root, Object newValue) {
        if (root instanceof Map)
            mapUpdate((Map) root, _identifier, newValue);
        else
            beanUpdate(root, _identifier, newValue);
    }

    public Object evaluate(Object value) {
        if (value instanceof Map)
            return mapLookup((Map) value, _identifier);
        else
            return beanLookup(value, _identifier);
    }

    public String getTokenString() {
        if (_dblQuote)
            return "[\"" + _identifier + "\"]";
        else
            return "['" + _identifier + "']";
    }

    public String toString() {
        return _identifier;
    }
}
