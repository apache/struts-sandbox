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
package org.apache.ti.pageflow.faces.internal;

import org.apache.ti.pageflow.FlowControllerFactory;
import org.apache.ti.pageflow.PageFlowConstants;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.logging.Logger;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import java.util.Map;


/**
 * Internal class used in JSF/Page Flow integration.  This NavigationHandler raises Page Flow actions for JSF pages
 * that are in Page Flow directories.
 *
 * @see org.apache.ti.pageflow.faces.PageFlowApplicationFactory
 */
public class PageFlowNavigationHandler
        extends NavigationHandler {

    private static final Logger _log = Logger.getInstance(PageFlowNavigationHandler.class);
    static final String ALREADY_FORWARDED_ATTR = InternalConstants.ATTR_PREFIX + "navHandled";

    private NavigationHandler _baseHandler;


    public PageFlowNavigationHandler(NavigationHandler base) {
        if (_log.isDebugEnabled()) {
            _log.debug("Adapting NavigationHandler" + base);
        }

        _baseHandler = base;
    }

    public void handleNavigation(FacesContext context, String fromAction, String outcome) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        Map requestScope = actionContext.getRequestScope();

        //
        // If we're coming in on a forwarded request from this NavigationHandler, bail out; we don't want to
        // forward again.
        //
        if (requestScope.get(ALREADY_FORWARDED_ATTR) != null) {
            requestScope.remove(ALREADY_FORWARDED_ATTR);
            return;
        }

        try {
            //
            // We only forward to Page Flow actions if there's a page flow appropriate for this request.
            //
            FlowControllerFactory fcFactory = FlowControllerFactory.get();
            PageFlowController pfc = fcFactory.getPageFlowForRequest();

            if (pfc != null) {
                if (outcome != null) {
                    String actionURI = outcome + PageFlowConstants.ACTION_EXTENSION;

                    if (_log.isDebugEnabled()) {
                        _log.debug("Forwarding to " + actionURI);
                    }

                    context.responseComplete();
                    requestScope.put(ALREADY_FORWARDED_ATTR, actionURI);

                    try {
                        Handlers.get().getForwardRedirectHandler().forward(actionURI);
                    } catch (PageFlowException e) {
                        _log.error("Could not forward to " + actionURI);
                    }
                }

                return;
            }
        } catch (InstantiationException e) {
            _log.error("Could not instantiate PageFlowController for request " + actionContext.getRequestPath(), e);
            return;
        } catch (IllegalAccessException e) {
            _log.error("Could not access PageFlowController for request " + actionContext.getRequestPath(), e);
            return;
        }

        // Fall back to base JSF navigation.
        _baseHandler.handleNavigation(context, fromAction, outcome);
    }
}
