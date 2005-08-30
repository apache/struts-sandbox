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



public class NetUIELParserTokenManager
        implements NetUIELParserConstants {

    public java.io.PrintStream debugStream = System.out;

    public void setDebugStream(java.io.PrintStream ds) {
        debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 0x8L) != 0L) {
                    jjmatchedKind = 1;
                    return 1;
                }
                return -1;
            case 1:
                if ((active0 & 0x8L) != 0L) {
                    if (jjmatchedPos == 0) {
                        jjmatchedKind = 1;
                        jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            default :
                return -1;
        }
    }

    private final int jjStartNfa_0(int pos, long active0) {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }

    private final int jjStopAtPos(int pos, int kind) {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        return pos + 1;
    }

    private final int jjStartNfaWithStates_0(int pos, int kind, int state) {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            return pos + 1;
        }
        return jjMoveNfa_0(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_0() {
        switch (curChar) {
            case 92:
                return jjMoveStringLiteralDfa1_0(0x8L);
            case 123:
                return jjStopAtPos(0, 2);
            default :
                return jjMoveNfa_0(2, 0);
        }
    }

    private final int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (curChar) {
            case 92:
                return jjMoveStringLiteralDfa2_0(active0, 0x8L);
            default :
                break;
        }
        return jjStartNfa_0(0, active0);
    }

    private final int jjMoveStringLiteralDfa2_0(long old0, long active0) {
        if (((active0 &= old0)) == 0L)
            return jjStartNfa_0(0, old0);
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_0(1, active0);
            return 2;
        }
        switch (curChar) {
            case 123:
                if ((active0 & 0x8L) != 0L)
                    return jjStopAtPos(2, 3);
                break;
            default :
                break;
        }
        return jjStartNfa_0(1, active0);
    }

    private final void jjCheckNAdd(int state) {
        if (jjrounds[state] != jjround) {
            jjstateSet[jjnewStateCnt++] = state;
            jjrounds[state] = jjround;
        }
    }

    private final void jjAddStates(int start, int end) {
        do {
            jjstateSet[jjnewStateCnt++] = jjnextStates[start];
        } while (start++ != end);
    }

    private final void jjCheckNAddTwoStates(int state1, int state2) {
        jjCheckNAdd(state1);
        jjCheckNAdd(state2);
    }

    private final void jjCheckNAddStates(int start, int end) {
        do {
            jjCheckNAdd(jjnextStates[start]);
        } while (start++ != end);
    }

    private final void jjCheckNAddStates(int start) {
        jjCheckNAdd(jjnextStates[start]);
        jjCheckNAdd(jjnextStates[start + 1]);
    }

    static final long[] jjbitVec0 = {
        0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
    };
    static final long[] jjbitVec2 = {
        0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
    };

    private final int jjMoveNfa_0(int startState, int curPos) {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 4;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (; ;) {
            if (++jjround == 0x7fffffff)
                ReInitRounds();
            if (curChar < 64) {
                long l = 1L << curChar;
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 2:
                        case 0:
                            kind = 1;
                            jjCheckNAdd(0);
                            break;
                        default :
                            break;
                    }
                } while (i != startsAt);
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 2:
                            if ((0xf7ffffffefffffffL & l) != 0L) {
                                if (kind > 1)
                                    kind = 1;
                                jjCheckNAdd(0);
                            } else if (curChar == 92) {
                                if (kind > 1)
                                    kind = 1;
                            }
                            if (curChar == 92)
                                jjstateSet[jjnewStateCnt++] = 1;
                            break;
                        case 0:
                            if ((0xf7ffffffefffffffL & l) == 0L)
                                break;
                            kind = 1;
                            jjCheckNAdd(0);
                            break;
                        case 1:
                            if (curChar == 123)
                                kind = 1;
                            break;
                        case 3:
                            if (curChar == 92 && kind > 1)
                                kind = 1;
                            break;
                        default :
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = (int) (curChar >> 8);
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 077);
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 2:
                        case 0:
                            if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                                break;
                            if (kind > 1)
                                kind = 1;
                            jjCheckNAdd(0);
                            break;
                        default :
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != 0x7fffffff) {
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }
            ++curPos;
            if ((i = jjnewStateCnt) == (startsAt = 4 - (jjnewStateCnt = startsAt)))
                return curPos;
            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStopStringLiteralDfa_1(int pos, long active0) {
        switch (pos) {
            default :
                return -1;
        }
    }

    private final int jjStartNfa_1(int pos, long active0) {
        return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
    }

    private final int jjStartNfaWithStates_1(int pos, int kind, int state) {
        jjmatchedKind = kind;
        jjmatchedPos = pos;
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            return pos + 1;
        }
        return jjMoveNfa_1(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_1() {
        switch (curChar) {
            case 34:
                return jjStartNfaWithStates_1(0, 14, 33);
            case 39:
                return jjStartNfaWithStates_1(0, 15, 34);
            case 46:
                return jjStopAtPos(0, 13);
            case 91:
                return jjStopAtPos(0, 16);
            case 93:
                return jjStopAtPos(0, 17);
            case 125:
                return jjStopAtPos(0, 4);
            default :
                return jjMoveNfa_1(0, 0);
        }
    }

    static final long[] jjbitVec3 = {
        0x1ff00000fffffffeL, 0xffffffffffffc000L, 0xffffffffL, 0x600000000000000L
    };
    static final long[] jjbitVec4 = {
        0x0L, 0x0L, 0x0L, 0xff7fffffff7fffffL
    };
    static final long[] jjbitVec5 = {
        0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
    };
    static final long[] jjbitVec6 = {
        0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffL, 0x0L
    };
    static final long[] jjbitVec7 = {
        0xffffffffffffffffL, 0xffffffffffffffffL, 0x0L, 0x0L
    };
    static final long[] jjbitVec8 = {
        0x3fffffffffffL, 0x0L, 0x0L, 0x0L
    };

    private final int jjMoveNfa_1(int startState, int curPos) {
        int[] nextStates;
        int startsAt = 0;
        jjnewStateCnt = 33;
        int i = 1;
        jjstateSet[0] = startState;
        int j, kind = 0x7fffffff;
        for (; ;) {
            if (++jjround == 0x7fffffff)
                ReInitRounds();
            if (curChar < 64) {
                long l = 1L << curChar;
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 33:
                            if ((0xffffffffffffdbffL & l) != 0L)
                                jjCheckNAddStates(0, 5);
                            if ((0xff000000000000L & l) != 0L)
                                jjCheckNAddStates(6, 11);
                            else if (curChar == 34) {
                                if (kind > 5)
                                    kind = 5;
                            }
                            if ((0xf000000000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 10;
                            break;
                        case 34:
                            if ((0xffffffffffffdbffL & l) != 0L)
                                jjCheckNAddStates(12, 17);
                            if ((0xff000000000000L & l) != 0L)
                                jjCheckNAddStates(18, 23);
                            else if (curChar == 39) {
                                if (kind > 5)
                                    kind = 5;
                            }
                            if ((0xf000000000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 25;
                            break;
                        case 0:
                            if ((0x3ff000000000000L & l) != 0L) {
                                if (kind > 12)
                                    kind = 12;
                                jjCheckNAdd(32);
                            } else if ((0x1800000000L & l) != 0L) {
                                if (kind > 8)
                                    kind = 8;
                                jjCheckNAdd(31);
                            } else if (curChar == 39)
                                jjCheckNAddStates(12, 17);
                            else if (curChar == 34)
                                jjCheckNAddStates(0, 5);
                            break;
                        case 1:
                            if ((0xffffffffffffdbffL & l) != 0L)
                                jjCheckNAddStates(0, 5);
                            break;
                        case 3:
                            if ((0x3ff000000000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 4;
                            break;
                        case 4:
                            if ((0x3ff000000000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 5;
                            break;
                        case 5:
                        case 8:
                            if ((0x3ff000000000000L & l) != 0L)
                                jjCheckNAdd(6);
                            break;
                        case 6:
                            if ((0x3ff000000000000L & l) != 0L)
                                jjCheckNAddStates(0, 5);
                            break;
                        case 9:
                            if ((0xf000000000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 10;
                            break;
                        case 10:
                            if ((0xff000000000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 11;
                            break;
                        case 11:
                            if ((0xff000000000000L & l) != 0L)
                                jjCheckNAddStates(0, 5);
                            break;
                        case 12:
                            if ((0xff000000000000L & l) != 0L)
                                jjCheckNAddStates(6, 11);
                            break;
                        case 13:
                            if (curChar == 34 && kind > 5)
                                kind = 5;
                            break;
                        case 14:
                            if ((0xff000000000000L & l) != 0L)
                                jjCheckNAddStates(24, 30);
                            break;
                        case 15:
                            if (curChar == 39)
                                jjCheckNAddStates(12, 17);
                            break;
                        case 16:
                            if ((0xffffffffffffdbffL & l) != 0L)
                                jjCheckNAddStates(12, 17);
                            break;
                        case 18:
                            if ((0x3ff000000000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 19;
                            break;
                        case 19:
                            if ((0x3ff000000000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 20;
                            break;
                        case 20:
                        case 23:
                            if ((0x3ff000000000000L & l) != 0L)
                                jjCheckNAdd(21);
                            break;
                        case 21:
                            if ((0x3ff000000000000L & l) != 0L)
                                jjCheckNAddStates(12, 17);
                            break;
                        case 24:
                            if ((0xf000000000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 25;
                            break;
                        case 25:
                            if ((0xff000000000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 26;
                            break;
                        case 26:
                            if ((0xff000000000000L & l) != 0L)
                                jjCheckNAddStates(12, 17);
                            break;
                        case 27:
                            if ((0xff000000000000L & l) != 0L)
                                jjCheckNAddStates(18, 23);
                            break;
                        case 28:
                            if (curChar == 39 && kind > 5)
                                kind = 5;
                            break;
                        case 29:
                            if ((0xff000000000000L & l) != 0L)
                                jjCheckNAddStates(31, 37);
                            break;
                        case 30:
                            if ((0x1800000000L & l) == 0L)
                                break;
                            if (kind > 8)
                                kind = 8;
                            jjCheckNAdd(31);
                            break;
                        case 31:
                            if ((0x3ff001000000000L & l) == 0L)
                                break;
                            if (kind > 8)
                                kind = 8;
                            jjCheckNAdd(31);
                            break;
                        case 32:
                            if ((0x3ff000000000000L & l) == 0L)
                                break;
                            if (kind > 12)
                                kind = 12;
                            jjCheckNAdd(32);
                            break;
                        default :
                            break;
                    }
                } while (i != startsAt);
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 33:
                            jjCheckNAddStates(0, 5);
                            if ((0x100000001000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 8;
                            else if ((0x20000000200000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 3;
                            break;
                        case 34:
                            jjCheckNAddStates(12, 17);
                            if ((0x100000001000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 23;
                            else if ((0x20000000200000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 18;
                            break;
                        case 0:
                        case 31:
                            if ((0x7fffffe87fffffeL & l) == 0L)
                                break;
                            if (kind > 8)
                                kind = 8;
                            jjCheckNAdd(31);
                            break;
                        case 1:
                            jjCheckNAddStates(0, 5);
                            break;
                        case 2:
                            if ((0x20000000200000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 3;
                            break;
                        case 3:
                            if ((0x7e0000007eL & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 4;
                            break;
                        case 4:
                            if ((0x7e0000007eL & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 5;
                            break;
                        case 5:
                        case 8:
                            if ((0x7e0000007eL & l) != 0L)
                                jjCheckNAdd(6);
                            break;
                        case 6:
                            if ((0x7e0000007eL & l) != 0L)
                                jjCheckNAddStates(0, 5);
                            break;
                        case 7:
                            if ((0x100000001000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 8;
                            break;
                        case 16:
                            jjCheckNAddStates(12, 17);
                            break;
                        case 17:
                            if ((0x20000000200000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 18;
                            break;
                        case 18:
                            if ((0x7e0000007eL & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 19;
                            break;
                        case 19:
                            if ((0x7e0000007eL & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 20;
                            break;
                        case 20:
                        case 23:
                            if ((0x7e0000007eL & l) != 0L)
                                jjCheckNAdd(21);
                            break;
                        case 21:
                            if ((0x7e0000007eL & l) != 0L)
                                jjCheckNAddStates(12, 17);
                            break;
                        case 22:
                            if ((0x100000001000000L & l) != 0L)
                                jjstateSet[jjnewStateCnt++] = 23;
                            break;
                        default :
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = (int) (curChar >> 8);
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 077);
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
                MatchLoop: do {
                    switch (jjstateSet[--i]) {
                        case 33:
                        case 1:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                                jjCheckNAddStates(0, 5);
                            break;
                        case 34:
                        case 16:
                            if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                                jjCheckNAddStates(12, 17);
                            break;
                        case 0:
                        case 31:
                            if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                                break;
                            if (kind > 8)
                                kind = 8;
                            jjCheckNAdd(31);
                            break;
                        default :
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != 0x7fffffff) {
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }
            ++curPos;
            if ((i = jjnewStateCnt) == (startsAt = 33 - (jjnewStateCnt = startsAt)))
                return curPos;
            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    static final int[] jjnextStates = {
        1, 2, 7, 9, 12, 13, 1, 2, 7, 9, 13, 14, 16, 17, 22, 24,
        27, 28, 16, 17, 22, 24, 28, 29, 1, 2, 7, 9, 12, 13, 14, 16,
        17, 22, 24, 27, 28, 29,
    };

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0:
                return ((jjbitVec2[i2] & l2) != 0L);
            default :
                if ((jjbitVec0[i1] & l1) != 0L)
                    return true;
                return false;
        }
    }

    private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0:
                return ((jjbitVec4[i2] & l2) != 0L);
            case 48:
                return ((jjbitVec5[i2] & l2) != 0L);
            case 49:
                return ((jjbitVec6[i2] & l2) != 0L);
            case 51:
                return ((jjbitVec7[i2] & l2) != 0L);
            case 61:
                return ((jjbitVec8[i2] & l2) != 0L);
            default :
                if ((jjbitVec3[i1] & l1) != 0L)
                    return true;
                return false;
        }
    }

    public static final String[] jjstrLiteralImages = {
        "", null, "\173", "\134\134\173", "\175", null, null, null, null, null, null,
        null, null, "\56", "\42", "\47", "\133", "\135", };
    public static final String[] lexStateNames = {
        "DEFAULT",
        "IN_EXPRESSION",
    };
    public static final int[] jjnewLexState = {
        -1, -1, 1, 1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    };
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[33];
    private final int[] jjstateSet = new int[66];
    protected char curChar;

    public NetUIELParserTokenManager(SimpleCharStream stream) {
        if (SimpleCharStream.staticFlag)
            throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
        input_stream = stream;
    }

    public NetUIELParserTokenManager(SimpleCharStream stream, int lexState) {
        this(stream);
        SwitchTo(lexState);
    }

    public void ReInit(SimpleCharStream stream) {
        jjmatchedPos = jjnewStateCnt = 0;
        curLexState = defaultLexState;
        input_stream = stream;
        ReInitRounds();
    }

    private final void ReInitRounds() {
        int i;
        jjround = 0x80000001;
        for (i = 33; i-- > 0;)
            jjrounds[i] = 0x80000000;
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        ReInit(stream);
        SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 2 || lexState < 0)
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
        else
            curLexState = lexState;
    }

    protected Token jjFillToken() {
        Token t = Token.newToken(jjmatchedKind);
        t.kind = jjmatchedKind;
        String im = jjstrLiteralImages[jjmatchedKind];
        t.image = (im == null) ? input_stream.GetImage() : im;
        t.beginLine = input_stream.getBeginLine();
        t.beginColumn = input_stream.getBeginColumn();
        t.endLine = input_stream.getEndLine();
        t.endColumn = input_stream.getEndColumn();
        return t;
    }

    int curLexState = 0;
    int defaultLexState = 0;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public Token getNextToken() {
        int kind;
        Token specialToken = null;
        Token matchedToken;
        int curPos = 0;

        EOFLoop :
        for (; ;) {
            try {
                curChar = input_stream.BeginToken();
            } catch (java.io.IOException e) {
                jjmatchedKind = 0;
                matchedToken = jjFillToken();
                return matchedToken;
            }

            switch (curLexState) {
                case 0:
                    jjmatchedKind = 0x7fffffff;
                    jjmatchedPos = 0;
                    curPos = jjMoveStringLiteralDfa0_0();
                    break;
                case 1:
                    jjmatchedKind = 0x7fffffff;
                    jjmatchedPos = 0;
                    curPos = jjMoveStringLiteralDfa0_1();
                    break;
            }
            if (jjmatchedKind != 0x7fffffff) {
                if (jjmatchedPos + 1 < curPos)
                    input_stream.backup(curPos - jjmatchedPos - 1);
                matchedToken = jjFillToken();
                if (jjnewLexState[jjmatchedKind] != -1)
                    curLexState = jjnewLexState[jjmatchedKind];
                return matchedToken;
            }
            int error_line = input_stream.getEndLine();
            int error_column = input_stream.getEndColumn();
            String error_after = null;
            boolean EOFSeen = false;
            try {
                input_stream.readChar();
                input_stream.backup(1);
            } catch (java.io.IOException e1) {
                EOFSeen = true;
                error_after = curPos <= 1 ? "" : input_stream.GetImage();
                if (curChar == '\n' || curChar == '\r') {
                    error_line++;
                    error_column = 0;
                } else
                    error_column++;
            }
            if (!EOFSeen) {
                input_stream.backup(1);
                error_after = curPos <= 1 ? "" : input_stream.GetImage();
            }
            throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
        }
    }

}
