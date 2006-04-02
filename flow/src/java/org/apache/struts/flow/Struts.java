/*
 *  Copyright 1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.struts.flow;

import org.apache.struts.flow.core.*;
import org.apache.struts.flow.core.javascript.fom.FOM_Flow;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessages;
import javax.servlet.*;
import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.struts.util.MessageResources;

import java.util.Map;

/**
 *  Access to Struts and Servlet resources
 *
 * @jsname struts
 */
public class Struts implements WebContextAware {
    
    protected static final Logger logger = Factory.getLogger();
    protected ServletWebContext ctx;

    
    public void init(WebContext webctx) {
        if (webctx instanceof ServletWebContext) {
            ctx = (ServletWebContext)webctx;
        } else {
            throw new IllegalArgumentException("The struts variable requires the servlet web context");
        }
    }
    
    public void cleanup() {
        this.ctx = null;
    }
    
    
    /**
     *  Gets a map of request parameters as Strings
     */
    public Map getParam() {
        return ctx.getParam();
    }
    
    /**
     *  Gets a map of request parameters as String arrays
     */
    public Map getParamValues() {
        return ctx.getParamValues();
    }
    
    /**
     *  Gets a map of request attributes
     */
    public Map getRequestScope() {
        return ctx.getRequestScope();
    }
    
    /**
     *  Gets a map of session attributes
     */
    public Map getSessionScope() {
        return ctx.getSessionScope();
    }
    
    /**
     *  Gets a map of application attributes
     */
    public Map getApplicationScope() {
        return ctx.getApplicationScope();
    }
    
    /**
     *  Gets the servlet request
     */
    public ServletRequest getRequest() {
        return ctx.getRequest();
    }
    
    /**
     *  Gets the servlet context
     */
    public ServletContext getServletContext() {
        return ctx.getContext();
    }

    /**
     *  Gets an application resources message
     *
     * @param key The message key
     */
    public String getMessage(String key) {
        MessageResources res = (MessageResources)ctx.get(Constants.MESSAGE_RESOURCES_KEY);
        return res.getMessage(key);
    }
    
    /**
     *  Gets the action mapping
     */
    public ActionMapping getMapping() {
        return (ActionMapping)ctx.get(Constants.ACTION_CONFIG_KEY);
    }
    
    /**
     * Gets if the action has been canceled
     */
    public boolean isCanceled() {
        FlowAction action = (FlowAction)ctx.get(Constants.ACTION_KEY);
        return action.isCancelled(ctx.getRequest());
    }
    
    /**
     *  Gets if the current token is valid
     */
    public boolean isTokenValid() {
        FlowAction action = (FlowAction)ctx.get(Constants.ACTION_KEY);
        return action.isTokenValid(ctx.getRequest());
    }
    
    /**
     *  Resets the current transation token
     */
    public void resetToken() {
        FlowAction action = (FlowAction)ctx.get(Constants.ACTION_KEY);
        action.resetToken(ctx.getRequest());
    }
    
    /**
     *  Saves the action errors in the request
     *
     * @param errors The action errors
     */
    public void saveErrors(ActionErrors errors) {
        FlowAction action = (FlowAction)ctx.get(Constants.ACTION_KEY);
        action.saveErrors(ctx.getRequest(), errors);
    }
    
    /** 
     *   Saves the action messages in the request
     *
     * @param msgs The action messages
     */
    public void saveMessages(ActionMessages msgs) {
        FlowAction action = (FlowAction)ctx.get(Constants.ACTION_KEY);
        action.saveMessages(ctx.getRequest(), msgs);
    }
    
    /**
     *  Saves a transaction token in the request
     */
    public void saveToken() {
        FlowAction action = (FlowAction)ctx.get(Constants.ACTION_KEY);
        action.saveToken(ctx.getRequest());
    }
}

