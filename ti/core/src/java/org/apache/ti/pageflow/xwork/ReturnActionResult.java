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

import org.apache.ti.pageflow.EmptyNestingStackException;
import org.apache.ti.pageflow.FlowController;
import org.apache.ti.pageflow.FlowControllerException;
import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.PageFlowConstants;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowStack;
import org.apache.ti.pageflow.PageFlowUtils;
import org.apache.ti.pageflow.interceptor.action.ActionInterceptor;
import org.apache.ti.pageflow.interceptor.action.AfterNestedInterceptContext;
import org.apache.ti.pageflow.interceptor.action.InterceptorForward;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.script.common.ImplicitObjectUtil;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;

public class ReturnActionResult extends PageFlowResult {

    private static final Logger _log = Logger.getInstance(ReturnActionResult.class);

    private String _returnAction;

    protected boolean shouldSavePreviousPageInfo() {
        return true;
    }

    public boolean isPath() {
        return false;
    }

    public String getReturnAction() {
        return _returnAction;
    }

    public void setReturnAction(String returnAction) {
        _returnAction = returnAction;
    }

    public boolean preprocess(Forward fwd, PageFlowActionContext actionContext) {
        PageFlowStack pfStack = PageFlowStack.get();

        assert _returnAction != null : "param returnAction was not set on ReturnActionResult";

        if (pfStack.isEmpty()) {
            PageFlowController curJpf = PageFlowUtils.getCurrentPageFlow();

            if (_log.isWarnEnabled()) {
                InternalStringBuilder msg = new InternalStringBuilder("Tried to pop from empty PageFlow stack.");
                msg.append("  Current page flow is ");
                msg.append(curJpf != null ? curJpf.getClass().getName() : null);
                _log.warn(msg.append('.').toString());
            }

            FlowControllerException ex = new EmptyNestingStackException(curJpf);
            ex.setActionName(_returnAction);
            InternalUtils.throwPageFlowException(ex);
        }
                
        // Only nested PageFlowControllers can have return actions.
        FlowController flowController = actionContext.getFlowController();
        assert flowController instanceof PageFlowController
                : flowController.getClass().getName() + " is not a " + PageFlowController.class.getName();
        Forward exceptionFwd = ((PageFlowController) flowController).exitNesting();
        if (exceptionFwd != null) return true;

        PageFlowStack.PushedPageFlow pushedPageFlowWrapper = pfStack.pop();
        PageFlowController poppedPageFlow = pushedPageFlowWrapper.getPageFlow();

        if (_log.isDebugEnabled()) {
            _log.debug("Popped page flow " + poppedPageFlow + " from the nesting stack");
        }

        InternalUtils.setCurrentPageFlow(poppedPageFlow);

                
        //
        // If an ActionInterceptor forwarded to the nested page flow, give it a chance to change the URI as the nested
        // flow is returning.  If it doesn't, we'll go to the originally-intended forward.
        //
        ActionInterceptor interceptor = pushedPageFlowWrapper.getInterceptor();

        if (interceptor != null) {
            return handleInterceptorReturn(actionContext, poppedPageFlow, pushedPageFlowWrapper, _returnAction, interceptor);
        }

        //
        // Raise the returned action on the popped pageflow.
        //                    
        if (_log.isDebugEnabled()) {
            _log.debug("action on popped page flow is " + _returnAction);
        }

        InternalStringBuilder returnActionPath = new InternalStringBuilder();
        returnActionPath.append(poppedPageFlow.getNamespace());
        returnActionPath.append('/').append(_returnAction).append(PageFlowConstants.ACTION_EXTENSION);

        //
        // Store the returned form in the request.
        //
        Object retForm = fwd.getFirstOutputForm();
        if (retForm != null) {
            InternalUtils.setForwardedFormBean(retForm);
            ImplicitObjectUtil.loadOutputFormBean(retForm);
        }
        
        //
        // forward to the return-action on the nesting page flow.
        //
        setLocation(returnActionPath.toString());
        return false;
    }

    private boolean handleInterceptorReturn(PageFlowActionContext actionContext, PageFlowController poppedPageFlow,
                                            PageFlowStack.PushedPageFlow pushedPageFlowWrapper,
                                            String returnAction, ActionInterceptor interceptor) {
        actionContext.setReturningFromActionIntercept(true);

        try {
            AfterNestedInterceptContext interceptorContext =
                    new AfterNestedInterceptContext(poppedPageFlow,
                            pushedPageFlowWrapper.getInterceptedForward(),
                            pushedPageFlowWrapper.getInterceptedActionName(),
                            returnAction);

            interceptor.afterNestedIntercept(interceptorContext);

            if (interceptorContext.hasInterceptorForward()) {
                InterceptorForward fwd = interceptorContext.getInterceptorForward();

                if (_log.isDebugEnabled()) {
                    InternalStringBuilder message = new InternalStringBuilder();
                    message.append("Interceptor ");
                    message.append(interceptor.getClass().getName());
                    message.append(" after nested page flow: ");

                    if (fwd != null) {
                        message.append("forwarding to ");
                        message.append(fwd.getPath());
                    } else {
                        message.append("returned InterceptorForward is null.");
                    }

                    _log.debug(message.toString());
                }

                if (fwd != null) {
                    fwd.rehydrateRequest();
                    setLocation(fwd.getPath());
                    return false;
                }

                return true;   // null forward -- cancel processing
            }
        } catch (Throwable e) {
            //
            // Yes, we *do* mean to catch Throwable here.  It will get re-thrown if the page flow does not handle it.
            //
            _log.error("Exception in " + interceptor.getClass().getName() + ".afterNestedIntercept", e);

            try {
                poppedPageFlow.handleException(e);
                return true;    // handled exception -- cancel processing
            } catch (Exception anotherException) {
                _log.error("Exception thrown while handling exception.", anotherException);
            }
        }
        
        //
        // The interceptor declined to forward us anywhere -- just go to the originally-intended forward.
        //
        InterceptorForward fwd = pushedPageFlowWrapper.getInterceptedForward();
        fwd.rehydrateRequest();
        setLocation(fwd.getPath());
        return false;
    }
}
