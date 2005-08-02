/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.entities.ActionConfig;
import org.springframework.beans.factory.*;

import java.util.Map;


/**
 *  Creates special action invocation instances that handle ControllerActions
 */
public class SpringActionProxyFactory extends DefaultActionProxyFactory implements BeanFactoryAware {

    public static final String ACTION_INVOCATION = "actionInvocation";

    protected BeanFactory beanFactory;
    
    public void setBeanFactory(BeanFactory factory) {
        this.beanFactory = factory;
    }
    
    public ActionInvocation createActionInvocation(ActionProxy actionProxy) throws Exception {
        return new ControllerActionInvocation(beanFactory, actionProxy);
    }

    public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext) throws Exception {
        return new ControllerActionInvocation(beanFactory, actionProxy, extraContext);
    }

    public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext, boolean pushAction) throws Exception {
        return new ControllerActionInvocation(beanFactory, actionProxy, extraContext, pushAction);
    }

}
