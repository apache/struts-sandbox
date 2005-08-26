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
 * An exception thrown when an expression is written in an illegal syntax.
 */
public class IllegalExpressionException
        extends RuntimeExpressionException {

    private String expression = null;

    /**
     * Construct an IllegalExpressionException.
     */
    public IllegalExpressionException() {
        super();
    }

    /**
     * Construct an IllegalExpressionException with the given message.
     *
     * @param message a String containing the text of the exception message
     */
    public IllegalExpressionException(String message) {
        super(message);
    }

    /**
     * Construct an IllegalExpressionException with the given cause
     *
     * @param cause a <code>Throwable<code> that caused this exception to be thrown
     */
    public IllegalExpressionException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct an IllegalExpressionException with the given <code>message</code> and <code>cause</code>.
     *
     * @param message a String containing the text of the exception message
     * @param cause   a <code>Throwable</code> that caused this exception to be thrown
     */
    public IllegalExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construct an IllegalExpressionException with the given <code>message</code> and the malformed expression.
     *
     * @param message    a String containing the text of this exception message
     * @param expression the expression that was malformed and caused this exception to be thrown
     */
    public IllegalExpressionException(String message, String expression) {
        this(message);
        this.expression = expression;
    }

    /**
     * Construct an IllegalExpressionException with the given <code>message</code>, the malformed expression, and the <code>cause</code>.
     *
     * @param message    a String containing the text of this exception message
     * @param expression the expression that was malformed and caused this exception to be thrown
     * @param cause      a <code>Throwable</code> that caused this exception to be thrown
     */
    public IllegalExpressionException(String message, String expression, Throwable cause) {
        this(message, cause);
        this.expression = expression;
    }

    /**
     * Get the malformed expression.
     *
     * @return the malformed expression
     */
    public String getExpression() {
        return expression;
    }
}

