/*
 * $Id: CreateValidatorContext.java 230578 2005-08-06 20:21:45Z mrdon $
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
package org.apache.ti.processor.chain;

import java.util.Map;

import org.apache.ti.processor.ProcessorException;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.web.WebContext;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.validator.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ti.processor.*;
import java.util.Locale;

import com.opensymphony.xwork.*;

/**
 *  Initializes XWork by replacing default factories.
 */
public class CreateValidatorContext implements Command {

    private static final Log log = LogFactory.getLog(CreateValidatorContext.class);

    public boolean execute(Context origctx) throws Exception {
        
        ActionContext ctx = ActionContext.getContext();
        ControllerActionInvocation inv = (ControllerActionInvocation)ctx.getActionInvocation();
        Object action = inv.getAction();
        Object form = inv.getForm();
        
        LocaleProvider locProv = getLocaleProvider(action, form);
        TextProvider textProv = getTextProvider(action, form, locProv);
        ValidationAware val = getValidationAware(action, form);
        
        ValidatorContext validatorCtx = new DelegatingValidatorContext(val, textProv, locProv);
        ControllerContext.getContext().setValidatorContext(validatorCtx);
        
        return false;
    }
    
    protected TextProvider getTextProvider(Object action, Object form, LocaleProvider locProv) {
        TextProvider prov = null;
        if (form != null && form instanceof TextProvider) {
            prov = (TextProvider)form;
        } else if (action instanceof TextProvider) {
            prov = (TextProvider)action;
        } else {
            if (form != null) {
                prov = new TextProviderSupport(form.getClass(), locProv);
            } else {
                prov = new TextProviderSupport(action.getClass(), locProv);
            }
        }
        return prov;
    }
    
    protected ValidationAware getValidationAware(Object action, Object form) {
        ValidationAware prov = null;
        if (form != null && form instanceof ValidationAware) {
            prov = (ValidationAware)form;
        } else if (action instanceof ValidationAware) {
            prov = (ValidationAware)action;
        } else {
            if (form != null) {
                prov = new ValidationAwareSupport();
            } else {
                prov = new ValidationAwareSupport();
            }
        }
        return prov;
    }
    
    protected LocaleProvider getLocaleProvider(Object action, Object form) {
        LocaleProvider prov = null;
        if (form != null && form instanceof LocaleProvider) {
            prov = (LocaleProvider)form;
        } else if (action instanceof LocaleProvider) {
            prov = (LocaleProvider)action;
        } else {
            prov = new LocaleProvider() {
                public Locale getLocale() {
                    return ActionContext.getContext().getLocale();
                }
            };
        }
        return prov;
    }
    
}
