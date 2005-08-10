/*
 * $Id: ExecuteActionChain.java 230569 2005-08-06 19:41:10Z mrdon $
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

import org.apache.ti.processor.*;
import java.lang.reflect.Method;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.impl.ChainBase;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Initializes XWork by replacing default factories.
 */
public class ExecuteActionChain extends ChainBase {

    private static final Log log = LogFactory.getLog(ExecuteActionChain.class);

    
    public boolean execute(Context origctx) throws Exception {
        log.info("Processing execute action chain");
        
        boolean retCode = super.execute(origctx);
        if (!retCode) {
            throw new IllegalStateException("Unable to execute action "+
                ActionContext.getContext().getActionInvocation().getAction());
        }
        return false;
    }
}
