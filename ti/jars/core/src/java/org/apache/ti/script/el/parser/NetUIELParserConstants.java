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
package org.apache.ti.script.el.parser;

public interface NetUIELParserConstants {

    int EOF = 0;
    int NON_EXPRESSION_TEXT = 1;
    int START_EXPRESSION = 2;
    int ESCAPED_START_EXPRESSION = 3;
    int END_EXPRESSION = 4;
    int STRING_LITERAL = 5;
    int ECMA_ESCAPE_SEQUENCE = 6;
    int HIT = 7;
    int IDENTIFIER = 8;
    int IMPL_OBJ_START = 9;
    int LETTER = 10;
    int DIGIT = 11;
    int INTEGER = 12;
    int DOT = 13;
    int DQUOTE = 14;
    int SQUOTE = 15;
    int LBRACKET = 16;
    int RBRACKET = 17;

    int DEFAULT = 0;
    int IN_EXPRESSION = 1;

    String[] tokenImage = {
        "<EOF>",
        "<NON_EXPRESSION_TEXT>",
        "\"{\"",
        "\"\\\\\\\\{\"",
        "\"}\"",
        "<STRING_LITERAL>",
        "<ECMA_ESCAPE_SEQUENCE>",
        "<HIT>",
        "<IDENTIFIER>",
        "\"#\"",
        "<LETTER>",
        "<DIGIT>",
        "<INTEGER>",
        "\".\"",
        "\"\\\"\"",
        "\"\\\'\"",
        "\"[\"",
        "\"]\"",
    };

}
