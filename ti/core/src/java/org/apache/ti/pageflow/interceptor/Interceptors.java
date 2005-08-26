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
package org.apache.ti.pageflow.interceptor;


import java.util.List;

public class Interceptors {

    public static void doPreIntercept(InterceptorContext context, List/*< Interceptor >*/ interceptors)
            throws InterceptorException {
        if (interceptors != null) {
            PreInvokeInterceptorChain chain = new PreInvokeInterceptorChain(context, interceptors);
            chain.continueChain();
        }
    }

    public static void doPostIntercept(InterceptorContext context, List/*< Interceptor >*/ interceptors)
            throws InterceptorException {
        if (interceptors != null) {
            PostInvokeInterceptorChain chain = new PostInvokeInterceptorChain(context, interceptors);
            chain.continueChain();
        }
    }

    private static final class PreInvokeInterceptorChain
            extends InterceptorChain {

        public PreInvokeInterceptorChain(InterceptorContext context, List/*< Interceptor >*/ interceptors) {
            super(context, interceptors);
        }

        protected Object invoke(Interceptor interceptor)
                throws InterceptorException {
            interceptor.preInvoke(getContext(), this);
            return null;
        }
    }

    private static final class PostInvokeInterceptorChain
            extends InterceptorChain {

        public PostInvokeInterceptorChain(InterceptorContext context, List/*< Interceptor >*/ interceptors) {
            super(context, interceptors);
        }

        protected Object invoke(Interceptor interceptor)
                throws InterceptorException {
            interceptor.postInvoke(getContext(), this);
            return null;
        }
    }
}
