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
 * Exception that occurs when the
 * <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousAction previousAction}</code>
 * attribute is used on a {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}, a
 * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}, or a
 * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward},
 * but the previous action was the same as the current action (an infinite loop).
 */
public class InfiniteReturnToActionException extends FlowControllerException {

    public InfiniteReturnToActionException(FlowController fc) {
        super(fc);
    }

    protected Object[] getMessageArgs() {
        return new Object[]{getActionName(), getFlowControllerURI()};
    }

    protected String[] getMessageParts() {
        return new String[]
        {
            "Infinite loop of returnTo=\"action\" for action ",
            " in Page Flow ",
            "."
        };
    }

    /**
     * Tell whether the root cause may be session expiration in cases where the requested session ID is different than
     * the actual session ID.  In this case, the answer is <code>false</code>.
     */
    public boolean causeMayBeSessionExpiration() {
        return false;
    }
}
