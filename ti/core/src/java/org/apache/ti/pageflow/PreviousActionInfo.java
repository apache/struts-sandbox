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

import java.io.Serializable;

/**
 * Stores information about a recent action execution within a pageflow -- used with
 * Used with
 * <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousAction ti.NavigateTo.previousAction}</code>
 * on {@link org.apache.ti.pageflow.annotations.ti.forward ti.forward},
 * {@link org.apache.ti.pageflow.annotations.ti.simpleAction ti.simpleAction}, or
 * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward ti.conditionalForward}.
 */
public class PreviousActionInfo
        extends PreviousInfo
        implements Serializable {

    private String _actionURI;


    public PreviousActionInfo() {
        super();
        _actionURI = PageFlowUtils.getActionPath();
    }

    /**
     * Get the URI that was used to execute the action.
     *
     * @return the String URI that was used to execute the action.
     */
    public String getActionURI() {
        return _actionURI;
    }

    /**
     * Set the URI that was used to execute the action.
     *
     * @param actionURI the URI that was used to execute the action.
     */
    public void setActionURI(String actionURI) {
        _actionURI = actionURI;
    }
}
