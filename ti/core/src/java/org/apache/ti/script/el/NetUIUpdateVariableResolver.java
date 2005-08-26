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
package org.apache.ti.script.el;

import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.script.IllegalExpressionException;
import org.apache.ti.script.common.ImplicitObjectUtil;
import org.apache.ti.util.logging.Logger;

import java.util.Map;

/**
 *
 */
public class NetUIUpdateVariableResolver
        extends NetUIVariableResolver {

    private static final Logger LOGGER = Logger.getInstance(NetUIVariableResolver.class);

    private boolean _requestParameter = true;
    private Object _form = null;

    public NetUIUpdateVariableResolver(Object form, boolean requestParameter) {
        super();

        _requestParameter = requestParameter;
        _form = form;
    }

    public Object resolveVariable(String name) {
        if (name.equals("actionForm"))
            return _form;
        else if (name.equals("pageFlow"))
            return getPageFlow();
        else if (name.equals("sharedFlow"))
            return getSharedFlow();
        else if (name.equals("requestScope")) {
            if (_requestParameter == false)
                return PageFlowActionContext.get().getWebContext().getRequestScope();
            else
                throw new IllegalExpressionException("The request data binding context can not be updated from a request parameter.");
        } else if (name.equals("sessionScope")) {
            if (_requestParameter == false)
                return PageFlowActionContext.get().getWebContext().getSessionScope();
            else
                throw new IllegalExpressionException("The session data binding context can not be updated from a request parameter.");
        } else if (name.equals("applicationScope")) {
            if (_requestParameter == false)
                return PageFlowActionContext.get().getWebContext().getApplicationScope();
            else
                throw new IllegalExpressionException("The application data binding context can not be updated from a request parameter.");
        } else {
            String msg = "Could not resolve variable named \"" + name + "\" for an expression update.";
            if (LOGGER.isErrorEnabled())
                LOGGER.error(msg);

            throw new IllegalExpressionException(msg);
        }
    }

    public String[] getAvailableVariables() {
        if (_requestParameter)
            return new String[]{"actionForm", "pageFlow", "globalApp"};
        else
            return new String[]{"actionForm", "pageFlow", "globalApp", "request", "session", "application"};
    }

    private static final Map/*<String, SharedFlowController>*/ getSharedFlow() {
        return ImplicitObjectUtil.getSharedFlow();
    }

    private static final PageFlowController getPageFlow() {
        return ImplicitObjectUtil.getPageFlow();
    }

}
