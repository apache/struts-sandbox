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

import com.opensymphony.xwork.Action;
import org.apache.ti.core.ActionMessage;
import org.apache.ti.core.urls.MutableURI;
import org.apache.ti.pageflow.handler.ExceptionsHandler;
import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.handler.LoginHandler;
import org.apache.ti.pageflow.internal.AdapterManager;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.InternalExpressionUtils;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.xwork.PageFlowAction;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.pageflow.xwork.PageFlowResult;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.internal.ServletUtils;
import org.apache.ti.util.internal.cache.ClassLevelCache;
import org.apache.ti.util.logging.Logger;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;


/**
 * Base class for user-written flow controllers - {@link PageFlowController}s and {@link SharedFlowController}s.
 */
public abstract class FlowController extends PageFlowManagedObject
        implements PageFlowConstants, ActionResolver {

    private static final Logger _log = Logger.getInstance(FlowController.class);

    private static final String ONCREATE_EXCEPTION_FORWARD = InternalConstants.ATTR_PREFIX + "onCreateException";
    private static final String CACHEID_ACTION_METHODS = InternalConstants.ATTR_PREFIX + "actionMethods";
    private static final int DEFAULT_MAX_CONCURRENT_REQUEST_COUNT = 4;
    private static final int EXCEEDED_MAX_CONCURRENT_REQUESTS_ERRORCODE = 503;
    private static final Forward NULL_ACTION_FORWARD = new Forward();


    /**
     * Cached reference to the associated Struts ModuleConfig.
     */
    private transient ModuleConfig _moduleConfig = null;

    /**
     * @see #incrementRequestCount
     */
    private transient int _requestCount = 0;

    /**
     * Default constructor.
     */
    protected FlowController() {
    }

    /**
     * Reinitialize the object for a new request.  Used by the framework; normally should not be called directly.
     */
    public void reinitialize() {
        //
        // Cache the associated ModuleConfig.  This is used throughout the code, in places where the request
        // isn't available to do a lazy initialization.
        //
        super.reinitialize();
        initModuleConfig();
    }

    /**
     * Send a Page Flow error to the browser.
     *
     * @param errText the error message to display.
     */
    protected void sendError(String errText)
            throws IOException {
        InternalUtils.sendError("PageFlow_Custom_Error", null, new Object[]{getDisplayName(), errText});
    }

    /**
     * Handle the given exception - invoke user code if appropriate and return a destination URI.
     *
     * @param ex the Exception to handle.
     * @throws PageFlowException if another Exception is thrown during handling of <code>ex</code>.
     */
    public synchronized Forward handleException(Throwable ex)
            throws PageFlowException {
        ExceptionsHandler eh = Handlers.get().getExceptionsHandler();
        
        // First, put the exception into the request (or other applicable context).
        Throwable unwrapped = eh.unwrapException(ex);
        eh.exposeException(unwrapped);
        eh.handleException(unwrapped);
        return new Forward(Action.NONE);
    }

    /**
     * Get the name of the current action being executed.  This call is only valid
     * during {@link FlowController#execute} (where any user action method is invoked), and during the lifecycle
     * methods {@link FlowController#beforeAction} and {@link FlowController#afterAction}.
     *
     * @return the name of the current action being executed.
     * @throws IllegalStateException if this method is invoked outside of action method
     *                               execution (i.e., outside of the call to {@link FlowController#execute}, and outside of
     *                               {@link FlowController#onCreate}, {@link FlowController#beforeAction}, {@link FlowController#afterAction}.
     */

    protected static String getCurrentActionName() {
        return getContext().getName();
    }

    /**
     * Perform decision logic to determine the next URI to be displayed.
     *
     * @return a Struts forward object that specifies the next URI to be displayed.
     * @throws PageFlowException if an Exception was thrown during user action-handling code.
     */
    public Forward execute()
            throws PageFlowException {
        //
        // Don't actually run the action (and perform the associated synchronization) if there are too many
        // concurrent requests to this instance.
        //
        if (incrementRequestCount()) {
            try {
                synchronized (this) {
                    return internalExecute();
                }
            } finally {
                decrementRequestCount();
            }
        } else {
            return null;    // error was written to the response by incrementRequestCount()
        }
    }

    /**
     * An internal method for executing an action; should not be invoked directly.
     */
    protected Forward internalExecute()
            throws PageFlowException {
        ContainerAdapter sca = AdapterManager.getContainerAdapter();
        PageFlowEventReporter eventReporter = sca.getEventReporter();
        eventReporter.actionRaised(this);
        long startTime = System.currentTimeMillis();
        
        //
        // If we handled an exception in onCreate, just forward to the result of that.
        //
        Forward onCreateFwd = (Forward) getContext().getRequestScope().get(ONCREATE_EXCEPTION_FORWARD);

        if (onCreateFwd != null) {
            return onCreateFwd == NULL_ACTION_FORWARD ? null : onCreateFwd;
        }


        PageFlowUtils.setActionPath();
        
        // Store information on this action for use with navigateTo=ti.NavigateTo.previousAction.
        savePreviousActionInfo();
        
        
        //
        // First change the actionPath (path) so that it lines up with our naming convention
        // for action methods.
        //
        boolean gotPastBeforeAction = false;

        try {
            //
            // beforeAction callback
            //
            beforeAction();
            gotPastBeforeAction = true;

            PageFlowActionContext actionContext = PageFlowActionContext.get();
            PageFlowAction pfAction = actionContext.getAction();
            String actionName = actionContext.getName();
            
            //
            // Check whether isLoginRequired=true for this action.
            //
            LoginHandler loginHandler = Handlers.get().getLoginHandler();

            if (pfAction.isLoginRequired() && loginHandler.getUserPrincipal() == null) {
                NotLoggedInException ex = createNotLoggedInException(actionName);
                return handleException(ex);
            }
            
            //
            // Now delegate to the appropriate action method, or if it's a simple action, handle it that way.
            //
            Forward retVal;
            if (pfAction.isSimpleAction()) {
                retVal = handleSimpleAction(pfAction);
            } else {
                retVal = getActionMethodForward(actionName);
            }

            long timeTaken = System.currentTimeMillis() - startTime;
            eventReporter.actionSuccess(this, retVal, timeTaken);
            return retVal;
        } catch (Exception e) {
            //
            // Even though we handle any Throwable thrown by the user's action method, we don't need
            // to catch Throwable here, because anything thrown by the action method will be wrapped
            // in an InvocationTargetException.  Any Error (or other Throwable) that appears here
            // should not be handled by handleException() -- it's probably a framework problem and
            // should bubble out to the container.
            //
            return handleException(e);
        } finally {
            Forward overrideReturn = null;

            if (gotPastBeforeAction) {
                //
                // afterAction callback
                //
                try {
                    afterAction();
                } catch (Throwable th) {
                    overrideReturn = handleException(th);
                }
            }

            if (overrideReturn != null) return overrideReturn;
        }
    }

    NotLoggedInException createNotLoggedInException(String actionName) {
        if (ServletUtils.isSessionExpired(getContext().getWebContext())) {
            return new LoginExpiredException(this);
        } else {
            return new NotLoggedInException(this);
        }
    }

    public void login(String username, String password) throws LoginException {
        Handlers.get().getLoginHandler().login(username, password);
    }

    public void logout(boolean invalidateSessions) {
        Handlers.get().getLoginHandler().logout(invalidateSessions);
    }

    /**
     * Initialize after object creation.  This is a framework-invoked method; it should not normally be called directly.
     */
    public synchronized void create() {
        try {
            super.create();
        } catch (Throwable th) {
            try {
                _log.info("Handling exception in onCreate(), FlowController " + this, th);
                Forward fwd = handleException(th);
                if (fwd == null) fwd = NULL_ACTION_FORWARD;
                getContext().getRequestScope().put(ONCREATE_EXCEPTION_FORWARD, fwd);
            } catch (Exception e) {
                _log.error("Exception thrown while handling exception in onCreate(): " + e.getMessage(), th);
            }
        }

        PageFlowEventReporter er = AdapterManager.getContainerAdapter().getEventReporter();
        er.flowControllerCreated(this);
    }

    /**
     * Internal destroy method that is invoked when this object is being removed from the session.  This is a
     * framework-invoked method; it should not normally be called directly.
     */
    void destroy() {
        super.destroy();
        PageFlowEventReporter er = AdapterManager.getContainerAdapter().getEventReporter();
        er.flowControllerDestroyed(this, Handlers.get().getStorageHandler().getStorageLocation());
    }

    /**
     * Get the namespace for this controller.
     *
     * @return a String that is the namespace for this controller.
     */
    public abstract String getNamespace();

    /**
     * Callback that occurs before any user action method is invoked.
     */
    protected synchronized void beforeAction()
            throws Exception {
    }

    /**
     * Callback that occurs after any user action method is invoked.
     */
    protected synchronized void afterAction()
            throws Exception {
    }

    /**
     * Callback that is invoked when this controller instance is created.
     */
    protected void onCreate()
            throws Exception {
    }

    /**
     * Callback that is invoked when this controller instance is "destroyed", i.e., removed from storage.
     * <br>
     * Note that this method is <strong>not synchronized</strong>.  It is dangerous to synchronize your override of
     * this method because it may be invoked during a callback from the container.  Depending on the container,
     * synchronization here can cause deadlocks.
     */
    protected void onDestroy() {
    }

    /**
     * Get an action handler method of the given name/signature.
     *
     * @param methodName the name of the action handler method to query.
     * @param argType    the type of the argument to the action handler method; if <code>null</code>,
     *                   the method takes no arguments.
     * @return the desired Method, or <code>null</code> if it doesn't exist.
     */
    protected Method getActionMethod(String methodName, Class argType) {
        String cacheKey = argType != null ? methodName + '/' + argType.getName() : methodName;
        Class thisClass = getClass();
        ClassLevelCache cache = ClassLevelCache.getCache(thisClass);
        Method actionMethod = (Method) cache.get(CACHEID_ACTION_METHODS, cacheKey);

        if (actionMethod != null) {
            return actionMethod;
        } else {
            //
            // We didn't find it in the cache.  Look for it reflectively.
            //
            if (argType == null) {
                //
                // No form -- look for a method with no arguments.
                //
                actionMethod = InternalUtils.lookupMethod(thisClass, methodName, null);
            } else {
                //
                // Has a form.  Look for a method with a single argument -- either the given type
                // or any superclass.
                //
                while (argType != null) {
                    actionMethod = InternalUtils.lookupMethod(thisClass, methodName, new Class[]{argType});

                    if (actionMethod != null) {
                        break;
                    }

                    argType = argType.getSuperclass();
                }
            }

            if (actionMethod != null) {
                Class returnType = actionMethod.getReturnType();
                if (returnType.equals(Forward.class) || returnType.equals(String.class)) {
                    if (!Modifier.isPublic(actionMethod.getModifiers())) actionMethod.setAccessible(true);
                    cache.put(CACHEID_ACTION_METHODS, cacheKey, actionMethod);
                    return actionMethod;
                }
            }
        }

        return null;
    }

    private Class getFormClass(Object form)
            throws ClassNotFoundException {
        String formClassName = getAction().getFormBeanType();
        if (formClassName != null) return InternalUtils.getReloadableClass(formClassName);
        return form != null ? form.getClass() : null;
    }

    /**
     * Get the forward returned by the action handler method that corresponds to the
     * given action name and form-bean, or send an error to the browser if there is no
     * matching method.
     *
     * @param actionName the name of the Struts action to handle.
     * @return the forward returned by the action handler method, or <code>null</code> if
     *         there was no matching method (in which case an error was written to the
     *         browser.
     * @throws Exception if an Exception was raised in user code.
     */
    Forward getActionMethodForward(String actionName)
            throws Exception {
        //
        // Find the method.
        //
        Object formBean = getContext().getFormBean();
        Class formClass = getFormClass(formBean);
        Method actionMethod = getActionMethod(actionName, formClass);

        //
        // Invoke the method.
        //
        if (actionMethod != null) {
            return invokeActionMethod(actionMethod, formBean);
        }

        if (_log.isWarnEnabled()) {
            InternalStringBuilder msg = new InternalStringBuilder("Could not find matching action method for action=");
            msg.append(actionName).append(", form=");
            msg.append(formBean != null ? formBean.getClass().getName() : "[none]");
            _log.warn(msg.toString());
        }

        FlowControllerException ex = new NoMatchingActionMethodException(formBean, this);
        InternalUtils.throwPageFlowException(ex);
        return null;
    }

    /**
     * Invoke the given action handler method, passing it an argument if appropriate.
     *
     * @param method the action handler method to invoke.
     * @param arg    the form-bean to pass; may be <code>null</code>.
     * @return the forward returned by the action handler method.
     * @throws Exception if an Exception was raised in user code.
     */
    Forward invokeActionMethod(Method method, Object arg)
            throws Exception {
        Class[] paramTypes = method.getParameterTypes();

        try {
            if (paramTypes.length > 0 && paramTypes[0].isInstance(arg)) {
                if (_log.isDebugEnabled()) {
                    _log.debug("Invoking action method " + method.getName() + '(' + paramTypes[0].getName() + ')');
                }

                return invokeForwardMethod(method, new Object[]{arg});
            } else if (paramTypes.length == 0) {
                if (_log.isDebugEnabled()) {
                    _log.debug("Invoking action method " + method.getName() + "()");
                }

                return invokeForwardMethod(method, null);
            }
        } finally {
            if (!getAction().isReadonly()) {
                ensureFailover();
            }
        }

        if (_log.isWarnEnabled()) {
            _log.warn("Could not find action method " + method.getName() + " with appropriate signature.");
        }

        return null;
    }

    private Forward invokeForwardMethod(Method method, Object[] args)
            throws IllegalAccessException, InvocationTargetException {
        Object result = method.invoke(this, args);
        if (result instanceof String) {
            result = new Forward((String) result);
        }
        return (Forward) result;
    }

    private void initModuleConfig() {
        if (_moduleConfig == null) {
            _moduleConfig = Handlers.get().getModuleRegistrationHandler().getModuleConfig(getNamespace());
            assert _moduleConfig != null : getNamespace() + "; " + getClass().getName();
        }
    }

    /**
     * Gets the Struts module configuration associated with this controller.
     *
     * @return the Struts ModuleConfig for this controller.
     */
    public ModuleConfig getModuleConfig() {
        initModuleConfig();
        return _moduleConfig;
    }

    /**
     * Call an action and return the result URI.
     * 
     * @param actionName the name of the action to run.
     * @param form the form bean instance to pass to the action, or <code>null</code> if none should be passed.
     * @return the result webapp-relative URI, as a String.
     * @throws ActionNotFoundException when the given action does not exist in this FlowController.
     * @throws Exception if the action method throws an Exception.
     */ 
    /* TODO: re-enable this method
    public String resolveAction( String actionName, Object form )
        throws Exception
    {
        ActionConfig mapping = ( ActionConfig ) getModuleConfig().findActionConfig( '/' + actionName );
        
        if ( mapping == null )
        {
            InternalUtils.throwPageFlowException( new ActionNotFoundException( actionName, this, form ) );
        }
        
        forward fwd = getActionMethodForward( actionName, form );
        
        if ( fwd instanceof forward )
        {
            ( ( forward ) fwd ).initialize();
        }
        
        String path = fwd.getPath();
        if ( path.startsWith("/") || FileUtils.isAbsoluteURI( path ) )
        {
            return path;
        }
        else
        {
            return '/' + getNamespace() + '/' + path;
        }
    }
    */

    /**
     * Get a list of the names of actions handled by methods in this PageFlowController.
     *
     * @return a String array containing the names of actions handled by methods in this PageFlowController.
     */
    public String[] getActions() {
        Map actionConfigs = getModuleConfig().getActionConfigs();
        return (String[]) actionConfigs.keySet().toArray(new String[actionConfigs.size()]);
    }

    /**
     * Tell whether a given String is the name of an action handled by a method in this PageFlowController.
     *
     * @param name the action-name to query.
     * @return <code>true</code> if <code>name</code> is the name of an action handled by a method in this
     *         PageFlowController.
     */
    public boolean isAction(String name) {
        return getModuleConfig().findActionConfig(name) != null;
    }

    /**
     * Called on this object for non-lookup (refresh) requests.  This is a framework-invoked method that should not
     * normally be called directly.
     */
    public final synchronized void refresh() {
        onRefresh();
    }

    /**
     * Callback that is invoked when this controller is involved in a refresh request, as can happen in a portal
     * environment on a request where no action is run in the current page flow, but a previously-displayed page in the
     * page flow is re-rendered.
     */
    protected void onRefresh() {
    }

    /**
     * Remove this instance from the user session.
     */
    protected void remove() {
        removeFromSession();
    }

    /**
     * Used by derived classes to store information on the most recent action executed.
     */
    void savePreviousActionInfo() {
    }

    /**
     * Store information about recent pages displayed.  This is a framework-invoked method that should not normally be
     * called directly.
     */
    public void savePreviousPageInfo(PageFlowResult result, Forward fwd, Object form) {
    }

    /**
     * When this FlowController does not use a {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}
     * annotation with a
     * <code>navigateTo=</code>{@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousAction ti.NavigateTo.previousAction}
     * attribute, the following methods always return <code>null</code> by default.
     * <ul>
     * <li>getPreviousActionInfo</li>
     * <li>getPreviousActionURI</li>
     * <li>getPreviousForm</li>
     * </ul>
     * Override <code>alwaysTrackPreviousAction</code> (which always returns <code>false</code>) to enable these methods
     * in all cases.
     *
     * @return <code>true</code> if the previous action should always be tracked, regardless of whether
     *         <code>return-to="previousAction"</code> is used.
     * @see PageFlowController#getPreviousActionInfo
     * @see PageFlowController#getPreviousActionURI
     * @see PageFlowController#getPreviousFormBean
     */
    protected boolean alwaysTrackPreviousAction() {
        return false;
    }

    /**
     * When this FlowController does not use a {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}
     * annotation with either a
     * <code>navigateTo</code>={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#currentPage ti.NavigateTo.currentPage}
     * attribute or a
     * <code>navigateTo</code>={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousPage ti.NavigateTo.previousPage}
     * attribute, the following methods always return <code>null</code> by default.
     * <ul>
     * <li>getCurrentPageInfo</li>
     * <li>getPreviousPageInfo</li>
     * <li>getCurrentForwardPath</li>
     * <li>getPreviousForwardPath</li>
     * </ul>
     * Override <code>alwaysTrackPreviousPage</code> (which always returns <code>false</code>) to enable these methods
     * in all cases.
     *
     * @return <code>true</code> if the previous page should always be tracked, regardless
     *         of whether <code>return-to="currentPage"</code> or <code>return-to="previousPage"</code>
     *         is used.
     * @see PageFlowController#getCurrentPageInfo
     * @see PageFlowController#getPreviousPageInfo
     * @see PageFlowController#getCurrentForwardPath
     * @see PageFlowController#getPreviousForwardPath
     */
    protected boolean alwaysTrackPreviousPage() {
        return false;
    }

    /**
     * Increment the count of concurrent requests to this FlowController.  Note that this method
     * is not synchronized -- it is used to decide whether to synchronize on this instance,
     * or to bail out with an error message about too many concurrent requests.  This is a framework-invoked
     * method that should not normally be called directly.
     */
    public boolean incrementRequestCount()
            throws PageFlowException {
        //
        // Now, if the current count of concurrent requests to this instance is greater than the max,
        // send an error on the response.
        //
        if (_requestCount >= DEFAULT_MAX_CONCURRENT_REQUEST_COUNT) {
            if (_log.isDebugEnabled()) {
                _log.debug("Too many requests to FlowController " + getDisplayName() + " ("
                        + (_requestCount + 1) + '>' + DEFAULT_MAX_CONCURRENT_REQUEST_COUNT
                        + "); returning error code " + EXCEEDED_MAX_CONCURRENT_REQUESTS_ERRORCODE);
            }

            // TODO: re-add (need an abstraction for this)
            //response.sendError( EXCEEDED_MAX_CONCURRENT_REQUESTS_ERRORCODE );
            return false;
        }
        
        //
        // We're ok -- increment the count and continue.
        //
        ++_requestCount;
        return true;
    }

    /**
     * Decrement the count of concurrent requests to this FlowController.  Note that this method is not synchronized --
     * it is used in conjunction with {@link #incrementRequestCount} to decide whether to synchronize on this instance,
     * or to bail out with an error message about too many concurrent requests.  This is a framework-invoked
     * method that should not normally be called directly.
     */
    public void decrementRequestCount() {
        assert _requestCount > 0 : getContext().getRequestPath();
        --_requestCount;
    }

    /**
     * Invoke the given exception handler method.  This is a framework-invoked method that should not normally be called
     * directly
     *
     * @param method   the action handler method to invoke.
     * @param ex       the Throwable that is to be handled.
     * @param message  the String message that is to be passed to the handler method.
     * @param formBean the form bean that is associated with the action being processed; may be <code>null</code>.
     * @param readonly if <code>true</code>, session failover will not be triggered after invoking the method.
     * @return the forward returned by the exception handler method.
     */
    public synchronized Forward invokeExceptionHandler(Method method, Throwable ex, String message,
                                                       Object formBean, boolean readonly)
            throws PageFlowException {
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("Invoking exception handler method " + method.getName() + '('
                        + method.getParameterTypes()[0].getName() + ", ...)");
            }

            try {
                Forward retVal = null;

                try {
                    Object[] args = new Object[]{ex, message};
                    retVal = invokeForwardMethod(method, args);
                } finally {
                    if (!readonly) {
                        ensureFailover();
                    }
                }

                return retVal;
            } catch (InvocationTargetException e) {
                Throwable target = e.getTargetException();

                if (target instanceof Exception) {
                    throw (Exception) target;
                } else {
                    throw e;
                }
            }
        } catch (Throwable e) {
            _log.error("Exception while handling exception " + ex.getClass().getName()
                    + ".  The original exception will be thrown.", e);

            ExceptionsHandler eh = Handlers.get().getExceptionsHandler();
            Throwable unwrapped = eh.unwrapException(e);

            if (!eh.eatUnhandledException(unwrapped)) {
                if (ex instanceof PageFlowException) throw (PageFlowException) ex;
                if (ex instanceof Error) throw (Error) ex;
                throw new PageFlowException(ex);
            }

            return null;
        }
    }

    /**
     * Add a property-related message that will be shown with the Errors and Error tags.
     *
     * @param propertyName the name of the property with which to associate this error.
     * @param messageKey   the message-resources key for the message.
     * @param messageArgs  zero or more arguments to the message.
     */
    protected static void addActionError(String propertyName, String messageKey, Object[] messageArgs) {
        InternalUtils.addActionError(propertyName, new ActionMessage(messageKey, messageArgs));
    }

    /**
     * Add a property-related message as an expression that will be evaluated and shown with the Errors and Error tags.
     *
     * @param propertyName the name of the property with which to associate this error.
     * @param expression   the expression that will be evaluated to generate the error message.
     * @param messageArgs  zero or more arguments to the message; may be expressions.
     */
    protected static void addActionErrorExpression(String propertyName, String expression, Object[] messageArgs) {
        PageFlowUtils.addActionErrorExpression(propertyName, expression, messageArgs);
    }

    private static Forward handleSimpleAction(PageFlowAction action) {
        Map/*< String, String >*/ conditionalForwards = action.getConditionalForwardsMap();

        if (!conditionalForwards.isEmpty()) {
            for (Iterator/*< Map.Entry< String, String > >*/ i = conditionalForwards.entrySet().iterator(); i.hasNext();) {
                Map.Entry/*< String, String >*/ entry = (Map.Entry) i.next();
                String expression = (String) entry.getKey();
                String forwardName = (String) entry.getValue();

                try {
                    if (InternalExpressionUtils.evaluateCondition(expression)) {
                        if (_log.isTraceEnabled()) {
                            PageFlowActionContext actionContext = PageFlowActionContext.get();
                            _log.trace("Expression '" + expression + "' evaluated to true on simple action "
                                    + actionContext.getName() + "; using forward "
                                    + forwardName + '.');
                        }

                        return new Forward(forwardName);
                    }
                } catch (Exception e)  // ELException
                {
                    if (_log.isErrorEnabled()) {
                        _log.error("Exception occurred evaluating navigation expression '" + expression
                                + "'.  Cause: " + e.getCause(), e);
                    }
                }
            }
        }


        String defaultForwardName = action.getDefaultForward();
        assert defaultForwardName != null : "defaultForwardName is null on simple action "
                + PageFlowActionContext.get().getName();

        if (_log.isTraceEnabled()) {
            PageFlowActionContext actionContext = PageFlowActionContext.get();
            _log.trace("No expression evaluated to true on simple action " + actionContext.getName()
                    + "; using forward " + defaultForwardName + '.');
        }

        return new Forward(defaultForwardName);
    }


    /**
     * Get the flow-scoped form bean member associated with the given ActionConfig.  This is a framework-invoked
     * method that should not normally be called directly.
     */
    public Object getFormBean(PageFlowAction action) {
        String formMember = action.getFormBeanMember();

        try {
            if (formMember != null) {
                Field field = getClass().getDeclaredField(formMember);
                field.setAccessible(true);
                return field.get(this);
            }
        } catch (Exception e) {
            _log.error("Could not use member field " + formMember + " as the form bean.", e);
        }

        return null;
    }

    /**
     * Create a raw action URI, which can be modified before being sent through the registered URL rewriting chain
     * using {@link org.apache.ti.core.urls.URLRewriterService#rewriteURL}.
     *
     * @param actionName the action name to convert into a MutableURI; may be qualified with a path from the webapp
     *                   root, in which case the parent directory from the current request is <i>not</i> used.
     * @return a MutableURI for the given action, suitable for URL rewriting.
     * @throws URISyntaxException    if there is a problem converting the action URI (derived
     *                               from processing the given action name) into a MutableURI.
     * @throws IllegalStateException if this method is invoked outside of action method
     *                               execution (i.e., outside of the call to {@link FlowController#execute},
     *                               and outside of {@link FlowController#onCreate},
     *                               {@link FlowController#beforeAction}, {@link FlowController#afterAction}.
     */
    public MutableURI getActionURI(String actionName)
            throws URISyntaxException {
        return PageFlowUtils.getActionURI(actionName);
    }

    /**
     * Create a fully-rewritten URI given an action and parameters.
     *
     * @param actionName the action name to convert into a fully-rewritten URI; may be qualified with a path from the
     *                   webapp root, in which case the parent directory from the current request is <i>not</i> used.
     * @param parameters the additional parameters to include in the URI query.
     * @param asValidXml flag indicating that the query of the uri should be written
     *                   using the &quot;&amp;amp;&quot; entity, rather than the character, '&amp;'
     * @return a fully-rewritten URI for the given action.
     * @throws URISyntaxException    if there is a problem converting the action url (derived
     *                               from processing the given action name) into a URI.
     * @throws IllegalStateException if this method is invoked outside of action method
     *                               execution (i.e., outside of the call to {@link FlowController#execute},
     *                               and outside of {@link FlowController#onCreate},
     *                               {@link FlowController#beforeAction}, {@link FlowController#afterAction}.
     */
    public String getRewrittenActionURI(String actionName, Map parameters, boolean asValidXml)
            throws URISyntaxException {
        return PageFlowUtils.getRewrittenActionURI(actionName, parameters, null, asValidXml);
    }
}
