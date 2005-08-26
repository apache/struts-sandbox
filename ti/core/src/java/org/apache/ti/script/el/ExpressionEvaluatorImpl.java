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
import org.apache.ti.script.ExpressionEvaluationException;
import org.apache.ti.script.ExpressionEvaluator;
import org.apache.ti.script.ExpressionUpdateException;
import org.apache.ti.script.IllegalExpressionException;
import org.apache.ti.script.el.util.ParseUtils;
import org.apache.ti.util.logging.Logger;

import javax.servlet.jsp.el.VariableResolver;

/**
 *
 */
public class ExpressionEvaluatorImpl
        implements ExpressionEvaluator {

    private static final Logger LOGGER = Logger.getInstance(ExpressionEvaluatorImpl.class);
    private static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();

    public static class NetUIELEngineFactory
            extends org.apache.ti.script.ExpressionEngineFactory {

        public ExpressionEvaluator getInstance() {
            return new ExpressionEvaluatorImpl();
        }
    }

    public Object evaluateStrict(String expression, VariableResolver variableResolver)
            throws ExpressionEvaluationException {
        NetUIReadVariableResolver vr = null;
        try {
            /* todo: is there compelling enough reson to keep this code here? */
            vr = new NetUIReadVariableResolver(variableResolver);

            return ParseUtils.evaluate(expression, vr);
        } catch (Exception e) {
            if (DEBUG_ENABLED)
                LOGGER.debug("Expression evaluation failed in NetUIEL.  Cause: " + e, e);

            Exception act = e;

            String contextStr = ParseUtils.getContextString(vr.getAvailableVariables());
            String msg = "Caught exception when evaluating expression \"" + expression + "\" with available binding contexts " +
                    contextStr + ". Root cause: " + ParseUtils.getRootCause(act).toString();

            if (LOGGER.isErrorEnabled())
                LOGGER.error(msg, act);

            throw new ExpressionEvaluationException(msg, expression, act);
        }
    }

    public void update(String expression, Object value, VariableResolver variableResolver, boolean requestParameter)
            throws ExpressionUpdateException {
        assert variableResolver instanceof NetUIVariableResolver;

        NetUIVariableResolver vr = (NetUIVariableResolver) variableResolver;

        try {
            if (DEBUG_ENABLED)
                LOGGER.debug("Update expression \"" + expression + "\"");

            ParseUtils.update(expression, value, vr);
        } catch (Exception e) {
            if (DEBUG_ENABLED)
                LOGGER.debug("Expression update failed in NetUIEL.  Cause: " + e, e);

            String contextStr = ParseUtils.getContextString(vr.getAvailableVariables());
            String msg = "Exception when attempting to update the expression \"" + expression + "\" with available binding contexts " +
                    contextStr + ". Root cause: " + ParseUtils.getRootCause(e).toString();

            if (LOGGER.isErrorEnabled())
                LOGGER.error(msg, e);

            ExpressionUpdateException eee = new ExpressionUpdateException(msg, expression, e);
            eee.setLocalizedMessage(msg);

            throw eee;
        }
    }

    /* todo: fix the lookup index to be Object */
    public String changeContext(String expression, String oldContext, String newContext, int lookupIndex)
            throws ExpressionEvaluationException {
        try {
            ParsedExpression pe = ParseUtils.parse(expression);
            return pe.changeContext(oldContext, newContext, new Integer(lookupIndex));
        } catch (Exception e) {
            String msg = "Error when trying to replace old context '" + oldContext + "' with new context '" +
                    newContext + "' and index '" + lookupIndex + "': " + ParseUtils.getRootCause(e).toString();

            if (LOGGER.isErrorEnabled()) LOGGER.error(msg, e);

            throw new ExpressionEvaluationException(msg, e);
        }
    }

    public String qualify(String contextName, String expression)
            throws ExpressionEvaluationException {
        try {
            ParsedExpression pe = ParseUtils.parse(expression);
            return pe.qualify(contextName);
        } catch (Exception e) {
            String msg = "Error when trying to create an expression in namespace \"" + contextName + "\" with fragment \"" +
                    expression + "\".  Root cause: " + ParseUtils.getRootCause(e).toString();

            throw new ExpressionEvaluationException(msg, e);
        }
    }

    public boolean isExpression(String expression) {
        try {
            ParsedExpression pe = ParseUtils.parse(expression);
            return pe.isExpression();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Exception parsing expression \"" + expression + "\".  Cause: " +
                        ParseUtils.getRootCause(e).toString(), e);

            if (e instanceof IllegalExpressionException)
                throw (IllegalExpressionException) e;
            else if (e instanceof ExpressionParseException)
                throw new IllegalExpressionException(e);
            else
                return false;
        }
    }

    public boolean containsExpression(String expression) {
        if (expression == null) return false;

        try {
            ParsedExpression pe = ParseUtils.parse(expression);
            assert pe != null;
            return pe.containsExpression();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Exception checking for expressions in \"" + expression + "\"", e);

            return false;
        }
    }

    public Expression parseExpression(String expression) {
        if (isExpression(expression)) {
            ParsedExpression pe = ParseUtils.parse(expression);
            assert pe != null;
            return pe.getAtomicExpressionTerm();
        } else
            throw new IllegalExpressionException("The expression \"" + expression + "\" can not be parsed as it is not an atomic expression.");
    }
}
