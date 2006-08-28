/*
 * $Id: Action.java 240373 2005-08-27 01:58:39Z jmitchell $
 *
 * Copyright 2000-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.action2.legacy;

import com.opensymphony.xwork.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.webwork.ServletActionContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 *  Calls the reset() method on the ActionForm, if it exists.
 */
public class ActionFormResetInterceptor extends AroundInterceptor {

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ScopedModelDriven) {
            ScopedModelDriven modelDriven = (ScopedModelDriven) action;
            Object model = modelDriven.getModel();
            if (model != null) {
                ActionMapping mapping = StrutsFactory.getStrutsFactory().createActionMapping(invocation.getProxy().getConfig());
                HttpServletRequest req = ServletActionContext.getRequest();
                ((ActionForm)model).reset(mapping, req);
            }
        }
    }
}
