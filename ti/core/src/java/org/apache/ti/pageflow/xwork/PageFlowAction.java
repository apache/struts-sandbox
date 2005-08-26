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
package org.apache.ti.pageflow.xwork;

import com.opensymphony.xwork.Action;
import org.apache.ti.Globals;
import org.apache.ti.core.urls.URLRewriterService;
import org.apache.ti.pageflow.ContainerAdapter;
import org.apache.ti.pageflow.FlowController;
import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.ModuleConfig;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowEventReporter;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.PageFlowUtils;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.interceptor.Interceptor;
import org.apache.ti.pageflow.interceptor.InterceptorException;
import org.apache.ti.pageflow.interceptor.Interceptors;
import org.apache.ti.pageflow.interceptor.action.ActionInterceptorContext;
import org.apache.ti.pageflow.interceptor.action.InterceptorForward;
import org.apache.ti.pageflow.interceptor.action.internal.ActionInterceptors;
import org.apache.ti.pageflow.interceptor.request.RequestInterceptorContext;
import org.apache.ti.pageflow.internal.AdapterManager;
import org.apache.ti.pageflow.internal.DefaultURLRewriter;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.internal.UnhandledException;
import org.apache.ti.script.common.ImplicitObjectUtil;
import org.apache.ti.util.logging.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Bean class to handle our extensions to the Struts &lt;action&gt; element.
 */
public class PageFlowAction implements Action, Serializable {

    private static final long serialVersionUID = 1;
    private static final Logger _log = Logger.getInstance(PageFlowAction.class);

    private String _unqualifiedActionPath;
    private boolean _loginRequired = false;
    private boolean _preventDoubleSubmit = false;
    private boolean _simpleAction = false;
    private boolean _isOverloaded = false;
    private String _formMember;
    private String _formBeanType;
    private String _formBeanAttribute;  // the attribute under which the form bean will be stored
    private boolean _readonly = false;
    private Map/*< String, String >*/ _conditionalForwards = new LinkedHashMap/*< String, String >*/();
    private String _formBeanMessageResourcesKey;
    private String _defaultForward;
    private String _validationErrorForward;

    public String getUnqualifiedActionPath() {
        return _unqualifiedActionPath;
    }

    public final void setUnqualifiedActionPath(String unqualifiedActionPath) {
        _unqualifiedActionPath = unqualifiedActionPath;
    }

    public final boolean isLoginRequired() {
        return _loginRequired;
    }

    public void setLoginRequired(boolean loginRequired) {
        _loginRequired = loginRequired;
    }

    public boolean isPreventDoubleSubmit() {
        return _preventDoubleSubmit;
    }

    public void setPreventDoubleSubmit(boolean preventDoubleSubmit) {
        _preventDoubleSubmit = preventDoubleSubmit;
    }

    public boolean isSimpleAction() {
        return _simpleAction;
    }

    public void setSimpleAction(boolean simpleAction) {
        _simpleAction = simpleAction;
    }

    public boolean isOverloaded() {
        return _isOverloaded;
    }

    public void setOverloaded(boolean overloaded) {
        _isOverloaded = overloaded;
    }

    public String getFormMember() {
        return _formMember;
    }

    public void setFormMember(String formMember) {
        _formMember = formMember;
    }

    public String getFormBeanType() {
        return _formBeanType;
    }

    public void setFormBeanType(String formBeanType) {
        _formBeanType = formBeanType;
    }

    public boolean isReadonly() {
        return _readonly;
    }

    public void setReadonly(boolean readonly) {
        _readonly = readonly;
    }

    public String getValidationErrorForward() {
        return _validationErrorForward;
    }

    public void setValidationErrorForward(String validationErrorForward) {
        _validationErrorForward = validationErrorForward;
    }

