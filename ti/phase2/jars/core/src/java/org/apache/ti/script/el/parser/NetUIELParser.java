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

import org.apache.ti.script.el.ExpressionTerm;
import org.apache.ti.script.el.LiteralTerm;
import org.apache.ti.script.el.ParsedExpression;
import org.apache.ti.script.el.Term;
import org.apache.ti.script.el.tokens.ArrayIndexToken;
import org.apache.ti.script.el.tokens.ContextToken;
import org.apache.ti.script.el.tokens.ExpressionToken;
import org.apache.ti.script.el.tokens.IdentifierToken;
import org.apache.ti.script.el.tokens.MapKeyToken;

public class NetUIELParser
        implements NetUIELParserConstants {

    public static void main(String[] args)
            throws Exception {
        NetUIELParser parser = new NetUIELParser(System.in);
        parser.parse();
    }

// 3.
    final public ParsedExpression parse() throws ParseException {
        Token t = null;
        ParsedExpression pe = new ParsedExpression();
        Term term = null;
        label_1:
        while (true) {
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case NON_EXPRESSION_TEXT:
                case START_EXPRESSION:
                case ESCAPED_START_EXPRESSION:
                    ;
                    break;
                default:
                    jj_la1[0] = jj_gen;
                    break label_1;
            }
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case NON_EXPRESSION_TEXT:
                    term = parseLiteralTerm();
                    pe.addTerm(term);
                    break;
                case START_EXPRESSION:
                    jj_consume_token(START_EXPRESSION);
                    term = parseExpression();
                    jj_consume_token(END_EXPRESSION);
                    pe.addTerm(term);
                    break;
                case ESCAPED_START_EXPRESSION:
                    jj_consume_token(ESCAPED_START_EXPRESSION);
                    term = parseExpression();
                    jj_consume_token(END_EXPRESSION);
                    pe.addTerm(new LiteralTerm("\\"));
                    pe.addTerm(term);
                    break;
                default:
                    jj_la1[1] = jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
        }
        jj_consume_token(0);
        {
            if (true) return pe;
        }
        throw new Error("Missing return statement in function");
    }

    final public LiteralTerm parseLiteralTerm() throws ParseException {
        Token t = null;
        LiteralTerm ls = null;
        t = jj_consume_token(NON_EXPRESSION_TEXT);
        ls = new LiteralTerm(t.image);
        {
            if (true) return ls;
        }
        throw new Error("Missing return statement in function");
    }

    final public ExpressionTerm parseExpression() throws ParseException {
        ExpressionTerm expr = new ExpressionTerm();
        ExpressionToken eTok = null;
        eTok = Context();
        expr.addToken(eTok);
        label_2:
        while (true) {
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case DOT:
                case LBRACKET:
                    ;
                    break;
                default:
                    jj_la1[2] = jj_gen;
                    break label_2;
            }
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case DOT:
                    jj_consume_token(DOT);
                    eTok = ExprIdentifier();
                    break;
                default:
                    jj_la1[3] = jj_gen;
                    if (jj_2_1(2)) {
                        eTok = MapKey();
                    } else {
                        switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                            case LBRACKET:
                                eTok = ArrayIndex();
                                break;
                            default:
                                jj_la1[4] = jj_gen;
                                jj_consume_token(-1);
                                throw new ParseException();
                        }
                    }
            }
            {
                expr.addToken(eTok);
            }
        }
        expr.seal();
        {
            if (true) return expr;
        }
        throw new Error("Missing return statement in function");
    }

    final public ExpressionToken Context() throws ParseException {
        Token t = null;
        ExpressionToken eTok = null;
        t = jj_consume_token(IDENTIFIER);
//System.out.println("** Parser found context: " + t.image);
        eTok = new ContextToken(t.image);
        {
            if (true) return eTok;
        }
        throw new Error("Missing return statement in function");
    }

    final public ExpressionToken ExprIdentifier() throws ParseException {
        Token t = null;
        ExpressionToken eTok = null;
        t = jj_consume_token(IDENTIFIER);
//System.out.println("** Parser found identifier: " + t.image);
        eTok = new IdentifierToken(t.image);
        {
            if (true) return eTok;
        }
        throw new Error("Missing return statement in function");
    }

