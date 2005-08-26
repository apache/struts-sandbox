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
package org.apache.ti.pageflow;

import org.apache.commons.chain.web.WebContext;
import org.apache.ti.core.factory.Factory;
import org.apache.ti.core.factory.FactoryConfig;
import org.apache.ti.pageflow.internal.PageFlowBeanContext;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.logging.Logger;

import javax.security.auth.login.LoginException;


/**
 * Default implementation of a container adapter.
 */
public abstract class DefaultContainerAdapter
        implements ContainerAdapter {

    private static final Logger _log = Logger.getInstance(DefaultContainerAdapter.class);

    private static boolean _productionMode = true;

    private PageFlowEventReporter _eventReporter;

    static {
        String productionModeFlag = System.getProperty("beehive.productionmode");

        if (productionModeFlag != null) {
            _productionMode = Boolean.valueOf(productionModeFlag).booleanValue();
        } else {
            //
            // This is our default definition of "production mode": when asserts are disabled (the following statement
            // sets _productionMode to false when asserts are enabled).
            //
            assert (_productionMode = false) || true;
        }
    }

    protected DefaultContainerAdapter() {
    }

    /**
     * Tell whether the system is in production mode.
     *
     * @return <code>true</code> if the system property "beehive.productionmode" is set to "true", or if asserts are
     *         disabled for this class in the case where the system property has no value; <code>false</code>  if the
     *         system property is set to "false", or if asserts are enabled for this class in the case where the
     *         system property has no value.
     */
    public boolean isInProductionMode() {
        return _productionMode;
    }

    /**
     * Tell whether a web application resource requires a secure transport protocol.  This default implementation
     * simply returns {@link SecurityProtocol#UNSPECIFIED} for all paths.
     *
     * @param path a webapp-relative path for a resource.
     * @return {@link SecurityProtocol#UNSPECIFIED}.
     */
    public SecurityProtocol getSecurityProtocol(String path) {
        // TODO: implement this based on parsing of web.xml
        return SecurityProtocol.UNSPECIFIED;
    }

    /**
     * Cause the server to do a security check for the given path.  This default implementation does nothing.
     *
     * @return <code>false</code>
     */
    public boolean doSecurityRedirect(String path) {
        return false;
    }

    /**
     * Get the port on which the server is listening for unsecure connections.  This default implementation always
     * returns <code>-1</code>.
     *
     * @return <code>-1</code>.
     */
    public int getListenPort() {
        // TODO: have a configuration in netui-config.xml to specify this; an alternative to having to have an adapter.
        return -1;
    }

    /**
     * Get the port on which the server is listening for secure connections.  This default implementation always
     * returns <code>-1</code>.
     *
     * @return <code>-1</code>.
     */
    public int getSecureListenPort() {
        // TODO: have a configuration in netui-config.xml to specify this; an alternative to having to have an adapter.
        return -1;
    }

    /**
     * Log in the user, using "weak" username/password authentication.  This default implementation always throws
     * {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException in all cases.
     */
    public void login(String username, String password)
            throws LoginException {
        throw new UnsupportedOperationException("login is not supported by "
                + DefaultContainerAdapter.class.getName());
    }

    /**
     * Log out the user.  This default implementation always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException in all cases.
     */
    public void logout(boolean invalidateSessions) {
        throw new UnsupportedOperationException("logout is not supported by "
                + DefaultContainerAdapter.class.getName());
    }

    public String getFullContextPath() {
        return PageFlowActionContext.get().getRequestContextPath();
    }

    /**
     * Ensure that the given session attribute is replicated in a cluster for session failover.
     * This method does not need to be implemented for servers that do not support clustering and
     * session failover.  The default implementation does nothing.
     *
     * @param attrName the name of the session attribute for which failover should be ensured.
     * @param attrVal  the value of the given session attribute.
     */
    public void ensureFailover(String attrName, Object attrVal) {
    }

    /**
     * Called at the beginning of each processed request.  This default implementation does nothing.
     */
    public void beginRequest() {
    }

    /**
     * Called at the end of each processed request.  This default implementation does nothing.
     */
    public void endRequest() {
    }

    /**
     * Get a context object to support Beehive Controls.  This default implementation returns an instance of
     * {@link PageFlowBeanContext}.
     *
     * @return a new ControlBeanContext.
     */
    public Object createControlBeanContext() {
        return new PageFlowBeanContext();
    }

    /**
     * Set the AdapterContext.
     *
     * @param context the AdapterContext to set.
     */
    public void initialize(WebContext context) {
        _eventReporter = createEventReporter(context);
    }

    /**
     * Get the name of the platform, which may be used to find platform-specific configuration files.  This default
     * implementation returns "generic".
     *
     * @return the name of the platform ("generic" in this default implementation).
     */
    public String getPlatformName() {
        return "generic";
    }

    /**
     * Get an event reporter, which will be notified of events like "page flow created", "action raised", etc.
     * This default implementation returns an instance of {@link DefaultPageFlowEventReporter}.
     *
     * @return a {@link PageFlowEventReporter}.
     */
    public PageFlowEventReporter getEventReporter() {
        return _eventReporter;
    }

    protected PageFlowEventReporter createEventReporter(WebContext context) {
        return new DefaultPageFlowEventReporter();
    }

    /**
     * Generic method to get a Factory class that may be container dependent.
     * <p/>
     * <p/>
     * This method is called to get the following Factory implementations:
     * </p>
     * <ul>
     * <li>{@link org.apache.ti.core.urltemplates.URLTemplatesFactory}</li>
     * </ul>
     *
     * @param factoryType the class type that the factory should extend or implement
     * @param id          can be used for the case where there is more than one possible Factory
     *                    that extends or implaments the class type.
     * @param config      a configuration object passed to a {@link org.apache.ti.core.factory.Factory}
     * @return a Factory class that extends or implemtents the given class type.
     */
    public Factory getFactory(Class factoryType, String id, FactoryConfig config) {
        return null;
    }
}
