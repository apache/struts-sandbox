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
package org.apache.ti.script;

// java imports

// internal imports

// external imports

/**
 * The most general exception thrown when an error occurs in evaluating
 * an expression.
 */
public class ExpressionEvaluationException
        extends Exception {

    private String expression = null;
    private String[] contexts = null;
    private String localizedMessage = null;

    /**
     * Construct an ExpressionEvaluationException.
     */
    public ExpressionEvaluationException() {
        super();
    }

    /**
     * Construct an ExpressionEvaluationException with the given message and
     * the failed expression.
     *
     * @param message    a String containing the text of the exception message
     * @param expression the expression whose evaluation failed
     */
    public ExpressionEvaluationException(String message, String expression) {
        super(message);
        this.expression = expression;
    }

    /**
     * Construct an ExpressionEvaluationException with the given message,
     * the failed expression, and cause.
     *
     * @param message    a String containing the text of the exception message
     * @param expression the expression whose evaluation failed
     * @param cause      a <code>Throwable</code> that is wrapped by this exception
     */
    public ExpressionEvaluationException(String message, String expression, Throwable cause) {
        super(message, cause);
        this.expression = expression;
    }

    /**
     * Construct a ExpressionEvaluationException with the given <code>message</code> and <code>cause</code>.
     *
     * @param expression a String containing the text of the exception message
     * @param cause      a <code>Throwable</code> that is wrapped by this exception
     */
    public ExpressionEvaluationException(String expression, Throwable cause) {
        super(cause);
        this.expression = expression;
    }

    /**
     * Get the expression whose failed evaluation cause this exception to be thrown.
     *
     * @return the expression that caused the problem
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Set the set of top-level contexts that were available at the time
     * that the expression failed.
     *
     * @param contexts the list of available contexts.
     */
    public void setAvailableContexts(String[] contexts) {
        this.contexts = contexts;
    }

    /**
     * Get the top-level contexts that were available at the time that the
     * expression failed.
     *
     * @return the contexts that were available at the time the expression was evaluated or <code>null</code>
     *         if the contexts were not set.
     */
    public String[] getAvailableContexts() {
        return contexts;
    }

    public void setLocalizedMessage(String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }

    public String getLocalizedMessage() {
        return localizedMessage;
    }
}
