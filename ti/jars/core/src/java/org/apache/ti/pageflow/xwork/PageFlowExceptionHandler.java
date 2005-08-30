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
import org.apache.ti.core.ActionMessage;
import org.apache.ti.pageflow.ExpressionMessage;
import org.apache.ti.pageflow.FlowController;
import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.InternalExpressionUtils;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.util.Bundle;
import org.apache.ti.util.MessageResources;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.internal.cache.ClassLevelCache;
import org.apache.ti.util.logging.Logger;

import javax.servlet.jsp.el.ELException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Locale;

public class PageFlowExceptionHandler extends PageFlowAction {

    private static final Logger _log = Logger.getInstance(PageFlowExceptionHandler.class);

    private static final String CACHEID_EXCEPTION_HANDLER_METHODS = InternalConstants.ATTR_PREFIX + "exceptionHandlers";

    private String _methodName;
    private String _defaultMessage;
    private String _messageKey;
    private boolean _readOnly;

    public String execute() throws Exception {
        Forward fwd;
        if (_methodName != null) {
            fwd = invokeExceptionHandlerMethod();
            PageFlowActionContext.get().setForward(fwd);
        } else {
            fwd = new Forward(Action.SUCCESS);
            PageFlowActionContext.get().setForward(fwd);
        }
        return fwd != null ? fwd.getName() : null;
    }

    protected Forward invokeExceptionHandlerMethod()
            throws PageFlowException {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        Throwable ex = actionContext.getExceptionBeingHandled();
        FlowController flowController = actionContext.getFlowController();
        Method method = getExceptionHandlerMethod(getMethodName(), ex);
        Object formBean = actionContext.getFormBean();

        if (method != null) {
            // First see if there's a hard-coded message set.
            String message = getDefaultMessage();
            ActionMessage error = null;

            if (message != null) {
                error = new ExpressionMessage(message, new Object[]{ex.getMessage()});

                try {
                    // The message may be an expression.  Evaluate it.
                    message = InternalExpressionUtils.evaluateMessage(message);
                } catch (ELException e) {
                    _log.error("error while evaluating expression in exception-handler for " + ex.getClass().getName(), e);
                }
            }


            if (message == null) {
                // No hard-coded message.  Get the message based on the message key.
                String messageKey = getMessageKey();

                if (messageKey != null && messageKey.length() > 0) {
                    message = getMessage(messageKey, null, null);
                }
            }
            
            //
            // Expose the exception to the errors tag.
            //
            String msgKey = getMessageKey();
            if (error == null) error = new ActionMessage(msgKey, ex.getMessage());
            ArrayList errors = new ArrayList();
            errors.add(error);
            actionContext.getRequestScope().put(Globals.ERROR_KEY, errors);

            return flowController.invokeExceptionHandler(method, ex, message, formBean, isReadOnly());
        } else {
            //
            // This shouldn't happen except in out-of-date-class situations.  JpfChecker
            // should prevent this at compilation time.
            //
            String err;
            if (formBean != null) {
                err = Bundle.getString("PageFlow_MissingExceptionHandlerWithForm",
                        new Object[]{getMethodName(), formBean.getClass().getName()});
            } else {
                err = Bundle.getString("PageFlow_MissingExceptionHandler", getMethodName());
            }

            InternalUtils.sendError("PageFlow_Custom_Error", null, new Object[]{flowController.getDisplayName(), err});
            return null;
        }
    }

    /**
     * Get an Exception handler method.
     *
     * @param methodName the name of the method to get.
     * @param ex         the Exception that is to be handled.
     * @return the Method with the given name that handles the given Exception, or <code>null</code>
     *         if none matches.
     */
    protected Method getExceptionHandlerMethod(String methodName, Throwable ex) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        FlowController flowController = actionContext.getFlowController();
        String cacheKey = methodName + '/' + ex.getClass().getName();
        ClassLevelCache cache = ClassLevelCache.getCache(flowController.getClass());
        Method method = (Method) cache.get(CACHEID_EXCEPTION_HANDLER_METHODS, cacheKey);

        if (method != null) {
            return method;
        }

        Class flowControllerClass = flowController.getClass();
        for (Class exClass = ex.getClass(); exClass != null; exClass = exClass.getSuperclass()) {
            Class[] args = new Class[]{exClass, String.class};
            Method foundMethod = InternalUtils.lookupMethod(flowControllerClass, methodName, args);

            if (foundMethod != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("Found exception handler for " + exClass.getName());
                }

                if (!Modifier.isPublic(foundMethod.getModifiers())) foundMethod.setAccessible(true);
                cache.put(CACHEID_EXCEPTION_HANDLER_METHODS, cacheKey, foundMethod);
                return foundMethod;
            } else {
                if (_log.isErrorEnabled()) {
                    InternalStringBuilder msg = new InternalStringBuilder("Could not find exception handler method ");
                    msg.append(methodName).append(" for ").append(exClass.getName()).append('.');
                    _log.error(msg.toString());
                }
            }
        }

        return null;
    }

    protected String getMessage(String messageKey, String bundle, Object[] args) {
        if (bundle == null) bundle = Globals.MESSAGES_KEY;

        MessageResources resources = InternalUtils.getMessageResources(bundle);

        if (resources == null) {
            _log.error("Could not find message-resources for bundle " + bundle);
            return null;
        }

        Locale userLocale = PageFlowActionContext.get().getLocale();

        if (args == null) {
            return resources.getMessage(userLocale, messageKey);
        } else {
            return resources.getMessage(userLocale, messageKey, args);
        }
    }


    public String getDefaultMessage() {
        return _defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        _defaultMessage = defaultMessage;
    }

    public String getMessageKey() {
        return _messageKey;
    }

    public void setMessageKey(String messageKey) {
        _messageKey = messageKey;
    }

    public boolean isReadOnly() {
        return _readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        _readOnly = readOnly;
    }

    public String getMethodName() {
        return _methodName;
    }

    public void setMethodName(String methodName) {
        _methodName = methodName;
    }
}
