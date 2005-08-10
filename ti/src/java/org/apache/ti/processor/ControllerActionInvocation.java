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
<<<<<<< .mine
    protected Object form;
    protected InvokeAction invokeAction;
    
=======
    protected Object form;
    
>>>>>>> .r231157
    protected ControllerActionInvocation(ActionProxy proxy) throws Exception {
        this(proxy, null);
    }

    protected ControllerActionInvocation(ActionProxy proxy, Map extraContext) throws Exception {
        this(proxy, extraContext, true);
    }

    protected ControllerActionInvocation(ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        super(proxy, extraContext, pushAction);
    }
    
    public void setInvokeAction(InvokeAction inv) {
        this.invokeAction = inv;
    }

    public Method getActionMethod() {
        // TODO: this should be optimized 
        if (actionMethod == null) {
            if (getAction() != null) {
                try {
                    actionMethod = proxy.getConfig().getMethod(getAction().getClass());
                } catch (NoSuchMethodException ex) {
                    Class cls = getAction().getClass();
                    String methodName = proxy.getConfig().getMethodName();

                    Method[] methods = cls.getMethods();
                    Class[] args;
                    for (int x=0; x<methods.length; x++) {
                        if (methods[x].getName().equals(methodName) &&
                            methods[x].getParameterTypes().length == 1) {
                            actionMethod = methods[x];
                            break;
                        }
                    }
                }
                
                if (actionMethod == null) {
                    throw new IllegalStateException("Cannot location method '"+proxy.getConfig().getMethodName()
                        + "' in action '"+getAction().getClass()+"'");
                }
            }    
        }    
        return actionMethod;
    }
<<<<<<< .mine
    
    public Object getForm() {
        return form;
    }
    
    public void setForm(Object o) {
        this.form = o;
    }

    /**
     *  Invokes action.  If the action method contains one parameter, this method
     *  handles its execution.  Otherwise, it is delegated to the super class.
     */
    protected String invokeAction(Object action, ActionConfig actionConfig) throws Exception {
        
        return invokeAction(action, actionConfig);
    }
    
    public String invokeXWorkAction(Object action, ActionConfig actionConfig)
            throws Exception {
        return super.invokeAction(action, actionConfig);
    }    
        
=======
    
    public Object getForm() {
        return form;
    }
    
    public void setForm(Object o) {
        this.form = o;
    }

    /**
     *  Invokes action.  If the action method contains one parameter, this method
     *  handles its execution.  Otherwise, it is delegated to the super class.
     */
    protected String invokeAction(Object action, ActionConfig actionConfig) throws Exception {
        
        Method method = getActionMethod();
                
        if (method.getParameterTypes().length == 1) {
            try {
                return (String) method.invoke(action, new Object[] {form});
            } catch (InvocationTargetException e) {
                // We try to return the source exception.
                Throwable t = e.getTargetException();
    
                if (t instanceof Exception) {
                    throw (Exception) t;
                } else {
                    throw e;
                }
            }
        } else {
            return super.invokeAction(action, actionConfig);
        }
    }
>>>>>>> .r231157
}
