/*
 * $Id: StrutsModels.java 549177 2007-06-20 18:17:22Z musachy $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.opensymphony.xwork;

import java.util.Map;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;


public class ActionContext extends com.opensymphony.xwork2.ActionContext {

    private com.opensymphony.xwork2.ActionContext realContext;
    
    
    public ActionContext(com.opensymphony.xwork2.ActionContext ctx) {
        super(ctx.getContextMap());
        this.realContext = ctx;
    }
    
    /**
     * Returns the ActionContext specific to the current thread.
     *
     * @return the ActionContext for the current thread, is never <tt>null</tt>.
     */
    public static ActionContext getContext() {
        return new ActionContext(com.opensymphony.xwork2.ActionContext.getContext());
    }

}
