/*
 * $Id$
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

import java.util.Map;

import org.apache.ti.config.mapper.ActionMapping;
import org.apache.ti.processor.ProcessorException;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.web.WebContext;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.config.ConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Initializes XWork by replacing default factories.
 */
public class CreateActionProxy implements Command {

    private static final Log log = LogFactory.getLog(CreateActionProxy.class);

    public boolean execute(Context origctx) throws Exception {
        WebContext ctx = (WebContext)origctx;
        
        ActionMapping mapping = (ActionMapping) ctx.get("actionMapping");
        ActionProxy proxy = getActionProxy(ctx, mapping);
        ctx.put("actionProxy", proxy);
        
        return false;
    }
    
    protected ActionProxy getActionProxy(Map extraCtx, ActionMapping mapping) {

        try {
            log.debug("Trying to get proxy");
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(mapping.getNamespace(), mapping.getName(), extraCtx);
            return proxy;
        } catch (ConfigurationException e) {
            log.error("Could not find action", e);
            throw new ProcessorException(e);
        } catch (Exception e) {
            log.error("Could not execute action", e);
            throw new ProcessorException(e);
        }
    }
}
