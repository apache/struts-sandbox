/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.springframework.beans.factory.BeanFactory;

import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionInvocation;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlUtil;
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
    protected Object form;
    protected InvokeAction invokeAction;
    protected boolean devMode;
    
    protected ControllerActionInvocation(ActionProxy proxy) throws Exception {
        this(proxy, null);
    }

    protected ControllerActionInvocation(ActionProxy proxy, Map extraContext) throws Exception {
        this(proxy, extraContext, true);
    }

    protected ControllerActionInvocation(ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        super(proxy, extraContext, pushAction);
        
        // TODO: DefaultActionInvocation should make the context-creation (currently in private init()) overridable.
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        invocationContext = new PageFlowActionContext(createContextMap(), actionContext.getWebContext());
        invocationContext.setName(proxy.getActionName());
    }
    
    public void setInvokeAction(InvokeAction inv) {
        this.invokeAction = inv;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }    

    public Object invokeActionEvent(String eventName, boolean optional) throws Exception {
        return invokeActionEvent(eventName, null, null, optional);
    }
    
    public Object invokeActionEvent(String eventName, Class[] otherArgs, 
            Object[] otherParams, boolean optional) throws Exception {
    
        // TODO: this should be optimized
        Object result = null;
        Method method = null;
        Class[] args = otherArgs;
        Object[] params = otherParams;
        if (form != null) {
            if (otherArgs == null) {
                args = new Class[] {form.getClass()};
                params = new Object[] {form};
            } else {
                args = new Class[otherArgs.length + 1];
                args[0] = form.getClass();
                System.arraycopy(otherArgs, 0, args, 1, otherArgs.length);
                
                params = new Object[otherParams.length + 1];
                params[0] = form;
                System.arraycopy(otherParams, 0, params, 1, otherParams.length);
            }
        }
        
        String methodName = getActionMethod().getName();
        methodName += "_" + eventName;
        Class cls = getAction().getClass();
        try {
            method = cls.getMethod(methodName, args);
        } catch (NoSuchMethodException ex) {
            log.debug("Unable to locate action event method "+methodName);
            if (!optional) {
                throw ex;
            } 
        }
        
        if (method != null) {
            try {
                result = method.invoke(action, params);
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
    
    public Method getActionMethod() {
        Method method = null;
        
        // TODO: this should be optimized 
        if (actionMethod == null) {
            if (getAction() != null) {
                try {
                    method = proxy.getConfig().getMethod(getAction().getClass());
                } catch (NoSuchMethodException ex) {
                    Class cls = getAction().getClass();
                    String methodName = proxy.getConfig().getMethodName();

                    Method[] methods = cls.getMethods();
                    Class[] args;
                    for (int x=0; x<methods.length; x++) {
                        if (methods[x].getName().equals(methodName) &&
                            methods[x].getParameterTypes().length == 1) {
                            method = methods[x];
                            break;
                        }
                    }
                }
                
                if (method == null) {
                    throw new IllegalStateException("Cannot location method '"+proxy.getConfig().getMethodName()
                        + "' in action '"+getAction().getClass()+"'");
                }
            }    
        } else {
            method = actionMethod;
         }   
        
        if (!devMode) {
            actionMethod = method;
        }    
        
        return method;
    }

    protected void createAction() {
        super.createAction();
        
        // TODO: have to find out why this is necessary; shouldn't it be part of the base createAction()?
        OgnlUtil.setProperties(proxy.getConfig().getParams(), action, ActionContext.getContext().getContextMap());
    }

    
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
        
        return invokeAction.invoke(action, actionConfig);
    }
    
    public String invokeXWorkAction(Object action, ActionConfig actionConfig)
            throws Exception {
        return super.invokeAction(action, actionConfig);
    }    
}
