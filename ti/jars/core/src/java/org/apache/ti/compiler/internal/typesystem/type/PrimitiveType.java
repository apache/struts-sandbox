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
package org.apache.ti.compiler.internal.typesystem.type;

public interface PrimitiveType
        extends TypeInstance {

    Kind getKind();

    public class Kind {

        public static final int INT_BOOLEAN = 0;
        public static final int INT_BYTE = 1;
        public static final int INT_SHORT = 2;
        public static final int INT_INT = 3;
        public static final int INT_LONG = 4;
        public static final int INT_CHAR = 5;
        public static final int INT_FLOAT = 6;
        public static final int INT_DOUBLE = 7;

        public static final Kind BOOLEAN = new Kind(INT_BOOLEAN);
        public static final Kind BYTE = new Kind(INT_BYTE);
        public static final Kind SHORT = new Kind(INT_SHORT);
        public static final Kind INT = new Kind(INT_INT);
        public static final Kind LONG = new Kind(INT_LONG);
        public static final Kind CHAR = new Kind(INT_CHAR);
        public static final Kind FLOAT = new Kind(INT_FLOAT);
        public static final Kind DOUBLE = new Kind(INT_DOUBLE);

        private int _val;

        private Kind(int val) {
            _val = val;
        }

        public String toString() {
            switch (_val) {
                case INT_BOOLEAN:
                    return "boolean";
                case INT_BYTE:
                    return "byte";
                case INT_SHORT:
                    return "short";
                case INT_INT:
                    return "int";
                case INT_LONG:
                    return "long";
                case INT_CHAR:
                    return "char";
                case INT_FLOAT:
                    return "float";
                case INT_DOUBLE:
                    return "double";
            }

            assert false : _val;
            return "<unknown Kind>";
        }

        public boolean equals(Object o) {
            if (o == null) return false;
            if (o == this) return true;
            if (! (o instanceof Kind)) return false;
            return ((Kind) o)._val == _val;
        }

        public final int asInt() {
            return _val;
        }

        public final int hashCode() {
            return _val;
        }

        protected Kind() {
            _val = -1;
        }

        protected void setVal(int val) {
            _val = val;
        }
    }
}
