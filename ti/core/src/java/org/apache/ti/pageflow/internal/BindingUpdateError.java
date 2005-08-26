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
package org.apache.ti.pageflow.internal;


/**
 * Encapsulation of an error that happens when a databinding expression is applied on POST.
 */
public class BindingUpdateError implements java.io.Serializable {

    private String _expression;
    private String _message;
    private Throwable _cause;

    /**
     * Constructor to initialize all values.
     *
     * @param expression the expression associated with this error.
     * @param message    the error message.
     * @param cause      the Throwable that caused the error.
     */
    public BindingUpdateError(String expression, String message, Throwable cause) {
        _expression = expression;
        _message = message;
        _cause = cause;
    }

    /**
     * Get the expression associated with this error.
     *
     * @return a String containing the expression associated with this error.
     */
    public String getExpression() {
        return _expression;
    }

    /**
     * Set the expression associated with this error.
     *
     * @param expression a String containing the expression associated with this error.
     */
    public void setExpression(String expression) {
        _expression = expression;
    }

    /**
     * Get the error message.
     *
     * @return a String containing the error message.
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Set the error message.
     *
     * @param message a String containing the error message.
     */
    public void setMessage(String message) {
        _message = message;
    }

    /**
     * Get the cause of the error.
     *
     * @return the Throwable that caused the error.
     */
    public Throwable getCause() {
        return _cause;
    }

    /**
     * Set the cause of the error.
     *
     * @param cause the Throwable that caused the error.
     */
    public void setCause(Throwable cause) {
        _cause = cause;
    }
}
