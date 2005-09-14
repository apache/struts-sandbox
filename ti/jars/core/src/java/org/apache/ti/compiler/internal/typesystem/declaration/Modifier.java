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
package org.apache.ti.compiler.internal.typesystem.declaration;

public class Modifier {

    protected static final int INT_ABSTRACT = 0;
    protected static final int INT_PRIVATE = 1;
    protected static final int INT_PROTECTED = 2;
    protected static final int INT_PUBLIC = 3;
    protected static final int INT_STATIC = 4;
    protected static final int INT_TRANSIENT = 5;
    protected static final int INT_FINAL = 6;
    protected static final int INT_SYNCHRONIZED = 7;
    protected static final int INT_NATIVE = 8;

    public static final Modifier ABSTRACT = new Modifier(INT_ABSTRACT);
    public static final Modifier PRIVATE = new Modifier(INT_PRIVATE);
    public static final Modifier PROTECTED = new Modifier(INT_PROTECTED);
    public static final Modifier PUBLIC = new Modifier(INT_PUBLIC);
    public static final Modifier STATIC = new Modifier(INT_STATIC);
    public static final Modifier TRANSIENT = new Modifier(INT_TRANSIENT);
    public static final Modifier FINAL = new Modifier(INT_FINAL);
    public static final Modifier SYNCHRONIZED = new Modifier(INT_SYNCHRONIZED);
    public static final Modifier NATIVE = new Modifier(INT_NATIVE);

    private int _val;

    private Modifier(int val) {
        _val = val;
    }

    public String toString() {
        switch (_val) {
            case INT_ABSTRACT:
                return "abstract";
            case INT_PRIVATE:
                return "private";
            case INT_PROTECTED:
                return "protected";
            case INT_PUBLIC:
                return "public";
            case INT_STATIC:
                return "static";
            case INT_TRANSIENT:
                return "transient";
            case INT_FINAL:
                return "final";
            case INT_SYNCHRONIZED:
                return "synchronized";
            case INT_NATIVE:
                return "native";
        }

        assert false : _val;
        return "<unknown Modifier>";
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (! (o instanceof Modifier)) return false;
        return ((Modifier) o)._val == _val;
    }

    public int hashCode() {
        return _val;
    }

    protected Modifier() {
        _val = -1;
    }

    protected void setVal(int val) {
        _val = val;
    }
}
