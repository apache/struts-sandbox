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

import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import java.util.Map;

/**
 * Internal class used in JSF/Page Flow integration.  This exists to pass form beans from JSF pages to Page Flow
 * actions, and to abort event processing if {@link PageFlowNavigationHandler} forwarded to an action.
 *
 * @see org.apache.ti.pageflow.faces.PageFlowApplicationFactory
 */
public class PageFlowActionListener
        implements ActionListener {

    private ActionListener _delegate;

    public PageFlowActionListener(ActionListener delegate) {
        _delegate = delegate;
    }

    public void processAction(ActionEvent event) throws AbortProcessingException {
        Object submitFormBean = event.getComponent().getAttributes().get("submitFormBean");
        FacesContext context = FacesContext.getCurrentInstance();

        if (submitFormBean != null) {
            ValueBinding binding = context.getApplication().createValueBinding("#{" + submitFormBean + '}');
            Object beanInstance = binding.getValue(context);
            Object request = context.getExternalContext().getRequest();
            InternalUtils.setForwardedFormBean(beanInstance);
        }

        _delegate.processAction(event);

        Map requestScope = PageFlowActionContext.get().getRequestScope();
        String actionURI = (String) requestScope.get(PageFlowNavigationHandler.ALREADY_FORWARDED_ATTR);

        if (actionURI != null) {
            throw new AbortProcessingException("PageFlowNavigationHandler forwarded to: " + actionURI);
        }
    }
}
