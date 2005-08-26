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
import org.apache.ti.pageflow.internal.CachedPageFlowInfo;
import org.apache.ti.pageflow.internal.CachedSharedFlowRefInfo;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.pageflow.internal.ViewRenderer;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.pageflow.xwork.PageFlowPathResult;
import org.apache.ti.pageflow.xwork.PageFlowResult;
import org.apache.ti.util.internal.DiscoveryUtils;
import org.apache.ti.util.internal.FileUtils;
import org.apache.ti.util.internal.cache.ClassLevelCache;
import org.apache.ti.util.logging.Logger;

import javax.servlet.http.HttpSessionBindingEvent;
import java.lang.reflect.Field;
import java.util.Map;


/**
 * <p/>
 * Base class for controller logic, exception handlers, and state associated with a particular web directory path.
 * The class is configured through the
 * {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller} annotation.
 * </p>
 * <p/>
 * <p/>
 * When a page flow request (the page flow URI itself, or any ".do" or page URI in the directory path), arrives, an
 * instance of the associated PageFlowController class is set as the <i>current page flow</i>, and remains stored in the
 * session until a different one becomes active ("long lived" page flows stay in the session indefinitely;
 * see {@link org.apache.ti.pageflow.annotations.ti.controller#longLived longLived}
 * on {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller}).
 * </p>
 * <p/>
 * <p/>
 * The page flow class handles <i>actions</i> that are most commonly raised by user interaction with pages.  The actions
 * are handled by <i>action methods</i> or <i>action annotations</i> that determine the next URI to be displayed, after
 * optionally performing arbitrary logic.
 * </p>
 * <p/>
 * <p/>
 * If the PageFlowController is a "nested page flow"
 * ({@link org.apache.ti.pageflow.annotations.ti.controller#nested nested} is set to <code>true</code>
 * on {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller}), then this
 * is a reusable, modular flow that can be "nested" during other flows.  It has entry points (actions with optional form
 * bean arguments), and exit points ({@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward},
 * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}, or
 * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward} annotations
 * that have <code>returnAction</code> attributes).
 * </p>
 * <p/>
 * <p/>
 * The page flow class also handles exceptions thrown by actions or during page execution
 * (see {@link org.apache.ti.pageflow.annotations.ti.controller#catches catches} on
 * {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller}).  Unhandled exceptions are
 * handled in order by declared {@link SharedFlowController}s
 * (see {@link org.apache.ti.pageflow.annotations.ti.controller#sharedFlowRefs sharedFlowRefs} on
 * {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller}).
 * </p>
 * <p/>
 * <p/>
 * Properties in the current page flow instance can be accessed from JSP 2.0-style expressions like this one:
 * <code>${pageFlow.someProperty}</code>.
 * </p>
 * <p/>
 * <p/>
 * There may only be one page flow in any package.
 * </p>
 *
 * @see SharedFlowController
 */
