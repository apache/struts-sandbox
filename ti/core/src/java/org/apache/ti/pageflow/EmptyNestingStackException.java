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


/**
 * Exception that occurs when the user invokes an action in a nested page flow that uses a
 * <code>{@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}(</code>...<code>
 * returnAction="</code><i>action-name-in-calling-pageflow</i><code>")</code>
 * annotation, but there is no calling page flow. This can happen in iterative
 * development mode when you have modified files and caused the web application to be redeployed,
 * or when the session expires.
 */
public class EmptyNestingStackException extends FlowControllerException {

    public EmptyNestingStackException(FlowController fc) {
        super(fc);
    }

    protected Object[] getMessageArgs() {
        return new Object[]{getActionName(), getFlowControllerURI()};
    }

    protected String[] getMessageParts() {
        return new String[]{"Empty nesting stack for returned action ", " from Page Flow ", "."};
    }

    /**
     * Tell whether the root cause may be session expiration in cases where the requested session ID is different than
     * the actual session ID.  In this case, the answer is <code>true</code>.
     */
    public boolean causeMayBeSessionExpiration() {
        return true;
    }
}
