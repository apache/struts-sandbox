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

import com.opensymphony.xwork.ActionContext;

import org.apache.ti.pageflow.interceptor.InterceptorContext;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.InterceptorConfig;
import org.apache.ti.util.config.bean.RequestInterceptorsConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Context passed to methods on {@link RequestInterceptor}.
 */
public class RequestInterceptorContext
        extends InterceptorContext {
    private static final String INTERCEPTORS_LIST_ATTR = InternalConstants.ATTR_PREFIX + "requestInterceptors";

    public RequestInterceptorContext() {
    }

    public void cancelRequest(RequestInterceptor interceptor) {
        setResultOverride(null, interceptor);
    }

    public boolean requestWasCancelled() {
        return hasResultOverride() && (getResultOverride() == null);
    }

    public static void init(Map appScope) {
        RequestInterceptorsConfig requestInterceptors = ConfigUtil.getConfig().getRequestInterceptors();

        if (requestInterceptors != null) {
            InterceptorConfig[] globalRequestInterceptors = requestInterceptors.getGlobalRequestInterceptors();

            if (globalRequestInterceptors != null) {
                ArrayList /*< Interceptor >*/ interceptorsList = new ArrayList /*< Interceptor >*/();
                addInterceptors(globalRequestInterceptors, interceptorsList, RequestInterceptor.class);
                appScope.put(INTERCEPTORS_LIST_ATTR, interceptorsList);
            }
        }
    }

    public List /*< Interceptor >*/ getRequestInterceptors() {
        return (List /*< Interceptor >*/) ActionContext.getContext().getApplication().get(INTERCEPTORS_LIST_ATTR);
    }

    public static void addInterceptor(RequestInterceptor interceptor) {
        Map appScope = ActionContext.getContext().getApplication();

        List /*< Interceptor >*/ interceptorsList = (List /*< Interceptor >*/) appScope.get(INTERCEPTORS_LIST_ATTR);

        if (interceptorsList == null) {
            interceptorsList = new ArrayList /*< Interceptor >*/();
        }

        interceptorsList.add(interceptor);
        appScope.put(INTERCEPTORS_LIST_ATTR, interceptorsList);
    }
}
