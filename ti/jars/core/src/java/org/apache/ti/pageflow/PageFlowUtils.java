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

import org.apache.ti.core.ActionMessage;
import org.apache.ti.core.urls.FreezableMutableURI;
import org.apache.ti.core.urls.MutableURI;
import org.apache.ti.core.urls.URIContext;
import org.apache.ti.core.urls.URLRewriterService;
import org.apache.ti.core.urls.URLType;
import org.apache.ti.core.urltemplates.URLTemplatesFactory;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.handler.StorageHandler;
import org.apache.ti.pageflow.internal.AdapterManager;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.internal.URIContextFactory;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.script.common.ImplicitObjectUtil;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.UrlConfig;
import org.apache.ti.util.internal.FileUtils;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.internal.concurrent.InternalConcurrentHashMap;
import org.apache.ti.util.logging.Logger;

import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility methods related to Page Flow.
 */
public class PageFlowUtils
        implements PageFlowConstants, InternalConstants {
    private static final Logger _log = Logger.getInstance(PageFlowUtils.class);
    private static final String ACTION_PATH_ATTR = ATTR_PREFIX + "_actionPath";
    private static final int PAGEFLOW_EXTENSION_LEN = PAGEFLOW_EXTENSION.length();
    private static final String[] DEFAULT_AUTORESOLVE_EXTENSIONS = new String[] { ACTION_EXTENSION, PAGEFLOW_EXTENSION };

    /**
     * Map of Struts module prefix to Map of form-type-name to form-name.
     */
    private static Map /*< String, Map< String, List< String > > >*/ _formNameMaps = new InternalConcurrentHashMap /*< String, Map< String, List< String > > >*/();

    /**
     * Get a URI for the "begin" action in the PageFlowController associated with the given
     * request URI.
     *
     * @return a String that is the URI for the "begin" action in the PageFlowController associated
     *         with the given request URI.
     */
    public static String getBeginActionURI(String requestURI) {
        // Translate this to a request for the begin action ("begin.do") for this PageFlowController.
        InternalStringBuilder retVal = new InternalStringBuilder();
        int lastSlash = requestURI.lastIndexOf('/');

        if (lastSlash != -1) {
            retVal.append(requestURI.substring(0, lastSlash));
        }

        retVal.append('/').append(BEGIN_ACTION_NAME).append(ACTION_EXTENSION);

        return retVal.toString();
    }

    /**
     * Get the {@link PageFlowController} that is nesting the current one.
     *
     * @return the nesting {@link PageFlowController}, or <code>null</code> if the current one
     *         is not being nested.
     */
    public static PageFlowController getNestingPageFlow() {
        PageFlowStack jpfStack = PageFlowStack.get(false);

        if ((jpfStack != null) && !jpfStack.isEmpty()) {
            PageFlowController top = jpfStack.peek().getPageFlow();

            return top;
        }

        return null;
    }

    /**
     * Get the current PageFlowController.
     *
     * @return the current PageFlowController from the user session, or <code>null</code>
     *         if there is none.
     */
    public static final PageFlowController getCurrentPageFlow() {
        ActionResolver cur = getCurrentActionResolver();

        return (cur instanceof PageFlowController) ? (PageFlowController) cur : null;
    }

    /**
     * Get the current ActionResolver.
     *
     * @return the current ActionResolver from the user session, or <code>null</code> if there is none.
     */
    public static ActionResolver getCurrentActionResolver() {
        StorageHandler sh = Handlers.get().getStorageHandler();

        //
        // First see if the current page flow is a long-lived, which is stored in its own attribute.
        //
        String currentLongLivedAttrName = InternalUtils.getScopedAttrName(CURRENT_LONGLIVED_ATTR);
        String currentLongLivedNamespace = (String) sh.getAttribute(currentLongLivedAttrName);

        if (currentLongLivedNamespace != null) {
            return getLongLivedPageFlow(currentLongLivedNamespace);
        } else {
            String currentJpfAttrName = InternalUtils.getScopedAttrName(CURRENT_JPF_ATTR);

            return (ActionResolver) sh.getAttribute(currentJpfAttrName);
        }
    }

    /**
     * Get the a map of shared flow name to shared flow instance, based on the names defined in the
     * {@link org.apache.ti.pageflow.annotations.ti.controller#sharedFlowRefs sharedFlowRefs} attribute
     * of the {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller} annotation on the
     * <strong>current page flow</strong>.
     *
     * @return a Map of shared flow name (string) to shared flow instance ({@link SharedFlowController}).
     */
    public static Map /*< String, SharedFlowController >*/ getSharedFlows() {
        Map /*< String, SharedFlowController >*/ sharedFlows = ImplicitObjectUtil.getSharedFlow();

        return (sharedFlows != null) ? sharedFlows : Collections.EMPTY_MAP;
    }

    /**
     * Get the shared flow with the given class name.
     *
     * @param sharedFlowClassName the class name of the shared flow to remove.
     * @return the {@link SharedFlowController} of the given class name which is stored in the user session.
     */
    public static SharedFlowController getSharedFlow(String sharedFlowClassName) {
        StorageHandler sh = Handlers.get().getStorageHandler();

        return (SharedFlowController) sh.getAttribute(SHARED_FLOW_ATTR_PREFIX + sharedFlowClassName);
    }

    /**
     * Destroy the current {@link SharedFlowController} of the given class name.
     *
     * @param sharedFlowClassName the class name of the current SharedFlowController to destroy.
     */
    public static void removeSharedFlow(String sharedFlowClassName) {
        StorageHandler sh = Handlers.get().getStorageHandler();
        sh.removeAttribute(SHARED_FLOW_ATTR_PREFIX + sharedFlowClassName);
    }

    /**
     * Remove a "long-lived" page flow from the session. Once it is created, a long-lived page flow
     * is never removed from the session unless this method or {@link PageFlowController#remove} is
     * called.  Navigating to another page flow hides the current long-lived controller, but does not
     * remove it.
     */
    public static void removeLongLivedPageFlow(String namespace) {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = InternalUtils.getLongLivedFlowAttr(namespace);
        attrName = InternalUtils.getScopedAttrName(attrName);
        sh.removeAttribute(attrName);

        //
        // Now, if the current page flow is long-lived, remove the reference.
        //
        String currentLongLivedAttrName = InternalUtils.getScopedAttrName(CURRENT_LONGLIVED_ATTR);
        String currentLongLivedNamespace = (String) sh.getAttribute(currentLongLivedAttrName);

        if (namespace.equals(currentLongLivedNamespace)) {
            sh.removeAttribute(currentLongLivedAttrName);
        }
    }

    /**
     * Get the long-lived page flow instance associated with the given module (directory) path.
     *
     * @param namespace the namespace of the long-lived page flow.
     * @return the long-lived page flow instance associated with the given module, or <code>null</code> if none is found.
     */
    public static PageFlowController getLongLivedPageFlow(String namespace) {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = InternalUtils.getLongLivedFlowAttr(namespace);
        attrName = InternalUtils.getScopedAttrName(attrName);

        PageFlowController retVal = (PageFlowController) sh.getAttribute(attrName);

        return retVal;
    }

    /**
     * Make any form beans in the given {@link Forward} object available as attributets in the
     * request/session (as appropriate).
     *
     * @param fwd the {@link Forward} object that contains the ActionForm instances to be
     *            made available in the request/session (as appropriate).
     */
    public static void setOutputForms(Forward fwd, boolean overwrite) {
        if (fwd != null) {
            setOutputForms(fwd.getOutputForms(), overwrite);
            InternalUtils.setForwardedFormBean(fwd.getFirstOutputForm());
        }
    }

    /**
     * Make a set of form beans available as attributets in the request/session (as appropriate).
     *
     * @param outputForms an array of Object instances to be made available in the
     *                    request/session (as appropriate).
     */
    public static void setOutputForms(Object[] outputForms) {
        setOutputForms(outputForms, true);
    }

    /**
     * Make a set of form beans available as attributets in the request/session (as appropriate).
     *
     * @param outputForms an array of Object instances to be made available in the
     *                    request/session (as appropriate).
     * @param overwrite   if <code>false</code> a form from <code>fwd</code> will only be set
     *                    in the request if there is no existing form with the same name.
     */
    public static void setOutputForms(Object[] outputForms, boolean overwrite) {
        for (int i = 0; i < outputForms.length; ++i) {
            setOutputForm(outputForms[i], overwrite);
        }
    }

    /**
     * Make a form bean available as an attribute in the request/session (as appropriate).
     *
     * @param formBean  an Object instance to be made available in the request/session
     *                  (as appropriate).
     * @param overwrite if <code>false</code> a form from <code>fwd</code> will only be set
     *                  in the request if there is no existing form with the same name.
     */
    public static void setOutputForm(Object formBean, boolean overwrite) {
        if (formBean != null) {
            PageFlowActionContext actionContext = PageFlowActionContext.get();
            ModuleConfig moduleConfig = actionContext.getModuleConfig();
            Class formClass = formBean.getClass();

            //
            // Get the names of *all* form beans of the desired type, and blast out this instance under all those names.
            //
            Map formBeanAttrNames = moduleConfig.getFormBeanAttributeNames();
            List formNames = (List) formBeanAttrNames.get(formClass.getName());
            List additionalFormNames = null;

            //
            // formNames is a statically-scoped list.  Below, we create a dynamic list of form names that correspond
            // to *implemented interfaces* of the given form bean class.
            //
            Class[] interfaces = formClass.getInterfaces();

            for (int i = 0; i < interfaces.length; i++) {
                Class formInterface = interfaces[i];
                List toAdd = (List) formBeanAttrNames.get(formInterface.getName());

                if (toAdd != null) {
                    if (additionalFormNames == null) {
                        additionalFormNames = new ArrayList();
                    }

                    additionalFormNames.addAll(toAdd);
                }
            }

            if ((formNames == null) && (additionalFormNames == null)) {
                String formName = generateFormBeanName(formClass);
                InternalUtils.setFormInScope(formName, formBean, overwrite);
            } else {
                if (formNames != null) {
                    for (Iterator i = formNames.iterator(); i.hasNext();) {
                        String formName = (String) i.next();
                        InternalUtils.setFormInScope(formName, formBean, overwrite);
                    }
                }

                if (additionalFormNames != null) {
                    for (Iterator i = additionalFormNames.iterator(); i.hasNext();) {
                        String formName = (String) i.next();
                        InternalUtils.setFormInScope(formName, formBean, overwrite);
                    }
                }
            }
        }
    }

    /**
     * Get the name for the type of a Object instance.  Use a name looked up from
     * the current Struts module, or, if none is found, create one.
     *
     * @param formInstance the Object instance whose type will determine the name.
     * @return the name found in the Struts module, or, if none is found, a name that is either:
     *         <ul>
     *         <li>a camel-cased version of the base class name (minus any package or outer-class
     *         qualifiers, or, if that name is already taken,</li>
     *         <li>the full class name, with '.' and '$' replaced by '_'.</li>
     *         </ul>
     */
    public static String getFormBeanName(Object formInstance) {
        return getFormBeanName(formInstance.getClass());
    }

    /**
     * Get the name for an Object type.  Use a name looked up from the current Struts module, or,
     * if none is found, create one.
     *
     * @param formBeanClass the Object-derived class whose type will determine the name.
     * @return the name found in the Struts module, or, if none is found, a name that is either:
     *         <ul>
     *         <li>a camel-cased version of the base class name (minus any package or outer-class
     *         qualifiers, or, if that name is already taken,</li>
     *         <li>the full class name, with '.' and '$' replaced by '_'.</li>
     *         </ul>
     */
    public static String getFormBeanName(Class formBeanClass) {
        ModuleConfig moduleConfig = PageFlowActionContext.get().getModuleConfig();
        List names = (List) moduleConfig.getFormBeanAttributeNames().get(formBeanClass.getName());

        if (names != null) {
            assert names.size() > 0; // getFormNamesFromModuleConfig returns null or a nonempty list

            return (String) names.get(0);
        }

        return generateFormBeanName(formBeanClass);
    }

    /**
     * Create the name for a form bean type.
     *
     * @param formBeanClass the class whose type will determine the name.
     * @return the name found in the Struts module, or, if none is found, a name that is either:
     *         <ul>
     *         <li>a camel-cased version of the base class name (minus any package or outer-class
     *         qualifiers, or, if that name is already taken,</li>
     *         <li>the full class name, with '.' and '$' replaced by '_'.</li>
     *         </ul>
     */
    private static String generateFormBeanName(Class formBeanClass) {
        ModuleConfig moduleConfig = PageFlowActionContext.get().getModuleConfig();
        String formBeanClassName = formBeanClass.getName();

        //
        // A form-bean wasn't found for this type, so we'll create a name.  First try and create
        // name that is a camelcased version of the classname without all of its package/outer-class
        // qualifiers.  If one with that name already exists, munge the fully-qualified classname.
        //
        String formType = formBeanClassName;
        int lastQualifier = formType.lastIndexOf('$');

        if (lastQualifier == -1) {
            lastQualifier = formType.lastIndexOf('.');
        }

        String formName = formType.substring(lastQualifier + 1);
        formName = Character.toLowerCase(formName.charAt(0)) + formName.substring(1);

        if (moduleConfig.getFormBeans().get(formName) != null) {
            formName = formType.replace('.', '_').replace('$', '_');
            assert moduleConfig.getFormBeans().get(formName) == null : formName;
        }

        return formName;
    }

    /**
     * Get the class name of a {@link PageFlowController}, given the URI to it.
     *
     * @param uri the URI to the {@link PageFlowController}, which should be relative to the
     *            web application root (i.e., it should not include the context path).
     */
    public static String getPageFlowClassName(String uri) {
        assert uri != null;
        assert uri.length() > 0;

        if (uri.charAt(0) == '/') {
            uri = uri.substring(1);
        }

        assert FileUtils.osSensitiveEndsWith(uri, PAGEFLOW_EXTENSION) : uri;

        if (FileUtils.osSensitiveEndsWith(uri, PAGEFLOW_EXTENSION)) {
            uri = uri.substring(0, uri.length() - PAGEFLOW_EXTENSION_LEN);
        }

        return uri.replace('/', '.');
    }

    /**
     * Get the URI for a {@link PageFlowController}, given its class name.
     *
     * @param className the name of the {@link PageFlowController} class.
     * @return a String that is the URI for the {@link PageFlowController}, relative to the web
     *         application root (i.e., not including the context path).
     */
    public static String getPageFlowURI(String className) {
        return '/' + className.replace('.', '/') + PAGEFLOW_EXTENSION;
    }

    /**
     * Get the most recent action URI that was processed by {@link FlowController#execute}.
     *
     * @return a String that is the most recent action URI.  This is only valid during a request
     *         that has been forwarded from the action URI.
     */
    public static String getActionPath() {
        return (String) getWebContext().getRequestScope().get(ACTION_PATH_ATTR);
    }

    private static WebContext getWebContext() {
        return PageFlowActionContext.get().getWebContext();
    }

    /**
     * Sets the most recent action URI that was processed by {@link FlowController#execute}.
     */
    static void setActionPath() {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        getWebContext().getRequestScope().put(ACTION_PATH_ATTR, actionContext.getRequestPath());
    }

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
     * @param uri a webapp-relative URI for a resource.  There must not be query parameters or a scheme
     *            on the URI.
     * @return <code>Boolean.TRUE</code> if a transport-guarantee of <code>CONFIDENTIAL</code> or
     *         <code>INTEGRAL</code> is associated with the given resource; <code>Boolean.FALSE</code>
     *         a transport-guarantee of <code>NONE</code> is associated with the given resource; or
     *         <code>null</code> if there is no transport-guarantee associated with the given resource.
     */
    public static SecurityProtocol getSecurityProtocol(String uri) {
        return AdapterManager.getContainerAdapter().getSecurityProtocol(uri);
    }

    /**
     * Set a named action output, which corresponds to an input declared by the <code>pageInput</code> JSP tag.
     * The actual value can be read from within a JSP using the <code>"pageInput"</code> databinding context.
     *
     * @param name  the name of the action output.
     * @param value the value of the action output.
     */
    public static void addActionOutput(String name, Object value) {
        Map map = InternalUtils.getActionOutputMap(true);

        if (map.containsKey(name)) {
            if (_log.isWarnEnabled()) {
                _log.warn("Overwriting action output\"" + name + "\".");
            }
        }

        map.put(name, value);
    }

    /**
     * Get a named action output that was registered in the current request.
     *
     * @param name the name of the action output.
     * @see #addActionOutput
     */
    public static Object getActionOutput(String name) {
        Map map = InternalUtils.getActionOutputMap(false);

        return (map != null) ? map.get(name) : null;
    }

    /**
     * Add a property-related message that will be shown with the Errors and Error tags.
     *
     * @param propertyName the name of the property with which to associate this error.
     * @param messageKey   the message-resources key for the message.
     * @param messageArgs  zero or more arguments to the message.
     */
    public static void addActionError(String propertyName, String messageKey, Object[] messageArgs) {
        InternalUtils.addActionError(propertyName, new ActionMessage(messageKey, messageArgs));
    }

    /**
     * Add a property-related message that will be shown with the Errors and Error tags.
     *
     * @param propertyName the name of the property with which to associate this error.
     * @param messageKey   the message-resources key for the message.
     */
    public static void addActionError(String propertyName, String messageKey) {
        InternalUtils.addActionError(propertyName, new ActionMessage(messageKey, null));
    }

    /**
     * Add a property-related message that will be shown with the Errors and Error tags.
     *
     * @param propertyName the name of the property with which to associate this error.
     * @param messageKey   the message-resources key for the message.
     * @param messageArg   an argument to the message
     */
    public static void addActionError(String propertyName, String messageKey, Object messageArg) {
        Object[] messageArgs = new Object[] { messageArg };
        InternalUtils.addActionError(propertyName, new ActionMessage(messageKey, messageArgs));
    }

    /**
     * Add a property-related message that will be shown with the Errors and Error tags.
     *
     * @param propertyName the name of the property with which to associate this error.
     * @param messageKey   the message-resources key for the message.
     * @param messageArg1  the first argument to the message
     * @param messageArg2  the second argument to the message
     */
    public static void addActionError(String propertyName, String messageKey, Object messageArg1, Object messageArg2) {
        Object[] messageArgs = new Object[] { messageArg1, messageArg2 };
        InternalUtils.addActionError(propertyName, new ActionMessage(messageKey, messageArgs));
    }

    /**
     * Add a property-related message that will be shown with the Errors and Error tags.
     *
     * @param propertyName the name of the property with which to associate this error.
     * @param messageKey   the message-resources key for the message.
     * @param messageArg1  the first argument to the message
     * @param messageArg2  the second argument to the message
     * @param messageArg3  the third argument to the message
     */
    public static void addActionError(String propertyName, String messageKey, Object messageArg1, Object messageArg2,
                                      Object messageArg3) {
        Object[] messageArgs = new Object[] { messageArg1, messageArg2, messageArg3 };
        InternalUtils.addActionError(propertyName, new ActionMessage(messageKey, messageArgs));
    }

    /**
     * Add a property-related message as an expression that will be evaluated and shown with the Errors and Error tags.
     *
     * @param propertyName the name of the property with which to associate this error.
     * @param expression   the expression that will be evaluated to generate the error message.
     * @param messageArgs  zero or more arguments to the message.
     */
    public static void addActionErrorExpression(String propertyName, String expression, Object[] messageArgs) {
        ExpressionMessage msg = new ExpressionMessage(expression, messageArgs);
        InternalUtils.addActionError(propertyName, msg);
    }

    /**
     * Resolve the given action to a URI by running an entire request-processing cycle on the given ScopedRequest
     * and ScopedResponse.
     *
     * @param actionOverride if not <code>null</code>, this qualified action-path is used to construct an action
     *                       URI which is set as the request URI.  The action-path <strong>must</strong> begin with '/',
     *                       which makes it qualified from the webapp root.
     * @param autoResolveExtensions a list of URI extensions (e.g., ".do", ".jpf") that will be auto-resolved, i.e.,
     *                              on which this method will be recursively called.  If <code>null</code>, the
     *                              default extensions ".do" and ".jpf" will be used.
     */

    /* TODO: re-add some form of this, for portal/portlet support
    public static ActionResult strutsLookup( String actionOverride, String[] autoResolveExtensions )
        throws Exception
    {
        ScopedRequest scopedRequest = ScopedUtils.unwrapRequest( request );
        ScopedResponse scopedResponse = ScopedUtils.unwrapResponse( response );
        assert scopedRequest != null : request.getClass().getName();
        assert scopedResponse != null : response.getClass().getName();

        if ( scopedRequest == null )
        {
            throw new IllegalArgumentException( "request must be of type " + ScopedRequest.class.getName() );
        }
        if ( scopedResponse == null )
        {
            throw new IllegalArgumentException( "response must be of type " + ScopedResponse.class.getName() );
        }

        ActionServlet as = InternalUtils.getActionServlet( context );

        if ( as == null )
        {
            _log.error( "There is no initialized ActionServlet.  The ActionServlet must be set to load-on-startup." );
            return null;
        }

        if ( actionOverride != null )
        {
            // The action must be fully-qualified with its namespace.
            assert actionOverride.charAt( 0 ) == '/' : actionOverride;
            InternalStringBuilder uri = new InternalStringBuilder( scopedRequest.getContextPath() );
            uri.append( actionOverride );
            uri.append( PageFlowConstants.ACTION_EXTENSION );
            scopedRequest.setRequestURI( uri.toString() );
        }

        //
        // In case the request was already forwarded once, clear out the recorded forwarded-URI.  This
        // will allow us to tell whether processing the request actually forwarded somewhere.
        //
        scopedRequest.setForwardedURI( null );

        //
        // Now process the request.  We create a PageFlowRequestWrapper for pageflow-specific request-scoped info.
        //
        PageFlowRequestWrapper wrappedRequest = PageFlowRequestWrapper.wrapRequest( ( HttpServletRequest ) request );
        as.doGet( wrappedRequest, scopedResponse );  // this just calls process() -- same as doPost()

        String returnURI;

        if ( ! scopedResponse.didRedirect() )
        {
            returnURI = scopedRequest.getForwardedURI();

            if ( autoResolveExtensions == null )
            {
                autoResolveExtensions = DEFAULT_AUTORESOLVE_EXTENSIONS;
            }

            if ( returnURI != null )
            {
                for ( int i = 0; i < autoResolveExtensions.length; ++i )
                {
                    if ( FileUtils.uriEndsWith( returnURI, autoResolveExtensions[i] ) )
                    {
                        scopedRequest.doForward();
                        return strutsLookup( context, wrappedRequest, scopedResponse, null, autoResolveExtensions );
                    }
                }
            }
        }
        else
        {
            returnURI = scopedResponse.getRedirectURI();
        }

        DeferredSessionStorageHandler.applyChanges( scopedRequest, context );

        if ( returnURI != null )
        {
            return new ActionResultImpl( returnURI, scopedResponse.didRedirect(), scopedResponse.getStatusCode(),
                                         scopedResponse.getStatusMessage(), scopedResponse.isError() );
        }
        else
        {
            return null;
        }
    }
    */

    /**
     * Create a raw action URI, which can be modified before being sent through the registered URL rewriting chain
     * using {@link URLRewriterService#rewriteURL}.  Use {@link #getRewrittenActionURI} to get a fully-rewritten URI.
     *
     * @param actionName the action name to convert into a MutableURI; may be qualified with a path from the webapp
     *                   root, in which case the parent directory from the current request is <i>not</i> used.
     * @return a MutableURI for the given action, suitable for URL rewriting.
     * @throws URISyntaxException if there is a problem converting the action URI (derived from processing the given
     *                            action name) into a MutableURI.
     */
    public static MutableURI getActionURI(String actionName)
            throws URISyntaxException {
        // TODO: need ActionMapper to be reversible -- it should construct the URI.
        if (actionName.length() < 1) {
            throw new IllegalArgumentException("actionName must be non-empty");
        }

        PageFlowActionContext actionContext = PageFlowActionContext.get();
        InternalStringBuilder actionURI = new InternalStringBuilder(actionContext.getRequestContextPath());

        if (actionName.charAt(0) != '/') {
            actionURI.append(actionContext.getNamespace());
            actionURI.append('/');
        }

        actionURI.append(actionName);

        if (!actionName.endsWith(ACTION_EXTENSION)) {
            actionURI.append(ACTION_EXTENSION);
        }

        FreezableMutableURI uri = new FreezableMutableURI();

        // TODO: re-add the following line, using some abstraction
        //uri.setEncoding( response.getCharacterEncoding() );
        uri.setURI(actionURI.toString(), true);

        return uri;
    }

    /**
     * Create a fully-rewritten URI given an action name and parameters.
     *
     * @param actionName the action name to convert into a fully-rewritten URI; may be qualified with a path from the
     *                   webapp root, in which case the parent directory from the current request is <i>not</i> used.
     * @param params     the additional parameters to include in the URI query.
     * @param fragment   the fragment (anchor or location) for this url.
     * @param forXML     flag indicating that the query of the uri should be written
     *                   using the &quot;&amp;amp;&quot; entity, rather than the character, '&amp;'.
     * @return a fully-rewritten URI for the given action.
     * @throws URISyntaxException if there is a problem converting the action URI (derived
     *                            from processing the given action name) into a MutableURI.
     */
    public static String getRewrittenActionURI(String actionName, Map params, String fragment, boolean forXML)
            throws URISyntaxException {
        MutableURI uri = getActionURI(actionName);

        if (params != null) {
            uri.addParameters(params, false);
        }

        if (fragment != null) {
            uri.setFragment(uri.encode(fragment));
        }

        boolean needsToBeSecure = needsToBeSecure(uri.getPath(), true);
        URLRewriterService.rewriteURL(uri, URLType.ACTION, needsToBeSecure);

        String key = getURLTemplateKey(URLType.ACTION, needsToBeSecure);
        URIContext uriContext = URIContextFactory.getInstance(forXML);

        return URLRewriterService.getTemplatedURL(uri, key, uriContext);
    }

    /**
     * Create a fully-rewritten URI given a path and parameters.
     * <p/>
     * <p> Calls the rewriter service using a type of {@link URLType#RESOURCE}. </p>
     *
     * @param path     the path to process into a fully-rewritten URI.
     * @param params   the additional parameters to include in the URI query.
     * @param fragment the fragment (anchor or location) for this URI.
     * @param forXML   flag indicating that the query of the uri should be written
     *                 using the &quot;&amp;amp;&quot; entity, rather than the character, '&amp;'.
     * @return a fully-rewritten URI for the given action.
     * @throws URISyntaxException if there's a problem converting the action URI (derived
     *                            from processing the given action name) into a MutableURI.
     */
    public static String getRewrittenResourceURI(String path, Map params, String fragment, boolean forXML)
            throws URISyntaxException {
        return rewriteResourceOrHrefURL(path, params, fragment, forXML, URLType.RESOURCE);
    }

    /**
     * Create a fully-rewritten URI given a path and parameters.
     * <p/>
     * <p> Calls the rewriter service using a type of {@link URLType#ACTION}. </p>
     *
     * @param path     the path to process into a fully-rewritten URI.
     * @param params   the additional parameters to include in the URI query.
     * @param fragment the fragment (anchor or location) for this URI.
     * @param forXML   flag indicating that the query of the uri should be written
     *                 using the &quot;&amp;amp;&quot; entity, rather than the character, '&amp;'.
     * @return a fully-rewritten URI for the given action.
     * @throws URISyntaxException if there's a problem converting the action URI (derived
     *                            from processing the given action name) into a MutableURI.
     */
    public static String getRewrittenHrefURI(String path, Map params, String fragment, boolean forXML)
            throws URISyntaxException {
        return rewriteResourceOrHrefURL(path, params, fragment, forXML, URLType.ACTION);
    }

    private static String rewriteResourceOrHrefURL(String path, Map params, String fragment, boolean forXML, URLType urlType)
            throws URISyntaxException {
        boolean encoded = false;
        UrlConfig urlConfig = ConfigUtil.getConfig().getUrlConfig();

        if (urlConfig != null) {
            encoded = !urlConfig.isUrlEncodeUrls();
        }

        FreezableMutableURI uri = new FreezableMutableURI();

        // TODO: re-add the following line, using some abstraction
        //uri.setEncoding( response.getCharacterEncoding() );
        uri.setURI(path, encoded);

        if (params != null) {
            uri.addParameters(params, false);
        }

        if (fragment != null) {
            uri.setFragment(uri.encode(fragment));
        }

        URIContext uriContext = URIContextFactory.getInstance(forXML);

        if (uri.isAbsolute()) {
            return uri.getURIString(uriContext);
        }

        if ((path.length() != 0) && (path.charAt(0) != '/')) {
            PageFlowActionContext actionContext = PageFlowActionContext.get();
            String reqPath = actionContext.getRequestPath();
            reqPath = reqPath.substring(0, reqPath.lastIndexOf('/') + 1);
            uri.setPath(reqPath + uri.getPath());
        }

        boolean needsToBeSecure = needsToBeSecure(uri.getPath(), true);
        URLRewriterService.rewriteURL(uri, urlType, needsToBeSecure);

        String key = getURLTemplateKey(urlType, needsToBeSecure);

        return URLRewriterService.getTemplatedURL(uri, key, uriContext);
    }

    /**
     * Tell whether a given URI should be written to be secure.
     *
     * @param uri              the URI to check.
     * @param stripContextPath if <code>true</code>, strip the webapp context path from the URI before
     *                         processing it.
     * @return <code>true</code> when:
     *         <ul>
     *         <li>the given URI is configured in the deployment descriptor to be secure (according to
     *         {@link SecurityProtocol}), or
     *         <li>the given URI is not configured in the deployment descriptor, and the current request
     *         is secure ({@link javax.servlet.http.HttpServletRequest#isSecure} returns
     *         <code>true</code>).
     *         </ul>
     *         <code>false</code> when:
     *         <ul>
     *         <li>the given URI is configured explicitly in the deployment descriptor to be unsecure
     *         (according to {@link SecurityProtocol}), or
     *         <li>the given URI is not configured in the deployment descriptor, and the current request
     *         is unsecure ({@link javax.servlet.http.HttpServletRequest#isSecure} returns
     *         <code>false</code>).
     *         </ul>
     */
    public static boolean needsToBeSecure(String uri, boolean stripContextPath) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();

        // Get the web-app relative path for security check
        String secureCheck = uri;

        if (stripContextPath) {
            String contextPath = actionContext.getRequestContextPath();

            if (secureCheck.startsWith(contextPath)) {
                secureCheck = secureCheck.substring(contextPath.length());
            }
        }

        boolean secure = false;

        if (secureCheck.indexOf('?') > -1) {
            secureCheck = secureCheck.substring(0, secureCheck.indexOf('?'));
        }

        SecurityProtocol sp = getSecurityProtocol(secureCheck);

        if (sp.equals(SecurityProtocol.UNSPECIFIED)) {
            secure = actionContext.isRequestSecure();
        } else {
            secure = sp.equals(SecurityProtocol.SECURE);
        }

        return secure;
    }

    /**
     * Returns a key for the URL template type given the URL type and a
     * flag indicating a secure URL or not.
     *
     * @param urlType         the type of URL (ACTION, RESOURCE).
     * @param needsToBeSecure indicates that the template should be for a secure URL.
     * @return the key/type of template to use.
     */
    public static String getURLTemplateKey(URLType urlType, boolean needsToBeSecure) {
        String key = URLTemplatesFactory.ACTION_TEMPLATE;

        if (urlType.equals(URLType.ACTION)) {
            if (needsToBeSecure) {
                key = URLTemplatesFactory.SECURE_ACTION_TEMPLATE;
            } else {
                key = URLTemplatesFactory.ACTION_TEMPLATE;
            }
        } else if (urlType.equals(URLType.RESOURCE)) {
            if (needsToBeSecure) {
                key = URLTemplatesFactory.SECURE_RESOURCE_TEMPLATE;
            } else {
                key = URLTemplatesFactory.RESOURCE_TEMPLATE;
            }
        }

        return key;
    }
}
