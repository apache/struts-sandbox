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
package org.apache.ti.pageflow.xwork;

import org.apache.ti.pageflow.FlowControllerException;
import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.NoCurrentPageFlowException;
import org.apache.ti.pageflow.NoPreviousActionException;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowUtils;
import org.apache.ti.pageflow.PreviousActionInfo;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.util.logging.Logger;

public class NavigateToActionResult extends NavigateToResult {

    private static final Logger _log = Logger.getInstance(NavigateToActionResult.class);

    protected boolean preprocess(Forward fwd, PageFlowActionContext actionContext) {

        //
        // We need access to previousPageInfo from the *current PageFlow*.  That is often this FlowController,
        // but if it's a shared flow, then we don't want to use that.
        //
        PageFlowController curJpf = PageFlowUtils.getCurrentPageFlow();

        if (curJpf == null) {
            FlowControllerException ex = new NoCurrentPageFlowException(this);
            InternalUtils.throwPageFlowException(ex);
            assert false;   // throwPageFlowException() must throw.
        }

        PreviousActionInfo prevActionInfo = curJpf.getPreviousActionInfo();

        if (prevActionInfo != null) {
            String actionURI = prevActionInfo.getActionURI();

            if (_log.isDebugEnabled()) _log.debug("navigate-to-action: " + actionURI);

            //
            // If there's no form specified in this return-to-action forward, then use the original form that was saved
            // in the action.  Only do this if we're not doing a redirect, which precludes request attributes.
            //
            if (!isRedirect() && prevActionInfo.getFormBean() != null
                    && fwd.getFirstOutputForm() == null) {
                fwd.addOutputForm(prevActionInfo.getFormBean());
            }

            String query = getQueryString(fwd, prevActionInfo);
            setLocation(actionURI + query);
            return false;
        } else {
            if (_log.isInfoEnabled()) {
                _log.info("Attempted return-to-action, but previous action info was missing.");
            }

            FlowControllerException ex = new NoPreviousActionException(this, curJpf);
            InternalUtils.throwPageFlowException(ex);
            assert false;   // previous method always throws
            return true;
        }
    }

    protected boolean shouldSavePreviousPageInfo() {
        return true;
    }

    public boolean isPath() {
        return false;
    }

    public String getNavigateToAsString() {
        return "ti.NavigateTo.previousAction";  // TODO: constant
    }

}
