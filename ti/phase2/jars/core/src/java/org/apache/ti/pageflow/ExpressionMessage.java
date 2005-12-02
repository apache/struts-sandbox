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

import org.apache.ti.core.ActionMessage;
import org.apache.ti.pageflow.internal.InternalConstants;

/**
 * Extension of the base Struts ActionMessage; instead of retrieving messages and their arguments from message
 * resources, it calculates them by evaluating JSP 2.0-style expressions (or, in the degenerate case, from hardcoded
 * strings).
 */
public class ExpressionMessage
        extends ActionMessage {

    /**
     * Constructor, using an array for the message arguments.
     *
     * @param expression            the JSP 2.0-style expression (e.g., <code>${pageFlow.myProperty}</code>) or literal string
     *                              that will be used as the message.
     * @param messageArgExpressions an array of JSP 2.0-style expressions <i>or</i> raw Objects to be used as arguments
     *                              to the message.  Expressions are evaluated; all other Objects are passed as-is.
     */
    public ExpressionMessage(String expression, Object[] messageArgExpressions) {
        super(InternalConstants.MESSAGE_IS_EXPRESSION_PREFIX + expression, prefixArgs(messageArgExpressions));
    }

    /**
     * Constructor, for a message without message arguments.
     *
     * @param expression the JSP 2.0-style expression (e.g., <code>${pageFlow.myProperty}</code>) or literal string
     *                   that will be used as the message.
     */
    public ExpressionMessage(String expression) {
        this(expression, null);
    }

    /**
     * Constructor, for a message with a single argument.
     *
     * @param expression           the JSP 2.0-style expression (e.g., <code>${pageFlow.myProperty}</code>) or literal string
     *                             that will be used as the message.
     * @param messageArgExpression a JSP 2.0-style expression <i>or</i> raw Object to be used the argument
     *                             to the message.  Expressions are evaluated; all other Objects are passed as-is.
     */
    public ExpressionMessage(String expression, Object messageArgExpression) {
        this(expression, new Object[]{messageArgExpression});
    }

    /**
     * Constructor, for a message with two arguments.
     *
     * @param expression            the JSP 2.0-style expression (e.g., <code>${pageFlow.myProperty}</code>) or literal string
     *                              that will be used as the message.
     * @param messageArgExpression1 a JSP 2.0-style expression <i>or</i> raw Object to be used the first argument
     *                              to the message.  Expressions are evaluated; all other Objects are passed as-is.
     * @param messageArgExpression2 a JSP 2.0-style expression <i>or</i> raw Object to be used the second argument
     *                              to the message.  Expressions are evaluated; all other Objects are passed as-is.
     */
    public ExpressionMessage(String expression, Object messageArgExpression1, Object messageArgExpression2) {
        this(expression, new Object[]{messageArgExpression1, messageArgExpression2});
    }

    /**
     * Constructor, for a message with two arguments.
     *
     * @param expression            the JSP 2.0-style expression (e.g., <code>${pageFlow.myProperty}</code>) or literal string
     *                              that will be used as the message.
     * @param messageArgExpression1 a JSP 2.0-style expression <i>or</i> raw Object to be used the first argument
     *                              to the message.  Expressions are evaluated; all other Objects are passed as-is.
     * @param messageArgExpression2 a JSP 2.0-style expression <i>or</i> raw Object to be used the second argument
     *                              to the message.  Expressions are evaluated; all other Objects are passed as-is.
     * @param messageArgExpression3 a JSP 2.0-style expression <i>or</i> raw Object to be used the third argument
     *                              to the message.  Expressions are evaluated; all other Objects are passed as-is.
     */
    public ExpressionMessage(String expression, Object messageArgExpression1, Object messageArgExpression2,
                             Object messageArgExpression3) {
        this(expression, new Object[]{messageArgExpression1, messageArgExpression2, messageArgExpression3});
    }

    private static Object[] prefixArgs(Object[] messageArgExpressions) {
        if (messageArgExpressions == null) return null;

        Object[] ret = new Object[messageArgExpressions.length];

        for (int i = 0; i < messageArgExpressions.length; i++) {
            ret[i] = InternalConstants.MESSAGE_IS_EXPRESSION_PREFIX + messageArgExpressions[i];
        }

        return ret;
    }
}
