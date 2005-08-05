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
 * Adds code to handle ControllerActions if detected
 * 
 * @see com.opensymphony.xwork.DefaultActionInvocation
 */
public class ControllerActionInvocation extends DefaultActionInvocation {

    private static final Log log = LogFactory.getLog(ControllerActionInvocation.class);
    
    protected static final Map methodsCache = Collections.synchronizedMap(new HashMap());

    protected BeanFactory beanFactory;
    protected ControllerAction ctrAction;
    protected Method actionMethod;

    protected ControllerActionInvocation(BeanFactory factory, ActionProxy proxy) throws Exception {
        this(factory, proxy, null);
    }

    protected ControllerActionInvocation(BeanFactory factory, ActionProxy proxy, Map extraContext) throws Exception {
        this(factory, proxy, extraContext, true);
    }

    protected ControllerActionInvocation(BeanFactory factory, ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        super(proxy, extraContext, pushAction);
    }
    
    protected Object getController() {
        return ((ControllerAction)action).getController();
    }
    
    public Method getActionMethod() {
        return actionMethod;
    }

    protected void createAction() {
        super.createAction();
        
        if (action instanceof ControllerAction) {
            ctrAction = (ControllerAction)action;
            createActionMethod();
        }
    }

    protected String invokeAction(Action action, ActionConfig actionConfig) throws Exception {

        if (action instanceof ControllerAction) {
            try {
                if (actionMethod.getParameterTypes().length == 1) {
                    return (String) actionMethod.invoke(getController(), new Object[]{ctrAction.getForm()});
                } else {
                    return (String) actionMethod.invoke(getController(), new Object[0]);
                }
            } catch (InvocationTargetException e) {
                // We try to return the source exception.
                Throwable t = e.getTargetException();

                if (t instanceof Exception) {
                    throw (Exception) t;
                } else {
                    throw e;
                }
            }
            // DO STUFF
        } else {
            return super.invokeAction(action, actionConfig);
        }
    }
    
    
    protected void createActionMethod() {
        //String methodName = proxy.getMethod();
        
        //if (methodName == null && actionConfig.getMethodName() == null) {
        //    return null;
        //}
        //if (methodName == null) {
        //    methodName = actionConfig.getMethodName();
        //}
        String methodName = getProxy().getConfig().getMethodName();
        Method method = null;
        Class ctrClass = getController().getClass();
        
        
        
        Method[] methods = (Method[])methodsCache.get(ctrClass);
        if (methods == null) {
            methods = ctrClass.getMethods();
            methodsCache.put(ctrClass, methods);
        }
        
        Method m;
        for (int x=0; x < methods.length; x++) {
            m = methods[x];
            if (m.getName().equals(methodName) && m.getParameterTypes().length < 2) {
                method = m;
                break;
            }
        }
        
        if (method == null) {
            throw new IllegalArgumentException("Method '" + methodName + "()' is not defined in controller '" + ctrClass + "'");
        }
        
        actionMethod = method;
    }
}
