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

import org.apache.ti.util.Bundle;


/**
 * Exception that occurs when the current action method does not accept the type of form bean passed in the
 * {@link Forward} to the action.  This may happen when control is returned from a nested page flow, with a specified
 * form bean type
 * (<code>outputFormBean</code> or <code>outputFormBeanType</code> set on
 * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward},
 * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}, or
 * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward})
 * but no action in the calling page flow accepts that form bean type.
 */
public class NoMatchingActionMethodException extends FlowControllerException {

    private String _formClassName;


    public NoMatchingActionMethodException(Object form, FlowController fc) {
        super(fc);
        _formClassName =
                form != null ? form.getClass().getName() : Bundle.getString("PageFlow_NoFormString");
    }

    public String getFormClassName() {
        return _formClassName;
    }

    protected Object[] getMessageArgs() {
        return new Object[]{getActionName(), _formClassName, getFlowControllerURI()};
    }

    protected String[] getMessageParts() {
        return new String[]
        {
            "Could not find matching action method for action=", ", form=", " on Page Flow ", "."
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
