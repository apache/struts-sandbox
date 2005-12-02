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

import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */
public class ParsedExpression {

    private static Logger LOGGER = Logger.getInstance(ParsedExpression.class);

    /* cache the debug status; this needs to be _fast_ */
    private static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();
    private static final String EMPTY_STRING = "";

    private ArrayList/*<Term>*/ _terms = new ArrayList/*<Term>*/(3);
    private boolean _isExpression = false;
    private boolean _containsExpression = false;
    private ExpressionTerm _atomicExpression = null;
    private Term[] _termArray = null;
    private String _exprStr;

    public void seal() {
        _termArray = new Term[_terms.size()];

        InternalStringBuilder buf = new InternalStringBuilder();
        for (int i = 0; i < _terms.size(); i++) {
            Term t = (Term) _terms.get(i);
            t.seal();

            if (t instanceof ExpressionTerm) {
                if (_terms.size() == 1) {
                    _atomicExpression = (ExpressionTerm) _terms.get(0);
                    _isExpression = true;
                }
                _containsExpression = true;
            } else if (t instanceof LiteralTerm) {
                String lit = t.getExpressionString();
                if (lit != null && lit.indexOf("{") > -1)
                    _containsExpression = true;
            }

            _termArray[i] = (Term) _terms.get(i);
            buf.append(t.getExpressionString());
        }
        _exprStr = buf.toString();
    }

    public boolean isExpression() {
        return _isExpression;
    }

    public boolean containsExpression() {
        return _containsExpression;
    }

    public void addTerm(Term term) {
        _terms.add(term);
    }

    public int getTokenCount() {
        return _terms.size();
    }

    public Term getTerm(int i) {
        assert _termArray != null;
        assert i > 0 && i < _termArray.length;

        return _termArray[i];
    }

    public ExpressionTerm getAtomicExpressionTerm() {
        return _atomicExpression;
    }

    public Object evaluate(NetUIVariableResolver vr) {
        if (DEBUG_ENABLED)
            LOGGER.debug("evaluate expression: " + _exprStr);

        if (_isExpression) {
            if (DEBUG_ENABLED)
                LOGGER.debug("atoimc expression");

            return _atomicExpression.evaluate(vr);
        } else {
            InternalStringBuilder buf = new InternalStringBuilder();

            for (int i = 0; i < _terms.size(); i++) {
                if (DEBUG_ENABLED)
                    LOGGER.debug("term[" + i + "]: " + _termArray[i].getClass().getName() +
                            " expression string: " + _termArray[i].getExpressionString());

                Object result = _termArray[i].evaluate(vr);

                buf.append(result != null ? result.toString() : EMPTY_STRING);
            }

            return buf.toString();
        }
    }

    public void update(Object value, NetUIVariableResolver vr) {
        if (!_isExpression) {
            String msg = "The expression can not be updated because it is not atomic.";
            if (LOGGER.isErrorEnabled())
                LOGGER.error(msg);
            throw new RuntimeException(msg);
        }

        _atomicExpression.update(value, vr);
    }

    public String changeContext(String oldContext, String newContext, Object index) {
        if (!_isExpression) {
            String msg = "The expression can not change context because it is not atomic.";

            if (LOGGER.isErrorEnabled())
                LOGGER.error(msg);

            throw new RuntimeException(msg);
        }

        return _atomicExpression.changeContext(oldContext, newContext, index);
    }

    public String qualify(String contextName) {
        /* todo: could check to see if first term is literal */

        return "{" + contextName + "." + getExpressionString() + "}";
    }

    // only call on atomic expressions
    public String getExpressionString() {
        if (_isExpression)
            return _atomicExpression.getExpressionString();
        else
            return _exprStr;
    }

    public String toString() {
        InternalStringBuilder builder = new InternalStringBuilder();
        for (Iterator i = _terms.iterator(); i.hasNext();) {
            Term term = (Term) i.next();
            builder.append(term.toString());
        }
        return builder.toString();
    }
}
