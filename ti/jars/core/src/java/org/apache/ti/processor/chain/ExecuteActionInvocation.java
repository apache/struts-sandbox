/*
 * $Id: ExecuteAction.java 230535 2005-08-06 07:56:40Z mrdon $
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

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Initializes XWork by replacing default factories.
 */
public class ExecuteActionInvocation implements Command {

    private static final Log log = LogFactory.getLog(ExecuteActionInvocation.class);

    public boolean execute(Context origctx) throws Exception {
        log.debug("Executing action invocation");

        ActionContext ctx = ActionContext.getContext();
        ActionProxy proxy = ctx.getActionInvocation().getProxy();

        String ret = proxy.execute();

        origctx.put("result", ret);
        return false;
    }


}
