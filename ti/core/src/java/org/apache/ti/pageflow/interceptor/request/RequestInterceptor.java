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
package org.apache.ti.pageflow.interceptor.request;

import org.apache.ti.pageflow.interceptor.AbstractInterceptor;
import org.apache.ti.pageflow.interceptor.InterceptorChain;
import org.apache.ti.pageflow.interceptor.InterceptorContext;
import org.apache.ti.pageflow.interceptor.InterceptorException;


/**
 * A request interceptor, which can run before and/or after a request.  Request interceptors are configured in the
 * <code>&lt;request-interceptors&gt;</code> element of WEB-INF/beehive-netui-config.xml.
 */
public abstract class RequestInterceptor
        extends AbstractInterceptor {

    /**
     * Callback invoked before the request is processed.  During this method, {@link #cancelRequest} may be called to
     * cancel further request processing.  {@link InterceptorChain#continueChain} is called to invoke the rest of the
     * interceptor chain, anywhere within this method (e.g., at the end, or within a try/finally).
     */
    public abstract void preRequest(RequestInterceptorContext context, InterceptorChain chain)
            throws InterceptorException;

    /**
     * Callback invoked before the request is processed.  {@link #preRequest} may be used instead.
     */
    public void preInvoke(InterceptorContext context, InterceptorChain chain) throws InterceptorException {
        preRequest((RequestInterceptorContext) context, chain);
    }

    /**
     * Callback invoked after the request is processed.  {@link InterceptorChain#continueChain} should be called to
     * invoke the rest of the interceptor chain, anywhere within this method (e.g., at the end, or within a try/finally).
     */
    public abstract void postRequest(RequestInterceptorContext context, InterceptorChain chain)
            throws InterceptorException;

    /**
     * Callback invoked after the request is processed.  {@link #postRequest} may be used instead.
     */
    public void postInvoke(InterceptorContext context, InterceptorChain chain) throws InterceptorException {
        postRequest((RequestInterceptorContext) context, chain);
    }

    /**
     * Cancel the request.  After this is called, no further processing will happen in the request.
     *
     * @param context the current RequestInterceptorContext.
     */
    protected void cancelRequest(RequestInterceptorContext context) {
        context.cancelRequest(this);
    }
}
