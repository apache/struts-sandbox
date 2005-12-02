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
package org.apache.ti.util.config;

import org.apache.ti.util.config.bean.NetUIConfig;
import org.apache.ti.util.config.parser.NetUIConfigParser;
import org.apache.ti.util.logging.Logger;
import org.apache.ti.util.xml.XmlInputStreamResolver;

/**
 * <p>
 * Utility class for reading properties from the NetUI configuration file.
 * </p>
 * <p>
 * The webapp runtime is read from the {@link XmlInputStreamResolver} passed to the
 * {@link #init(XmlInputStreamResolver)} method.  The configuration should be initialized with
 * this method before the first call to the {@link #getConfig()} method.  If the configuration
 * has not been initialized, {@link #getConfig()} will initialize a default, minimal runtime
 * configuration.  Depending on the web application, this default configuration
 * may lead to runtime errors.
 * </p>
 */
public class ConfigUtil {
    private static final Logger LOGGER = Logger.getInstance(ConfigUtil.class);
    private static NetUIConfig CONFIG_BEAN = null;

    /* do not construct */
    protected ConfigUtil() {
    }

    /**
     * <p>
     * Initialize the NetUI configuration JavaBean.
     * </p>
     * <p>
     * This method can be called exactly once in the a given J2EE web application.  The provided
     * {@link XmlInputStreamResolver} is used to resolve an {@link java.io.InputStream} that references
     * a NetUI config file instance.  If an error occurs loading the configuration, a
     * {@link ConfigInitializationException} exception will be thrown.
     * </p>
     *
     * @param xmlInputStreamResolver a resolver that can provide an InputStream to the config file
     * @throws ConfigInitializationException thrown when an error occurs loading the configuration file
     * or when the configuration is reinitialized.
     */
    public static void init(XmlInputStreamResolver xmlInputStreamResolver)
            throws ConfigInitializationException {
        if (CONFIG_BEAN != null) {
            throw new ConfigInitializationException("Config initialization already completed; unable to reload the NetUI config file.");
        }

        internalInit(xmlInputStreamResolver);
    }

    protected static void internalInit(XmlInputStreamResolver xmlInputStreamResolver) {
        NetUIConfigParser configParser = new NetUIConfigParser();
        CONFIG_BEAN = configParser.parse(xmlInputStreamResolver);
    }

    public static boolean isInit() {
        return CONFIG_BEAN != null;
    }

    /**
     * Get the NetUI configuration JavaBean.
     *
     * @return a JavaBean that provides configuration information for the NetUI runtime.
     */
    public static NetUIConfig getConfig() {
        if (CONFIG_BEAN != null) {
            return CONFIG_BEAN;
        }
        /*
          If the config file wasn't initialized, attempt to initialize a configuration
          from the default config file contained in the utility JAR.
         */
        else {
            /*
              This hopefully never happens and would only occur if the default config file isn't found in the util JAR.
             */
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("An error occurred parsing the default config file.  " +
                             "The NetUI runtime is not properly configured.");
            }

            throw new IllegalStateException("The NetUI runtime could not find the default config file.  The webapp may not function properly.");
        }
    }
}
