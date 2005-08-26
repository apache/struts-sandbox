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
package org.apache.ti.pageflow.interceptor.action;

import org.apache.ti.pageflow.interceptor.AbstractInterceptor;
import org.apache.ti.pageflow.interceptor.InterceptorChain;
import org.apache.ti.pageflow.interceptor.InterceptorContext;
import org.apache.ti.pageflow.interceptor.InterceptorException;


/**
 * Base class for Page Flow action interceptors.  These are configured in /WEB-INF/beehive-netui-config.xml like this:
 * <pre>
 *    &lt;netui-config xmlns="http://beehive.apache.org/netui/2004/server/config"&gt;
 *        ...
 * <p/>
 *        &lt;pageflow-action-interceptors&gt;
 *            &lt;global&gt;
 *                &lt;before-action&gt;
 *                    &lt;action-interceptor&gt;
 *                        &lt;interceptor-class&gt;test.BeforeActionInterceptor1&lt;/interceptor-class&gt;
 *                    &lt;/action-interceptor&gt;
 *                    &lt;action-interceptor&gt;
 *                        &lt;interceptor-class&gt;test.BeforeActionInterceptor2&lt;/interceptor-class&gt;
 *                    &lt;/action-interceptor&gt;
 *                    ...
 *                &lt;/before-action&gt;
 *                &lt;after-action&gt;
 *                    &lt;action-interceptor&gt;
 *                        &lt;interceptor-class&gt;test.AfterActionInterceptor1&lt;/interceptor-class&gt;
 *                    &lt;/action-interceptor&gt;
 *                    &lt;action-interceptor&gt;
 *                        &lt;interceptor-class&gt;test.AfterActionInterceptor2&lt;/interceptor-class&gt;
 *                    &lt;/action-interceptor&gt;
 *                &lt;/after-action&gt;
 *            &lt;/global&gt;
 *        &lt;/pageflow-action-interceptors&gt;
 * <p/>
 *        ...
 *    &lt;/netui-config&gt;
 * <p/>
 * </pre>
 * <p/>
 * Note that a registered ActionInterceptor is created and cached as a single instance per application.
 * It should not hold any per-request or per-session state.
 */
public abstract class ActionInterceptor
        extends AbstractInterceptor {

    /**
     * Callback invoked before the action is processed.  During this method, {@link #setOverrideForward} may be called
     * to:
     * <ul>
     * <li>change the destination URI and thus prevent the action from running, or,</li>
     * <li>set the destination URI to <code>null</code> (no forwarding) and thus prevent the action from running, or,</li>
     * <li>
     * "inject" an entire nested page flow to run before the action is invoked.  If the override forward URI
     * is a nested page flow, then it will run until it raises one of its return actions.  At that point,
     * {@link #afterNestedIntercept} is called on <i>this interceptor</i>, which can again choose to override
     * the forward or allow the original action to run.
     * </li>
     * </ul>
     * <p/>
     * {@link InterceptorChain#continueChain} is called to invoke the rest of the
     * interceptor chain, anywhere within this method (e.g., at the end, or within a try/finally).
     *
     * @param context the current ActionInterceptorContext.
     * @param chain   the interceptor chain. Calling <code>continueChain</code> on this runs the rest of the interceptors.
     */
    public abstract void preAction(ActionInterceptorContext context, InterceptorChain chain)
            throws InterceptorException;

    /**
     * Callback invoked before the action is processed.  {@link #preAction} may be used instead.
     */
    public void preInvoke(InterceptorContext context, InterceptorChain chain) throws InterceptorException {
        preAction((ActionInterceptorContext) context, chain);
    }

    /**
     * Callback invoked after the action is processed.  During this method, {@link #setOverrideForward} may be called
     * to:
     * <ul>
     * <li>change the destination URI and thus override the one returned from the action, or,</li>
     * <li>set the destination URI to <code>null</code> (no forwarding).</li>
     * </ul>
     * <p/>
     * {@link InterceptorChain#continueChain} is called to invoke the rest of the
     * interceptor chain, anywhere within this method (e.g., at the end, or within a try/finally).
     *
     * @param context the current ActionInterceptorContext.
     * @param chain   the interceptor chain. Calling <code>continueChain</code> on this runs the rest of the interceptors.
     */
    public abstract void postAction(ActionInterceptorContext context, InterceptorChain chain)
            throws InterceptorException;

    /**
     * Callback invoked after the action is processed.  {@link #postAction} may be used instead.
     */
    public void postInvoke(InterceptorContext context, InterceptorChain chain) throws InterceptorException {
        postAction((ActionInterceptorContext) context, chain);
    }

    /**
     * Callback invoked after a nested page flow has been "injected" by {@link #preAction}, and before the original
     * action has run.  During this method, {@link #setOverrideForward} may be called to:
     * <p/>
     * <ul>
     * <li>change the destination URI that was returned by the action, or,</li>
     * <li>set the destination URI to <code>null</code> (no forwarding).</li>
     * </ul>
     * <p/>
     * {@link InterceptorChain#continueChain} is called to invoke the rest of the
     * interceptor chain, anywhere within this method (e.g., at the end, or within a try/finally).
     *
     * @param context an extension of {@link ActionInterceptorContext} which contains the return action from the
     *                injected nested page flow.
     * @throws InterceptorException
     */
    public abstract void afterNestedIntercept(AfterNestedInterceptContext context)
            throws InterceptorException;

    /**
     * Override the forward, either before or after the target action is run.  See {@link #preAction} and
     * {@link #postAction} for information on how this is used.
     *
     * @param forward an InterceptorForward that will override the target action's forward; or <code>null</code> to
     *                cancel navigation.
     * @param context the current ActionInterceptorContext.
     */
    protected void setOverrideForward(InterceptorForward forward, ActionInterceptorContext context) {
        context.setOverrideForward(forward, this);
    }

    /**
     * Optional method that "wraps" the target action invocation.  This is mainly useful for surrounding an action
     * (and the rest of the interceptor chain) with try/catch/finally.  This default implementation simply <i>returns</i>
     * <code>continueChain</code> on the passed-in InterceptorChain, which allows the rest of the interceptors
     * <i>and</i> the action to run.
     *
     * @param context the current ActionInterceptorContext.
     * @param chain   the interceptor chain.  This chain is different from the ones passed to {@link #preAction} and
     *                {@link #postAction} in that the action invocation itself is included in it.
     * @return the forward returned by the action.
     */
    public Object wrapAction(ActionInterceptorContext context, InterceptorChain chain)
            throws InterceptorException {
        return chain.continueChain();
    }
}
