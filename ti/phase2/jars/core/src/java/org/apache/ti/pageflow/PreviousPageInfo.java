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

import org.apache.ti.pageflow.xwork.PageFlowAction;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.pageflow.xwork.PageFlowResult;

import java.io.Serializable;


/**
 * Stores information about a previously-displayed page, as well as its initialization data.
 * Used with
 * <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#currentPage ti.NavigateTo.currentPage}</code>
 * or <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousPage ti.NavigateTo.previousPage}</code>
 * on {@link org.apache.ti.pageflow.annotations.ti.forward ti.forward},
 * {@link org.apache.ti.pageflow.annotations.ti.simpleAction ti.simpleAction}, or
 * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward ti.conditionalForward}.
 */
public class PreviousPageInfo
        extends PreviousInfo
        implements Serializable {

    private static final long serialVersionUID = 1;
    private PageFlowResult _result;
    private Forward _forward;
    private PageFlowAction _action;
    private Object _clientState;


    /**
     * Constructor which accepts the PageFlowResult used to display the page, the ActionForm
     * used to initialize the page, and the associated ActionMapping, which represents the
     * action that forwarded to the page.
     *
     * @param result the PageFlowResult that contains the path to the page.
     */
    public PreviousPageInfo(PageFlowResult result, Forward forward) {
        super();
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        _action = actionContext.getAction();
        _result = result;
        _forward = forward;
    }

    /**
     * Get the action that forwarded to the page.
     *
     * @return the action that forwarded to this page, or <code>null</code> if the page was requested directly.
     */
    public PageFlowAction getAction() {
        return _action;
    }

    /**
     * Set the action that forwarded to the page.
     *
     * @param action the action that forwarded to this page.
     */
    public void setAction(PageFlowAction action) {
        _action = action;
    }

    /**
     * Reinitialize transient state.
     */
    public void reinitialize(PageFlowController pfc) {
    }

    /**
     * Get the object that was used to forward to the page.
     *
     * @return the PageFlowResult resolved from the action that forwarded to this page.
     */
    public PageFlowResult getResult() {
        return _result;
    }

    /**
     * Set the object that was used to forward to the page.
     *
     * @param result the PageFlowResult resolved from the action that forwarded to this page.
     */
    public void setResult(PageFlowResult result) {
        _result = result;
    }

    /**
     * Get client state associated with the page (e.g., component tree state for a JSF page).
     */
    public Object getClientState() {
        return _clientState;
    }

    /**
     * Set client state associated with the page (e.g., component tree state for a JSF page).
     */
    public void setClientState(Object clientState) {
        _clientState = clientState;
    }

    /**
     * Get the object that was used to forward to the page.
     *
     * @return the ActionForward returned by the action that forwarded to this page.
     */
    public Forward getForward() {
        return _forward;
    }

    /**
     * Set the object that was used to forward to the page.
     *
     * @param forward the ActionForward returned by the action that forwarded to this page.
     */
    public void setForward(Forward forward) {
        _forward = forward;
    }
}
