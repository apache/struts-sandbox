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
package org.apache.ti.pageflow;

/**
 * Enumeration to represent secure/unsecure/unspecified values associated with paths in the webapp.
 */
public class SecurityProtocol {

    protected static final int INT_SECURE = 0;
    protected static final int INT_UNSECURE = 1;
    protected static final int INT_UNSPECIFIED = 2;

    public static final SecurityProtocol SECURE = new SecurityProtocol(INT_SECURE);
    public static final SecurityProtocol UNSECURE = new SecurityProtocol(INT_UNSECURE);
    public static final SecurityProtocol UNSPECIFIED = new SecurityProtocol(INT_UNSPECIFIED);

    private int _val;

    private SecurityProtocol(int val) {
        _val = val;
    }

    public String toString() {
        switch (_val) {
            case INT_SECURE:
                return "secure";
            case INT_UNSECURE:
                return "unsecure";
            case INT_UNSPECIFIED:
                return "unspecified";
        }

        assert false : _val;
        return "<unknown Modifier>";
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof SecurityProtocol)) return false;
        return ((SecurityProtocol) o)._val == _val;
    }

    public int hashCode() {
        return _val;
    }
}
