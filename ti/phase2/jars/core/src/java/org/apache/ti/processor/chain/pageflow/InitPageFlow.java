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
package org.apache.ti.processor.chain.pageflow;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.ti.core.urls.TemplatedURLFormatter;
import org.apache.ti.core.urltemplates.URLTemplatesFactory;
import org.apache.ti.pageflow.ContainerAdapter;
import org.apache.ti.pageflow.FacesBackingBeanFactory;
import org.apache.ti.pageflow.FlowControllerFactory;
import org.apache.ti.pageflow.RequestParameterHandler;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.interceptor.action.ActionInterceptorContext;
import org.apache.ti.pageflow.interceptor.request.RequestInterceptorContext;
import org.apache.ti.pageflow.internal.AdapterManager;
import org.apache.ti.pageflow.internal.DefaultTemplatedURLFormatter;
import org.apache.ti.pageflow.internal.DefaultURLTemplatesFactory;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.ProcessPopulate;
import org.apache.ti.processor.chain.InitXWork;
import org.apache.ti.util.SourceResolver;
import org.apache.ti.util.config.ConfigInitializationException;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.PrefixHandlerConfig;
import org.apache.ti.util.xml.XmlInputStreamResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Map;

public class InitPageFlow
        implements Command {
    private static final Log log = LogFactory.getLog(InitXWork.class);
    private static final String ALREADY_INIT_ATTR = InternalConstants.ATTR_PREFIX + "contextInit";
    private SourceResolver _sourceResolver;
    private Handlers _handlers;

    private static class TransientFlag
            implements Serializable {
        private static final long serialVersionUID = 1;
        private transient boolean _flag;

        public TransientFlag() {
            _flag = true;
        }

        public boolean isSet() {
            return _flag;
        }
    }

    public boolean execute(Context context) throws Exception {
        // TODO: turn this whole thing into a chain
        log.debug("Initializing Page Flow");

        WebContext webContext = (WebContext) context;
        Map appScope = webContext.getApplicationScope();

        // If the flag is present, but was serialized, then the webapp was redeployed.  At this point, we want
        // to go through the init logic again.
        TransientFlag flag = (TransientFlag) appScope.get(ALREADY_INIT_ATTR);

        if ((flag != null) && flag.isSet()) {
            return false;
        }

        appScope.put(ALREADY_INIT_ATTR, new TransientFlag());

        //
        // Initialize the config file, unless it's already initialized.  This can happen because the scope for the 
        // config (static) isn't the same as the scope for PageFlowActionServlet, which may get created and destroyed
        // within a classloader (which is the case during StrutsTestCase tests).
        //
        if (!ConfigUtil.isInit()) {
            try {
                ConfigUtil.init(new NetUIConfigResolver(webContext));
            } catch (ConfigInitializationException e) {
                log.fatal("Could not initialize from " + InternalConstants.NETUI_CONFIG_PATH, e);

                IllegalStateException ie = new IllegalStateException("Could not initialize from " +
                                                                     InternalConstants.NETUI_CONFIG_PATH);
                ie.initCause(e);
                throw ie;
            }
        }

        ContainerAdapter containerAdapter = AdapterManager.init(webContext);
        _handlers.initApplication(appScope);
        FlowControllerFactory.init(appScope);
        FacesBackingBeanFactory.init(appScope);
        initPrefixHandlers();

        // Create a URLTemplatesFactory (may be container specific from the ContainerAdapter) and the the default
        // TemplatedURLFormatter (registered in the netui config). These classes are used by the URLRewriterService.
        TemplatedURLFormatter formatter = TemplatedURLFormatter.initApplication(appScope, new DefaultTemplatedURLFormatter());
        URLTemplatesFactory.initApplication(webContext, new DefaultURLTemplatesFactory(), formatter, containerAdapter,
                                            _sourceResolver);

        //
        // Initialize the request interceptors and action interceptors.
        //
        ActionInterceptorContext.init(appScope);
        RequestInterceptorContext.init(appScope);

        return false;
    }

    /**
     * This method will initialize all of the PrefixHandlers registered in the netui config.
     * The prefix handlers are registered with ProcessPopulate and are typically implemented as
     * public inner classes in the tags that require prefix handlers.
     */
    private static void initPrefixHandlers() {
        PrefixHandlerConfig[] prefixHandlers = ConfigUtil.getConfig().getPrefixHandlers();

        if (prefixHandlers == null) {
            return;
        }

        for (int i = 0; i < prefixHandlers.length; i++) {
            try {
                Class prefixClass = Class.forName(prefixHandlers[i].getHandlerClass());
                String name = prefixHandlers[i].getName();

                if ((name == null) || name.equals("")) {
                    log.warn("The name for the prefix handler '" + prefixHandlers[i].getHandlerClass() + "' must not be null");

                    continue;
                }

                Object o = prefixClass.newInstance();

                if (!(o instanceof RequestParameterHandler)) {
                    log.warn("The class '" + prefixHandlers[i].getHandlerClass() +
                             "' must be an instance of RequestParameterHandler");

                    continue;
                }

                ProcessPopulate.registerPrefixHandler(name, (RequestParameterHandler) o);
            } catch (ClassNotFoundException e) {
                log.warn("Class '" + prefixHandlers[i].getHandlerClass() + "' not found", e);
            } catch (IllegalAccessException e) {
                log.warn("Illegal access on Class '" + prefixHandlers[i].getHandlerClass() + "'", e);
            } catch (InstantiationException e) {
                log.warn("InstantiationException on Class '" + prefixHandlers[i].getHandlerClass() + "'", e.getCause());
            }
        }
    }

    public void setHandlers(Handlers handlers) {
        _handlers = handlers;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        _sourceResolver = sourceResolver;
    }

    private class NetUIConfigResolver
            extends XmlInputStreamResolver {
        private WebContext _webContext = null;

        private NetUIConfigResolver(WebContext servletContext) {
            _webContext = servletContext;
        }

        public String getResourcePath() {
            return InternalConstants.NETUI_CONFIG_PATH;
        }

        public InputStream getInputStream() throws IOException {
            try {
                URL url = _sourceResolver.resolve(getResourcePath(), _webContext);

                return (url != null) ? url.openStream() : null;
            } catch (MalformedURLException e) {
                assert false : "should never get MalformedURLException here";

                return null;
            }
        }
    }
}
