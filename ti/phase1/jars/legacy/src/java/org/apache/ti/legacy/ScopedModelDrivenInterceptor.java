/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.legacy;

import com.opensymphony.xwork.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.ObjectFactory;

import java.util.Map;

public class ScopedModelDrivenInterceptor extends ModelDrivenInterceptor {

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ScopedModelDriven) {
            ScopedModelDriven modelDriven = (ScopedModelDriven) action;
            ActionContext ctx = ActionContext.getContext();
            ActionConfig config = invocation.getProxy().getConfig();
            String scope = (String)config.getParams().get("modelScope");
            String attr = (String)config.getParams().get("modelName");
            String clsName = (String)config.getParams().get("modelClass");
           
            Object model = resolveModel(ObjectFactory.getObjectFactory(), ctx.getSession(), clsName, scope, attr);
            modelDriven.setModel(model);
        }
        super.before(invocation);
    }

    protected Object resolveModel(ObjectFactory factory, Map session, String className, String scope, String name) throws Exception {
        Object model = null;
        if ("session".equals(scope)) {
            model = session.get(name);
            if (model == null) {
                model = factory.buildBean(className, null);
                session.put(name, model);
            }
        } else {
            model = factory.buildBean(className, null);
        }
        return model;
    }    
                    
}
