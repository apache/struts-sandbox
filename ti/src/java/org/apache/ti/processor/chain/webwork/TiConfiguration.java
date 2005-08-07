/*
 * $Id: Init.java 230535 2005-08-06 07:56:40Z mrdon $
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
package org.apache.ti.processor.chain.webwork;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.config.*;
import com.opensymphony.xwork.util.LocalizedTextUtil;

/**
 *  Initializes  by replacing default factories
 */
public class TiConfiguration extends DefaultConfiguration {

    private static final Log log = LogFactory.getLog(TiConfiguration.class);

    private static final String WEBWORK_PREFIX = "webwork.";
    private static final String TI_PREFIX = "ti.";
    
    private Properties props;
    private Configuration config;
    
    public TiConfiguration(Map config) {
        props = new Properties();
        Map.Entry entry;
        String key, newkey;
        for (Iterator i = config.entrySet().iterator(); i.hasNext(); ) {
            entry = (Map.Entry)i.next();
            key = (String)entry.getKey();
            if (key.startsWith(TI_PREFIX) && entry.getValue() instanceof String) {
                newkey = WEBWORK_PREFIX + key.substring(TI_PREFIX.length());
                props.setProperty(newkey, (String)entry.getValue());
            }
        }    
        init();
    }
    
    /**
     * Creates a new DefaultConfiguration object by loading all property files
     * and creating an internal {@link DelegatingConfiguration} object. All calls to get and set
     * in this class will call that configuration object.
     */
    private void init() {
        // Create default implementations 
        // Use default properties and webwork.properties
        ArrayList list = new ArrayList();

        try {
            list.add(new PropertiesConfiguration("com/opensymphony/webwork/default"));
        } catch (Exception e) {
            log.error("Could not find com/opensymphony/webwork/default.properties", e);
        }

        Configuration[] configList = new Configuration[list.size()];
        config = new DelegatingConfiguration((Configuration[]) list.toArray(configList));

        // Add list of additional properties configurations
        try {
            StringTokenizer configFiles = new StringTokenizer((String) config.getImpl("webwork.custom.properties"), ",");

            while (configFiles.hasMoreTokens()) {
                String name = configFiles.nextToken();

                try {
                    list.add(new PropertiesConfiguration(name));
                } catch (Exception e) {
                    log.error("Could not find " + name + ".properties. Skipping");
                }
            }

            configList = new Configuration[list.size()];
            config = new DelegatingConfiguration((Configuration[]) list.toArray(configList));
        } catch (IllegalArgumentException e) {
        }

        // Add addtional list of i18n global resource bundles
        try {
            StringTokenizer bundleFiles = new StringTokenizer((String) config.getImpl("webwork.custom.i18n.resources"), ", ");

            while (bundleFiles.hasMoreTokens()) {
                String name = bundleFiles.nextToken();

                try {
                    log.info("Loading global messages from " + name);
                    LocalizedTextUtil.addDefaultResourceBundle(name);
                } catch (Exception e) {
                    log.error("Could not find " + name + ".properties. Skipping");
                }
            }
        } catch (IllegalArgumentException e) {
            // webwork.custom.i18n.resources wasn't provided
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Sets the given property - delegates to the internal config implementation.
     *
     * @see #set(String, Object)
     */
    public void setImpl(String aName, Object aValue) throws IllegalArgumentException, UnsupportedOperationException {
        config.setImpl(aName, aValue);
    }

    /**
     * Gets the specified property - delegates to the internal config implementation.
     *
     * @see #get(String)
     */
    public Object getImpl(String aName) throws IllegalArgumentException {
        // Delegate
        String val = props.getProperty(aName);
        if (val == null) {
            return config.getImpl(aName);
        } else {
            return val;
        }    
    }

    /**
     * Determines whether or not a value has been set - delegates to the internal config implementation.
     *
     * @see #isSet(String)
     */
    public boolean isSetImpl(String aName) {
        String val = props.getProperty(aName);
        if (val == null) {
            return config.isSetImpl(aName);
        } else {
            return false;
        }
    }

    /**
     * Returns a list of all property names - delegates to the internal config implementation.
     *
     * @see #list()
     */
    public Iterator listImpl() {
        ArrayList settingList = new ArrayList();
        Iterator list = config.listImpl();

        while (list.hasNext()) {
            settingList.add(list.next());
        }
        list = props.keySet().iterator();
        while (list.hasNext()) {
            settingList.add(list.next());
        }
        return settingList.iterator();
    }
}
