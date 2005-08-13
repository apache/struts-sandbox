/*
 * $Id$
 *
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ti.processor;

import org.apache.commons.chain.web.WebContext;
import org.apache.ti.config.mapper.ActionMapping;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.validator.ValidatorContext;

/**
 *  Context that adds Controller methods, using ActionContext for storage.
 */
public class ControllerContext {

    public static final String CONTROLLER_CONTEXT = "controllerContext";

    public static void setControllerContext(ControllerContext ctx) {
        ActionContext.getContext().put(CONTROLLER_CONTEXT, ctx);
    }

    public static ControllerContext getContext() {
        return (ControllerContext) ActionContext.getContext().get(CONTROLLER_CONTEXT);
    }

    public WebContext getWebContext() {
        return (WebContext) getFromStore("webContext");
    }

    public ActionMapping getActionMapping() {
        return (ActionMapping) get("actionMapping");
    }
    
    public ValidatorContext getValidatorContext() {
        return (ValidatorContext) getFromStore("validatorContext");
    }
    
    public void setValidatorContext(ValidatorContext ctx) {
        putInStore("validatorContext", ctx);
    }

    protected Object getFromStore(String key) {
        return ActionContext.getContext().get(key);
    }
    
    protected void putInStore(String key, Object val) {
        ActionContext.getContext().put(key, val);
    }

    protected Object get(String key) {
        return getWebContext().get(key);
    }
}
