/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.processor;

import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionProxyFactory;


/**
 *  Creates special action invocation instances that handle ControllerActions
 */
public class ControllerActionProxyFactory extends DefaultActionProxyFactory {

    public ActionInvocation createActionInvocation(ActionProxy actionProxy) throws Exception {
        return new ControllerActionInvocation(actionProxy);
    }

    public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext) throws Exception {
        return new ControllerActionInvocation(actionProxy, extraContext);
    }

    public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext, boolean pushAction) throws Exception {
        return new ControllerActionInvocation(actionProxy, extraContext, pushAction);
    }

}
