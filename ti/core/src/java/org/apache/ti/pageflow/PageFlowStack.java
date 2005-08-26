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

import org.apache.ti.pageflow.handler.Handlers;
import org.apache.ti.pageflow.handler.StorageHandler;
import org.apache.ti.pageflow.interceptor.action.ActionInterceptor;
import org.apache.ti.pageflow.interceptor.action.ActionInterceptorContext;
import org.apache.ti.pageflow.interceptor.action.InterceptorForward;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import java.util.Stack;


/**
 * Stack for keeping track of a series of nested page flows.  When a nested page flow is entered,
 * the previous page flow is pushed onto this stack, which is kept in the user session.
 */
public class PageFlowStack
        implements HttpSessionBindingListener, Serializable {

    private static final Logger _log = Logger.getInstance(PageFlowStack.class);
    private static final String JPF_STACK_ATTR = InternalConstants.ATTR_PREFIX + "nestingStack";

    private Stack _stack = new Stack();


    /**
     * Wrapper that contains a pushed page flow and information related to it.
     */
    public static class PushedPageFlow implements Serializable {

        private PageFlowController _pageFlow;
        private ActionInterceptor _interceptor;
        private InterceptorForward _interceptedForward;
        private String _interceptedActionName;

        public PushedPageFlow(PageFlowController pageFlow) {
            _pageFlow = pageFlow;
        }

        public PushedPageFlow(PageFlowController pageFlow, ActionInterceptor interceptor,
                              InterceptorForward interceptedFwd, String interceptedActionName) {
            this(pageFlow);
            _interceptor = interceptor;
            _interceptedForward = interceptedFwd;
            _interceptedActionName = interceptedActionName;
        }

        public PageFlowController getPageFlow() {
            return _pageFlow;
        }

        public ActionInterceptor getInterceptor() {
            return _interceptor;
        }

        public InterceptorForward getInterceptedForward() {
            return _interceptedForward;
        }

        public String getInterceptedActionName() {
            return _interceptedActionName;
        }
    }

    /**
     * Get the stack of nested page flows for the current user session.  Create and store an empty
     * stack if none exists.
     *
     * @return the stack of nested page flows {@link PushedPageFlow}s) for the current user session.
     */
    public static PageFlowStack get() {
        return get(true);
    }

    /**
     * Get the stack of nested page flows for the current user session.  Create and store an empty
     * stack if none exists.
     *
     * @return a {@link PageFlowStack} of nested page flows ({@link PageFlowController}s) for the current user session.
     */
    public static PageFlowStack get(boolean createIfNotExist) {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = InternalUtils.getScopedAttrName(JPF_STACK_ATTR);
        PageFlowStack jpfStack = (PageFlowStack) sh.getAttribute(attrName);

        if (jpfStack == null && createIfNotExist) {
            jpfStack = new PageFlowStack();
            jpfStack.save();
        }

        return jpfStack;
    }

    /**
     * Destroy the stack of {@link PageFlowController}s that have invoked nested page flows.
     */
    public void destroy() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = InternalUtils.getScopedAttrName(JPF_STACK_ATTR);

        sh.removeAttribute(attrName);
    }

    /**
     * Pop page flows from the nesting stack until one of the given type is found.
     *
     * @return the last popped page flow if one of the given type was found, or <code>null</code>
     *         if none was found.
     */
    PageFlowController popUntil(Class stopAt) {
        while (!isEmpty()) {
            PageFlowController popped = pop().getPageFlow();

            if (popped.getClass().equals(stopAt)) {
                //
                // If we've popped everything from the stack, remove the stack attribute from the session.
                //
                if (isEmpty()) destroy();
                return popped;
            } else {
                //
                // We're discarding the popped page flow.  Invoke its destroy() callback, unless it's longLived.
                //
                if (!popped.isLongLivedFlow()) popped.destroy();
            }
        }

        destroy();   // we're empty -- remove the attribute from the session.
        return null;
    }

    /**
     * Pop page flows from the nesting stack until the given index.
     *
     * @return the last popped page flow of the given type.
     */
    PageFlowController popUntil(int index) {
        for (int i = _stack.size() - 1; i > index; --i) {
            pop();
        }

        return pop().getPageFlow();
    }

    int lastIndexOf(Class target) {
        for (int i = _stack.size() - 1; i >= 0; --i) {
            if (((PushedPageFlow) _stack.elementAt(i)).getPageFlow().getClass().equals(target)) {
                return i;
            }
        }

        return -1;
    }

    void ensureFailover() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = InternalUtils.getScopedAttrName(JPF_STACK_ATTR);

        sh.ensureFailover(attrName, this);
    }

    void save() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = InternalUtils.getScopedAttrName(JPF_STACK_ATTR);

        sh.setAttribute(attrName, this);
    }

    private PageFlowStack() {
    }

    /**
     * Push a page flow onto the stack of nested page flows in the session.
     *
     * @param pageFlow the page flow to push.
     */
    public void push(PageFlowController pageFlow) {
        ActionInterceptorContext interceptorContext = ActionInterceptorContext.getActiveContext(true);

        if (interceptorContext != null) {
            ActionInterceptor interceptor = interceptorContext.getOverridingActionInterceptor();
            InterceptorForward originalForward = interceptorContext.getOriginalForward();
            String actionName = interceptorContext.getActionName();
            _stack.push(new PushedPageFlow(pageFlow, interceptor, originalForward, actionName));
        } else {
            _stack.push(new PushedPageFlow(pageFlow));
        }
        
        // Tell the page flow that it is on the nesting stack.
        pageFlow.setIsOnNestingStack(true);
        
        // To ensure that this attribute is replicated for session failover.
        ensureFailover();
    }

    /**
     * Pop the most recently-pushed page flow from the stack of nested page flows in the session.
     *
     * @return a {@link PushedPageFlow} that represents the popped page flow.
     */
    public PushedPageFlow pop() {
        PushedPageFlow ppf = (PushedPageFlow) _stack.pop();
        PageFlowController pfc = ppf.getPageFlow();
        pfc.setIsOnNestingStack(false);
        
        // Reinitialize the page flow, in case it's lost its transient state.
        pfc.reinitialize();
        ensureFailover();   // to ensure that this attribute is replicated for session failover

        return ppf;
    }

    /**
     * Get the most recently-pushed page flow from the stack of nested page flows in the session.
     *
     * @return a {@link PushedPageFlow} that represents the page flow at the top of the stack.
     */
    public PushedPageFlow peek() {
        return (PushedPageFlow) _stack.peek();
    }

    /**
     * Tell whether the stack of nested page flows is empty.
     *
     * @return <code>true</code> if there are no nested page flows on the stack.
     */
    public boolean isEmpty() {
        return _stack.isEmpty();
    }

    /**
     * Get the size of the stack of nested page flows.
     *
     * @return the number of page flows that are currently (hidden away) on the stack.
     */
    public int size() {
        return _stack.size();
    }

    /**
     * @exclude
     */
    public void valueBound(HttpSessionBindingEvent event) {
    }

    /**
     * @exclude
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        if (_log.isDebugEnabled()) {
            _log.debug("The page flow stack is being unbound from the session.");
        }

        while (!isEmpty()) {
            PageFlowController jpf = pop().getPageFlow();
            
            // Note that this page flow may have been serialized/deserialized, which will cause its transient info
            // to be lost.  Rehydrate it.
            HttpSession session = event.getSession();
            if (session != null) jpf.reinitialize();

            if (!jpf.isLongLivedFlow()) jpf.destroy();
        }
    }

    /**
     * Get a stack of PageFlowControllers, not of PushedPageFlows.
     */
    Stack getLegacyStack() {
        Stack ret = new Stack();

        for (int i = 0; i < _stack.size(); ++i) {
            ret.push(((PushedPageFlow) _stack.get(i)).getPageFlow());
        }

        return ret;
    }
}
