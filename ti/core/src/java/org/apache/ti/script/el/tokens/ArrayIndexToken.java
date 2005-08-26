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

/**
 *
 */
public class ArrayIndexToken
        extends ExpressionToken {

    private static final Logger LOGGER = Logger.getInstance(ArrayIndexToken.class);

    private int _index;

    public ArrayIndexToken(String identifier) {
        _index = Integer.parseInt(identifier);
    }

    public void update(Object root, Object newValue) {
        if (root instanceof List)
            listUpdate((List) root, _index, newValue);
        else if (root.getClass().isArray())
            arrayUpdate(root, _index, newValue);
        else {
            RuntimeException re = new RuntimeException("The _index \"" + _index + "\" can not be used to look-up the type of a property" +
                    " on an object that is not an array or list.");
            LOGGER.error("", re);
            throw re;
        }
    }

    public Object evaluate(Object value) {
        if (value instanceof List)
            return listLookup((List) value, _index);
        else if (value.getClass().isArray())
            return arrayLookup(value, _index);
        else {
            RuntimeException re = new RuntimeException("The _index \"" + _index + "\" can not be used to look-up a property on an object that is not an array or list.");
            LOGGER.error("", re);
            throw re;
        }
    }

    public String getTokenString() {
        return "[" + _index + "]";
    }

    public String toString() {
        return "" + _index;
    }
}
