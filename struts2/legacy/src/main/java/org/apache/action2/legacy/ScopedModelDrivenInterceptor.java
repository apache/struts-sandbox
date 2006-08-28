/*
 * $Id$
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.action2.legacy;

import com.opensymphony.xwork.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.ObjectFactory;

import java.util.Map;

/**
 *  Retrieves the model class from the configured scope, then provides it 
 *  to the Action.
 */
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
