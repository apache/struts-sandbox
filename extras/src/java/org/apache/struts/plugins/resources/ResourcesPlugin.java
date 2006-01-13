/*
 * $Id$ 
 *
 * Copyright 2003,2004 The Apache Software Foundation.
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


package org.apache.struts.plugins.resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;


/**
 * <p>An implementation of <code>PlugIn</code> which
 * can be configured to override the default Struts 
 * MessageResources implementation.
 *
 * This plugin was created after Struts 1.1, but is
 * compatible with Struts 1.1 and Struts 1.2.
 *
 * @version $Rev$
 * @see org.apache.struts.action.PlugIn
 * @since Struts 1.2
 */
public class ResourcesPlugin implements PlugIn{
    
    private static final Log log = LogFactory.getLog(ResourcesPlugin.class);
    
    protected ActionServlet servlet = null;
    
    protected ModuleConfig moduleConfig = null;
    
    private String strutsPluginFactoryClass = 
        "org.apache.struts.plugins.resources.CommonsResourcesFactory";
    
    private String implFactoryClass = 
        "org.apache.commons.resources.impl.WebappPropertyResourcesFactory";
    
    private String implClass = 
        "org.apache.commons.resources.impl.WebappPropertyResources";
    
    private String bundle;
    
    private String key;
    
    private boolean returnNull = false;

    
    public boolean isReturnNull() {
        return this.returnNull;
    }
    

    public void setReturnNull(boolean returnNull) {
        this.returnNull = returnNull;
    }
    

    public void init(ActionServlet servlet, ModuleConfig config) {
        
        this.servlet = servlet;
        this.moduleConfig = config;
        
        MessageResourcesFactory.setFactoryClass(this.strutsPluginFactoryClass);
        MessageResourcesFactory factoryObject =
            MessageResourcesFactory.createFactory();
        
        MessageResources resources = null;
        if (factoryObject instanceof CommonsResourcesFactory) {
            CommonsResourcesFactory fctry = 
                (CommonsResourcesFactory) factoryObject;
            try {
                resources = fctry.createResources(servlet.getServletContext(), 
                        this.implFactoryClass, this.implClass, bundle);
            } catch (Exception e) {
                // log output
                log.debug(e.getMessage());
                e.printStackTrace();
            }
        }else{
            resources =
                factoryObject.createResources(this.bundle);
        }
        resources.setReturnNull(this.returnNull);
        
        String bundleKey = this.key;
        bundleKey = (bundleKey == null ? "" : bundleKey);
        String k = bundleKey + config.getPrefix();
        if ("".equals(k))
            k = Globals.MESSAGES_KEY;
        
        servlet.getServletContext().setAttribute(k, resources);
        
    }

    public void destroy() {
        this.servlet = null;
        this.moduleConfig = null;
    }

    public String getImplFactoryClass() {
        return this.implFactoryClass;
    }
    

    public void setImplFactoryClass(String implFactoryClass) {
        this.implFactoryClass = implFactoryClass;
    }
    

    public String getStrutsPluginFactoryClass() {
        return this.strutsPluginFactoryClass;
    }
    

    public void setStrutsPluginFactoryClass(String strutsPluginFactoryClass) {
        this.strutsPluginFactoryClass = strutsPluginFactoryClass;
    }


    public String getBundle() {
        return this.bundle;
    }
    


    public void setBundle(String bundle) {
        this.bundle = bundle;
    }
    


    public String getKey() {
        return this.key;
    }
    


    public void setKey(String key) {
        this.key = key;
    }


    public String getImplClass() {
        return this.implClass;
    }
    


    public void setImplClass(String implClass) {
        this.implClass = implClass;
    }
    
    
    
    
}
