/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts2.rest;

import java.util.Map;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;


/**
 * 
 */
public class RestActionProxyFactory extends DefaultActionProxyFactory {

    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult, boolean cleanupContext) throws Exception {
        ActionProxy proxy = new RestActionProxy(namespace, actionName, extraContext, executeResult, cleanupContext);
        container.inject(proxy);
        proxy.prepare();
        return proxy;
    }

}
