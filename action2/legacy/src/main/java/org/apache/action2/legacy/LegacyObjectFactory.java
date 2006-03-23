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

import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.util.ObjectFactoryInitializable;
import com.opensymphony.webwork.util.ServletContextAware;
import com.opensymphony.xwork.config.entities.*;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.interceptor.Interceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 * Adds support for interceptors that want the ServletContext
 */
public class LegacyObjectFactory extends ObjectFactory implements ObjectFactoryInitializable {
    private static final Log log = LogFactory.getLog(LegacyObjectFactory.class);

    private ServletContext servletContext;
    
    public void init(ServletContext servletContext) {
        
        log.info("Initializing legacy integration...");
        this.servletContext = servletContext;
    }
    
    /**
     * Builds an Interceptor from the InterceptorConfig and the Map of
     * parameters from the interceptor reference. Implementations of this method
     * should ensure that the Interceptor is parameterized with both the
     * parameters from the Interceptor config and the interceptor ref Map (the
     * interceptor ref params take precedence), and that the Interceptor.init()
     * method is called on the Interceptor instance before it is returned.
     *
     * @param interceptorConfig    the InterceptorConfig from the configuration
     * @param interceptorRefParams a Map of params provided in the Interceptor reference in the
     *                             Action mapping or InterceptorStack definition
     */
    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map interceptorRefParams) throws ConfigurationException {
        Interceptor interceptor = super.buildInterceptor(interceptorConfig, interceptorRefParams);
        if (interceptor != null && interceptor instanceof ServletContextAware) {
            ((ServletContextAware)interceptor).setServletContext(servletContext);
        }
        return interceptor;
    }
}
