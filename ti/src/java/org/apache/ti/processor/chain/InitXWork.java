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

import org.apache.ti.processor.CompilingObjectFactory;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;

import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ObjectFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Initializes XWork by replacing default factories
 */
public class InitXWork implements Command {

    private static final Log log = LogFactory.getLog(InitXWork.class);

    protected CompilingObjectFactory clObjectFactory;
    protected ActionProxyFactory actionProxyFactory;
    protected boolean devMode = false;

    public void setActionProxyFactory(ActionProxyFactory factory) {
        this.actionProxyFactory = factory;
    }

    public void setCompilingObjectFactory(CompilingObjectFactory fac) {
        this.clObjectFactory = fac;
    }

    public void setDevMode(boolean mode) {
        this.devMode = mode;
    }    

    public boolean execute(Context origctx) {
        log.debug("Initializing XWork");
        WebContext ctx = (WebContext) origctx;

        ActionProxyFactory.setFactory(actionProxyFactory);

        if (devMode) {
            log.info("Dev mode enabled, using compiling classloader");
            ObjectFactory.setObjectFactory(clObjectFactory);
        }

        return false;
    }


}
