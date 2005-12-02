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
 * An exception thrown when an error occurs during an expression update.
 */
public class ExpressionUpdateException
        extends ExpressionEvaluationException {

    /**
     * Construct an ExpressionUpdateException.
     */
    public ExpressionUpdateException() {
        super();
    }

    /**
     * Construct an ExpressionUpdateException with the given message, failed expression, expected updated value,
     * and expression that was used to perform the update.
     *
     * @param message    a String containing the text of the exception message
     * @param expression the expression whose update failed
     * @param cause      the <code>Throwable</code> that is wrapped as the cause of this exception
     */
    public ExpressionUpdateException(String message, String expression, Throwable cause) {
        super(message, expression, cause);
    }

    /**
     * Construct an ExpressionUpdateException with the given message, failed expression, expected updated value,
     * and expression that was used to perform the update.
     *
     * @param message    a String containing the text of the exception message
     * @param expression the expression whose update failed
     */
    public ExpressionUpdateException(String message, String expression) {
        super(message, expression);
    }

    /**
     * Get the value that should have been written to the object
     * referenced by the failed update expression.  This method is deprecated
     * and will always return null because of the security risk associated
     * with making the value available.
     *
     * @return the failed update value
     * @deprecated
     */
    public Object getUpdateValue() {
        return null;
    }
}