    public void setConditionalForwards(String conditionalForwards) {
        String[] pairs = conditionalForwards.split(";");

        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            int delim = pair.indexOf(':');
            assert delim != -1 : pair;
            String forwardName = pair.substring(0, delim);
            String expression = pair.substring(delim + 1);
            _conditionalForwards.put(expression, forwardName);
        }
    }

    /**
     * Get a map of expression -> forward-name.  If the expression evaluates to <code>true</code> the forward is used.
     */
    public Map/*< String, String >*/ getConditionalForwardsMap() {
        return _conditionalForwards;
    }

    public String getFormBeanMessageResourcesKey() {
        return _formBeanMessageResourcesKey;
    }

    public void setFormBeanMessageResourcesKey(String formBeanMessageResourcesKey) {
        _formBeanMessageResourcesKey = formBeanMessageResourcesKey;
    }

    public String getDefaultForward() {
        return _defaultForward;
    }

    public void setDefaultForward(String defaultForward) {
        _defaultForward = defaultForward;
    }

    public String getFormBeanAttribute() {
        return _formBeanAttribute;
    }

    public void setFormBeanAttribute(String formBeanAttribute) {
        _formBeanAttribute = formBeanAttribute;
    }

    private static int requestNumber = 0;


    /**
     * <p>Automatically select a <code>Locale</code> for the current user, if requested.
     * <strong>NOTE</strong> - configuring Locale selection will trigger
     * the creation of a new <code>HttpSession</code> if necessary.</p>
     */
    protected void processLocale() {

        // Has a Locale already been selected?
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        Map sessionScope = actionContext.getSession();
        if (sessionScope.get(Globals.LOCALE_KEY) != null) {
            return;
        }
        
        // Use the Locale returned by the servlet container (if any)
        Locale locale = actionContext.getLocale();
        if (locale != null) {
            if (_log.isDebugEnabled()) {
                _log.debug("Setting user locale '" + locale + '\'');
            }
            sessionScope.put(Globals.LOCALE_KEY, locale);
        }

    }

    private void processInternal()
            throws IOException, PageFlowException {
        //
        // The requested action can be overridden by a request parameter.  In this case, we parse the action from
        // the request parameter and forward to a URI constructed from it.  If this happens, just return.
        //
        // TODO: re-add this, in Chain
//        if ( processActionOverride() ) return;
        
        //
        // Process any direct request for a page flow by forwarding to its "begin" action.
        //
        // TODO: re-add this
//        if ( processPageFlowRequest( request, response, uri ) ) return;
        
        PageFlowActionContext actionContext = PageFlowActionContext.get();        
        
        //
        // Remove any current JavaServer Faces backing bean.  We have "left" any JSF page and are now processing a
        // Page Flow action.
        //
        InternalUtils.removeCurrentFacesBackingBean();
        
        //
        // Set up implicit objects used by the expression language in simple actions and in declarative validation.
        //
        ImplicitObjectUtil.loadImplicitObjects(PageFlowUtils.getCurrentPageFlow());

        try {
            // Select a Locale for the current user if requested
            // TODO: re-add Locale-choosing, in Chain
            // processLocale();
            
            // Check for any role required to perform this action
            // TODO: re-add roles, in Chain
//        if (!processRoles(request, response, mapping)) {
//            return;
//        }
            
            // TODO: re-add login-required support, in Chain
            
            // TODO: re-add action form creation (store in context), population and validation (in Chain)
            // Process any ActionForm bean related to this request
//        ActionForm form = processActionForm(request, response, mapping);
//        processPopulate(request, response, form, mapping);
//        if (!processValidate(request, response, form, mapping)) {
//            return;
//        }
            
            // Call the action instance itself
            Forward forward = processActionPerform();
            actionContext.setForward(forward);
        } catch (UnhandledException unhandledException) {
            // If we get here, then we've already tried to find an exception handler.  Just throw.
            rethrowUnhandledException(unhandledException);
        } catch (PageFlowException servletEx) {
            // If a PageFlowException escapes out of any of the processing methods, let the current FlowController handle it.
            FlowController currentFlowController = actionContext.getFlowController();
            if (!handleException(servletEx, currentFlowController)) throw servletEx;
        } catch (IOException ioe) {
            // If an IOException escapes out of any of the processing methods, let the current FlowController handle it.
            FlowController currentFlowController = actionContext.getFlowController();
            if (!handleException(ioe, currentFlowController)) throw ioe;
        } catch (Throwable th) {
            // If a Throwable escapes out of any of the processing methods, let the current FlowController handle it.
            FlowController currentFlowController = actionContext.getFlowController();
            if (!handleException(th, currentFlowController)) {
                if (th instanceof Error) throw (Error) th;
                throw new PageFlowException(th);
            }
        }
    }

    private boolean handleException(Throwable th, FlowController fc) {
        if (fc != null) {
            try {
                Forward fwd = fc.handleException(th);
                PageFlowActionContext.get().setForward(fwd);
                return true;
            } catch (UnhandledException unhandledException) {
                if (_log.isInfoEnabled()) {
                    _log.info("This exception was unhandled by any exception handler.", unhandledException);
                }

                return false;
            } catch (Throwable t) {
                _log.error("Exception while handling exception " + th.getClass().getName()
                        + ".  The original exception will be thrown.", t);
                return false;
            }
        }

        return false;
    }

    public String execute() throws Exception {
        int localRequestCount = -1;

        if (_log.isTraceEnabled()) {
            localRequestCount = ++requestNumber;
            _log.trace("------------------------------- Start Request #" + localRequestCount
                    + " -----------------------------------");
        }
        
        //
        // First reinitialize the reloadable class handler.  This will bounce a classloader if necessary.
        //
        Handlers.get().getReloadableClassHandler().reloadClasses();
        
        //
        // Go through the chain of pre-request interceptors.
        //
        RequestInterceptorContext context = new RequestInterceptorContext();
        List/*< Interceptor >*/ interceptors = context.getRequestInterceptors();

        try {
            Interceptors.doPreIntercept(context, interceptors);

            if (context.requestWasCancelled()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("Interceptor " + context.getOverridingInterceptor() + " cancelled the request.");
                }

                return null;
            }
        } catch (InterceptorException e) {
            throw new PageFlowException(e);
        }
        
        //
        // Callback to the server adapter.
        //
        ContainerAdapter containerAdapter = AdapterManager.getContainerAdapter();
        PageFlowEventReporter er = containerAdapter.getEventReporter();
        containerAdapter.beginRequest();
        er.beginActionRequest();
        long startTime = System.currentTimeMillis();
        
        //
        // Initialize the ControlBeanContext in the session.
        //
        // TODO: re-add Controls support
        // JavaControlUtils.initializeControlContext( request, response, servletContext );
        
        //
        // Register the default URLRewriter
        //
        URLRewriterService.registerURLRewriter(0, new DefaultURLRewriter());

        try {
            processInternal();
        } finally {
            //
            // Clean up the ControlBeanContext in the session.
            //
            // TODO: re-add Controls support
            // JavaControlUtils.uninitializeControlContext( request, response, getServletContext() );
            
            //
            // Callback to the server adapter.
            //
            containerAdapter.endRequest();
            long timeTaken = System.currentTimeMillis() - startTime;
            er.endActionRequest(timeTaken);
        }
        
        //
        // Go through the chain of pre-request interceptors.
        //
        try {
            Interceptors.doPostIntercept(context, interceptors);
        } catch (InterceptorException e) {
            throw new PageFlowException(e);
        }

        if (_log.isTraceEnabled()) {
            _log.trace("-------------------------------- End Request #" + localRequestCount
                    + " ------------------------------------");
        }

        Forward fwd = PageFlowActionContext.get().getForward();
        return fwd != null ? fwd.getName() : null;
    }

    private static final void rethrowUnhandledException(UnhandledException ex)
            throws PageFlowException {
        Throwable rootCause = ex.getCause();
        
        //
        // We shouldn't (and don't need to) wrap Errors or RuntimeExceptions.
        //
        if (rootCause instanceof Error) {
            throw (Error) rootCause;
        } else if (rootCause instanceof RuntimeException) {
            throw (RuntimeException) rootCause;
        }

        throw ex;
    }

    public Forward processException(Exception ex, Object form, PageFlowAction mapping)
            throws IOException, PageFlowException {
        //
        // Note: we should only get here if FlowController.handleException itself throws an exception, or if the user
        // has merged in Struts code that delegates to an action/exception-handler outside of the pageflow.
        //
        // If this is an UnhandledException thrown from FlowController.handleException, don't try to re-handle it here.
        //
        
        if (ex instanceof UnhandledException) {
            rethrowUnhandledException((UnhandledException) ex);
            assert false;   // rethrowUnhandledException always throws something.
            return null;
        } else {
            throw new PageFlowException(ex);
        }
    }

    public void init()
            throws PageFlowException {
        //
        // Cache a list of overloaded actions for each overloaded action path (actions are overloaded by form bean type).
        //
        // TODO: re-add overloaded action support
//        cacheOverloadedPageFlowActions();
        
        //
        // Cache the form bean Classes by form bean name.
        //
        // TODO: re-add class caching?
//        cacheFormClasses();
    }


    private static class ActionRunner
            implements ActionInterceptors.ActionExecutor {

        public ActionRunner() {
        }

        public Forward execute()
                throws InterceptorException, PageFlowException {

            PageFlowActionContext actionContext = PageFlowActionContext.get();
            return actionContext.getFlowController().execute();
        }
    }

    protected Forward processActionPerform()
            throws IOException, PageFlowException {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        String actionName = actionContext.getName();
        ActionInterceptorContext context = null;
        List/*< Interceptor >*/ interceptors = null;

        FlowController fc = actionContext.getFlowController();

        if (fc instanceof PageFlowController) {
            PageFlowController pfc = (PageFlowController) fc;
            context = new ActionInterceptorContext(pfc, null, actionName);
            interceptors = context.getActionInterceptors();
        }

        if (interceptors != null && interceptors.size() == 0) interceptors = null;

        try {
            //
            // Run any before-action interceptors.
            //
            if (interceptors != null && !actionContext.isReturningFromActionIntercept()) {
                Interceptors.doPreIntercept(context, interceptors);

                if (context.hasInterceptorForward()) {
                    InterceptorForward fwd = context.getInterceptorForward();

                    if (_log.isDebugEnabled()) {

                        Interceptor overridingInterceptor = context.getOverridingInterceptor();
                        StringBuffer msg = new StringBuffer();
                        msg.append("action interceptor ");
                        msg.append(overridingInterceptor.getClass().getName());
                        msg.append(" before action ");
                        msg.append(actionName);
                        msg.append(": forwarding to ");
                        msg.append(fwd != null ? fwd.getPath() : "null [no forward]");
                        _log.debug(msg.toString());
                    }

                    return fwd;
                }
            } else {
                actionContext.setReturningFromActionIntercept(false);
            }
            
            //
            // Execute the action.
            //
            ActionRunner actionExecutor = new ActionRunner();
            Forward ret = ActionInterceptors.wrapAction(context, interceptors, actionExecutor);
            
            //
            // Run any after-action interceptors.
            //
            if (interceptors != null) {
                context.setOriginalForward(ret);
                Interceptors.doPostIntercept(context, interceptors);

                if (context.hasInterceptorForward()) {
                    InterceptorForward fwd = context.getInterceptorForward();

                    if (_log.isDebugEnabled()) {
                        _log.debug("action interceptor " + context.getOverridingInterceptor().getClass().getName()
                                + " after action " + actionName + ": forwarding to "
                                + fwd != null ? fwd.getPath() : "null [no forward]");
                    }

                    return fwd;
                }
            }

            return ret;
        } catch (InterceptorException e) {
            throw new PageFlowException(e);
        }
    }

    public String findExceptionHandler(Class exceptionType) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        ModuleConfig mc = actionContext.getModuleConfig();
        String actionName = actionContext.getName();

        for (Class i = exceptionType; i != null; i = i.getSuperclass()) {
            String handlerName = actionName + ':' + i.getName();
            if (mc.findActionConfig(handlerName) != null) return handlerName;
        }

        return mc.findExceptionHandler(exceptionType);
    }
}
