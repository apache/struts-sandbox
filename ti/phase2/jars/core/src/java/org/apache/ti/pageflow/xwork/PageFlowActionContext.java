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

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import org.apache.commons.chain.web.WebContext;
import org.apache.ti.config.mapper.ActionMapper;
import org.apache.ti.config.mapper.ActionMapping;
import org.apache.ti.pageflow.FlowController;
import org.apache.ti.pageflow.Forward;
import org.apache.ti.pageflow.ModuleConfig;
import org.apache.ti.pageflow.PreviousPageInfo;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.ViewRenderer;
import org.apache.ti.util.SourceResolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class PageFlowActionContext extends ActionContext {

    private static final String STATE_KEY = InternalConstants.ATTR_PREFIX + "contextState";
    private static final String FLOW_CONTROLLER_KEY = InternalConstants.ATTR_PREFIX + "flowController";
    private static final String FORWARD_KEY = InternalConstants.ATTR_PREFIX + "forward";
    private static final String FORM_BEAN_KEY = InternalConstants.ATTR_PREFIX + "formBean";

    /**
     * This is state that needs to be transferred all the way through a chain of requests.
     */
    private static final class State {

        public Integer forwardedRequestCount;
        public String originalServletPath;
        public ViewRenderer viewRenderer;
        public PreviousPageInfo previousPageInfo;
        public boolean returningFromActionIntercept = false;
        public String pageFlowScopedFormName;
        public boolean processPopulateAlreadyCalled = false;
        //public MultipartRequestWrapper multipartRequestWrapper;
        public Throwable exceptionBeingHandled = null;
        public boolean stayInCurrentModule = false;
        public ModuleConfig moduleConfig;
    }

    private FlowController _removingFlowController;
    private WebContext _webContext;

    // TODO: currently these are initialized externally.  Instead we could have specific implementations of this class.
    private String _requestPath;
    private String _requestContextPath;
    private String _requestQueryString;
    private boolean _requestSecure;


    private SourceResolver _sourceResolver;

    public static PageFlowActionContext get() {
        return (PageFlowActionContext) ActionContext.getContext();
    }

    public PageFlowActionContext(Map context, WebContext webContext) {
        super(context);
        _webContext = webContext;


        State state = (State) _webContext.getRequestScope().get(STATE_KEY);
        if (state == null) {
            state = new State();
            _webContext.getRequestScope().put(STATE_KEY, state);
        }
    }

    public WebContext getWebContext() {
        return _webContext;
    }

    public PageFlowAction getAction() {
        ActionInvocation actionInvocation = getActionInvocation();
        return actionInvocation != null ? (PageFlowAction) getActionInvocation().getAction() : null;
    }

    public boolean isNestedRequest() {
        return getState().forwardedRequestCount != null;
    }

    public int getForwardedRequestCount() {
        return getState().forwardedRequestCount != null ? getState().forwardedRequestCount.intValue() : 0;
    }

    public void setForwardedRequestCount(int count) {
        getState().forwardedRequestCount = new Integer(count);
    }

    public String getOriginalServletPath() {
        return getState().originalServletPath;
    }

    public void setOriginalServletPath(String originalServletPath) {
        getState().originalServletPath = originalServletPath;
    }

    public FlowController getFlowController() {
        return (FlowController) get(FLOW_CONTROLLER_KEY);
    }

    public void setCurrentFlowController(FlowController currentFlowController) {
        put(FLOW_CONTROLLER_KEY, currentFlowController);
    }

    public ViewRenderer getViewRenderer() {
        return getState().viewRenderer;
    }

    public void setViewRenderer(ViewRenderer viewRenderer) {
        getState().viewRenderer = viewRenderer;
    }

    public PreviousPageInfo getPreviousPageInfo(boolean remove) {
        PreviousPageInfo retVal = getState().previousPageInfo;
        if (remove) getState().previousPageInfo = null;
        return retVal;
    }

    public void setPreviousPageInfo(PreviousPageInfo previousPageInfo) {
        getState().previousPageInfo = previousPageInfo;
    }

    public boolean isReturningFromActionIntercept() {
        return getState().returningFromActionIntercept;
    }

    public void setReturningFromActionIntercept(boolean returningFromActionIntercept) {
        getState().returningFromActionIntercept = returningFromActionIntercept;
    }

    public String getPageFlowScopedFormName() {
        return getState().pageFlowScopedFormName;
    }

    public void setPageFlowScopedFormName(String pageFlowScopedFormName) {
        getState().pageFlowScopedFormName = pageFlowScopedFormName;
    }

    public boolean isProcessPopulateAlreadyCalled() {
        return getState().processPopulateAlreadyCalled;
    }

    public void setProcessPopulateAlreadyCalled(boolean processPopulateAlreadyCalled) {
        getState().processPopulateAlreadyCalled = processPopulateAlreadyCalled;
    }

    /*
    public MultipartRequestWrapper getMultipartRequestWrapper()
    {
        return getState().multipartRequestWrapper;
    }
    
    public void setMultipartRequestWrapper( MultipartRequestWrapper multipartRequestWrapper )
    {
        getState().multipartRequestWrapper = multipartRequestWrapper;
    }
    */
    
    public boolean isStayInCurrentModule() {
        return getState().stayInCurrentModule;
    }

    public void setStayInCurrentModule(boolean stayInCurrentModule) {
        getState().stayInCurrentModule = stayInCurrentModule;
    }

    public Throwable getExceptionBeingHandled() {
        return getState().exceptionBeingHandled;
    }

    public void setExceptionBeingHandled(Throwable th) {
        getState().exceptionBeingHandled = th;
    }

    private State getState() {
        return (State) _webContext.getRequestScope().get(STATE_KEY);
    }

    public URL getResource(String resourcePath)
            throws IOException, MalformedURLException {
        return _sourceResolver.resolve(resourcePath, _webContext);
    }

    public Map getRequestScope() {
        return _webContext.getRequestScope();
    }

    public final Map getSession() {
        return _webContext.getSessionScope();
    }

    public final Map getApplication() {
        return _webContext.getApplicationScope();
    }

    public Map getSessionScope() {
        return _webContext.getSessionScope();
    }

    public Map getApplicationScope() {
        return _webContext.getApplicationScope();
    }

    /**
     * Get the namespace for the current request.
     */
    public String getNamespace() {
        ModuleConfig moduleConfig = getState().moduleConfig;
        return moduleConfig != null ? moduleConfig.getNamespace() : null;
    }

    public void setModuleConfig(ModuleConfig moduleConfig) {
        getState().moduleConfig = moduleConfig;
    }

    public ModuleConfig getModuleConfig() {
        return getState().moduleConfig;
    }

    public String getRequestPath() {
        return _requestPath;
    }

    public void setRequestPath(String requestPath) {
        _requestPath = requestPath;
    }

    public String getRequestContextPath() {
        return _requestContextPath;
    }

    public void setRequestContextPath(String requestContextPath) {
        _requestContextPath = requestContextPath;
    }

    public String getRequestQueryString() {
        return _requestQueryString;
    }

    public void setRequestQueryString(String requestQueryString) {
        _requestQueryString = requestQueryString;
    }

    public boolean isRequestSecure() {
        return _requestSecure;
    }

    public void setRequestSecure(boolean requestSecure) {
        _requestSecure = requestSecure;
    }

    public String getRequestURI() {
        return getRequestContextPath() + getRequestPath();
    }

    /**
     * Get a request scope that is specific to an outer (e.g., outside-of-portlet request).
     */
    public Map getOuterRequestScope() {
        // TODO: implement this differently for portal/portlet support
        return getRequestScope();
    }

    /**
     * Get a request scope that is specific to an inner (e.g., portlet request), and which does not allow outer
     * request attributes to show through.
     */
    public Map getInnerRequestScope() {
        // TODO: implement this differently for portal/portlet support
        return getRequestScope();
    }

    public Forward getForward() {
        return (Forward) get(FORWARD_KEY);
    }

    public void setForward(Forward forward) {
        put(FORWARD_KEY, forward);
    }

    public Object getFormBean() {
        return get(FORM_BEAN_KEY);
    }

    public void setFormBean(Object formBean) {
        put(FORM_BEAN_KEY, formBean);
    }

    public SourceResolver getSourceResolver() {
        return _sourceResolver;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        _sourceResolver = sourceResolver;
    }

    public ActionMapping getActionMapping() {
        return (ActionMapping) get("actionMapping");
    }

    public ActionMapper getActionMapper() {
        return (ActionMapper) get("actionMapper");
    }

    public FlowController getRemovingFlowController() {
        // Note that this is stored as a member variable because it never gets propagated to another ActionContext.
        return _removingFlowController;
    }

    public void setRemovingFlowController(FlowController removingFlowController) {
        // Note that this is stored as a member variable because it never gets propagated to another ActionContext.
        _removingFlowController = removingFlowController;
    }
}
