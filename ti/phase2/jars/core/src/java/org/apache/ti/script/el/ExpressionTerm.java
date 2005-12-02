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
package org.apache.ti.script.el;

import org.apache.ti.script.Expression;
import org.apache.ti.script.el.tokens.ArrayIndexToken;
import org.apache.ti.script.el.tokens.ContextToken;
import org.apache.ti.script.el.tokens.ExpressionToken;
import org.apache.ti.script.el.tokens.IdentifierToken;
import org.apache.ti.script.el.tokens.MapKeyToken;
import org.apache.ti.script.el.util.BindingContext;
import org.apache.ti.script.el.util.ParseUtils;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class ExpressionTerm
        extends Expression
        implements Term {

    private static Logger LOGGER = Logger.getInstance(ExpressionTerm.class);

    private List _tokens = null;
    private String _exprStr = null;
    private ContextToken _context = null;
    private ExpressionToken[] _tokenArray = null;
    private List _noModTokens = null;

    public ExpressionTerm() {
        super();
        _tokens = new ArrayList();
    }

    public void seal() {
        _context = (ContextToken) _tokens.get(0);
        _tokenArray = new ExpressionToken[_tokens.size()];

        InternalStringBuilder buf = new InternalStringBuilder();
        for (int i = 0; i < _tokens.size(); i++) {
            buf.append(((ExpressionToken) _tokens.get(i)).getTokenString());
            _tokenArray[i] = (ExpressionToken) _tokens.get(i);
        }

        _exprStr = buf.toString();

        _noModTokens = Collections.unmodifiableList(_tokens);
    }

    public String getContext() {
        return _context.getName();
    }

    public List getTokens() {
        return _noModTokens;
    }

    public String getExpression(int start) {
        if (start >= _tokens.size())
            throw new IllegalStateException("The index \"" + start + "\" is an invalid reference into an expression with \"" +
                    _tokens.size() + "\" _tokens.");

        boolean needDot = true;
        InternalStringBuilder buf = new InternalStringBuilder();
        buf.append("{");
        for (int i = start; i < _tokens.size(); i++) {
            ExpressionToken tok = (ExpressionToken) _tokens.get(i);
            if (tok instanceof ArrayIndexToken) {
                buf.append(tok.getTokenString());
                needDot = false;
            } else if (tok instanceof IdentifierToken) {
                if (needDot && i != start) buf.append(".");
                buf.append(tok.toString());
                needDot = true;
            } else if (tok instanceof MapKeyToken) {
                buf.append(tok.getTokenString());
                needDot = false;
            }
        }
        buf.append("}");
        return buf.toString();
    }

    public void addToken(ExpressionToken token) {
        _tokens.add(token);
    }

    public Iterator getExpressionTokens() {
        return _tokens.iterator();
    }

    public int getTokenCount() {
        return _tokenArray.length;
    }

    public ExpressionToken getToken(int index) {
        // @TODO: error checking
        return _tokenArray[index];
    }

    public String getExpressionString() {
        return _exprStr;
    }

    public Object evaluate(NetUIVariableResolver vr) {
        return _evaluate(_tokens.size(), vr);
    }

    public void update(Object newValue, NetUIVariableResolver vr) {
        // find leaf
        Object branch = _evaluate(_tokens.size() - 1, vr);

        ExpressionToken token = _tokenArray[_tokens.size() - 1];

        if (LOGGER.isDebugEnabled()) LOGGER.debug("Update leaf token: " + token + " on object: " + branch);

        // apply value
        token.update(branch, newValue);
    }

    /* todo: perf. this could be done more effectively / efficiently */
    public String changeContext(String oldContext, String newContext, Object index) {
        String thisExpr = getExpressionString();

        if (LOGGER.isDebugEnabled()) LOGGER.debug("oldContext: " + oldContext + " newContext: " + newContext + " thisExpr: " + thisExpr);

        // needs to be checked for atomicity
        ParsedExpression pe = ParseUtils.parse(newContext);

        if (!pe.isExpression()) {
            String msg = "The expression can not be qualified into new _context because the new _context is not atomic.";
            if (LOGGER.isErrorEnabled()) LOGGER.error(msg);
            throw new RuntimeException(msg);
        }

        // this isn't a failure; it just means that there isn't anything else to replace
        if (!thisExpr.startsWith(oldContext)) {
            return "{" + thisExpr + "}";
        }

        if (index instanceof Integer && ((Integer) index).intValue() > 32767) {
            String msg = "Can not create an indexed expression with an array index greater than the Java array limit for the expression \"" +
                    thisExpr + "\"";

            if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
            throw new RuntimeException(msg);
        }

        String ctxStr = pe.getExpressionString();

        ctxStr = ctxStr + "[" + index + "]";

        if (LOGGER.isDebugEnabled()) LOGGER.debug("thisExpr: " + thisExpr + " ctxStr: " + ctxStr);

        thisExpr = thisExpr.replaceFirst(oldContext, ctxStr);

        InternalStringBuilder buf = new InternalStringBuilder();
        buf.append("{");
        buf.append(thisExpr);
        buf.append("}");

        return buf.toString();
    }

    public String qualify(String contextName) {
        InternalStringBuilder buf = new InternalStringBuilder();
        buf.append("{");
        buf.append(contextName);
        buf.append(".");
        buf.append(getExpressionString());
        buf.append("}");

        return buf.toString();
    }

    public String toString() {
        InternalStringBuilder buf = new InternalStringBuilder();
        buf.append("ExpressionTerm:\n");
        for (int i = 0; i < _tokens.size(); i++) {
            buf.append("  " + _tokens.get(i).toString() + "\n");
        }
        return buf.toString();
    }

    private final Object _evaluate(int index, NetUIVariableResolver vr) {
        Object result = null;

        if (_tokens.size() == 1) {
            if (LOGGER.isDebugEnabled()) LOGGER.debug("found single term expression");

            result = vr.resolveVariable(_context.getName());

            if (result != null && result instanceof BindingContext) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("result is of type BindingContext; return type: " + (((BindingContext) result).unwrap().getClass()));

                return ((BindingContext) result).unwrap();
            } else
                return result;
        } else {
            for (int i = 0; i < index; i++) {
                if (i == 0) {
                    result = vr.resolveVariable(_context.getName());
                } else
                    result = _tokenArray[i].evaluate(result);
            }

            return result;
        }
    }
}
