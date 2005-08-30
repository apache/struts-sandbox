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
package org.apache.ti.script.common;

import org.apache.ti.pageflow.FacesBackingBean;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowUtils;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.script.el.NetUIUpdateVariableResolver;
import org.apache.ti.util.logging.Logger;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.VariableResolver;
import java.util.Collections;
import java.util.Map;

/**
 *
 */
public class ImplicitObjectUtil {

    private static final Logger LOGGER = Logger.getInstance(ImplicitObjectUtil.class);

    private static final String PAGE_FLOW_IMPLICIT_OBJECT_KEY = "pageFlow";
    private static final String SHARED_FLOW_IMPLICIT_OBJECT_KEY = "sharedFlow";
//    private static final String BUNDLE_IMPLICIT_OBJECT_KEY = "bundle";
    private static final String BACKING_IMPLICIT_OBJECT_KEY = "backing";
    private static final String PAGE_INPUT_IMPLICIT_OBJECT_KEY = "pageInput";
    private static final String ACTION_FORM_IMPLICIT_OBJECT_KEY = "actionForm";
    private static final String OUTPUT_FORM_BEAN_OBJECT_KEY = "outputFormBean";

    /* do not construct */
    private ImplicitObjectUtil() {
    }

    public static final void loadActionForm(JspContext jspContext, Object form) {
        jspContext.setAttribute(ACTION_FORM_IMPLICIT_OBJECT_KEY, form);
    }

    public static final void unloadActionForm(JspContext jspContext) {
        jspContext.removeAttribute(ACTION_FORM_IMPLICIT_OBJECT_KEY);
    }

    public static final void loadPageFlow(PageFlowController pageFlow) {
        Map requestScope = PageFlowActionContext.get().getRequestScope();
        if (pageFlow != null)
            requestScope.put(PAGE_FLOW_IMPLICIT_OBJECT_KEY, pageFlow);

        Map map = InternalUtils.getPageInputMap();
        requestScope.put(PAGE_INPUT_IMPLICIT_OBJECT_KEY, map != null ? map : Collections.EMPTY_MAP);
    }

    public static final void loadFacesBackingBean(FacesBackingBean fbb) {
        if (fbb != null) {
            Map requestScope = PageFlowActionContext.get().getRequestScope();
            requestScope.put(BACKING_IMPLICIT_OBJECT_KEY, fbb);
        }
    }

    public static final void unloadFacesBackingBean() {
        Map requestScope = PageFlowActionContext.get().getRequestScope();
        requestScope.remove(BACKING_IMPLICIT_OBJECT_KEY);
    }

    public static final void loadSharedFlow(Map/*<String, SharedFlowController>*/ sharedFlows) {
        if (sharedFlows != null) {
            Map requestScope = PageFlowActionContext.get().getRequestScope();
            requestScope.put(SHARED_FLOW_IMPLICIT_OBJECT_KEY, sharedFlows);
        }
    }

    /* TODO: re-add bundle support
    public static final void loadBundleMap(ServletRequest servletRequest, BundleMap bundleMap) {
        servletRequest.setAttribute(BUNDLE_IMPLICIT_OBJECT_KEY, bundleMap);
    }
    */

    public static final Map/*<String, SharedFlowController>*/ getSharedFlow() {
        Map requestScope = PageFlowActionContext.get().getRequestScope();
        return (Map /*<String, SharedFlowController>*/) requestScope.get(SHARED_FLOW_IMPLICIT_OBJECT_KEY);
    }

    public static final PageFlowController getPageFlow() {
        PageFlowController jpf = PageFlowUtils.getCurrentPageFlow();
        if (jpf != null)
            return jpf;
        else {
            // @todo: i18n
            RuntimeException re = new RuntimeException("There is no current PageFlow for the expression.");
            if (LOGGER.isErrorEnabled()) LOGGER.error("", re);
            throw re;
        }
    }

    public static final VariableResolver getUpdateVariableResolver(Object form, boolean isHandlingPost) {

        /* todo: need to provide get(Read|Update)VariableResolver methods on the ExpressionEngineFactory */
        return new NetUIUpdateVariableResolver(form, isHandlingPost);
    }

    public static final VariableResolver getReadVariableResolver(PageContext pageContext) {
        assert pageContext != null;
        return pageContext.getVariableResolver();
    }

    public static final void loadImplicitObjects(PageFlowController curJpf) {
        // @todo: need to wrap this in checks for JSP 1.2
        // @todo: feature: need to add support for chaining in user-code to run when setting implicit objects on the request
        loadPageFlow(curJpf);
        
        // @todo: need to move bundleMap creation to a BundleMapFactory
        // TODO: re-add bundle support
//        BundleMap bundleMap = new BundleMap(request, servletContext);
//        loadBundleMap(request, bundleMap);
    }

    public static final void loadOutputFormBean(Object bean) {
        if (bean != null) {
            Map requestScope = PageFlowActionContext.get().getRequestScope();
            requestScope.put(OUTPUT_FORM_BEAN_OBJECT_KEY, bean);
        }
    }
}
