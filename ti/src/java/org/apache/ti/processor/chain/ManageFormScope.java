/*
 * $Id: ManageFormScope.java 230535 2005-08-06 07:56:40Z mrdon $
 *
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.ti.processor.chain;

import org.apache.commons.chain.*;
import org.apache.commons.chain.web.*;

import java.util.*;

import org.apache.ti.processor.*;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.config.entities.ActionConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Tries to instantiate the form class's no-arg constructor.
 */
public class ManageFormScope implements Filter {

    private static final Log log = LogFactory.getLog(ManageFormScope.class);

    public boolean execute(Context origctx) throws Exception {
        log.debug("Managing form scope");
        
        WebContext webCtx = (WebContext)origctx;
        String formName = getFormName();
        Map scope = getFormScope(webCtx);
        
        if (scope != null) {
            Object form = scope.get(formName);
            if (form == null) {
                origctx.put(CreateFormChain.FORM_OBJECT, form);
                return true;
            }
        }
        return false;
    }
    
    public boolean postprocess(Context origctx, Exception ex) {
        WebContext webCtx = (WebContext)origctx;
        
        Map scope = getFormScope(webCtx);
        if (scope != null) {
            Object form = webCtx.get(CreateFormChain.FORM_OBJECT);
            if (form != null) {
                String formName = getFormName();
                scope.put(formName, form);
            }
        }
        return false;
    }
    
    protected String getFormName() {
        ActionContext ctx = ActionContext.getContext();
        ControllerActionInvocation inv = (ControllerActionInvocation)ctx.getActionInvocation();
        String formName = (String)inv.getProxy().getConfig().getParams().get("formName");
        if (formName == null) {
            ActionConfig cfg = inv.getProxy().getConfig();
            formName = cfg.getPackageName() + ":"+cfg.getClassName()+":"+cfg.getMethodName();
        }
        return formName;
    }
    
    protected Map getFormScope(WebContext webCtx) {
        ActionContext ctx = ActionContext.getContext();
        ControllerActionInvocation inv = (ControllerActionInvocation)ctx.getActionInvocation();
        String scopeType = (String)inv.getProxy().getConfig().getParams().get("formScope");
        Map map = null;
        if ("request".equals(scopeType)) {
            map = webCtx.getRequestScope();
        } else if ("session".equals(scopeType)) {
            map = webCtx.getSessionScope();
        }
        return map;
    }
}
