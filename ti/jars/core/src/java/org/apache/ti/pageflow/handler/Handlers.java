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
package org.apache.ti.pageflow.handler;

import com.opensymphony.xwork.ActionContext;

import org.apache.ti.pageflow.internal.DefaultHandler;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.CustomPropertyConfig;
import org.apache.ti.util.config.bean.NetUIConfig;
import org.apache.ti.util.config.bean.PageFlowHandlersConfig;
import org.apache.ti.util.internal.DiscoveryUtils;
import org.apache.ti.util.logging.Logger;

import java.io.Serializable;

import java.util.Map;

/**
 * ServletContext-scoped container for various Page Flow {@link Handler} instances.
 */
public class Handlers
        implements Serializable {
    private static final long serialVersionUID = 1;
    private static final Logger _log = Logger.getInstance(Handlers.class);
    private static final String CONTEXT_ATTR = InternalConstants.ATTR_PREFIX + "_handlers";
    private ExceptionsHandler _exceptionsHandler = null;
    private ForwardRedirectHandler _forwardRedirectHandler = null;
    private LoginHandler _loginHandler = null;
    private StorageHandler _storageHandler = null;
    private ReloadableClassHandler _reloadableClassHandler = null;
    private ModuleRegistrationHandler _moduleRegistrationHandler = null;
    private AnnotationHandler _annotationHandler = null;
    private DefaultHandler _defaultExceptionsHandler;
    private DefaultHandler _defaultForwardRedirectHandler;
    private DefaultHandler _defaultLoginHandler;
    private DefaultHandler _defaultStorageHandler;
    private DefaultHandler _defaultReloadableClassHandler;
    private DefaultHandler _defaultModuleRegistrationHandler;
    private DefaultHandler _defaultAnnotationHandler;

    public static Handlers get() {
        ActionContext actionContext = ActionContext.getContext();
        Handlers handlers = (Handlers) actionContext.getApplication().get(CONTEXT_ATTR);
        assert handlers != null : "Page Flow Handlers not initialized.";

        return handlers;
    }

    public void initApplication(Map appScope) {
        //
        // Load/create Handlers.
        //
        NetUIConfig netuiConfig = ConfigUtil.getConfig();
        PageFlowHandlersConfig handlers = netuiConfig.getPageFlowHandlers();

        _exceptionsHandler = (ExceptionsHandler) adaptHandler((handlers != null) ? handlers.getExceptionsHandlers() : null,
                                                              _defaultExceptionsHandler, ExceptionsHandler.class);

        _forwardRedirectHandler = (ForwardRedirectHandler) adaptHandler((handlers != null)
                                                                        ? handlers.getForwardRedirectHandlers() : null,
                                                                        _defaultForwardRedirectHandler,
                                                                        ForwardRedirectHandler.class);

        _loginHandler = (LoginHandler) adaptHandler((handlers != null) ? handlers.getLoginHandlers() : null,
                                                    _defaultLoginHandler, LoginHandler.class);

        _storageHandler = (StorageHandler) adaptHandler((handlers != null) ? handlers.getStorageHandlers() : null,
                                                        _defaultStorageHandler, StorageHandler.class);

        _reloadableClassHandler = (ReloadableClassHandler) adaptHandler((handlers != null)
                                                                        ? handlers.getReloadableClassHandlers() : null,
                                                                        _defaultReloadableClassHandler,
                                                                        ReloadableClassHandler.class);

        _moduleRegistrationHandler = (ModuleRegistrationHandler) adaptHandler((handlers != null)
                                                                              ? handlers.getModuleRegistrationHandlers() : null,
                                                                              _defaultModuleRegistrationHandler,
                                                                              ModuleRegistrationHandler.class);

        _annotationHandler = (AnnotationHandler) adaptHandler((handlers != null) ? handlers.getAnnotationHandlers() : null,
                                                              _defaultAnnotationHandler, AnnotationHandler.class);

        appScope.put(CONTEXT_ATTR, this);
    }

    public ExceptionsHandler getExceptionsHandler() {
        return _exceptionsHandler;
    }

    public ForwardRedirectHandler getForwardRedirectHandler() {
        return _forwardRedirectHandler;
    }

    public LoginHandler getLoginHandler() {
        return _loginHandler;
    }

    public StorageHandler getStorageHandler() {
        return _storageHandler;
    }

    public ReloadableClassHandler getReloadableClassHandler() {
        return _reloadableClassHandler;
    }

    public ModuleRegistrationHandler getModuleRegistrationHandler() {
        return _moduleRegistrationHandler;
    }

    public AnnotationHandler getAnnotationHandler() {
        return _annotationHandler;
    }

    private static Handler adaptHandler(org.apache.ti.util.config.bean.HandlerConfig[] handlerBeanConfigs,
                                        DefaultHandler defaultHandler, Class baseClassOrInterface) {
        Handler retVal = defaultHandler;

        if (handlerBeanConfigs != null) {
            for (int i = 0; i < handlerBeanConfigs.length; i++) {
                String handlerClass = handlerBeanConfigs[i].getHandlerClass();
                CustomPropertyConfig[] props = handlerBeanConfigs[i].getCustomProperties();
                Handler handler = createHandler(handlerClass, baseClassOrInterface, retVal);

                if (handler != null) {
                    HandlerConfig config = new HandlerConfig(handlerClass);

                    if (props != null) {
                        for (int j = 0; j < props.length; j++) {
                            CustomPropertyConfig prop = props[j];
                            config.addCustomProperty(prop.getName(), prop.getValue());
                        }
                    }

                    handler.init(config, retVal);
                    retVal = handler;
                }
            }
        }

        defaultHandler.init(null, null);
        defaultHandler.setRegisteredHandler(retVal);

        return retVal;
    }

    /**
     * Instantiates a handler, based on the class name in the given HandlerConfig.
     *
     * @param className            the class name of the desired Handler.
     * @param baseClassOrInterface the required base class or interface.  May be <code>null</code>.
     * @return an initialized Handler.
     */
    private static Handler createHandler(String className, Class baseClassOrInterface, Handler previousHandler) {
        assert Handler.class.isAssignableFrom(baseClassOrInterface) : baseClassOrInterface.getName() + " cannot be assigned to " +
        Handler.class.getName();

        ClassLoader cl = DiscoveryUtils.getClassLoader();

        try {
            Class handlerClass = cl.loadClass(className);

            if (!baseClassOrInterface.isAssignableFrom(handlerClass)) {
                _log.error("Handler " + handlerClass.getName() + " does not implement or extend " +
                           baseClassOrInterface.getName());

                return null;
            }

            Handler handler = (Handler) handlerClass.newInstance();

            // TODO: add a way to set custom props on HandlerConfig
            handler.init(new HandlerConfig(className), previousHandler);

            return handler;
        } catch (ClassNotFoundException e) {
            _log.error("Could not find Handler class " + className, e);
        } catch (InstantiationException e) {
            _log.error("Could not instantiate Handler class " + className, e);
        } catch (IllegalAccessException e) {
            _log.error("Could not instantiate Handler class " + className, e);
        }

        return null;
    }

    public DefaultHandler getDefaultExceptionsHandler() {
        return _defaultExceptionsHandler;
    }

    public void setDefaultExceptionsHandler(DefaultHandler defaultExceptionsHandler) {
        _defaultExceptionsHandler = defaultExceptionsHandler;
    }

    public DefaultHandler getDefaultForwardRedirectHandler() {
        return _defaultForwardRedirectHandler;
    }

    public void setDefaultForwardRedirectHandler(DefaultHandler defaultForwardRedirectHandler) {
        _defaultForwardRedirectHandler = defaultForwardRedirectHandler;
    }

    public DefaultHandler getDefaultLoginHandler() {
        return _defaultLoginHandler;
    }

    public void setDefaultLoginHandler(DefaultHandler defaultLoginHandler) {
        _defaultLoginHandler = defaultLoginHandler;
    }

    public DefaultHandler getDefaultStorageHandler() {
        return _defaultStorageHandler;
    }

    public void setDefaultStorageHandler(DefaultHandler defaultStorageHandler) {
        _defaultStorageHandler = defaultStorageHandler;
    }

    public DefaultHandler getDefaultReloadableClassHandler() {
        return _defaultReloadableClassHandler;
    }

    public void setDefaultReloadableClassHandler(DefaultHandler defaultReloadableClassHandler) {
        _defaultReloadableClassHandler = defaultReloadableClassHandler;
    }

    public DefaultHandler getDefaultModuleRegistrationHandler() {
        return _defaultModuleRegistrationHandler;
    }

    public void setDefaultModuleRegistrationHandler(DefaultHandler defaultModuleRegistrationHandler) {
        _defaultModuleRegistrationHandler = defaultModuleRegistrationHandler;
    }

    public DefaultHandler getDefaultAnnotationHandler() {
        return _defaultAnnotationHandler;
    }

    public void setDefaultAnnotationHandler(DefaultHandler defaultAnnotationHandler) {
        _defaultAnnotationHandler = defaultAnnotationHandler;
    }
}
