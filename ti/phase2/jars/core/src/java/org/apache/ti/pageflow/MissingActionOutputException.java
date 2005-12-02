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
 * Exception that occurs when a required action output is missing from a {@link Forward}.
 */
public class MissingActionOutputException
        extends FlowControllerException {

    private String _actionOutputName;
    private String _forwardName;

    public MissingActionOutputException(FlowController flowController, String actionOutputName,
                                        String forwardName) {
        super(flowController);
        _actionOutputName = actionOutputName;
        _forwardName = forwardName;
    }

    protected Object[] getMessageArgs() {
        return new Object[]{_actionOutputName, _forwardName, getActionName(), getFlowControllerURI()};
    }

    protected String[] getMessageParts() {
        return new String[]{"The required action output \"", "\" was not present on forward \"",
                            "\" (action ", " in Page Flow ", ")."};
    }

    public String getActionOutputName() {
        return _actionOutputName;
    }

    public String getForwardName() {
        return _forwardName;
    }

    /**
     * Tell whether the root cause may be session expiration in cases where the requested session ID is different than
     * the actual session ID.  In this case, the answer is <code>false</code>.
     */
    public boolean causeMayBeSessionExpiration() {
        return false;
    }
}
