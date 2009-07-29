/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.json;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Deserializes and object from a JSON string
 * </p>
 */
class JSONReader {
    private static final Object OBJECT_END = new Object();
    private static final Object ARRAY_END = new Object();
    private static final Object COLON = new Object();
    private static final Object COMMA = new Object();
    private static Map<Character, Character> escapes = new HashMap<Character, Character>();

    static {
        escapes.put(new Character('"'), new Character('"'));
        escapes.put(new Character('\\'), new Character('\\'));
        escapes.put(new Character('/'), new Character('/'));
        escapes.put(new Character('b'), new Character('\b'));
        escapes.put(new Character('f'), new Character('\f'));
        escapes.put(new Character('n'), new Character('\n'));
        escapes.put(new Character('r'), new Character('\r'));
        escapes.put(new Character('t'), new Character('\t'));
    }

    private CharacterIterator it;
    private char c;
    private Object token;
    private StringBuilder buf = new StringBuilder();

    private char next() {
        this.c = this.it.next();

        return this.c;
    }

    private void skipWhiteSpace() {
        while (Character.isWhitespace(this.c)) {
            this.next();
        }
    }

    public Object read(String string) throws JSONException {
        this.it = new StringCharacterIterator(string);
        this.c = this.it.first();

        return this.read();
    }

    private Object read() throws JSONException {
        Object ret = null;

        this.skipWhiteSpace();

        if (this.c == '"') {
            this.next();
            ret = this.string('"');
        } else if (this.c == '\'') {
            this.next();
            ret = this.string('\'');
        } else if (this.c == '[') {
            this.next();
            ret = this.array();
        } else if (this.c == ']') {
            ret = ARRAY_END;
            this.next();
        } else if (this.c == ',') {
            ret = COMMA;
            this.next();
        } else if (this.c == '{') {
            this.next();
            ret = this.object();
        } else if (this.c == '}') {
            ret = OBJECT_END;
            this.next();
        } else if (this.c == ':') {
            ret = COLON;
            this.next();
        } else if ((this.c == 't') && (this.next() == 'r') && (this.next() == 'u') && (this.next() == 'e')) {
            ret = Boolean.TRUE;
            this.next();
        } else if ((this.c == 'f') && (this.next() == 'a') && (this.next() == 'l') && (this.next() == 's')
                && (this.next() == 'e')) {
            ret = Boolean.FALSE;
            this.next();
        } else if ((this.c == 'n') && (this.next() == 'u') && (this.next() == 'l') && (this.next() == 'l')) {
            ret = null;
            this.next();
        } else if (Character.isDigit(this.c) || (this.c == '-')) {
            ret = this.number();
        } else {
            throw buildInvalidInputException();
        }

        this.token = ret;

        return ret;
    }

    @SuppressWarnings("unchecked")
    private Map object() throws JSONException {
        Map ret = new HashMap();
        Object next = this.read();
        if (next != OBJECT_END) {
            String key = (String) next;
            while (this.token != OBJECT_END) {
                this.read(); // should be a colon

                if (this.token != OBJECT_END) {
                    ret.put(key, this.read());

                    if (this.read() == COMMA) {
                        Object name = this.read();

                        if (name instanceof String) {
                            key = (String) name;
                        } else
                            throw buildInvalidInputException();
                    }
                }
            }
        }

        return ret;
    }

    private JSONException buildInvalidInputException() {
        return new JSONException("Input string is not well formed JSON (invalid char " + this.c + ")");
    }

    @SuppressWarnings("unchecked")
    private List array() throws JSONException {
        List ret = new ArrayList();
        Object value = this.read();

        while (this.token != ARRAY_END) {
            ret.add(value);

            Object read = this.read();
            if (read == COMMA) {
                value = this.read();
            } else if (read != ARRAY_END) {
                throw buildInvalidInputException();
            }
        }

        return ret;
    }

    private Object number() {
        this.buf.setLength(0);

        if (this.c == '-') {
            this.add();
        }

        this.addDigits();

        if (this.c == '.') {
            this.add();
            this.addDigits();
        }

        if ((this.c == 'e') || (this.c == 'E')) {
            this.add();

            if ((this.c == '+') || (this.c == '-')) {
                this.add();
            }

            this.addDigits();
        }

        return (this.buf.indexOf(".") >= 0) ? (Object) Double.parseDouble(this.buf.toString())
                : (Object) Long.parseLong(this.buf.toString());
    }

    private Object string(char quote) {
        this.buf.setLength(0);

        while ((this.c != quote) && (this.c != CharacterIterator.DONE)) {
            if (this.c == '\\') {
                this.next();

                if (this.c == 'u') {
                    this.add(this.unicode());
                } else {
                    Object value = escapes.get(new Character(this.c));

                    if (value != null) {
                        this.add(((Character) value).charValue());
                    }
                }
            } else {
                this.add();
            }
        }

        this.next();

        return this.buf.toString();
    }

    private void add(char cc) {
        this.buf.append(cc);
        this.next();
    }

    private void add() {
        this.add(this.c);
    }

    private void addDigits() {
        while (Character.isDigit(this.c)) {
            this.add();
        }
    }

    private char unicode() {
        int value = 0;

        for (int i = 0; i < 4; ++i) {
            switch (this.next()) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                value = (value << 4) + (this.c - '0');

                break;

            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                value = (value << 4) + (this.c - 'W');

                break;

            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                value = (value << 4) + (this.c - '7');

                break;
            }
        }

        return (char) value;
    }
}
