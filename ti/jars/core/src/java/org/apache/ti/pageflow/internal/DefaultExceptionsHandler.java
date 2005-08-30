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

import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import org.apache.ti.Globals;
import org.apache.ti.pageflow.FlowController;
import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.ModuleConfig;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.PageFlowManagedObjectException;
import org.apache.ti.pageflow.SharedFlowController;
import org.apache.ti.pageflow.handler.ExceptionsHandler;
import org.apache.ti.pageflow.interceptor.InterceptorException;
import org.apache.ti.pageflow.xwork.PageFlowAction;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collection;
import java.util.Iterator;


public class DefaultExceptionsHandler
        extends DefaultHandler
        implements ExceptionsHandler {

    private static final Logger _log = Logger.getInstance(DefaultExceptionsHandler.class);


    public DefaultExceptionsHandler() {
    }

    public void handleException(Throwable ex)
            throws PageFlowException {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        FlowController flowController = actionContext.getFlowController();

        if (_log.isInfoEnabled()) {
            _log.info("Handling Throwable " + ex.getClass().getName());
        }
        
        //
        // If we're already in the process of handling an exception, bail out.
        //
        Throwable alreadyBeingHandled = actionContext.getExceptionBeingHandled();

        if (alreadyBeingHandled != null) {
            if (_log.isWarnEnabled()) {
                _log.warn("Already in the process of handling " + alreadyBeingHandled.getClass().getName()
                        + "; bailing out of handling for " + ex.getClass().getName());
            }

            throw new UnhandledException(ex);
        }

        actionContext.setExceptionBeingHandled(ex);
        
        
        // Callback to the event reporter.
        AdapterManager.getContainerAdapter().getEventReporter().exceptionRaised(ex, flowController);
        long startTime = System.currentTimeMillis();
    
        //
        // Look up the ExceptionConfig that's associated with this Throwable.
        //
        Class exClass = ex.getClass();
        PageFlowAction action = actionContext.getAction();

        String exceptionHandlerName;
        if (action != null) {
            exceptionHandlerName = action.findExceptionHandler(exClass);
        } else {
            // If the mapping was null (i.e., the exception happened before we got the action mapping), look for the
            // exception only in the module config.
            exceptionHandlerName = flowController.getModuleConfig().findExceptionHandler(exClass);
        }
        
        //
        // If there was no applicable exception handler in the current ModuleConfig, look in Global.app's module.
        //
        if (exceptionHandlerName == null) {
            FlowController fallbackFC = getFallbackFlowController(flowController, exClass);

            if (fallbackFC != null) {
                flowController = fallbackFC;
                exceptionHandlerName = flowController.getModuleConfig().findExceptionHandler(exClass);

                if (exceptionHandlerName != null) {
                    // This is the module that will be handling the exception.  Ensure that its message resources are
                    // initialized.
                    InternalUtils.selectModule(flowController.getModuleConfig());
                    actionContext.setCurrentFlowController(flowController);
                }
            }
        }

        if (exceptionHandlerName != null) {
            if (_log.isDebugEnabled()) {
                _log.debug("Found exception-config for exception " + exClass.getName()
                        + ": handler=" + exceptionHandlerName);
            }

            //
            // First, see if it should be handled by invoking a handler method.
            //
            invokeExceptionHandler(exceptionHandlerName);

            // Callback to the event reporter.
            long timeTaken = System.currentTimeMillis() - startTime;
            Forward fwd = actionContext.getForward();
            AdapterManager.getContainerAdapter().getEventReporter().exceptionHandled(ex, flowController, fwd, timeTaken);
            return;
        }

        if (_log.isErrorEnabled()) {
            InternalStringBuilder msg = new InternalStringBuilder("Throwable ").append(exClass.getName());
            _log.error(msg.append(" unhandled by the current page flow (and any shared flow)").toString(), ex);
        }

        if (!getRegisteredExceptionsHandler().eatUnhandledException(ex)) {
            // Throwing this ServletException derivative will prevent any outer try/catch blocks from re-processing
            // the exception.
            throw new UnhandledException(ex);
        }
    }

    public Throwable unwrapException(Throwable ex) {
        if (ex instanceof InterceptorException) {
            Throwable cause = ex.getCause();
            if (cause != null) return unwrapException(cause);
        }
        
        //
        // If the exception was thrown in a method we called through reflection, it will be an
        // InvocationTargetException.  Unwrap it.  Do the same for the UndeclaredThrowable exceptions thrown when
        // invoking methods through dynamic proxies.
        //
        if (ex instanceof InvocationTargetException) {
            return unwrapException(((InvocationTargetException) ex).getTargetException());
        }

        if (ex instanceof UndeclaredThrowableException) {
            return unwrapException(((UndeclaredThrowableException) ex).getUndeclaredThrowable());
        }

        if (ex instanceof ServletException) {
            ServletException servletException = (ServletException) ex;
            Throwable rootCause = servletException.getRootCause();
            if (rootCause != null) return unwrapException(rootCause);
        }

        return ex;
    }

    public void exposeException(Throwable ex) {
        //
        // Put the exception in a place where Struts/NetUI tags will find it.
        //
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        actionContext.getRequestScope().put(Globals.EXCEPTION_KEY, ex);
    }

    protected static FlowController getFallbackFlowController(FlowController originalFlowController, Class exClass) {
        if (originalFlowController instanceof PageFlowController) {
            Collection/*< SharedFlowController >*/ sharedFlows =
                    ((PageFlowController) originalFlowController).getSharedFlows().values();

            for (Iterator ii = sharedFlows.iterator(); ii.hasNext();) {
                SharedFlowController sf = (SharedFlowController) ii.next();
                if (checkForExceptionConfig(sf, exClass)) return sf;
            }
        }

        return null;
    }

    private static boolean checkForExceptionConfig(SharedFlowController sf, Class exClass) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        ModuleConfig mc = sf.getModuleConfig();
        String handlerName = mc.findExceptionHandler(exClass);

        if (handlerName != null) {
            if (_log.isDebugEnabled()) {
                _log.debug("Found exception-config for " + exClass.getName() + " in SharedFlowController "
                        + sf.getDisplayName());
            }


            actionContext.setModuleConfig(mc);
            return true;
        }

        return false;
    }

    protected static void invokeExceptionHandler(String exceptionHandlerName)
            throws PageFlowException {
        PageFlowActionContext actionContext = PageFlowActionContext.get();

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(actionContext.getNamespace(), exceptionHandlerName, actionContext.getContextMap());
            proxy.execute();
        } catch (Exception e) {
            throw new PageFlowException(e);
        }
    }


    public boolean eatUnhandledException(Throwable ex) {
        _log.error("Unhandled Page Flow Exception", ex);

        try {
            //
            // PageFlowExceptions know what to do in the unhandled case.
            //
            boolean prodMode = AdapterManager.getContainerAdapter().isInProductionMode();

            if (!prodMode && ex instanceof PageFlowManagedObjectException) {
                ((PageFlowManagedObjectException) ex).sendError();
                return true;
            }
        } catch (IOException ioEx) {
            _log.error(ioEx.getMessage(), ioEx);
        }

        return false;
    }

    public ExceptionsHandler getRegisteredExceptionsHandler() {
        return (ExceptionsHandler) super.getRegisteredHandler();
    }
}
