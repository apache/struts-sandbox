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
package com.opensymphony.webwork.dispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Properties;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.opensymphony.xwork2.config.ConfigurationException;

public class FilterDispatcher extends org.apache.struts2.dispatcher.FilterDispatcher {

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(new FilterConfigWrapper(config));
    }
    
    class FilterConfigWrapper implements FilterConfig {

        FilterConfig cfg;
        Map<String,String> extraProps = new HashMap<String,String>();
        
        public FilterConfigWrapper(FilterConfig cfg) {
            this.cfg = cfg;
            extraProps.put("config", "struts-default.xml,struts-plugin.xml,struts.xml,xwork.xml");
            extraProps.put("configProviders", "org.apache.struts2.webwork2.WebWorkConfigurationProvider");
        }
        
        public String getFilterName() {
            return cfg.getFilterName();
        }

        public String getInitParameter(String name) {
            if (extraProps.containsKey(name)) {
                return extraProps.get(name);
            } else {
                return cfg.getInitParameter(name);
            }
        }

        public Enumeration getInitParameterNames() {
            Set list = new HashSet();
            for (Enumeration e = cfg.getInitParameterNames(); e.hasMoreElements(); ) {
                list.add(e.nextElement());
            }
            list.addAll(extraProps.keySet());
            return Collections.enumeration(list);
        }

        public ServletContext getServletContext() {
            return cfg.getServletContext();
        }
    }
}
