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

import org.apache.ti.pageflow.FlowController;
import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.ModuleConfig;
import org.apache.ti.pageflow.PageFlowConstants;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.internal.ViewRenderer;
import org.apache.ti.util.logging.Logger;

/**
 * There is a special forward ("auto"), which signals us to render using a registered ViewRenderer.
 * This is used as part of popup window support.
 */
public class AutoViewRenderResult extends PageFlowResult {

    private static final Logger _log = Logger.getInstance(AutoViewRenderResult.class);

    protected boolean shouldSavePreviousPageInfo() {
        return true;
    }

    public boolean isPath() {
        return false;
    }

    protected Forward applyForward(Forward fwd, ModuleConfig altModuleConfig) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        ViewRenderer vr = actionContext.getViewRenderer();

        if (vr != null) {
            _log.debug("null forward -- delegating to ViewRenderer " + vr + " to handle response.");

            try {
                vr.renderView();
            } catch (Throwable th) {
                try {
                    FlowController flowController = actionContext.getFlowController();
                    return flowController.handleException(th);
                } catch (PageFlowException e) {
                    _log.error("Exception thrown while handling exception in ViewRenderer " + vr + ": "
                            + e.getMessage(), th);
                }
            }

        } else {
            _log.error("Auto-render forward " + PageFlowConstants.AUTO_VIEW_RENDER_FORWARD_NAME
                    + " used, but no ViewRenderer " + "was registered -- not doing any forward or redirect.");
        }

        return null;
    }
}
