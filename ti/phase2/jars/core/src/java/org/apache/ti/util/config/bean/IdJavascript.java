/**
 Copyright 2004 The Apache Software Foundation.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 $Header:$
 */
package org.apache.ti.util.config.bean;


/**
 *
 */
public final class IdJavascript
        implements java.io.Serializable {
    public static final int INT_DEFAULT = 0;
    public static final int INT_LEGACY = 1;
    public static final int INT_LEGACY_ONLY = 2;

    /**
     */
    public static final IdJavascript DEFAULT = new IdJavascript(INT_DEFAULT);

    /**
     */
    public static final IdJavascript LEGACY = new IdJavascript(INT_LEGACY);

    /**
     */
    public static final IdJavascript LEGACY_ONLY = new IdJavascript(INT_LEGACY_ONLY);
    private int _val;

    private IdJavascript(int val) {
        _val = val;
    }

    /**
     * Convert this id javascript to a readable String.
     * @return the readable id javascript name
     */
    public String toString() {
        switch (_val) {
            case INT_DEFAULT:
                return "default";

            case INT_LEGACY:
                return "legacy";

            case INT_LEGACY_ONLY:
                return "legacyOnly";
        }

        String message = "Encountered an unknown id javascript with value \"" + _val + "\"";
        assert false : message;
        throw new IllegalStateException(message);
    }

    /**
     * Equals method.
     * @param value value to check
     * @return <code>true</code> if this id javascript matches the <code>value</code>; <code>false</code> otherwise.
     */
    public boolean equals(Object value) {
        if (value == this) {
            return true;
        }

        if ((value == null) || !(value instanceof IdJavascript)) {
            return false;
        }

        return ((IdJavascript) value)._val == _val;
    }

    /**
     * Hash code.
     * @return the hash code
     */
    public int hashCode() {
        return _val;
    }

    /**
     * The id javascript's int value.
     *
     * @return the id javascript's value
     */
    public int getValue() {
        return _val;
    }
}
