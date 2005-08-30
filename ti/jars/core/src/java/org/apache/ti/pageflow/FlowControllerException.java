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

import org.apache.ti.pageflow.xwork.PageFlowActionContext;


/**
 * Base class for PageFlow-related Exceptions.
 */
public abstract class FlowControllerException
        extends PageFlowManagedObjectException {

    private String _actionName;


    protected FlowControllerException(FlowController fc) {
        super(fc);
        _actionName = PageFlowActionContext.get().getName();
    }

    protected FlowControllerException(FlowController fc, Throwable cause) {
        super(fc, cause);
        _actionName = PageFlowActionContext.get().getName();
    }

    /**
     * Get the related FlowController.
     *
     * @return the {@link FlowController} associated with this exception.
     */
    public FlowController getFlowController() {
        return (FlowController) getManagedObject();
    }

    /**
     * Get the name of the related FlowController.
     *
     * @return the class name of the {@link FlowController} associated with this exception.
     */
    public String getFlowControllerURI() {
        FlowController flowController = getFlowController();
        return flowController != null ? flowController.getDisplayName() : null;
    }

    /**
     * Get the name of the action associated with this exception.
     *
     * @return a String that is the name of the action associated with this exception.
     */
    public String getActionName() {
        return _actionName;
    }

    public void setActionName(String actionName) {
        _actionName = actionName;
    }

    /**
     * Tell whether the root cause may be session expiration in cases where the requested session ID is different than
     * the actual session ID; if <code>true</code>, then a {@link SessionExpiredException} will be thrown instead of
     * this one in these situations.
     */
    public abstract boolean causeMayBeSessionExpiration();
}
