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

import org.apache.ti.core.factory.Factory;
import org.apache.ti.core.factory.FactoryConfig;
import org.apache.ti.pageflow.adapter.Adapter;

import javax.security.auth.login.LoginException;


/**
 * Adapter interface for plugging into various containers.  An implementor of this interface is "discovered" at
 * runtime.  The discovery process is as follows:
 * <ul>
 * <li>
 * A list of META-INF/services/org.apache.ti.pageflow.ContainerAdapter resources is obtained
 * from classpath.  This means, for example, that a file called
 * "org.apache.ti.pageflow.ContainerAdapter" under directory META-INF/services would be
 * found inside any JAR file on classpath.
 * </li>
 * <li>
 * Inside each of these resources is the name of a class that implements ContainerAdapter.  This class
 * is loaded, and its {@link #accept accept} method is called.
 * </li>
 * <li>
 * If {@link #accept accept} returns <code>true</code>, then that implementation class is chosen as the current
 * adapter; otherwise, the next one in the list is tried.
 * </li>
 * <li>If no adapters are discovered, an instance of {@link DefaultContainerAdapter} is used.
 * </ul>
 */
public interface ContainerAdapter
        extends Adapter {

    /**
     * Tell whether the server is running in production mode.
     *
     * @return <code>true</code> if the server is running in production mode.
     */
    public boolean isInProductionMode();

    /**
     * Tell whether a web application resource requires a secure transport protocol.  This is
     * determined from web.xml; for example, the following block specifies that all resources under
     * /login require a secure transport protocol.
     * <pre>
     *    &lt;security-constraint&gt;
     *        &lt;web-resource-collection&gt;
     *          &lt;web-resource-name&gt;Secure PageFlow - begin&lt;/web-resource-name&gt;
     *          &lt;url-pattern&gt;/login/*&lt;/url-pattern&gt;
     *        &lt;/web-resource-collection&gt;
     *        &lt;user-data-constraint&gt;
     *           &lt;transport-guarantee&gt;CONFIDENTIAL&lt;/transport-guarantee&gt;
     *        &lt;/user-data-constraint&gt;
     *    &lt;/security-constraint&gt;
     * </pre>
     *
     * @param path a webapp-relative path for a resource.
     * @return <code>Boolean.TRUE</code> if a transport-guarantee of <code>CONFIDENTIAL</code> or
     *         <code>INTEGRAL</code> is associated with the given resource; <code>Boolean.FALSE</code>
     *         a transport-guarantee of <code>NONE</code> is associated with the given resource; or
     *         <code>null</code> if there is no transport-guarantee associated with the given resource.
     */
    public SecurityProtocol getSecurityProtocol(String path);

    /**
     * Cause the server to do a security check for the given path.  If required, it does a redirect to
     * change the scheme (http/https).
     *
     * @param path the webapp-relative path on which to run security checks.
     * @return <code>true</code> if a redirect occurred.
     */
    boolean doSecurityRedirect(String path);


    /**
     * Get the port on which the server is listening for unsecure connections.
     *
     * @return the port on which the server is listening for unsecure connections.
     */
    public int getListenPort();

    /**
     * Get the port on which the server is listening for secure connections.
     *
     * @return the port on which the server is listening for secure connections.
     */
    public int getSecureListenPort();

    /**
     * Log in the user, using "weak" username/password authentication.
     *
     * @param username the user's login name.
     * @param password the user's password.
     * @throws LoginException if the authentication failed
     */
    public void login(String username, String password)
            throws LoginException;

    /**
     * Log out the current user.
     *
     * @param invalidateSessions if <code>true</code>, the session is invalidated (on all single-signon webapps);
     *                           otherwise the session and its data are left intact.  To invalidate the session in only the
     *                           current webapp, set this parameter to <code>false</code> and call invalidate() on the HttpSession.
     */
    public void logout(boolean invalidateSessions);

    /**
     * Return the webapp context path for the given request.  This differs from HttpServletRequest.getContextPath()
     * only in that it will return a valid value even if the request is for the default webapp.
     */
    public String getFullContextPath();

    /**
     * Ensure that the given session attribute is replicated in a cluster for session failover.
     * This method does not need to be implemented for servers that do not support clustering and
     * session failover.
     *
     * @param attrName the name of the session attribute for which failover should be ensured.
     * @param attrVal  the value of the given session attribute.
     */
    public void ensureFailover(String attrName, Object attrVal);

    /**
     * Called at the beginning of each processed request.
     */
    public void beginRequest();

    /**
     * Called at the end of each processed request.
     */
    public void endRequest();

    /**
     * Get a context object to support Beehive Controls.
     *
     * @return a new ControlBeanContext.
     */
    public Object createControlBeanContext();

    /**
     * Get the name of the platform, which may be used to find platform-specific configuration files.
     *
     * @return the name of the platform
     */
    public String getPlatformName();

    /**
     * Get an event reporter, which will be notified of events like "page flow created", "action raised", etc.
     */
    public PageFlowEventReporter getEventReporter();

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
     * @param classType the class type that the factory should extend or implement.
     * @param id        can be used for the case where there is more than one possible Factory
     *                  that extends or implaments the class type.
     * @param config    a configuration object passed to a {@link org.apache.ti.core.factory.Factory}
     * @return a Factory class that extends or implemtents the given class type.
     */
    public Factory getFactory(Class classType, String id, FactoryConfig config);
}
