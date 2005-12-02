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
 * Exception thrown by {@link Forward} when its name does not resolve to one defined by a
 * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward} annotation in the current action's
 * {@link org.apache.ti.pageflow.annotations.ti.action &#64;ti.action} annotation.
 */
public class UnresolvableForwardException extends FlowControllerException {

    private String _forwardName;

    /**
     * Constructor.
     *
     * @param forwardName the name of the unresolvable {@link Forward}.
     */
    public UnresolvableForwardException(String forwardName, FlowController fc) {
        super(fc);
        _forwardName = forwardName;
    }

    /**
     * Get the name of the unresolvable {@link Forward}.
     *
     * @return a String that is the name of the unresolvable {@link Forward}.
     */
    public String getForwardName() {
        return _forwardName;
    }

    /**
     * Set the name of the unresolvable {@link Forward}.
     *
     * @param forwardName a String that is the name of the unresolvable {@link Forward}.
     */
    public void setForwardName(String forwardName) {
        _forwardName = forwardName;
    }

    protected Object[] getMessageArgs() {
        return new Object[]{_forwardName, getActionName(), getFlowControllerURI()};
    }

    public String[] getMessageParts() {
        return new String[]
        {
            "Unable to find a forward named \"", "\" on action ", " in Page Flow ", "."
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
