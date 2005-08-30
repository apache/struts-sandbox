/*
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
 * $Header:$
 */
package org.apache.ti.servlet;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.UrlResource;
import org.apache.ti.util.ServletSourceResolver;
import org.apache.ti.processor.RequestProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.chain.web.servlet.ServletWebContext;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.ServletContext;
import java.net.URL;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Iterator;

// This class is package-protected -- only exposed to our Servlet/Filters for now.
/**
 * Manages config (Spring and Properties) for Ti Servlets/Filters
 */ 
class ServletConfiguration {
    
    private static Log log = LogFactory.getLog(ServletConfiguration.class);
    private static final String CONFIG_KEY = "ti.servletConfig";
    
    protected String springConfig = "org/apache/ti/config/spring-config-servlet.xml";
    protected BeanFactory beanFactory = null;
    protected Properties tiProps = new Properties();
    protected String tiConfig = "ti.properties";
    protected String tiDefaultsConfig = "ti-defaults.properties";
    
    
    public static ServletConfiguration init(ServletContext servletContext) throws ServletException {
        ServletConfiguration servletConfiguration = (ServletConfiguration) servletContext.getAttribute(CONFIG_KEY);
        if (servletConfiguration == null) {
            servletConfiguration = new ServletConfiguration(servletContext);
            servletContext.setAttribute(CONFIG_KEY, servletConfiguration);
        }
        return servletConfiguration;
    }
    
    public static ServletConfiguration get(ServletContext servletContext) {
        ServletConfiguration servletConfiguration = (ServletConfiguration) servletContext.getAttribute(CONFIG_KEY);
        assert servletConfiguration != null : "ServletConfiguration not found in context under attr " + CONFIG_KEY;
        return servletConfiguration;
    }

    public RequestProcessor createRequestProcessor(ServletContext servletContext, String processorName, Map params) {
        Map initParameters = new HashMap();
        if (params != null) {
            initParameters.putAll(params);
        }
        RequestProcessor processor = (RequestProcessor) beanFactory.getBean(processorName);
        processor.init(initParameters, new ServletWebContext(servletContext, null, null)); 
        return processor;
    }
    
    protected ServletConfiguration(ServletContext servletContext) throws ServletException {
        initProperties(servletContext);
        initSpring(servletContext);
    }
    
    protected void initProperties(ServletContext servletContext) throws ServletException {
        String s = servletContext.getInitParameter("tiConfig");
        if (s != null) {
            tiConfig = s;
        }    
        try {
            // First, load defaults
            URL tiDefaultsConfigURL = ServletSourceResolver.resolve(tiDefaultsConfig, true, servletContext);
            tiProps.load(tiDefaultsConfigURL.openStream());

            // Next, load from web.xml
            String key;
            for (Enumeration e = servletContext.getInitParameterNames(); e.hasMoreElements();) {
                key = (String) e.nextElement();
                tiProps.put("ti."+key, servletContext.getInitParameter(key));
            }
            
            // Finally, load from user's properties file 
            URL resource = ServletSourceResolver.resolve(tiConfig, false, servletContext);
            if (resource != null) {
                tiProps.load(resource.openStream());
             }
        } catch(IOException ex) {
            log.error("Unable to load properties", ex);
            throw new UnavailableException("Unable to load properties:"+ex.getMessage());
        }

        // Resolve all properties ending with Path against the servlet context, if possibile
        String key, value;
        String realPath;
        for (Iterator i = tiProps.keySet().iterator(); i.hasNext(); ) {
            key = (String) i.next();
            if (key.startsWith("ti.") && key.endsWith("Path")) {
                value = tiProps.getProperty(key);
                if (value != null && value.length() > 0) {
                    realPath = servletContext.getRealPath(value);
                    if (realPath != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Resolved "+value+" as "+realPath+" for key "+key);
                        }    
                        tiProps.setProperty(key, realPath);
                    } else {
                        log.info("Unable to resolve path "+value+" for key "+key);
                    }
                }    
            }
        }    
    }    
                    
    protected void initSpring(ServletContext servletContext) throws ServletException {
        // Parse the configuration file specified by path or resource
        try {
            String paths = servletContext.getInitParameter("springConfig");
            if (paths != null) {
                springConfig = paths;
            }

            URL resource = ServletSourceResolver.resolve(springConfig, true, servletContext);
            log.info("Loading spring configuration from " + resource);
            beanFactory = new XmlBeanFactory(new UrlResource(resource));

            // create placeholderconfigurer to bring in some property
            // values from a Properties file
            PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
            cfg.setProperties(tiProps);
            // now actually do the replacement
            cfg.postProcessBeanFactory((XmlBeanFactory)beanFactory);
            
        } catch (Exception e) {
            String msg = "Exception loading spring configuration";
            log.error(msg, e);
            throw new UnavailableException(msg);
        }
    }
}