public abstract class PageFlowController
        extends FlowController
        implements InternalConstants {

    /**
     * A 'return-to="page"' forward brings the user back to the previous page. This object
     * stores information about the current state of affairs, such as the origin URI and
     * its form bean.
     */
    private PreviousPageInfo _previousPageInfo = null;
    private PreviousPageInfo _currentPageInfo = null;

    /**
     * A 'return-to="action"' forward reruns the previous action. This object stores the previous
     * action URI and its form bean.
     */
    private PreviousActionInfo _previousActionInfo;

    private boolean _isOnNestingStack = false;
    private ViewRenderer _returnActionViewRenderer = null;

    private static final String SAVED_PREVIOUS_PAGE_INFO_ATTR = InternalConstants.ATTR_PREFIX + "savedPrevPageInfo";
    private static final String CACHED_INFO_KEY = "cachedInfo";
    private static final Logger _log = Logger.getInstance(PageFlowController.class);


    /**
     * Default constructor.
     */
    protected PageFlowController() {
    }

    /**
     * Get the namespace for this page flow.
     *
     * @return a String that is the namespace for this controller, and which is also
     *         the directory path from the web application root to this PageFlowController
     *         (not including the action filename).
     */
    public String getNamespace() {
        return getCachedInfo().getNamespace();
    }

    /**
     * Get the path for addressing this object within an application.
     *
     * @return a String that is the path which will execute the begin action on this controller.
     */
    public String getPath() {
        return getCachedInfo().getPath();
    }

    /**
     * Tell whether this PageFlowController can be "nested", i.e., if it can be invoked from another page
     * flow with the intention of returning to the original one.  Page flows are declared to be nested by specifying
     * <code>{@link org.apache.ti.pageflow.annotations.ti.controller#nested nested}=true</code> on the
     * {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller} annotation.
     *
     * @return <code>true</code> if this PageFlowController can be nested.
     */
    protected boolean isNestedFlow() {
        return getModuleConfig().isNestedFlow();
    }

    /**
     * Tell whether this is a "long lived" page flow.  Once it is invoked, a long lived page flow is never
     * removed from the session unless {@link #remove} is called.  Navigating to another page flow hides
     * the current long lived controller, but does not remove it.
     */
    protected boolean isLongLivedFlow() {
        return getModuleConfig().isLongLivedFlow();
    }

    /**
     * Remove this instance from the session.  When inside a page flow action, {@link #remove} may be called instead.
     */
    protected synchronized void removeFromSession() {
        // This request attribute is used in persistInSession to prevent re-saving of this instance.
        getContext().setRemovingFlowController(this);

        if (isLongLivedFlow()) {
            PageFlowUtils.removeLongLivedPageFlow(getNamespace());
        } else {
            InternalUtils.removeCurrentPageFlow();
        }
    }

    /**
     * Store this object in the user session, in the appropriate place.  Used by the framework; normally should not be
     * called directly.
     */
    public void persistInSession() {
        PageFlowController currentPageFlow = PageFlowUtils.getCurrentPageFlow();

        if (currentPageFlow != null && !currentPageFlow.isOnNestingStack()) {
            //
            // We're going to be implicitly destroying the current page flow.  Synchronize on it so we don't mess
            // with concurrent requests.
            //
            synchronized (currentPageFlow) {
                InternalUtils.setCurrentPageFlow(this);
            }
        } else {
            InternalUtils.setCurrentPageFlow(this);
        }
    }

    /**
     * @exclude
     */
    protected Forward internalExecute(Object form)
            throws Exception {
        initializeSharedFlowFields();
        return super.internalExecute();
    }

    private void initializeSharedFlowFields() {
        //
        // Initialize the shared flow fields.
        //
        CachedSharedFlowRefInfo.SharedFlowFieldInfo[] sharedFlowMemberFields =
                getCachedInfo().getSharedFlowMemberFields();

        if (sharedFlowMemberFields != null) {
            for (int i = 0; i < sharedFlowMemberFields.length; i++) {
                CachedSharedFlowRefInfo.SharedFlowFieldInfo fi = sharedFlowMemberFields[i];
                Field field = fi.field;

                if (fieldIsUninitialized(field)) {
                    Map/*< String, SharedFlowController >*/ sharedFlows = PageFlowUtils.getSharedFlows();
                    String name = fi.sharedFlowName;
                    assert name != null;
                    SharedFlowController sf = (SharedFlowController) sharedFlows.get(name);

                    if (sf != null) {
                        initializeField(field, sf);
                    } else {
                        _log.error("Could not find shared flow with name \"" + fi.sharedFlowName
                                + "\" to initialize field " + field.getName() + " in " + getClass().getName());
                    }
                }
            }
        }
    }

    /**
     * Get the a map of shared flow name to shared flow instance.
     *
     * @return a Map of shared flow name (string) to shared flow instance ({@link SharedFlowController}).
     */
    public Map/*< String, SharedFlowController >*/ getSharedFlows() {
        return PageFlowUtils.getSharedFlows();
    }

    /**
     * Get a shared flow, based on its name as defined in this page flow's
     * {@link org.apache.ti.pageflow.annotations.ti.controller#sharedFlowRefs sharedFlowRefs}
     * attribute on {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller}.
     * To retrieve any shared flow based on its class name, use {@link PageFlowUtils#getSharedFlow}.
     *
     * @param sharedFlowName the name of the shared flow, as in this page flows's
     *                       {@link org.apache.ti.pageflow.annotations.ti.controller#sharedFlowRefs sharedFlowRefs}
     *                       attribute on the {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller}
     *                       annotation.
     * @return the {@link SharedFlowController} with the given name.
     */
    public SharedFlowController getSharedFlow(String sharedFlowName) {
        return (SharedFlowController) PageFlowUtils.getSharedFlows().get(sharedFlowName);
    }

    /**
     * Remove a shared flow from the session, based on its name as defined in this page flow's
     * {@link org.apache.ti.pageflow.annotations.ti.controller#sharedFlowRefs sharedFlowRefs}
     * attribute on {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller}.
     * To remove any shared flow based on its class name, use {@link PageFlowUtils#removeSharedFlow}.
     *
     * @param sharedFlowName the name of the shared flow, as in this page flows's
     *                       {@link org.apache.ti.pageflow.annotations.ti.controller#sharedFlowRefs sharedFlowRefs}
     *                       attribute on the {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller}
     *                       annotation.
     */
    public void removeSharedFlow(String sharedFlowName) {
        SharedFlowController sf = getSharedFlow(sharedFlowName);
        if (sf != null) sf.removeFromSession();
    }

    /**
     * This is a framework method for initializing a newly-created page flow, and should not normally be called
     * directly.
     */
    public final synchronized void create() {
        reinitialize();
        initializeSharedFlowFields();

        if (isNestedFlow()) {
            //
            // Initialize a ViewRenderer for exiting the nested page flow.
            //
            String vrClassName = (String) getRequestScope().get(InternalConstants.RETURN_ACTION_VIEW_RENDERER_PARAM);

            if (vrClassName != null) {
                ViewRenderer vr =
                        (ViewRenderer) DiscoveryUtils.newImplementorInstance(vrClassName, ViewRenderer.class);

                if (vr != null) {
                    vr.init();
                    _returnActionViewRenderer = vr;
                }
            }
        }

        super.create();
    }

    /**
     * Get the submitted form bean from the most recent action execution in this PageFlowController.
     * <p/>
     * <i>Note: if the current page flow does not contain a
     * </i>{@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward}<i> or a
     * </i>{@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}<i> with
     * </i><code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousAction ti.NavigateTo.previousAction}</code><i>,
     * then this method will always return </i><code>null</code><i> by default.  To enable it in this
     * situation, add the following method to the page flow:</i><br>
     * <blockquote>
     * <code>
     * protected boolean alwaysTrackPreviousAction()<br>
     * {<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;return true;<br>
     * }<br>
     * </code>
     * </blockquote>
     *
     * @return the form bean instance from the most recent action execution, or <code>null</code>
     *         if there was no form bean submitted.
     * @see #getPreviousPageInfo
     * @see #getCurrentPageInfo
     * @see #getPreviousActionInfo
     * @see #getPreviousActionURI
     * @see #getPreviousForwardPath
     * @see #getCurrentForwardPath
     */
    protected Object getPreviousFormBean() {
        checkPreviousActionInfoDisabled();
        return _previousActionInfo != null ? _previousActionInfo.getFormBean() : null;
    }

    /**
     * Get the URI for the most recent action in this PageFlowController.
     * <p/>
     * <i>Note: if the current page flow does not use a
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward},
     * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}, or
     * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward}
     * with <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousAction previousAction}</code>,
     * then this method will always return </i><code>null</code><i> by default.  To enable it in this situation, add the
     * following method to the page flow:</i><br>
     * <blockquote>
     * <code>
     * protected boolean alwaysTrackPreviousAction()<br>
     * {<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;return true;<br>
     * }<br>
     * </code>
     * </blockquote>
     *
     * @return a String that is the most recent URI.
     * @see #getPreviousPageInfo
     * @see #getCurrentPageInfo
     * @see #getPreviousActionInfo
     * @see #getPreviousFormBean
     * @see #getPreviousForwardPath
     * @see #getCurrentForwardPath
     */
    protected String getPreviousActionURI() {
        checkPreviousActionInfoDisabled();
        return _previousActionInfo != null ? _previousActionInfo.getActionURI() : null;
    }

    /**
     * Get the webapp-relative URI for the most recent page (in this page flow) shown to the user.
     * <p/>
     * <i>Note: if the current page flow does not use a
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward},
     * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}, or
     * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward}
     * with <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#currentPage currentPage}</code>
     * or <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousPage previousPage}</code>,
     * then this method will always return </i><code>null</code><i> by default.  To enable it in this situation, add the
     * following method to the page flow:</i><br>
     * <blockquote>
     * <code>
     * protected boolean alwaysTrackPreviousPage()<br>
     * {<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;return true;<br>
     * }<br>
     * </code>
     * </blockquote>
     *
     * @return a String that is the URI path for the most recent page shown to the user.
     * @see #getPreviousPageInfo
     * @see #getCurrentPageInfo
     * @see #getPreviousActionInfo
     * @see #getPreviousActionURI
     * @see #getPreviousFormBean
     * @see #getPreviousForwardPath
     */
    public String getCurrentForwardPath() {
        PreviousPageInfo curPageInfo = getCurrentPageInfo();
        String path = null;

        if (curPageInfo != null) {
            String resultPath = curPageInfo.getResult().getLocation();

            if (resultPath != null) {
                if (resultPath.startsWith("/")) {
                    path = resultPath;
                } else {
                    path = getNamespace() + '/' + resultPath;
                }
            }
        }
        return path;
    }

    /**
     * Get the webapp-relative URI for the previous page (in this page flow) shown to the user.
     * The previous page is the one shown before the most recent page.
     * <p/>
     * <i>Note: if the current page flow does not use a
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward},
     * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}, or
     * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward}
     * with <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#currentPage currentPage}</code>
     * or <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousPage previousPage}</code>,
     * then this method will always return </i><code>null</code><i> by default.  To enable it in this situation, add the
     * following method to the page flow:</i><br>
     * <blockquote>
     * <code>
     * protected boolean alwaysTrackPreviousPage()<br>
     * {<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;return true;<br>
     * }<br>
     * </code>
     * </blockquote>
     *
     * @return a String that is the URI path for the previous page shown to the user.
     * @see #getPreviousPageInfo
     * @see #getCurrentPageInfo
     * @see #getPreviousActionInfo
     * @see #getPreviousActionURI
     * @see #getPreviousFormBean
     * @see #getCurrentForwardPath
     */
    protected String getPreviousForwardPath() {
        PreviousPageInfo prevPageInfo = getPreviousPageInfo();

        if (prevPageInfo != null) {
            PageFlowResult prevResult = prevPageInfo.getResult();
            return prevResult != null ? prevResult.getLocation() : null;
        } else {
            return null;
        }
    }

    /**
     * Get information about the most recent page (in this page flow) shown to the user.
     * <p/>
     * <i>Note: if the current page flow does not use a
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward},
     * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}, or
     * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward}
     * with <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#currentPage currentPage}</code>
     * or <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousPage previousPage}</code>,
     * then this method will always return </i><code>null</code><i> by default.  To enable it in this situation, add the
     * following method to the page flow:</i><br>
     * <blockquote>
     * <code>
     * protected boolean alwaysTrackPreviousPage()<br>
     * {<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;return true;<br>
     * }<br>
     * </code>
     * </blockquote>
     *
     * @return a PreviousPageInfo with information about the most recent page shown to the user.
     * @see #getPreviousPageInfo
     * @see #getPreviousActionInfo
     * @see #getPreviousActionURI
     * @see #getPreviousFormBean
     * @see #getPreviousForwardPath
     * @see #getCurrentForwardPath
     */
    public final PreviousPageInfo getCurrentPageInfo() {
        checkPreviousPageInfoDisabled();

        if (_currentPageInfo != null) {
            // Allows it to reconstruct transient members after session failover
            _currentPageInfo.reinitialize(this);
        }

        return _currentPageInfo;
    }

    /**
     * Get information about the previous page (in this page flow) shown to the user.  The previous
     * page is the one shown before the most recent page.
     * <p/>
     * <i>Note: if the current page flow does not use a
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward},
     * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}, or
     * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward}
     * with <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#currentPage currentPage}</code>
     * or <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousPage previousPage}</code>,
     * then this method will always return </i><code>null</code><i> by default.  To enable it in this situation, add the
     * following method to the page flow:</i><br>
     * </blockquote>
     *
     * @return a PreviousPageInfo with information about the previous page shown to the user.
     * @see #getCurrentPageInfo
     * @see #getPreviousActionInfo
     * @see #getPreviousActionURI
     * @see #getPreviousFormBean
     * @see #getPreviousForwardPath
     * @see #getCurrentForwardPath
     */
    public final PreviousPageInfo getPreviousPageInfo() {
        checkPreviousPageInfoDisabled();

        PreviousPageInfo ret = _previousPageInfo != null ? _previousPageInfo : _currentPageInfo;

        if (ret != null) {
            ret.reinitialize(this); // Allows it to reconstruct transient members after session failover
        }

        return ret;
    }

    /**
     * Get information about the most recent action run in this page flow.
     * <p/>
     * <i>Note: if the current page flow does not use a
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward},
     * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction}, or
     * {@link org.apache.ti.pageflow.annotations.ti.conditionalForward &#64;ti.conditionalForward}
     * with <code>navigateTo={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#previousAction previousAction}</code>,
     * then this method will always return </i><code>null</code><i> by default.  To enable it in this situation, add the
     * following method to the page flow:</i><br>
     * <blockquote>
     * <code>
     * protected boolean alwaysTrackPreviousAction()<br>
     * {<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;return true;<br>
     * }<br>
     * </code>
     * </blockquote>
     *
     * @return a PreviousActionInfo with information about the most recent action run in this page flow.
     * @see #getPreviousPageInfo
     * @see #getCurrentPageInfo
     * @see #getPreviousActionURI
     * @see #getPreviousFormBean
     * @see #getPreviousForwardPath
     * @see #getCurrentForwardPath
     */
    public final PreviousActionInfo getPreviousActionInfo() {
        checkPreviousActionInfoDisabled();

        PageFlowActionContext actionContext = PageFlowActionContext.get();
        PreviousActionInfo inContext = (PreviousActionInfo) actionContext.get("ttttt");
        if (inContext != null) return inContext;

        return _previousActionInfo;
    }

    private void checkPreviousActionInfoDisabled() {
        if (isPreviousActionInfoDisabled()) {
            throw new IllegalStateException("Previous action information has been disabled in this page flow.  Override alwaysTrackPreviousAction() to enable it.");
        }
    }

    private void checkPreviousPageInfoDisabled() {
        if (isPreviousPageInfoDisabled()) {
            throw new IllegalStateException("Previous page information has been disabled in this page flow.  Override alwaysTrackPreviousPage() to enable it.");
        }
    }

    /**
     * Get the display name of this page flow.
     *
     * @return the display name (the URI) of this page flow.
     */
    public String getDisplayName() {
        return getPath();
    }

    public boolean isPreviousActionInfoDisabled() {
        if (alwaysTrackPreviousAction()) return false;
        return getModuleConfig().isReturnToActionDisabled();
    }

    public boolean isPreviousPageInfoDisabled() {
        if (alwaysTrackPreviousPage()) return false;
        return getModuleConfig().isReturnToPageDisabled();
    }

    /**
     * Called from {@link FlowController#execute}.
     */
    void savePreviousActionInfo() {
        //
        // If previous-action is disabled (unused in this pageflow), just return.
        //
        if (isPreviousActionInfoDisabled()) return;
        
        // Keep the current PreviousActionInstance until this context is finished.
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        actionContext.put("ttttt", _previousActionInfo);
        _previousActionInfo = new PreviousActionInfo();
    }

    /**
     * Store information about recent pages displayed.  This is a framework-invoked method that should not normally be
     * called directly.
     */
    public void savePreviousPageInfo(PageFlowResult result, Forward fwd, Object form) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();

        if (result != null) {
            Object formBean = actionContext.getFormBean();
            
            //
            // If previous-page is disabled (unused in this pageflow), or if we've already saved prevous-page info in
            // this request (for example, forward to foo.faces which forwards to foo.jsp), just return.
            //
            if (getRequestScope().get(SAVED_PREVIOUS_PAGE_INFO_ATTR) == this || isPreviousPageInfoDisabled()) return;

            String path = result.getLocation();
            int queryPos = path.indexOf('?');
            if (queryPos != -1) path = path.substring(0, queryPos);
            
            //
            // If a form bean was generated in this request, add it to the most recent PreviousPageInfo, so when we
            // go back to that page, the *updated* field values are restored (i.e., we don't revert to the values of
            // the form that was passed into the page originally).
            //
            if (formBean != null && _currentPageInfo != null) {
                Object oldForm = _currentPageInfo.getFormBean();
                if (oldForm == null || oldForm.getClass().equals(formBean.getClass())) {
                    _currentPageInfo.setFormBean(formBean);
                    _currentPageInfo.setAction(actionContext.getAction());
                }
            }
            
            //
            // Only keep track of *pages* forwarded to -- not actions or pageflows.
            //
            if (!FileUtils.osSensitiveEndsWith(path, ACTION_EXTENSION)) {
                //
                // Only save previous-page info if the page is within this pageflow.
                //
                if (isLocalFile(result)) // || PageFlowUtils.osSensitiveEndsWith( path, JPF_EXTENSION ) )
                {
                    _previousPageInfo = _currentPageInfo;
                    _currentPageInfo = new PreviousPageInfo(result, fwd);
                    getRequestScope().put(SAVED_PREVIOUS_PAGE_INFO_ATTR, this);
                }
            }
        }
    }

    private boolean isLocalFile(PageFlowResult result) {
        String path = result.getLocation();
        if (!path.startsWith("/")) return true;
        
        // TODO: re-evaluate whether path and namespace can be linked this way
        String modulePath = getNamespace();

        if (!path.startsWith(modulePath)) {
            return false;
        } else {
            return path.indexOf('/', modulePath.length() + 1) == -1;
        }
    }

    private boolean isOnNestingStack() {
        return _isOnNestingStack;
    }

    /**
     * Callback when this object is removed from the user session.  Causes {@link #onDestroy} to be called.  This is a
     * framework-invoked method that should not normally be called indirectly.
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        //
        // Unless this pageflow has been pushed onto the nesting stack, do the onDestroy() callback.
        //
        if (!_isOnNestingStack) {
            super.valueUnbound(event);
        }
    }

    void setIsOnNestingStack(boolean isOnNestingStack) {
        _isOnNestingStack = isOnNestingStack;
    }

    private CachedPageFlowInfo getCachedInfo() {
        ClassLevelCache cache = ClassLevelCache.getCache(getClass());
        CachedPageFlowInfo info = (CachedPageFlowInfo) cache.getCacheObject(CACHED_INFO_KEY);

        if (info == null) {
            info = new CachedPageFlowInfo(getClass());
            cache.setCacheObject(CACHED_INFO_KEY, info);
        }

        return info;
    }

    /**
     * Trigger before-page logic.  This is a framework-invoked method that should not normally be called directly.
     */
    public final void beforePage() {
        //
        // We may need to save the previous page info if the page was called directly (not forwarded through an action)
        // and we do not yet have the forward path in the page flow or it is a different path.
        //
        if (!isPreviousPageInfoDisabled()) {
            String relativeUri = getContext().getRequestPath();

            String path = getCurrentForwardPath();
            if (path == null || !path.equals(relativeUri)) {
                PageFlowPathResult result = new PageFlowPathResult();
                result.setLocation(relativeUri);
                savePreviousPageInfo(result, null, null);
            }
        }
    }

    public Forward exitNesting() {
        if (_returnActionViewRenderer != null) getContext().setViewRenderer(_returnActionViewRenderer);

        try {
            onExitNesting();
        } catch (Throwable th) {
            try {
                return handleException(th);
            } catch (Exception e) {
                _log.error("Exception thrown while handling exception.", e);
            }
        }

        return null;
    }

    /**
     * Callback that is invoked when this controller instance is exiting nesting (through a return action).
     */
    protected void onExitNesting()
            throws Exception {
    }

    /**
     * Ensures that any changes to this object will be replicated in a cluster (for failover),
     * even if the replication scheme uses a change-detection algorithm that relies on
     * HttpSession.setAttribute to be aware of changes.  Note that this method is used by the framework
     * and does not need to be called explicitly in most cases.
     */
    public void ensureFailover() {
        //
        // remove() puts the pageflow instance into a request attribute.  Make sure not to re-save this
        // instance if it's being removed.  Also, if the session is null (after having been invalidated
        // by the user), don't recreate it.
        //
        if (getContext().getRemovingFlowController() != this && sessionExists()) {
            StorageHandler sh = Handlers.get().getStorageHandler();
            
            //
            // If this is a long-lived page flow, there are two attributes to deal with.
            //
            if (isLongLivedFlow()) {
                String longLivedAttrName = InternalUtils.getLongLivedFlowAttr(getNamespace());
                longLivedAttrName = InternalUtils.getScopedAttrName(longLivedAttrName);
                String currentLongLivedAttrName =
                        InternalUtils.getScopedAttrName(CURRENT_LONGLIVED_ATTR);
                sh.ensureFailover(longLivedAttrName, this);
                sh.ensureFailover(currentLongLivedAttrName, getNamespace());
            } else {
                String attrName = InternalUtils.getScopedAttrName(CURRENT_JPF_ATTR);
                sh.ensureFailover(attrName, this);
            }
        }
    }

    protected boolean sessionExists() {
        return true;
    }
}
