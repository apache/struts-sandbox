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

import org.apache.ti.pageflow.FlowController;
import org.apache.ti.pageflow.FlowControllerException;
import org.apache.ti.pageflow.xwork.NavigateToResult;

public abstract class NavigateToException extends FlowControllerException {

    private String _returnToType;


    protected NavigateToException(NavigateToResult result, FlowController fc) {
        super(fc);
        _returnToType = result.getNavigateToAsString();
    }

    public String getReturnToType() {
        return _returnToType;
    }

    protected Object[] getMessageArgs() {
        return new Object[]{_returnToType, getActionName(), getFlowControllerURI()};
    }

    protected abstract String[] getMessageParts();

    /**
     * Tell whether the root cause may be session expiration in cases where the requested session ID is different than
     * the actual session ID.  In this case, the answer is <code>true</code>.
     */
    public boolean causeMayBeSessionExpiration() {
        return true;
    }
}
