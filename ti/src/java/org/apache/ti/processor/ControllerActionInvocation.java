/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionInvocation;
import com.opensymphony.xwork.config.entities.ActionConfig;


/**
 * Adds code to handle ControllerActions if detected.
 *
 * @see com.opensymphony.xwork.DefaultActionInvocation
 */
public class ControllerActionInvocation extends DefaultActionInvocation {

    private static final Log log = LogFactory.getLog(ControllerActionInvocation.class);

    protected BeanFactory beanFactory;
    protected Method actionMethod;

    protected ControllerActionInvocation(ActionProxy proxy) throws Exception {
        this(proxy, null);
    }

    protected ControllerActionInvocation(ActionProxy proxy, Map extraContext) throws Exception {
        this(proxy, extraContext, true);
    }

    protected ControllerActionInvocation(ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        super(proxy, extraContext, pushAction);
    }

    public Method getActionMethod() {
        if (actionMethod == null) {
            if (getPOJOAction() != null) {
                try {
                    actionMethod = proxy.getConfig().getMethod(getPOJOAction().getClass());
                } catch (NoSuchMethodException ex) {
                    throw new IllegalStateException("Cannot location method '"+proxy.getConfig().getMethodName()
                        + "' in action '"+getPOJOAction().getClass()+"'");
                }
            }    
        }    
        return actionMethod;
    }
}
