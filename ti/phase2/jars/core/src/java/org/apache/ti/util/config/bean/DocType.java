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
public final class DocType
        implements java.io.Serializable {
    public static final int INT_HTML4_LOOSE = 0;
    public static final int INT_HTML4_LOOSE_QUIRKS = 1;
    public static final int INT_XHTML1_TRANSITIONAL = 2;

    /**
     */
    public static final DocType HTML4_LOOSE = new DocType(INT_HTML4_LOOSE);

    /**
     */
    public static final DocType HTML4_LOOSE_QUIRKS = new DocType(INT_HTML4_LOOSE_QUIRKS);

    /**
     */
    public static final DocType XHTML1_TRANSITIONAL = new DocType(INT_XHTML1_TRANSITIONAL);
    private int _val;

    private DocType(int val) {
        _val = val;
    }

    /**
     * Convert this doc type to a readable String.
     * @return the readable doc type name
     */
    public String toString() {
        switch (_val) {
            case INT_HTML4_LOOSE:
                return "html4-loose";

            case INT_HTML4_LOOSE_QUIRKS:
                return "html4-loose-quirks";

            case INT_XHTML1_TRANSITIONAL:
                return "xhtml1-transitional";
        }

        String message = "Encountered an unknown doc type with value \"" + _val + "\"";
        assert false : message;
        throw new IllegalStateException(message);
    }

    /**
     * Equals method.
     * @param value value to check
     * @return <code>true</code> if this doc type matches the <code>value</code>; <code>false</code> otherwise.
     */
    public boolean equals(Object value) {
        if (value == this) {
            return true;
        }

        if ((value == null) || !(value instanceof DocType)) {
            return false;
        }

        return ((DocType) value)._val == _val;
    }

    /**
     * Hash code.
     * @return the hash code
     */
    public int hashCode() {
        return _val;
    }

    /**
     * The doc type's int value.
     *
     * @return the doc type's value
     */
    public int getValue() {
        return _val;
    }
}
