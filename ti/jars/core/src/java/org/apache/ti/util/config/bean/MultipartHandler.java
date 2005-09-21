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
public final class MultipartHandler
        implements java.io.Serializable {
    public static final int INT_DISABLED = 0;
    public static final int INT_MEMORY = 1;
    public static final int INT_DISK = 2;

    /**
     */
    public static final MultipartHandler DISABLED = new MultipartHandler(INT_DISABLED);

    /**
     */
    public static final MultipartHandler MEMORY = new MultipartHandler(INT_MEMORY);

    /**
     */
    public static final MultipartHandler DISK = new MultipartHandler(INT_DISK);
    private int _val;

    private MultipartHandler(int val) {
        _val = val;
    }

    /**
     * Convert this multipart handler to a readable String.
     * @return the readable multipart handler name
     */
    public String toString() {
        switch (_val) {
            case INT_DISABLED:
                return "disabled";

            case INT_MEMORY:
                return "memory";

            case INT_DISK:
                return "disk";
        }

        String message = "Encountered an unknown multipart handler with value \"" + _val + "\"";
        assert false : message;
        throw new IllegalStateException(message);
    }

    /**
     * Equals method.
     * @param value value to check
     * @return <code>true</code> if this multipart handler matches the <code>value</code>; <code>false</code> otherwise.
     */
    public boolean equals(Object value) {
        if (value == this) {
            return true;
        }

        if ((value == null) || !(value instanceof MultipartHandler)) {
            return false;
        }

        return ((MultipartHandler) value)._val == _val;
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
