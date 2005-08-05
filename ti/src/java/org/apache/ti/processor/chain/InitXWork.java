/*
 * $Id: ServletRequestHandler.java 170184 2005-05-14 23:54:24Z martinc $
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

package org.apache.ti.processor.chain;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;

import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ObjectFactory;


/**
 *  Initializes XWork by replacing default factories
 */
public class InitXWork implements Command {
    
    protected ObjectFactory objectFactory;
    protected ActionProxyFactory actionProxyFactory;
    
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }
    
    public void setActionProxyFactory(ActionProxyFactory factory) {
        this.actionProxyFactory = factory;
    }
    
    public boolean execute(Context origctx) {
        WebContext ctx = (WebContext)origctx;
    
        ObjectFactory.setObjectFactory(objectFactory);
        ActionProxyFactory.setFactory(actionProxyFactory);
    
        return false;
    }
    
    
}
