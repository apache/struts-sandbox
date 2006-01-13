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

import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.commons.resources.Resources;
import org.apache.commons.resources.ResourcesFactory;
import org.apache.commons.resources.impl.WebappResourcesFactoryBase;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.RequestUtils;

/**
 * Concrete subclass of <code>MessageResources</code> that wraps an
 * instance of an impl of the supplied 
 * <code>org.apache.commons.resources.Resources</code> interface.
 *
 * @version $Rev$ $Date$
 */
public class CommonsResources extends MessageResources{

    protected Resources resources;
    
    /**
     * The default Locale for our environment.
     */
    protected Locale defaultLocale = Locale.getDefault();
    
    
    public CommonsResources(MessageResourcesFactory factory,
            ServletContext servletContext,
            String implFactoryClass, String resourcesImpl, 
            String config, boolean returnNull) throws Exception{
        super(factory, config, returnNull);
        
        try {
            ResourcesFactory commonsFactory = 
                (ResourcesFactory)RequestUtils.applicationInstance(implFactoryClass);
            if (commonsFactory instanceof WebappResourcesFactoryBase) {
                WebappResourcesFactoryBase factoryBase = (WebappResourcesFactoryBase) commonsFactory;
                factoryBase.setServletContext(servletContext);
            }
            resources = commonsFactory.getResources(resourcesImpl, config);
            
        } catch (Exception e) {
            // log output
            log.debug(e.getMessage());
            throw e;
        }
    }
        
    public CommonsResources(MessageResourcesFactory factory, 
            String config, boolean returnNull) {
        
        super(factory, config, returnNull);
        
    }

    public CommonsResources(MessageResourcesFactory factory, 
            String config) {
        
        super(factory, config);
        
    }

    public String getMessage(Locale locale, String key) {
        
        // explicitly clear the cached on each call
        // we will cache them down in the impl
        // such that any subclass may provide it's 
        // own reload capabilities
        formats.clear();
        return resources.getString(key, locale, null);
        
        
    }

    
    
}
