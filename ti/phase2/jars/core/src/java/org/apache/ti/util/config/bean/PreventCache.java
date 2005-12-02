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
public final class PreventCache
        implements java.io.Serializable {
    public static final int INT_DEFAULT = 0;
    public static final int INT_ALWAYS = 1;
    public static final int INT_IN_DEV_MODE = 2;

    /**
     */
    public static final PreventCache DEFAULT = new PreventCache(INT_DEFAULT);

    /**
     */
    public static final PreventCache ALWAYS = new PreventCache(INT_ALWAYS);

    /**
     */
    public static final PreventCache IN_DEV_MODE = new PreventCache(INT_IN_DEV_MODE);
    private int _val;

    private PreventCache(int val) {
        _val = val;
    }

    /**
     * Convert this prevent cache to a readable String.
     * @return the readable multipart handler name
     */
    public String toString() {
        switch (_val) {
            case INT_DEFAULT:
                return "default";

            case INT_ALWAYS:
                return "always";

            case INT_IN_DEV_MODE:
                return "inDevMode";
        }

        String message = "Encountered an unknown prevent cache with value \"" + _val + "\"";
        assert false : message;
        throw new IllegalStateException(message);
    }

    /**
     * Equals method.
     * @param value value to check
     * @return <code>true</code> if this prevent cache matches the <code>value</code>; <code>false</code> otherwise.
     */
    public boolean equals(Object value) {
        if (value == this) {
            return true;
        }

        if ((value == null) || !(value instanceof PreventCache)) {
            return false;
        }

        return ((PreventCache) value)._val == _val;
    }

    /**
     * Hash code.
     * @return the hash code
     */
    public int hashCode() {
        return _val;
    }

    /**
     * The multipart handler's int value.
     *
     * @return the multipart handler's value
     */
    public int getValue() {
        return _val;
    }
}
