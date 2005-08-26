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
package org.apache.ti.util.type;

/**
 * A TypeConversionException is thrown when an error has occurred performing
 * a type conversion in the {@link TypeUtils} class.
 */
public class TypeConversionException
        extends RuntimeException {

    private String _localizedMessage = null;

    /**
     * Construct a TypeConversionException.
     */
    public TypeConversionException() {
        super();
    }

    /**
     * Construct a TypeConversionException.
     *
     * @param message message describing the error
     */
    public TypeConversionException(String message) {
        super(message);
    }

    /**
     * Construct a TypeConversionException.
     *
     * @param cause Throwable related to the cause of this exception
     */
    public TypeConversionException(Throwable cause) {
        super(cause);
    }

    /**
     * Construct a TypeConversionException.
     *
     * @param message message describing the error
     * @param cause   Throwable related to the cause of this exception
     */
    public TypeConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Set a localized message describing the cause of this exception.
     *
     * @param localizedMessage a localized message
     */
    public void setLocalizedMessage(String localizedMessage) {
        _localizedMessage = localizedMessage;
    }

    /**
     * Get a localized message describing the cause of this exception.
     *
     * @return a localized message string
     */
    public String getLocalizedMessage() {
        return (_localizedMessage != null ? _localizedMessage : getMessage());
    }
}
