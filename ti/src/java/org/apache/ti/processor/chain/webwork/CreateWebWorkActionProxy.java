/*
 * $Id: InjectWebWorkValueStack.java 230535 2005-08-06 07:56:40Z mrdon $
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
package org.apache.ti.processor.chain.webwork;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.Filter;
import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.ti.processor.chain.CreateActionProxy;
import org.apache.ti.config.mapper.ActionMapping;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Initializes XWork by replacing default factories.
 */
public class CreateWebWorkActionProxy extends CreateActionProxy implements Filter {

    private static final Log log = LogFactory.getLog(CreateWebWorkActionProxy.class);

    public boolean execute(Context origctx) throws Exception {
        WebContext ctx = (WebContext)origctx;
        log.debug("Injecting webwork value stack");

        // If there was a previous value stack, then create a new copy and pass it in to be used by the new Action
        OgnlValueStack stack = (OgnlValueStack) ctx.getRequestScope().get(ServletActionContext.WEBWORK_VALUESTACK_KEY);
        if (stack != null) {
            ctx.put(ActionContext.VALUE_STACK, new OgnlValueStack(stack));
        }
        ctx.put("origStack", stack);
        
        ActionMapping mapping = (ActionMapping) ctx.get("actionMapping");
        ActionProxy proxy = getActionProxy(ctx, mapping);
        ctx.getRequestScope().put(ServletActionContext.WEBWORK_VALUESTACK_KEY, proxy.getInvocation().getStack());
        ctx.put("actionProxy", proxy);
        
        return false;
    }
    
    public boolean postprocess(Context context, Exception exception) {
        OgnlValueStack stack = (OgnlValueStack)context.get("origStack");

        WebContext ctx = (WebContext)context;
        // If there was a previous value stack then set it back onto the request
        if (stack != null) {
            ctx.getRequestScope().put(ServletActionContext.WEBWORK_VALUESTACK_KEY, stack);
        }
        context.remove("origStack");

        return false;
    }
}
