/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor;

import java.util.Map;

import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.BeanFactoryAware;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionProxyFactory;


/**
 *  Creates special action invocation instances that handle ControllerActions
 */
public class ControllerActionProxyFactory extends DefaultActionProxyFactory implements BeanFactoryAware {

    private BeanFactory beanFactory;
    
    public void setBeanFactory(BeanFactory factory) {
        this.beanFactory = factory;
    }
    
    public ActionInvocation createActionInvocation(ActionProxy actionProxy) throws Exception {
        ActionInvocation inv = new ControllerActionInvocation(actionProxy);
        populate(inv);
        return inv;
    }

    public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext) throws Exception {
        ActionInvocation inv = new ControllerActionInvocation(actionProxy, extraContext);
        populate(inv);
        return inv;
    }

    public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext, boolean pushAction) throws Exception {
        ActionInvocation inv = new ControllerActionInvocation(actionProxy, extraContext, pushAction);
        populate(inv);
        return inv;
    }
    
    private void populate(Object o) {
        
        ((ControllerActionInvocation)o).setInvokeAction((InvokeAction)beanFactory.getBean("invokeAction"));
        if (beanFactory instanceof AutowireCapableBeanFactory) {
            AutowireCapableBeanFactory f = (AutowireCapableBeanFactory)beanFactory;
            f.autowireBeanProperties(o, f.AUTOWIRE_BY_NAME, false);
        }
    }

}
