/*
 * $Id: .java 230535 2005-08-06 07:56:40Z mrdon $
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.ti.config.mapper.ActionMapping;
import org.apache.ti.processor.ProcessorException;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.interceptor.component.ComponentInterceptor;
import com.opensymphony.xwork.interceptor.component.ComponentManager;

/**
 *  Creates an ActionProxy instance
 */
public class PopulateContextForRequest implements Command {

    protected static final Log log = LogFactory.getLog(PopulateContextForRequest.class);

    public boolean execute(Context origctx) {
        log.debug("Initializing context map");
        
        WebContext ctx = (WebContext) origctx;

        ActionMapping mapping = (ActionMapping) ctx.get("actionMapping");

        // request map wrapping the http request objects
        Map requestMap = ctx.getRequestScope();

        // parameters map wrapping the http paraneters.
        Map params = mapping.getParams();
        Map requestParams = ctx.getParamValues();
        if (params != null) {
            params.putAll(requestParams);
        } else {
            params = requestParams;
        }

        HashMap extraContext = createContextMap(requestMap, params, ctx.getSessionScope(), ctx.getApplicationScope(), ctx);

        ctx.putAll(extraContext);
        
        return false;
    }

    /**
     * Merges all application and servlet attributes into a single <tt>HashMap</tt> to represent the entire
     * <tt>Action</tt> context.
     *
     * @param requestMap     a Map of all request attributes.
     * @param parameterMap   a Map of all request parameters.
     * @param sessionMap     a Map of all session attributes.
     * @param applicationMap a Map of all servlet context attributes.
     * @return a HashMap representing the <tt>Action</tt> context.
     */
    protected HashMap createContextMap(Map requestMap,
                                    Map parameterMap,
                                    Map sessionMap,
                                    Map applicationMap,
                                    WebContext ctx) {
        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, new HashMap(parameterMap));
        extraContext.put(ActionContext.SESSION, sessionMap);
        extraContext.put(ActionContext.APPLICATION, applicationMap);
        //extraContext.put(ActionContext.LOCALE, (locale == null) ? request.getLocale() : locale);

        extraContext.put(ComponentInterceptor.COMPONENT_MANAGER, requestMap.get(ComponentManager.COMPONENT_MANAGER_KEY));
        
        extraContext.put("webContext", ctx);

        // helpers to get access to request/session/application scope
        extraContext.put("request", requestMap);
        extraContext.put("session", sessionMap);
        extraContext.put("application", applicationMap);
        extraContext.put("parameters", parameterMap);

        return extraContext;
    }



}
