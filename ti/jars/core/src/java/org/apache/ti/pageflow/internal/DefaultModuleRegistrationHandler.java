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
package org.apache.ti.pageflow.internal;

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;

import org.apache.commons.chain.web.WebContext;

import org.apache.ti.pageflow.ModuleConfig;
import org.apache.ti.pageflow.ModuleConfigLocator;
import org.apache.ti.pageflow.PageFlowConstants;
import org.apache.ti.pageflow.PageFlowEventReporter;
import org.apache.ti.pageflow.handler.Handler;
import org.apache.ti.pageflow.handler.HandlerConfig;
import org.apache.ti.pageflow.handler.ModuleRegistrationHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.SourceResolver;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.ModuleConfigLocatorConfig;
import org.apache.ti.util.config.bean.PageFlowConfig;
import org.apache.ti.util.internal.DiscoveryUtils;
import org.apache.ti.util.internal.concurrent.InternalConcurrentHashMap;
import org.apache.ti.util.logging.Logger;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Map;

public class DefaultModuleRegistrationHandler
        extends DefaultHandler
        implements ModuleRegistrationHandler {
    private Map /*< String, ModuleConfig >*/ _registeredModules = new InternalConcurrentHashMap /*< String, ModuleConfig >*/();
    private ModuleConfigLocator[] _moduleConfigLocators = null;
    private static final ModuleConfig NONEXISTANT_MODULE_CONFIG = new NonexistantModuleConfig();
    private static final Logger _log = Logger.getInstance(DefaultModuleRegistrationHandler.class);
    private static final String MODULE_METADATA_ACTION_NAME = "_moduleMetadata";
    private static final ModuleConfigLocator[] DEFAULT_MODULE_CONFIG_LOCATORS = new ModuleConfigLocator[] { new DefaultModuleConfigLocator() };
    private SourceResolver _sourceResolver;

    public DefaultModuleRegistrationHandler() {
    }

    public void init(HandlerConfig handlerConfig, Handler previousHandler) {
        setupModuleConfigLocators();
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        _sourceResolver = sourceResolver;
    }

    private static class DefaultModuleConfigLocator
            implements ModuleConfigLocator {
        public String getModuleResourcePath(String moduleName) {
            assert moduleName.startsWith("/") : moduleName;

            return PageFlowConstants.PAGEFLOW_MODULE_CONFIG_GEN_DIR + moduleName + "/pageflow.xml";
        }
    }

    private void setupModuleConfigLocators() {
        ModuleConfigLocator[] defaultLocators = getDefaultModuleConfigLocators();
        ArrayList /*< ModuleConfigLocator >*/ locators = new ArrayList /*< ModuleConfigLocator >*/();

        for (int i = 0; i < defaultLocators.length; ++i) {
            locators.add(defaultLocators[i]);
        }

        //
        // Look for ModuleConfigLocators in struts-ti-config.xml.
        //
        PageFlowConfig pfConfig = ConfigUtil.getConfig().getPageFlowConfig();

        if (pfConfig != null) {
            ModuleConfigLocatorConfig[] mcLocators = pfConfig.getModuleConfigLocators();

            if (mcLocators != null) {
                for (int i = 0; i < mcLocators.length; i++) {
                    addModuleConfigLocator(mcLocators[i].getLocatorClass().trim(), locators);
                }
            }
        }

        _moduleConfigLocators = (ModuleConfigLocator[]) locators.toArray(new ModuleConfigLocator[locators.size()]);
    }

    private static void addModuleConfigLocator(String locatorClassName, ArrayList /*< ModuleConfigLocator >*/ locators) {
        try {
            Class locatorClass = DiscoveryUtils.loadImplementorClass(locatorClassName, ModuleConfigLocator.class);

            if (locatorClass != null) // previous call will log an error if it can't find the class
             {
                ModuleConfigLocator locator = (ModuleConfigLocator) locatorClass.newInstance();
                locators.add(locator);
            }
        } catch (IllegalAccessException e) {
            _log.error("Could not create an instance of specified module-config-locator " + locatorClassName, e);
        } catch (InstantiationException e) {
            _log.error("Could not create an instance of specified module-config-locator " + locatorClassName, e);
        }
    }

    /**
     * Get the base list of ModuleConfigLocators, to specify locations for auto-registered Struts modules.  By default,
     * this list is empty; derived classes may override to provide locators, or the user may specify them using
     * the "moduleConfigLocators" init-parameter.  When an unrecognized Struts module is requested, each registered
     * ModuleConfigLocator is queried for a possible path to the configuration file for the module.  If the
     * configuration file is found, the module is auto-registered against the file.
     */
    protected ModuleConfigLocator[] getDefaultModuleConfigLocators() {
        return DEFAULT_MODULE_CONFIG_LOCATORS;
    }

    /**
     * Get the current list of registered ModuleConfigLocators.
     *
     * @return an array of registered ModuleConfigLocators.
     * @see #getDefaultModuleConfigLocators
     */
    public ModuleConfigLocator[] getModuleConfigLocators() {
        return _moduleConfigLocators;
    }

    /**
     * Get the resource URL the Struts module configration file for a given namespace.
     * based on registered ModuleConfigLocators.
     *
     * @param namespace the namespace of the module.
     * @return a String that is the path to the Struts configuration file, relative to the web application root,
     *         or <code>null</code> if no appropriate configuration file is found.
     * @see #getDefaultModuleConfigLocators
     */
    public URL getModuleConfURL(String namespace) {
        if (_moduleConfigLocators != null) {
            for (int i = 0; i < _moduleConfigLocators.length; ++i) {
                ModuleConfigLocator locator = _moduleConfigLocators[i];
                String moduleConfigPath = locator.getModuleResourcePath(namespace);
                URL url = getModuleConfigURL(moduleConfigPath);

                if (url != null) {
                    return url;
                }
            }
        }

        return null;
    }

    private URL getModuleConfigURL(String fileName) {
        assert fileName.charAt(0) != '/' : fileName;
        fileName = '/' + fileName;

        try {
            WebContext webContext = PageFlowActionContext.get().getWebContext();

            return _sourceResolver.resolve(fileName, webContext);
        } catch (IOException e) {
            _log.error("Could not resolve module config from " + fileName, e);

            return null;
        }
    }

    private class ModuleConfigProvider
            extends XmlConfigurationProvider {
        private URL _moduleConfigURL;

        public ModuleConfigProvider(URL moduleConfigURL) {
            super(moduleConfigURL.toString());
            _moduleConfigURL = moduleConfigURL;
        }

        protected InputStream getInputStream(String fileName) {
            try {
                return _moduleConfigURL.openStream();
            } catch (IOException e) {
                _log.error("Could not load module configuration from " + _moduleConfigURL.toString(), e);

                return null;
            }
        }

        /*`
        public boolean equals(Object o) {
            if (this == o) return true;
            if (! (o instanceof ModuleConfigProvider)) return false;
            return _moduleConfigURL.equals(((ModuleConfigProvider) o)._moduleConfigURL);
        }
        */
    }

    /**
     * Register a Struts module, initialized by the given configuration file.
     *
     * @param namespace     the namespace.
     * @param moduleConfURL the url to the module configuration file.
     * @return the Struts ModuleConfig that was initialized.
     */
    protected synchronized ModuleConfig registerModule(String namespace, URL moduleConfURL) {
        if (_log.isInfoEnabled()) {
            _log.info("Dynamically registering module " + namespace + ", config XML " + moduleConfURL);
        }

        ConfigurationManager.addConfigurationProvider(new ModuleConfigProvider(moduleConfURL));

        Configuration rootConfig = ConfigurationManager.getConfiguration();
        rootConfig.reload();

        Map actionConfigsByNamespace = rootConfig.getRuntimeConfiguration().getActionConfigs();
        Map actionConfigs = (Map) actionConfigsByNamespace.get(namespace);
        assert actionConfigs != null : "could not load action configs for namespace " + namespace;

        // TODO: This is a hack.  We write general module metadata to a special action.  We simply need params
        // at the module level.
        ActionConfig moduleMetadata = (ActionConfig) actionConfigs.get(MODULE_METADATA_ACTION_NAME);
        assert moduleMetadata != null : "Could not find module-metadata action " + MODULE_METADATA_ACTION_NAME + " for module " +
        namespace + '.';

        ModuleConfig moduleConfig = new ModuleConfig(namespace, moduleMetadata, actionConfigs);

        // Make a callback to the event reporter.
        PageFlowEventReporter er = AdapterManager.getContainerAdapter().getEventReporter();
        er.flowControllerRegistered(moduleConfig);

        if (_log.isDebugEnabled()) {
            _log.debug("Finished registering module " + namespace + ", config XML " + moduleConfURL);
        }

        return moduleConfig;
    }

    /**
     * Tell whether the given module can handle the given path.  By default, this is always true.
     */
    protected boolean moduleCanHandlePath(ModuleConfig moduleConfig, String servletPath) {
        return true;
    }

    void ensureModuleSelected(String namespace) {
        ModuleConfig moduleConfig = getModuleConfig(namespace);

        if (moduleConfig != null) {
            InternalUtils.selectModule(moduleConfig);
        }
    }

    public void clearRegisteredModules() {
        _registeredModules.clear();
    }

    /**
     * Get the ModuleConfig for the given namespace (registering the module dynamically if necessary).
     *
     * @param namespace the namespace.
     * @return the ModuleConfig that corresponds with <code>namespace</code>
     */
    public ModuleConfig getModuleConfig(String namespace) {
        assert namespace != null;

        //
        // Dynamically register the module, if appropriate.  If we've already
        // tried to register it (_registeredModules.containsKey( namespace )), don't
        // try again.
        //
        // Note that two threads could potentially get in here at the same time, and
        // both will register the module.  This is OK -- reads from _registeredModules
        // are consistent, and the worst that will happen is that the module will get
        // registered with a valid ModuleConfig a few times.
        //
        ModuleConfig mc = (ModuleConfig) _registeredModules.get(namespace);

        if (mc == null) {
            //
            // If we find the config file for this module, we can dynamically register it.
            //
            URL moduleConfURL = getModuleConfURL(namespace);

            if (moduleConfURL != null) {
                mc = registerModule(namespace, moduleConfURL);
            }

            if (mc == null) {
                _registeredModules.put(namespace, NONEXISTANT_MODULE_CONFIG);

                // ConcurrentHashMap doesn't allow null values
            } else {
                _registeredModules.put(namespace, mc);
            }
        }

        return mc;
    }

    private static class NonexistantModuleConfig
            extends ModuleConfig {
        public NonexistantModuleConfig() {
            super();
        }
    }
}
