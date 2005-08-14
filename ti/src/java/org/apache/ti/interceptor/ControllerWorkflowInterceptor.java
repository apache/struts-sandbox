/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.interceptor;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Validateable;
import com.opensymphony.xwork.*;
import com.opensymphony.xwork.validator.*;
import com.opensymphony.xwork.interceptor.Interceptor;

import org.apache.ti.processor.*;
import org.apache.commons.logging.*;


/**
 * An interceptor that does some basic validation workflow before allowing the interceptor chain to continue.
 * The order of execution in the workflow is:
 * <p/>
 * <ol>
 * <li>If the action being executed implements {@link Validateable}, the action's
 * {@link Validateable#validate() validate} method is called.</li>
 * <li>Next, if the action implements {@link ValidationAware}, the action's
 * {@link ValidationAware#hasErrors() hasErrors} method is called. If this
 * method returns true, this interceptor stops the chain from continuing and
 * immediately returns {@link Action#INPUT}</li>
 * </ol>
 * <p/>
 * <i>Note: if the action doesn't implement either interface, this interceptor effectively does nothing.</i>
 *
 * @author Jason Carreira
 */
public class ControllerWorkflowInterceptor implements Interceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(ControllerWorkflowInterceptor.class);
    
    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        log.debug("Executing controller validation workflow");
        ControllerActionInvocation inv = (ControllerActionInvocation)invocation;
        Object action = invocation.getAction();
        
        ValidatorContext val = ControllerContext.getContext().getValidatorContext();
        inv.invokeActionEvent("validate", 
            new Class[]{ValidatorContext.class},
            new Object[] {val},
            true);
        
        
        if (action instanceof Validateable) {
            Validateable validateable = (Validateable) action;
            validateable.validate();
        }

        if (val.hasErrors()) {
            if (inv.getProxy().getConfig().getResults().containsKey(Action.INPUT)) {
                return Action.INPUT;
            } else {
                log.debug("Input result not found, action will be invoked");
            }
        }

        return invocation.invoke();
    }
}
