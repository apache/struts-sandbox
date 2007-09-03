package org.apache.struts2.webwork2;

import java.io.IOException;
import java.net.URL;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;
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
import com.opensymphony.xwork2.util.location.LocationImpl;

public class WebWorkConfigurationProvider implements ConfigurationProvider {

    public void destroy() {
    }

    public void init(Configuration configuration) throws ConfigurationException {
    }

    public void loadPackages() throws ConfigurationException {
    }

    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props)
            throws ConfigurationException {
        URL url = ClassLoaderUtil.getResource("webwork.properties", WebWorkConfigurationProvider.class);
        LocatableProperties locProps = new LocatableProperties(new LocationImpl("webwork.properties", url.toString()));
        try {
            locProps.load(url.openStream());
        } catch (IOException e) {
            throw new ConfigurationException("Unable to load webwork.properties");
        }
        for (Object key : locProps.keySet()) {
            props.setProperty(
                    key.toString().replaceFirst("webwork\\.", "struts."), 
                    locProps.getProperty(key.toString()), 
                    locProps.getPropertyLocation(key.toString()));
        }
    }

}
