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

import org.apache.ti.schema.config.NetuiConfigDocument;
import org.apache.ti.schema.config.NetuiConfigDocument.NetuiConfig;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * <p/>
 * Utility class for reading properties from the NetUI configuration file.
 * <br/>
 * <br/>
 * The webapp runtime is read from the URL passed to the {@link #init} method.
 * The configuration should be initialized with this method and a valid URL
 * before the first time the {@link #getConfig()} method is called.  If the configuration
 * has not been initialized, {@link #getConfig()} will initialize a bare bones runtime
 * configuration.  Depending on the web application, this default configuration
 * may lead to runtime errors.
 * <br/>
 * <br/>
 * </p>
 */
public class ConfigUtil {

    // @todo: need to change NetuiConfigDocument.NetuiConfig to NetUIConfig
    // @todo: need to provide a read-only implementation so that users can't edit the config file on the fly

    private static final Logger LOGGER = Logger.getInstance(ConfigUtil.class);

    private static final String DEFAULT_CONFIG = "org/apache/ti/util/config/struts-ti-config-default.xml";

    private static NetuiConfigDocument _config = null;

    /* do not construct */
    protected ConfigUtil() {
    }

    /**
     * <p/>
     * Initialize the NetUI configuration data.
     * <br/>
     * <br/>
     * This method can be called exactly once in a J2EE web application.  The
     * URL parameter should reference a
     * netui-config.xml file.  If an error occurs loading the configuration
     * file, a {@link ConfigInitializationException} will be thrown.
     * </p>
     *
     * @param url the URL from which to read the configuration file
     * @throws ConfigInitializationException thrown when an error occurs loading the configuration file
     *                                       or when the configuration is reinitialized.
     */
    public static final void init(URL url)
            throws ConfigInitializationException {
        if (_config != null)
            throw new ConfigInitializationException("Config initialization already completed; unable to reload the NetUI config file.");

        internalInit(url);
    }

    public static final boolean isInit() {
        return (_config != null);
    }

    /**
     * Internal method used to re-initialize the static class member that holds the
     * ConfigDocument.  Note, this method does <b>no</b> checks to ensure that an
     * existing document is being overwritten.  The behavior of ConfigUtil clients
     * is undefined if their initial configuration is re-loaded.
     *
     * @param url The URL that contains the config document to load
     * @throws ConfigInitializationException thrown when an error occurs loading the
     *                                       configuration file.
     */
    protected static final void internalInit(URL url)
            throws ConfigInitializationException {

        // when initialized with a null URL, revert to using a default, barebones config file
        if (url == null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            url = cl.getResource(DEFAULT_CONFIG);

            if (url == null)
                throw new ConfigInitializationException("The NetUI runtime could not find the default config file.  " +
                        "The webapp may not function properly.");

            if (LOGGER.isInfoEnabled())
                LOGGER.info("Loading the default NetUI config file.  The runtime will be configured " +
                        "with a set of minimum parameters.");
        }

        if (_config == null) {
            try {
                XmlOptions loadOptions = new XmlOptions();
                loadOptions.setLoadLineNumbers();
                _config = NetuiConfigDocument.Factory.parse(url, loadOptions);
            }
                    // XmlException | IOException
            catch (Exception ex) {
                assert ex instanceof XmlException || ex instanceof IOException;

                throw new ConfigInitializationException("Unable load the NetUI config file.  Cause: " + ex, ex);
            }
        }

        assert _config != null;

        // Validate the document.
        XmlOptions validateOptions = new XmlOptions();
        ArrayList errorList = new ArrayList();
        validateOptions.setErrorListener(errorList);
        boolean isValid = _config.validate(validateOptions);

        // Throw an exception if the XML is invalid.
        if (!isValid) {
            InternalStringBuilder msg = new InternalStringBuilder("Invalid NetUI configuration file.");

            for (int i = 0; i < errorList.size(); i++) {
                XmlError error = (XmlError) errorList.get(i);
                msg.append("\n    line ");
                msg.append(error.getLine());
                msg.append(": ");
                msg.append(error.getMessage());
                msg.append(" (");
                msg.append(error.getCursorLocation().toString());
                msg.append(")");
            }

            throw new ConfigInitializationException(msg.toString());
        }
    }

    /**
     * Get the NetUI configuration object.
     *
     * @return a configuration bean that contains data
     *         parsed from the netui-config.xml file.
     */
    public static NetuiConfig getConfig() {
        if (_config != null) {
            return _config.getNetuiConfig();
        }
        /*
          If the config file wasn't initialized, attempt to initialize a configuration
          from the default config file contained in the utility JAR.
         */
        else {
            /*
              This hopefully never happens and would only occur if the default config file isn't found in the util JAR.
             */
            if (LOGGER.isErrorEnabled())
                LOGGER.error("An error occurred parsing the default config file.  " +
                        "The NetUI runtime is not properly configured.");

            throw new IllegalStateException("The NetUI runtime could not find the default config file.  The webapp may not function properly.");
        }
    }
}
