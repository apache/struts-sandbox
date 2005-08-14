/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.interceptor;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;
import com.opensymphony.xwork.validator.*;
import org.apache.ti.processor.ControllerActionInvocation;
import org.apache.ti.processor.ControllerContext;
import org.apache.commons.logging.*;


/**
 * Validates an action form. This interceptor extends the {@link AroundInterceptor} and implements only the
 * {@link AroundInterceptor#before(com.opensymphony.xwork.ActionInvocation)} method. This class
 * simply calls the {@link ActionValidatorManager#validate(java.lang.Object, java.lang.String)} method
 * with the given Action and its context.
 *
 * @author Jason Carreira
 */
public class ControllerValidationInterceptor extends AroundInterceptor {

    private static final Log log = LogFactory.getLog(ControllerValidationInterceptor.class);

    /**
     * Does nothing in this implementation.
     */
    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    /**
     * Gets the current action form and its context and calls
     * {@link ActionValidatorManager#validate(java.lang.Object, java.lang.String)}.
     *
     * @param invocation the execution state of the Action.
     * @throws Exception if an error occurs validating the action form.
     */
    protected void before(ActionInvocation invocation) throws Exception {
        log.debug("Validating action and/or form"); 
        ControllerActionInvocation inv = (ControllerActionInvocation)invocation;
        Object form = inv.getForm();
        Object action = inv.getAction();
        
        ValidatorContext val = ControllerContext.getContext().getValidatorContext();
        String context = invocation.getProxy().getActionName();
        if (log.isDebugEnabled()) {
            log.debug("Validating "
                    + invocation.getProxy().getNamespace() + "/" + invocation.getProxy().getActionName() + ".");
        }
        
        if (form != null) {
            ActionValidatorManager.validate(form, context, val);
        }
        if (!val.hasErrors()) {
            ActionValidatorManager.validate(action, context, val);
        }
    }
}
