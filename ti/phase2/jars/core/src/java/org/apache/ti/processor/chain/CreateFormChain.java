/*
 * $Id: CreateFormChain.java 230569 2005-08-06 19:41:10Z mrdon $
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
public class CreateFormChain extends ChainBase {

    private static final Log log = LogFactory.getLog(CreateFormChain.class);

    public static final String FORM_CLASS = "formClass";
    public static final String FORM_OBJECT = "formObject";
    
    public boolean execute(Context origctx) throws Exception {
        //WebContext ctx = (WebContext)origctx;
        
        log.info("Processing create form chain");

        ActionContext ctx = ActionContext.getContext();
        ControllerActionInvocation inv = (ControllerActionInvocation)ctx.getActionInvocation();
            
        Method method = inv.getActionMethod();
        Class[] params = method.getParameterTypes();
        if (params.length == 1) {
            origctx.put(FORM_CLASS, params[0]);

            boolean retCode = super.execute(origctx);
            if (retCode) {
                Object o = origctx.get(FORM_OBJECT);
                log.info("Created form: "+o);
                inv.setForm(o);
                ctx.getValueStack().push(o);
            } else {
                throw new IllegalStateException("Form "+params[0]+" unable to "
                    + "be created.");
            }
        }
        return false;
    }
}
