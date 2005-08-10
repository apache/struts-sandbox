/*
 * $Id: ExecuteFormAction.java 230535 2005-08-06 07:56:40Z mrdon $
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

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.apache.ti.processor.ControllerActionInvocation;

/**
 *  Initializes XWork by replacing default factories.
 */
public class ExecuteFormAction implements Command {

    private static final Log log = LogFactory.getLog(ExecuteFormAction.class);

    public boolean execute(Context origctx) throws Exception {
        log.debug("Executing form action");
        boolean result = false;
        
        ActionContext ctx = ActionContext.getContext();
        ControllerActionInvocation inv = (ControllerActionInvocation)ctx.getActionInvocation();
        Method method = inv.getActionMethod();
        Object form = inv.getForm();
        if (method.getParameterTypes().length == 1) {
            try {
                String res = (String) method.invoke(inv.getAction(), new Object[] {form});
                origctx.put(ChainInvokeAction.RESULT, res);
                result = true;
            } catch (InvocationTargetException e) {
                // We try to return the source exception.
                Throwable t = e.getTargetException();
    
                if (t instanceof Exception) {
                    throw (Exception) t;
                } else {
                    throw e;
                }
            }
        }

        return result;
    }


}