// handle text inside of map braces as ["..."] or ['...']
    final public ExpressionToken MapKey() throws ParseException {
        Token t = null;
        ExpressionToken eTok = null;
        jj_consume_token(LBRACKET);
        t = jj_consume_token(STRING_LITERAL);
        jj_consume_token(RBRACKET);
        eTok = new MapKeyToken(t.image);
        {
            if (true) return eTok;
        }
        throw new Error("Missing return statement in function");
    }

    final public ExpressionToken ArrayIndex() throws ParseException {
        Token t = null;
        ExpressionToken eTok = null;
        jj_consume_token(LBRACKET);
        t = jj_consume_token(INTEGER);
        jj_consume_token(RBRACKET);
//System.out.println("** Parser found array index: " + t.image);
        eTok = new ArrayIndexToken(t.image);
        {
            if (true) return eTok;
        }
        throw new Error("Missing return statement in function");
    }

    final private boolean jj_2_1(int xla) {
        jj_la = xla;
        jj_lastpos = jj_scanpos = token;
        boolean retval = !jj_3_1();
        jj_save(0, xla);
        return retval;
    }

    final private boolean jj_3_1() {
        if (jj_3R_3()) return true;
        if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
        return false;
    }

    final private boolean jj_3R_3() {
        if (jj_scan_token(LBRACKET)) return true;
        if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
        if (jj_scan_token(STRING_LITERAL)) return true;
        if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
        return false;
    }

    public NetUIELParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token, jj_nt;
    private int jj_ntk;
    private Token jj_scanpos, jj_lastpos;
    private int jj_la;
    public boolean lookingAhead = false;
    private boolean jj_semLA;
    private int jj_gen;
    final private int[] jj_la1 = new int[5];
    static private int[] jj_la1_0;

    static {
        jj_la1_0();
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{0xe, 0xe, 0x12000, 0x2000, 0x10000, };
    }

    final private JJCalls[] jj_2_rtns = new JJCalls[1];
    private boolean jj_rescan = false;
    private int jj_gc = 0;

    public NetUIELParser(java.io.InputStream stream) {
        jj_input_stream = new SimpleCharStream(stream, 1, 1);
        token_source = new NetUIELParserTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 5; i++) jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
    }

    public void ReInit(java.io.InputStream stream) {
        jj_input_stream.ReInit(stream, 1, 1);
        token_source.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 5; i++) jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
    }

    public NetUIELParser(java.io.Reader stream) {
        jj_input_stream = new SimpleCharStream(stream, 1, 1);
        token_source = new NetUIELParserTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 5; i++) jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
    }

    public void ReInit(java.io.Reader stream) {
        jj_input_stream.ReInit(stream, 1, 1);
        token_source.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 5; i++) jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
    }

    public NetUIELParser(NetUIELParserTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 5; i++) jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
    }

    public void ReInit(NetUIELParserTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 5; i++) jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
    }

    final private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken;
        if ((oldToken = token).next != null)
            token = token.next;
        else
            token = token.next = token_source.getNextToken();
        jj_ntk = -1;
        if (token.kind == kind) {
            jj_gen++;
            if (++jj_gc > 100) {
                jj_gc = 0;
                for (int i = 0; i < jj_2_rtns.length; i++) {
                    JJCalls c = jj_2_rtns[i];
                    while (c != null) {
                        if (c.gen < jj_gen) c.first = null;
                        c = c.next;
                    }
                }
            }
            return token;
        }
        token = oldToken;
        jj_kind = kind;
        throw generateParseException();
    }

    final private boolean jj_scan_token(int kind) {
        if (jj_scanpos == jj_lastpos) {
            jj_la--;
            if (jj_scanpos.next == null) {
                jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
            } else {
                jj_lastpos = jj_scanpos = jj_scanpos.next;
            }
        } else {
            jj_scanpos = jj_scanpos.next;
        }
        if (jj_rescan) {
            int i = 0;
            Token tok = token;
            while (tok != null && tok != jj_scanpos) {
                i++;
                tok = tok.next;
            }
            if (tok != null) jj_add_error_token(kind, i);
        }
        return (jj_scanpos.kind != kind);
    }

    final public Token getNextToken() {
        if (token.next != null)
            token = token.next;
        else
            token = token.next = token_source.getNextToken();
        jj_ntk = -1;
        jj_gen++;
        return token;
    }

    final public Token getToken(int index) {
        Token t = lookingAhead ? jj_scanpos : token;
        for (int i = 0; i < index; i++) {
            if (t.next != null)
                t = t.next;
            else
                t = t.next = token_source.getNextToken();
        }
        return t;
    }

    final private int jj_ntk() {
        if ((jj_nt = token.next) == null)
            return (jj_ntk = (token.next = token_source.getNextToken()).kind);
        else
            return (jj_ntk = jj_nt.kind);
    }

    private java.util.Vector jj_expentries = new java.util.Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) return;
        if (pos == jj_endpos + 1) {
            jj_lasttokens[jj_endpos++] = kind;
        } else if (jj_endpos != 0) {
            jj_expentry = new int[jj_endpos];
            for (int i = 0; i < jj_endpos; i++) {
                jj_expentry[i] = jj_lasttokens[i];
            }
            boolean exists = false;
            for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
                int[] oldentry = (int[]) (e.nextElement());
                if (oldentry.length == jj_expentry.length) {
                    exists = true;
                    for (int i = 0; i < jj_expentry.length; i++) {
                        if (oldentry[i] != jj_expentry[i]) {
                            exists = false;
                            break;
                        }
                    }
                    if (exists) break;
                }
            }
            if (!exists) jj_expentries.addElement(jj_expentry);
            if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
        }
    }

    public ParseException generateParseException() {
        jj_expentries.removeAllElements();
        boolean[] la1tokens = new boolean[18];
        for (int i = 0; i < 18; i++) {
            la1tokens[i] = false;
        }
        if (jj_kind >= 0) {
            la1tokens[jj_kind] = true;
            jj_kind = -1;
        }
        for (int i = 0; i < 5; i++) {
            if (jj_la1[i] == jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 18; i++) {
            if (la1tokens[i]) {
                jj_expentry = new int[1];
                jj_expentry[0] = i;
                jj_expentries.addElement(jj_expentry);
            }
        }
        jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);
        int[][] exptokseq = new int[jj_expentries.size()][];
        for (int i = 0; i < jj_expentries.size(); i++) {
            exptokseq[i] = (int[]) jj_expentries.elementAt(i);
        }
        return new ParseException(token, exptokseq, tokenImage);
    }

    final public void enable_tracing() {
    }

    final public void disable_tracing() {
    }

    final private void jj_rescan_token() {
        jj_rescan = true;
        for (int i = 0; i < 1; i++) {
            JJCalls p = jj_2_rtns[i];
            do {
                if (p.gen > jj_gen) {
                    jj_la = p.arg;
                    jj_lastpos = jj_scanpos = p.first;
                    switch (i) {
                        case 0:
                            jj_3_1();
                            break;
                    }
                }
                p = p.next;
            } while (p != null);
        }
        jj_rescan = false;
    }

    final private void jj_save(int index, int xla) {
        JJCalls p = jj_2_rtns[index];
        while (p.gen > jj_gen) {
            if (p.next == null) {
                p = p.next = new JJCalls();
                break;
            }
            p = p.next;
        }
        p.gen = jj_gen + xla - jj_la;
        p.first = token;
        p.arg = xla;
    }

    static final class JJCalls {

        int gen;
        Token first;
        int arg;
        JJCalls next;
    }

}
