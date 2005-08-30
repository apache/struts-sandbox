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

import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.ti.Globals;
import org.apache.ti.core.ActionMessage;
import org.apache.ti.pageflow.ActionResolver;
import org.apache.ti.pageflow.FacesBackingBean;
import org.apache.ti.pageflow.FlowControllerException;
import org.apache.ti.pageflow.ModuleConfig;
import org.apache.ti.pageflow.PageFlowConstants;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.SessionExpiredException;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.handler.ModuleRegistrationHandler;
import org.apache.ti.pageflow.handler.ReloadableClassHandler;
import org.apache.ti.pageflow.handler.StorageHandler;
import org.apache.ti.pageflow.xwork.PageFlowAction;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.schema.config.PageflowConfig;
import org.apache.ti.util.Bundle;
import org.apache.ti.util.MessageResources;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.internal.ServletUtils;
import org.apache.ti.util.logging.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public class InternalUtils
        implements PageFlowConstants, InternalConstants {

    private static final Logger _log = Logger.getInstance(InternalUtils.class);

    private static final String LONGLIVED_PAGEFLOWS_ATTR_PREFIX = ATTR_PREFIX + "longLivedPageFlow:";
    private static final String ACTIONOUTPUT_MAP_ATTR = ATTR_PREFIX + "actionOutputs";
    private static final String BINDING_UPDATE_ERRORS_ATTR = ATTR_PREFIX + "bindingUpdateErrors";
    private static final String SHARED_FLOW_CLASSNAME_ATTR = ATTR_PREFIX + "sharedFlowClass";
    private static final String AVOID_DIRECT_RESPONSE_OUTPUT_ATTR = ATTR_PREFIX + "_avoidDirectResponseOutput";
    private static final String FORWARDED_FORMBEAN_ATTR = ATTR_PREFIX + "forwardedForm";
    private static final String FORWARDING_MODULE_ATTR = ATTR_PREFIX + "forwardingModule";
    private static final String IGNORE_INCLUDE_SERVLET_PATH_ATTR = ATTR_PREFIX + "ignoreIncludeServletPath";


    /**
     * If not in production mode, write an error to the response; otherwise, set a response error code.
     */
    public static void sendDevTimeError(String messageKey, Throwable cause, int productionTimeErrorCode,
                                        Object[] messageArgs) {
        // TODO: re-add this
    }

    /**
     * Write an error to the response.
     */
    public static void sendError(String messageKey, Throwable cause, Object[] messageArgs) {
        sendError(messageKey, messageArgs, cause, avoidDirectResponseOutput());
    }

    /**
     * Write an error to the response.
     */
    public static void sendError(String messageKey, Object[] messageArgs, Throwable cause,
                                 boolean avoidDirectResponseOutput) {
        assert messageArgs.length == 0 || !(messageArgs[0] instanceof Object[])
                : "Object[] passed to sendError; this is probably a mistaken use of varargs";
        
        // request may be null because of deprecated FlowController.sendError().
        if (avoidDirectResponseOutput) {
            String baseMessage = Bundle.getString(messageKey + "_Message", messageArgs);
            throw new ResponseOutputException(baseMessage, cause);
        }

        String html = Bundle.getString(messageKey + "_Page", messageArgs);
        // TODO re-add
        //ServletUtils.writeHtml(getWebContext(), html, true);
        System.err.println(html);
        throw new UnsupportedOperationException("NYI -- HTML is: \n" + html); // TODO: NYI        
    }

    /**
     * Get a Method in a Class.
     *
     * @param parentClass the Class in which to find the Method.
     * @param methodName  the name of the Method.
     * @param signature   the argument types for the Method.
     * @return the Method with the given name and signature, or <code>null</code> if the method does not exist.
     */
    public static Method lookupMethod(Class parentClass, String methodName, Class[] signature) {
        try {
            return parentClass.getDeclaredMethod(methodName, signature);
        } catch (NoSuchMethodException e) {
            Class superClass = parentClass.getSuperclass();
            return superClass != null ? lookupMethod(superClass, methodName, signature) : null;
        }
    }

    public static String getLongLivedFlowAttr(String namespace) {
        return LONGLIVED_PAGEFLOWS_ATTR_PREFIX + namespace;
    }

    public static void setCurrentPageFlow(PageFlowController jpf) {
        setCurrentActionResolver(jpf);
    }

    public static void removeCurrentPageFlow() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String currentJpfAttrName = getScopedAttrName(CURRENT_JPF_ATTR);
        String currentLongLivedAttrName = getScopedAttrName(CURRENT_LONGLIVED_ATTR);
        sh.removeAttribute(currentJpfAttrName);
        sh.removeAttribute(currentLongLivedAttrName);
    }

    public static void removeCurrentFacesBackingBean() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = getScopedAttrName(FACES_BACKING_ATTR);
        sh.removeAttribute(attrName);
    }

    public static void addActionOutputs(Map toAdd, boolean overwrite) {
        if (toAdd != null) {
            Map map = getActionOutputMap(true);

            for (Iterator i = toAdd.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                String name = (String) entry.getKey();
                boolean alreadyExists = map.containsKey(name);

                if (overwrite || !alreadyExists) {
                    if (alreadyExists) {
                        if (_log.isWarnEnabled()) {
                            _log.warn("Overwriting action output \"" + name + "\".");
                        }
                    }

                    map.put(name, entry.getValue());
                }
            }
        }
    }

    public static void addActionError(String propertyName, ActionMessage error) {
        /* TODO: re-add error/message support
        ActionErrors errors = ( ActionErrors ) request.getAttribute( Globals.ERROR_KEY );
        if ( errors == null ) request.setAttribute( Globals.ERROR_KEY, errors = new ActionErrors() );
        errors.add( propertyName, error );
        */
    }

    public static Object newReloadableInstance(String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return getReloadableClass(className).newInstance();
    }

    public static Class getReloadableClass(String className)
            throws ClassNotFoundException {
        ReloadableClassHandler handler = Handlers.get().getReloadableClassHandler();
        return handler.loadClass(className);
    }

    public static Map getActionOutputMap(boolean createIfNotExist) {
        Map requestScope = PageFlowActionContext.get().getRequestScope();
        Map map = (Map) requestScope.get(ACTIONOUTPUT_MAP_ATTR);

        if (map == null && createIfNotExist) {
            map = new HashMap();
            requestScope.put(ACTIONOUTPUT_MAP_ATTR, map);
        }

        return map;
    }

    public static Map getPageInputMap() {
        Map actionOutputsFromPageFlow = getActionOutputMap(false);
        if (actionOutputsFromPageFlow != null) return actionOutputsFromPageFlow;
        FacesBackingBean fbb = getFacesBackingBean();
        return fbb != null ? fbb.getPageInputMap() : null;
    }

    /**
     * Add a BindingUpdateError to the request.
     *
     * @param expression the expression associated with this error.
     * @param message    the error message.
     * @param cause      the Throwable that caused the error.
     */
    public static void addBindingUpdateError(String expression, String message, Throwable cause) {
        Map errors = (Map) getWebContext().getRequestScope().get(BINDING_UPDATE_ERRORS_ATTR);

        if (errors == null) {
            errors = new LinkedHashMap();
            getWebContext().getRequestScope().put(BINDING_UPDATE_ERRORS_ATTR, errors);
        }

        errors.put(expression, new BindingUpdateError(expression, message, cause));
    }

    /**
     * Get a map of BindingUpdateErrors stored in the request.
     *
     * @return a Map of expression (String) -> BindingUpdateError.
     */
    public static Map getBindingUpdateErrors() {
        return (Map) getWebContext().getRequestScope().get(BINDING_UPDATE_ERRORS_ATTR);
    }

    public static WebContext getWebContext() {
        return PageFlowActionContext.get().getWebContext();
    }

    /**
     * Set the given form in either the request or session, as appropriate, so Struts/NetUI
     * tags will have access to it.
     */
    public static void setFormInScope(String formAttribute, Object formBean, boolean overwrite) {
        if (formBean != null) {
            //String formName = PageFlowActionContext.getContext().getAction().getFormBeanAttribute();
            assert formAttribute != null;

            if (overwrite || getWebContext().getRequestScope().get(formAttribute) == null) {
                getWebContext().getRequestScope().put(formAttribute, formBean);
            }
        }
    }

    public static Object getFormBean(PageFlowAction action) {
        String attrName = action.getFormBeanAttribute();

        if (attrName != null) {
            return getWebContext().getRequestScope().get(attrName);
        }

        return null;
    }

    /**
     * Set the current ActionResolver ({@link PageFlowController}) in the user session.
     *
     * @param resolver the ActionResolver to set as the current one in the user session.
     */
    public static void setCurrentActionResolver(ActionResolver resolver) {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String currentJpfAttrName = getScopedAttrName(CURRENT_JPF_ATTR);
        String currentLongLivedJpfAttrName = getScopedAttrName(CURRENT_LONGLIVED_ATTR);

        if (resolver == null) {
            sh.removeAttribute(currentJpfAttrName);
            sh.removeAttribute(currentLongLivedJpfAttrName);
            return;
        }
        
        //
        // If this is a long-lived page flow, also store the instance in an attribute that never goes away.
        //
        if (resolver.getModuleConfig().isLongLivedFlow()) {
            String longLivedAttrName = getLongLivedFlowAttr(resolver.getNamespace());
            longLivedAttrName = getScopedAttrName(longLivedAttrName);
            
            // Only set this attribute if it's not already there.  We want to avoid our onDestroy() callback that's
            // invoked when the page flow's session attribute is unbound.
            if (sh.getAttribute(longLivedAttrName) != resolver) {
                sh.setAttribute(longLivedAttrName, resolver);
            }

            sh.setAttribute(currentLongLivedJpfAttrName, resolver.getNamespace());
            sh.removeAttribute(currentJpfAttrName);
        } else {
            sh.setAttribute(currentJpfAttrName, resolver);
            sh.removeAttribute(currentLongLivedJpfAttrName);
        }
    }

    public static String getFlowControllerClassName(String namespace) {
        assert namespace != null;
        ModuleRegistrationHandler mrh = Handlers.get().getModuleRegistrationHandler();
        ModuleConfig moduleConfig = mrh.getModuleConfig(namespace);
        return moduleConfig != null ? moduleConfig.getControllerClassName() : null;
    }

    public static FacesBackingBean getFacesBackingBean() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = getScopedAttrName(FACES_BACKING_ATTR);
        return (FacesBackingBean) sh.getAttribute(attrName);
    }

    public static String inferNamespaceFromClassName(String className) {
        int lastDot = className.lastIndexOf('.');

        if (lastDot != -1) {
            className = className.substring(0, lastDot);
            return '/' + className.replace('.', '/');
        } else {
            return "/";
        }
    }

    public static PageflowConfig.MultipartHandler.Enum getMultipartHandlerType() {
        PageflowConfig pfConfig = ConfigUtil.getConfig().getPageflowConfig();
        return pfConfig != null ? pfConfig.getMultipartHandler() : null;
    }

    /**
     * Simply adds the context path and parent directory, based on the current request URI.
     */
    public static String createActionURL(String qualifiedAction) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        String pageURI = actionContext.getRequestURI();
        int lastSlash = pageURI.lastIndexOf('/');

        if (lastSlash != -1) {
            InternalStringBuilder value = new InternalStringBuilder(qualifiedAction.length() + lastSlash);
            value.append(pageURI.substring(0, lastSlash));
            value.append(qualifiedAction);
            return value.toString();
        }

        return qualifiedAction;
    }

    public static String createActionPath(String qualifiedAction) {
        ModuleConfig moduleConfig = PageFlowActionContext.get().getModuleConfig();


        if (moduleConfig != null) {
            InternalStringBuilder value = new InternalStringBuilder(qualifiedAction.length() + 16);
            value.append(moduleConfig.getNamespace());
            value.append(qualifiedAction);
            return value.toString();
        }

        return qualifiedAction;
    }

    public static String qualifyAction(String action) {
        assert action != null;
        InternalStringBuilder sb = null;

        String queryString = null;
        int question = action.indexOf('?');
        if (question >= 0) queryString = action.substring(question);

        String actionMapping = getActionMappingName(action);
        sb = new InternalStringBuilder(action.length() + ACTION_EXTENSION_LEN + 1);
        sb.append(actionMapping);
        sb.append(ACTION_EXTENSION);
        if (queryString != null) sb.append(queryString);

        return sb.toString();
    }

    /**
     * Return the form action converted into an action mapping path.  The
     * value of the <code>action</code> property is manipulated as follows in
     * computing the name of the requested mapping:
     * <ul>
     * <li>Any filename extension is removed (on the theory that extension
     * mapping is being used to select the controller servlet).</li>
     * <li>If the resulting value does not start with a slash, then a
     * slash is prepended.</li>
     * </ul>
     *
     * @param action the action name to be converted.
     * @return an action path, suitable for lookup in the Struts configuration file.
     */
    public static String getActionMappingName(String action) {
        return getCleanActionName(action, true);
    }

    public static String getCleanActionName(String action, boolean prependSlash) {
        int question = action.indexOf('?');
        if (question >= 0) {
            action = action.substring(0, question);
        }

        if (action.endsWith(ACTION_EXTENSION)) {
            action = action.substring(0, action.length() - ACTION_EXTENSION_LEN);
        }

        if (action.charAt(0) == '/') {
            if (!prependSlash) action = action.substring(1);
        } else {
            if (prependSlash) action = '/' + action;
        }

        return action;
    }


    /**
     * Add a parameter to the given URL. Assumes there is no trailing
     * anchor/fragment indicated with a '#'.
     *
     * @param url       the URL to which to append.
     * @param paramName the name of the parameter to add.
     * @param paramVal  the value of the parameter to add.
     * @return the URL, with the given parameter added.
     */
    public static String addParam(String url, String paramName, String paramVal) {
        return url + (url.indexOf('?') != -1 ? '&' : '?') + paramName + '=' + paramVal;
    }

    public static void throwPageFlowException(FlowControllerException effect)
            throws FlowControllerException {
        if (effect.causeMayBeSessionExpiration() && ServletUtils.isSessionExpired(getWebContext())) {
            PageflowConfig pfc = ConfigUtil.getConfig().getPageflowConfig();
            if (pfc == null || !pfc.isSetThrowSessionExpiredException() || pfc.getThrowSessionExpiredException()) {
                throw new SessionExpiredException(effect);
            }
        }

        throw effect;
    }

    /**
     * Get the request URI, relative to the URI of the given PageFlowController.
     *
     * @param relativeTo a PageFlowController to which the returned URI should be relative, or
     *                   <code>null</code> if the returned URI should be relative to the webapp root.
     */
    public static final String getRelativeURI(PageFlowController relativeTo) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        if (relativeTo == null) return actionContext.getRequestPath();
        return getRelativeURI(actionContext.getRequestURI(), relativeTo);
    }

    /**
     * Get a URI relative to the URI of the given PageFlowController.
     *
     * @param uri        the URI which should be made relative.
     * @param relativeTo a PageFlowController to which the returned URI should be relative, or
     *                   <code>null</code> if the returned URI should be relative to the webapp root.
     */
    public static final String getRelativeURI(String uri, PageFlowController relativeTo) {
        String contextPath = PageFlowActionContext.get().getRequestPath();
        if (relativeTo != null) contextPath += relativeTo.getNamespace();
        int overlap = uri.indexOf(contextPath + '/');
        if (overlap == -1) return null;
        return uri.substring(overlap + contextPath.length());
    }

    /**
     * Set the forwarded form.  This overrides the auto-generated form created by processActionForm
     * and populated by processPopulate (in PageFlowRequestProcessor).
     */
    public static void setForwardedFormBean(Object formBean) {
        if (formBean == null) {
            getWebContext().getRequestScope().remove(FORWARDED_FORMBEAN_ATTR);
        } else {
            getWebContext().getRequestScope().put(FORWARDED_FORMBEAN_ATTR, formBean);
        }
    }

    public static Object getForwardedFormBean(boolean removeFromRequest) {
        Map requestScope = getWebContext().getRequestScope();
        Object form = requestScope.get(FORWARDED_FORMBEAN_ATTR);
        if (removeFromRequest) requestScope.remove(FORWARDED_FORMBEAN_ATTR);
        return form;
    }

    /**
     * Tell whether a special request attribute was set, indicating that we should avoid writing to the response (or
     * setting response error codes).
     */
    public static boolean avoidDirectResponseOutput() {
        Boolean avoid = (Boolean) getWebContext().getRequestScope().get(AVOID_DIRECT_RESPONSE_OUTPUT_ATTR);
        return avoid != null && avoid.booleanValue();
    }

    /**
     * Set a special request attribute to indicate that we should avoid writing to the response (or
     * setting response error codes).
     */
    public static void setAvoidDirectResponseOutput() {
        getWebContext().getRequestScope().put(AVOID_DIRECT_RESPONSE_OUTPUT_ATTR, Boolean.TRUE);
    }

    /**
     * Set the module prefix for the ModuleConfig that is performing a forward in this request.
     */
    public static void setForwardingModule(String modulePrefix) {
        getWebContext().getRequestScope().put(FORWARDING_MODULE_ATTR, modulePrefix);
    }

    /**
     * Set the module prefix for the ModuleConfig that is performing a forward in this request.
     */
    public static String getForwardingModule() {
        return (String) getWebContext().getRequestScope().get(FORWARDING_MODULE_ATTR);
    }

    /**
     * Tell {@link #getRequestPath} (and all that call it) to ignore the attribute that specifies the Servlet
     * Include path, which is set when a Servlet include is done through RequestDispatcher.  Normally,
     * getDecodedServletPath tries the Servlet Include path before falling back to getServletPath() on the request.
     * Note that this is basically a stack of instructions to ignore the include path, and this method expects each
     * call with <code>ignore</code>==<code>true</code> to be balanced by a call with
     * <code>ignore</code>==<code>false</code>.
     */
    /* TODO: re-add for page template support
    public static void setIgnoreIncludeServletPath( boolean ignore )
    {
        Map requestScope = getWebContext().getRequestScope();
        Integer depth = ( Integer ) requestScope.get( IGNORE_INCLUDE_SERVLET_PATH_ATTR );
        
        if ( ignore )
        {
            if ( depth == null ) depth = new Integer( 0 );
            requestScope.put( IGNORE_INCLUDE_SERVLET_PATH_ATTR, new Integer( depth.intValue() + 1 ) );
        }
        else
        {
            assert depth != null : "call to setIgnoreIncludeServletPath() was imbalanced";
            depth = new Integer( depth.intValue() - 1 );
            
            if ( depth.intValue() == 0 )
            {
                requestScope.remove( IGNORE_INCLUDE_SERVLET_PATH_ATTR );
            }
            else
            {
                requestScope.put( IGNORE_INCLUDE_SERVLET_PATH_ATTR, depth );
            }
        }
    }
    */

    public static boolean ignoreIncludeServletPath() {
        return getWebContext().getRequestScope().get(IGNORE_INCLUDE_SERVLET_PATH_ATTR) != null;
    }

    /**
     * Set the given Struts module in the request, and expose its set of MessageResources as request attributes.
     *
     * @param namespace the namespace of the desired module.
     * @return the selected ModuleConfig, or <code>null</code> if there is none for the given module prefix.
     */
    public static ModuleConfig selectModule(String namespace) {
        ModuleRegistrationHandler mrh = Handlers.get().getModuleRegistrationHandler();
        return selectModule(mrh.getModuleConfig(namespace));
    }

    public static ModuleConfig selectModule(ModuleConfig config) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();

        if (config == null) {
            actionContext.setModuleConfig(null);
            return null;
        }
        
        // Just return it if it's already registered.
        if (actionContext.getModuleConfig() == config) return config;
        actionContext.setModuleConfig(config);       

        /* TODO: re-add messages support
        MessageResourcesConfig[] mrConfig = config.findMessageResourcesConfigs();
        Object formBean = PageFlowActionContext.getContext().getAction().getFormBean();
        
        for ( int i = 0; i < mrConfig.length; i++ )
        {
            String key = mrConfig[i].getKey();
            MessageResources resources = ( MessageResources ) servletContext.getAttribute( key + prefix );
            
            if ( resources != null )
            {
                if ( ! ( resources instanceof ExpressionAwareMessageResources ) )
                {
                    resources = new ExpressionAwareMessageResources( resources, formBean, request, servletContext );
                }
                
                requestScope.put( key, resources );
            }
            else
            {
                requestScope.remove( key );
            }
        }
        */
        
        return config;
    }

    public static MessageResources getMessageResources(String bundleName) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        MessageResources resources = (MessageResources) actionContext.getRequestScope().get(bundleName);

        if (resources == null) {
            String qualified = getQualifiedBundleName(bundleName);
            resources = (MessageResources) getWebContext().getApplicationScope().get(qualified);
        }
        
        // If we can't find resources with this name, try them at the root (unqualified).
        if (resources == null) resources = (MessageResources) getWebContext().getApplicationScope().get(bundleName);

        return resources;
    }

    /**
     * Qualify the given bundle name with the current namespace to return a full bundle name.
     *
     * @return the qualified Bundle name
     */
    public static String getQualifiedBundleName(String bundleName) {
        if (bundleName != null) {
            if (bundleName.indexOf('/') == -1) {
                PageFlowActionContext actionContext = PageFlowActionContext.get();
                ModuleConfig mc = actionContext.getModuleConfig();

                // Note that we don't append the namespace for the root module.
                if (mc != null && mc.getNamespace() != null && mc.getNamespace().length() > 1) {
                    bundleName += mc.getNamespace();
                }
            } else if (bundleName.endsWith("/")) {
                // Special handling for bundles referring to the root module
                bundleName = bundleName.substring(0, bundleName.length() - 1);
            }
        }

        return bundleName;
    }

    public static Locale lookupLocale() {
        WebContext webContext = getWebContext();
        Locale locale = (Locale) webContext.getSessionScope().get(Globals.LOCALE_KEY);
        if (locale == null && webContext instanceof ServletWebContext) {
            locale = ((ServletWebContext) webContext).getRequest().getLocale();
        }
        return locale;
    }

    /**
     * If the request is a ScopedRequest, this returns an attribute name scoped to
     * that request's scope-ID; otherwise, it returns the given attribute name.
     */
    public static String getScopedAttrName(String attrName) {
        // Now, this is simply handled by the ActionContext implementation.  If it needs to wrap session attribute
        // names, it does.
        
        /*
        String requestScopeParam = request.getParameter( SCOPE_ID_PARAM );
        
        if ( requestScopeParam != null )
        {
            return getScopedName( attrName, requestScopeParam );
        }
        
        ScopedRequest scopedRequest = unwrapRequest( request );
        return scopedRequest != null ? scopedRequest.getScopedName( attrName ) : attrName;
        */
        return attrName;
    }

}
