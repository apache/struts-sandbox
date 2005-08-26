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
package org.apache.ti.pageflow.interceptor.action.internal;

import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.interceptor.Interceptor;
import org.apache.ti.pageflow.interceptor.InterceptorChain;
import org.apache.ti.pageflow.interceptor.InterceptorContext;
import org.apache.ti.pageflow.interceptor.InterceptorException;
import org.apache.ti.pageflow.interceptor.action.ActionInterceptor;
import org.apache.ti.pageflow.interceptor.action.ActionInterceptorContext;

import java.util.List;

public class ActionInterceptors {

    private static final class WrapActionInterceptorChain
            extends InterceptorChain {

        private ActionExecutor _actionExecutor;

        public WrapActionInterceptorChain(InterceptorContext context, List/*< Interceptor >*/ interceptors,
                                          ActionExecutor actionExecutor) {
            super(context, interceptors);
            _actionExecutor = actionExecutor;
        }

        protected Object invoke(Interceptor interceptor)
                throws InterceptorException {
            return ((ActionInterceptor) interceptor).wrapAction((ActionInterceptorContext) getContext(), this);
        }

        public Object continueChain()
                throws InterceptorException {
            if (!isEmpty()) {
                return invoke(removeFirst());
            } else {
                try {
                    return _actionExecutor.execute();
                } catch (PageFlowException e) {
                    throw new InterceptorException(e);
                }
            }
        }
    }

    public static Forward wrapAction(ActionInterceptorContext context, List/*< Interceptor >*/ interceptors,
                                     ActionExecutor actionExecutor)
            throws InterceptorException, PageFlowException {
        try {
            if (interceptors != null) {
                WrapActionInterceptorChain chain = new WrapActionInterceptorChain(context, interceptors, actionExecutor);
                return (Forward) chain.continueChain();
            } else {
                return actionExecutor.execute();
            }
        } catch (InterceptorException e) {
            Throwable cause = e.getCause();

            if (cause instanceof PageFlowException) {
                throw (PageFlowException) cause;
            }

            throw e;
        }
    }

    public interface ActionExecutor {

        public Forward execute() throws PageFlowException, InterceptorException;
    }
}
