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
package org.apache.ti.pageflow.faces.internal;

import org.apache.ti.pageflow.FacesBackingBean;
import org.apache.ti.pageflow.FacesBackingBeanFactory;
import org.apache.ti.pageflow.PageFlowController;
import org.apache.ti.pageflow.PageFlowUtils;
import org.apache.ti.pageflow.PreviousPageInfo;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.script.common.ImplicitObjectUtil;
import org.apache.ti.util.internal.FileUtils;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;


/**
 * Internal class used in JSF/Page Flow integration.  Delegates in all cases except:
 * <ul>
 * <li>
 * {@link #restoreView}, which prevents view restoration if we're in a request forwarded by
 * {@link PageFlowNavigationHandler}.
 * </li>
 * <li>
 * {@link #createView}, which integrates with the "navigateTo" feature in Page Flow to save/restore the
 * component tree.
 * </li>
 * </ul>
 *
 * @see org.apache.ti.pageflow.faces.PageFlowApplicationFactory
 */
class PageFlowViewHandler
        extends ViewHandler {

    private ViewHandler _delegate;

    public PageFlowViewHandler(ViewHandler delegate) {
        _delegate = delegate;
    }

    public Locale calculateLocale(FacesContext context) {
        return _delegate.calculateLocale(context);
    }

    public String calculateRenderKitId(FacesContext context) {
        return _delegate.calculateRenderKitId(context);
    }

    private static class PageClientState implements Serializable {

        private UIViewRoot _viewRoot;
        private FacesBackingBean _backingBean;

        public PageClientState(UIViewRoot viewRoot, FacesBackingBean backingBean) {
            _viewRoot = viewRoot;
            _backingBean = backingBean;
        }

        public UIViewRoot getViewRoot() {
            return _viewRoot;
        }

        public FacesBackingBean getBackingBean() {
            return _backingBean;
        }
    }

    private static void setBackingBean() {
        FacesBackingBeanFactory factory = FacesBackingBeanFactory.get();
        FacesBackingBean fbb = factory.getFacesBackingBeanForRequest();

        if (fbb != null) {
            ImplicitObjectUtil.loadFacesBackingBean(fbb);
        } else {
            ImplicitObjectUtil.unloadFacesBackingBean();
        }
    }

    public UIViewRoot createView(FacesContext context, String viewId) {
        ExternalContext externalContext = context.getExternalContext();
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        
        //
        // If this is a navigateTo=ti.NavigateTo.currentPage or a navigateTo=ti.NavigateTo.previousPage,
        // see if we've saved view state from the original page.  If so, just restore that.
        //
        PreviousPageInfo prevPageInfo = actionContext.getPreviousPageInfo(true);

        if (prevPageInfo != null) {
            Object clientState = prevPageInfo.getClientState();

            if (clientState != null && clientState instanceof PageClientState) {
                PageClientState pcs = (PageClientState) clientState;
                FacesBackingBean fbb = pcs.getBackingBean();

                if (fbb != null) {
                    fbb.restore();
                } else {
                    InternalUtils.removeCurrentFacesBackingBean();
                }

                return pcs.getViewRoot();
            }
        }
    
        //
        // Create/restore the backing bean that corresponds to this request.
        //
        setBackingBean();

        UIViewRoot viewRoot = _delegate.createView(context, viewId);
        savePreviousPageInfo(viewId, viewRoot);
        return viewRoot;
    }

    public String getActionURL(FacesContext context, String viewId) {
        return _delegate.getActionURL(context, viewId);
    }

    public String getResourceURL(FacesContext context, String path) {
        return _delegate.getResourceURL(context, path);
    }

    public void renderView(FacesContext context, UIViewRoot viewToRender)
            throws IOException, FacesException {
        //
        // Create/restore the backing bean that corresponds to this request.
        //
        setBackingBean();
        _delegate.renderView(context, viewToRender);
    }

    /**
     * If we are in a request forwarded by {@link PageFlowNavigationHandler}, returns <code>null</code>; otherwise,
     * delegates to the base ViewHandler.
     */
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        //
        // If we did a forward in PageFlowNavigationHandler, don't try to restore the view.
        //
        Map requestScope = PageFlowActionContext.get().getRequestScope();
        if (requestScope.get(PageFlowNavigationHandler.ALREADY_FORWARDED_ATTR) != null) {
            return null;
        }
    
        //
        // Create/restore the backing bean that corresponds to this request.
        //
        setBackingBean();

        UIViewRoot viewRoot = _delegate.restoreView(context, viewId);
        savePreviousPageInfo(viewId, viewRoot);
        return viewRoot;
    }

    private static void savePreviousPageInfo(String viewID, UIViewRoot viewRoot) {
        //
        // Save the current view state in the PreviousPageInfo structure of the current page flow.
        //
        PageFlowController curPageFlow = PageFlowUtils.getCurrentPageFlow();

        if (curPageFlow != null && !curPageFlow.isPreviousPageInfoDisabled()) {
            //
            // Only save the previous page info if the JSF view-ID is the same as the current forward path.
            // Note that we strip the file extension from the view-ID -- different JSF implementations give
            // us different things (foo.jsp vs. foo.faces).
            //
            viewID = FileUtils.stripFileExtension(viewID);
            String currentForwardPath = FileUtils.stripFileExtension(curPageFlow.getCurrentForwardPath());
            if (viewID.equals(currentForwardPath)) {
                PreviousPageInfo prevPageInfo = curPageFlow.getCurrentPageInfo();
                FacesBackingBean backingBean = InternalUtils.getFacesBackingBean();
                prevPageInfo.setClientState(new PageClientState(viewRoot, backingBean));
            }
        }
    }

    public void writeState(FacesContext context) throws IOException {
        _delegate.writeState(context);
    }
}
