/*
 * $Id: CreateActionProxy.java 230535 2005-08-06 07:56:40Z mrdon $
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ti.config.mapper.ActionMapping;
import org.apache.ti.processor.ProcessorException;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.config.ConfigurationException;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.WebWorkStatics;
import com.opensymphony.webwork.util.AttributeMap;


/**
 *  Creates an ActionProxy instance
 */
public class PopulateContextForWebWork implements Command {

    protected static final Log log = LogFactory.getLog(PopulateContextForWebWork.class);

    public boolean execute(Context ctx) {
        log.debug("Initializing context map adding webwork values");
    
        ServletWebContext servletCtx = (ServletWebContext) ctx;
        ctx.put(WebWorkStatics.HTTP_REQUEST, servletCtx.getRequest());
        ctx.put(WebWorkStatics.HTTP_RESPONSE, servletCtx.getResponse());
        ctx.put(WebWorkStatics.SERVLET_CONTEXT, servletCtx.getContext());
        
        AttributeMap attrMap = new AttributeMap(ctx);
        ctx.put("attr", attrMap);
        
        return false;
    }



}
