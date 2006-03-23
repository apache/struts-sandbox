/*
 * $Id: Action.java 240373 2005-08-27 01:58:39Z jmitchell $
 *
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.action2.legacy;

import com.opensymphony.xwork.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.webwork.util.ServletContextAware;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.*;
import org.apache.struts.config.*;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.*;

/**
 *  Supports Struts Action 1.x-style PlugIn classes by wrapping them in this
 *  no-op interceptor.
 */
public class PlugInInterceptor extends AroundInterceptor 
        implements ServletContextAware{

    private String className;
    private Map params = new HashMap();
    private String modulePrefix;
    private PlugIn plugin = null;
    private static final Log LOG = LogFactory.getLog(PlugInInterceptor.class);

    public void setClassName(String name) {
        this.className = name;
    }    

    public void setParams(Map params) {
        this.params = params;
    }

    public Map getParams() {
        return params;
    }

    // TODO: we should be able to find the package name/namespace during init time, but for now it's passed in as param.
    public void setModulePrefix(String modulePrefix) {
        this.modulePrefix = modulePrefix;
    }

    public void setServletContext(final ServletContext servletContext) {
        ActionServlet servlet = new ActionServlet() {
            public ServletContext getServletContext() {
                return servletContext;
            }
        };

        // Create a ModuleConfig based on the module prefix.  This assumes that there is an existing XWork package
        // configuration with the given module prefix.
        ModuleConfig modConfig = StrutsFactory.getStrutsFactory().createModuleConfig(modulePrefix);

        try {
            plugin = (PlugIn) ObjectFactory.getObjectFactory().buildBean(className, null);
            plugin.init(servlet, modConfig);
        } catch (Exception ex) {
            LOG.error("Unable to create or init plugin "+className, ex);
            return;
        }    
        if (params != null) {
            OgnlUtil.setProperties(params, plugin);
        }
    }
    
    public void destroy() {
        plugin.destroy();
    }

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
    }
}
