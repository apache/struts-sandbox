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

import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.PageFlowStack;
import org.apache.ti.pageflow.handler.ForwardRedirectHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.PageFlowConfig;

import javax.servlet.http.HttpServletResponse;

/**
 * Handler for redirects and server forwards.
 */
public abstract class DefaultForwardRedirectHandler
        extends DefaultHandler
        implements ForwardRedirectHandler {
    public DefaultForwardRedirectHandler() {
    }

    public void forward(String uri) throws PageFlowException {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        int requestCount = actionContext.getForwardedRequestCount();

        //
        // See if we've exceeded the maximum number of forwards.
        //
        PageFlowConfig pageflowConfig = ConfigUtil.getConfig().getPageFlowConfig();

        // Why can't we read the default value from the XmlObjext?
        int forwardOverflowCount = pageflowConfig.getMaxForwardsPerRequest();

        if (requestCount > forwardOverflowCount) {
            InternalUtils.sendDevTimeError("PageFlow_Forward_Overflow", null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                           new Object[] { new Integer(forwardOverflowCount), uri });

            return;
        }

        //
        // See if we've exceeded the maximum nesting depth.
        //
        PageFlowStack pfStack = PageFlowStack.get(false);

        // Why can't we read the default value from the XmlObjext?
        int nestingOverflowCount = pageflowConfig.getMaxNestingStackDepth();

        if ((pfStack != null) && (pfStack.size() > nestingOverflowCount)) {
            Object[] args = new Object[] { new Integer(pfStack.size()), new Integer(nestingOverflowCount) };
            InternalUtils.sendDevTimeError("PageFlow_Nesting_Overflow", null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, args);

            return;
        }

        //
        // We're OK -- do the forward.
        //
        actionContext.setForwardedRequestCount(requestCount + 1);

        doForward(uri);
    }

    protected abstract void doForward(String uri) throws PageFlowException;
}
