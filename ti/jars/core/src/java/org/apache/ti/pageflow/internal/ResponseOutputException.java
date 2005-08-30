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
 * Unchecked exception thrown in situations where output would normally be written to the response,
 * but when the runtime is configured to throw an exception instead.
 */
public class ResponseOutputException extends RuntimeException {

    public ResponseOutputException(String msg) {
        super(msg);
    }

    public ResponseOutputException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
