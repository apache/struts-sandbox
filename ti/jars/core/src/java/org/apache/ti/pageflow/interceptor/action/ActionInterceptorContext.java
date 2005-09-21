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

import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.interceptor.action.internal.OriginalForward;
import org.apache.ti.pageflow.interceptor.request.RequestInterceptorContext;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.config.bean.GlobalPageFlowActionInterceptorConfig;
import org.apache.ti.util.config.bean.PageFlowActionInterceptorsConfig;
import org.apache.ti.util.config.bean.PerActionInterceptorConfig;
import org.apache.ti.util.config.bean.PerPageFlowActionInterceptorConfig;
import org.apache.ti.util.internal.concurrent.InternalConcurrentHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context passed to {@link ActionInterceptor} methods.
 */
public class ActionInterceptorContext
        extends RequestInterceptorContext {
    private static final String ACTIVE_INTERCEPTOR_CONTEXT_ATTR = InternalConstants.ATTR_PREFIX + "interceptorContext";
    private static final String CACHE_ATTR = InternalConstants.ATTR_PREFIX + "actionInterceptorConfig";
    private PageFlowController _pageFlow;
    private InterceptorForward _originalForward;
    private String _actionName;

    public ActionInterceptorContext(PageFlowController controller, InterceptorForward originalForward, String actionName) {
        super();
        _pageFlow = controller;
        _originalForward = originalForward;
        _actionName = actionName;
    }

    /**
     * Get the page flow on which the action is being raised.
     */
    public PageFlowController getPageFlow() {
        return _pageFlow;
    }

    /**
     * Get a wrapper for the original URI from the action that was intercepted.  This value will be <code>null</code>
     * if the interceptor was run before the action, or if the action itself returned <code>null</code>.
     */
    public InterceptorForward getOriginalForward() {
        return _originalForward;
    }

    /**
     * Get the name of the action being raised.
     */
    public String getActionName() {
        return _actionName;
    }

    /**
     * Set an {@link InterceptorForward} that changes the destination URI of the intercepted action.  If the
     * InterceptorForward points to a nested page flow, then {@link ActionInterceptor#afterNestedIntercept} will be
     * called before the nested page flow returns to the original page flow.
     */
    public void setOverrideForward(InterceptorForward fwd, ActionInterceptor interceptor) {
        setResultOverride(fwd, interceptor);

        //
        // If there was no original forward (i.e., this is happening before the action was invoked), create a
        // pseudo-forward out of the original request.
        //
        if (_originalForward == null) {
            _originalForward = new OriginalForward();
        }

        //
        // Store this context in the request.
        //
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        actionContext.getRequestScope().put(ACTIVE_INTERCEPTOR_CONTEXT_ATTR, this);
    }

    public ActionInterceptor getOverridingActionInterceptor() {
        return (ActionInterceptor) super.getOverridingInterceptor();
    }

    public InterceptorForward getInterceptorForward() {
        return (InterceptorForward) getResultOverride();
    }

    public boolean hasInterceptorForward() {
        return hasResultOverride();
    }

    public static ActionInterceptorContext getActiveContext(boolean consume) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        Map requestScope = actionContext.getRequestScope();
        ActionInterceptorContext context = (ActionInterceptorContext) requestScope.get(ACTIVE_INTERCEPTOR_CONTEXT_ATTR);

        if (consume) {
            requestScope.remove(ACTIVE_INTERCEPTOR_CONTEXT_ATTR);
        }

        return context;
    }

    public List /*< Interceptor >*/ getActionInterceptors() {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        InternalConcurrentHashMap /*< String, HashMap< String, ArrayList< Interceptor > > >*/ cache = (InternalConcurrentHashMap) actionContext.getApplication()
                                                                                                                                               .get(CACHE_ATTR);

        if (cache == null) {
            //
            // Don't have to synchronize here.  If by some chance two initial requests come in at the same time,
            // one of the caches will get overwritten in the ServletContext, but it will just get recreated the
            // next time.
            //
            cache = new InternalConcurrentHashMap /*< String, HashMap< String, ArrayList< Interceptor > > >*/();
            actionContext.getApplication().put(CACHE_ATTR, cache);
        }

        String namespace = getPageFlow().getNamespace();
        String actionName = getActionName();
        HashMap /*< String, ArrayList< Interceptor > >*/ cacheByPageFlow = (HashMap) cache.get(namespace);

        if (cacheByPageFlow != null) {
            List /*< Interceptor >*/ interceptors = (List) cacheByPageFlow.get(actionName);

            if (interceptors != null) {
                return interceptors;
            }
        }

        //
        // We didn't find it in the cache -- build it.
        //
        if (cacheByPageFlow == null) {
            cacheByPageFlow = new HashMap /*< String, ArrayList< Interceptor > >*/();
        }

        PageFlowActionInterceptorsConfig config = ConfigUtil.getConfig().getPageFlowActionInterceptors();
        ArrayList /*< Interceptor >*/ interceptorsList = new ArrayList /*< Interceptor >*/();

        if (config == null) {
            cacheByPageFlow.put(actionName, interceptorsList);
            cache.put(namespace, cacheByPageFlow);

            return interceptorsList;
        }

        //
        // Global interceptors.
        //
        GlobalPageFlowActionInterceptorConfig globalInterceptors = config.getGlobalPageFlowActionInterceptors();

        if (globalInterceptors != null) {
            addInterceptors(globalInterceptors.getActionInterceptors(), interceptorsList, ActionInterceptor.class);
            addSimpleInterceptors(globalInterceptors.getSimpleActionInterceptors(), interceptorsList);
        }

        //
        // Per-pageflow and per-action interceptors.
        //
        String pageFlowURI = getPageFlow().getPath();
        PerPageFlowActionInterceptorConfig[] perPageFlowInterceptorsConfig = config.getPerPageFlowActionInterceptors();

        if (perPageFlowInterceptorsConfig != null) {
            for (int i = 0; i < perPageFlowInterceptorsConfig.length; i++) {
                PerPageFlowActionInterceptorConfig ppfi = perPageFlowInterceptorsConfig[i];

                if ((ppfi != null) && pageFlowURI.equals(ppfi.getPageFlowUri())) {
                    //
                    // This is a matching page flow -- add per-pageflow interceptors.
                    //
                    addInterceptors(perPageFlowInterceptorsConfig[i].getActionInterceptors(), interceptorsList,
                                    ActionInterceptor.class);
                    addSimpleInterceptors(perPageFlowInterceptorsConfig[i].getSimpleActionInterceptors(), interceptorsList);

                    PerActionInterceptorConfig[] perActionConfigs = perPageFlowInterceptorsConfig[i].getPerActionInterceptors();

                    if (perActionConfigs != null) {
                        for (int j = 0; j < perActionConfigs.length; j++) {
                            PerActionInterceptorConfig perActionConfig = perActionConfigs[j];

                            if ((perActionConfig != null) && actionName.equals(perActionConfig.getActionName())) {
                                //
                                // This is a matching action -- add per-action interceptors.
                                //
                                addInterceptors(perActionConfig.getActionInterceptors(), interceptorsList, ActionInterceptor.class);
                                addSimpleInterceptors(perActionConfig.getSimpleActionInterceptors(), interceptorsList);
                            }
                        }
                    }
                }
            }
        }

        cacheByPageFlow.put(actionName, interceptorsList);
        cache.put(namespace, cacheByPageFlow);

        return interceptorsList;
    }

    private static void addSimpleInterceptors(org.apache.ti.util.config.bean.SimpleActionInterceptorConfig[] configBeans,
                                              List /*< Interceptor >*/ interceptorsList) {
        if (configBeans == null) {
            return;
        }

        for (int i = 0; i < configBeans.length; i++) {
            org.apache.ti.util.config.bean.SimpleActionInterceptorConfig configBean = configBeans[i];
            String path = configBean.getInterceptPath();
            boolean afterAction = configBean.getAfterAction();
            SimpleActionInterceptorConfig config = new SimpleActionInterceptorConfig(path, afterAction);
            interceptorsList.add(new SimpleActionInterceptor(config));
        }
    }

    public void setOriginalForward(Forward origFwd) {
        _originalForward = (origFwd != null) ? new InterceptorForward(origFwd) : null;
    }

    public static void init(Map appScope) {
        // TODO: move some of the lazy-load logic in getActionInterceptors into here.
    }
}
