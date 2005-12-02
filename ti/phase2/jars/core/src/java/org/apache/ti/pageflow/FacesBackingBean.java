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
import org.apache.ti.pageflow.internal.CachedFacesBackingInfo;
import org.apache.ti.pageflow.internal.CachedSharedFlowRefInfo;
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.internal.InternalUtils;
import org.apache.ti.util.internal.cache.ClassLevelCache;
import org.apache.ti.util.logging.Logger;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

/**
 * <p/>
 * A JavaServer Faces backing bean.  An instance of this class will be created whenever a corresponding JSF path is
 * requested (e.g., an instance of foo.MyPage will be created for the webapp-relative path "/foo/MyPage.faces").  The
 * instance will be released (removed from the user session) when a non-matching path is requested.  A faces backing
 * bean can hold component references and event/command handlers, and it can raise actions with normal JSF command event
 * handlers that are annotated with {@link org.apache.ti.pageflow.annotations.ti.commandHandler &#64;ti.commandHandler}.
 * The bean instance can be bound to with a JSF-style expression like <code>#{backing.myComponent}</code>.
 * </p>
 * <p/>
 * JSF backing beans are configured using the
 * {@link org.apache.ti.pageflow.annotations.ti.facesBacking &#64;ti.facesBacking} annotation.
 * </p>
 */
public abstract class FacesBackingBean
        extends PageFlowManagedObject {

    private static final String CACHED_INFO_KEY = "cachedInfo";
    private static final Logger _log = Logger.getInstance(FacesBackingBean.class);

    private Map _pageInputs;


    /**
     * Store this object in the user session, in the appropriate place.  Used by the framework; normally should not be
     * called directly.
     */
    public void persistInSession() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = InternalUtils.getScopedAttrName(InternalConstants.FACES_BACKING_ATTR);
        sh.setAttribute(attrName, this);
    }

    /**
     * Remove this instance from the session.
     */
    protected void removeFromSession() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = InternalUtils.getScopedAttrName(InternalConstants.FACES_BACKING_ATTR);

        sh.removeAttribute(attrName);
    }

    /**
     * Ensures that any changes to this object will be replicated in a cluster (for failover),
     * even if the replication scheme uses a change-detection algorithm that relies on
     * HttpSession.setAttribute to be aware of changes.  Note that this method is used by the framework
     * and does not need to be called explicitly in most cases.
     */
    public void ensureFailover() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attr = InternalUtils.getScopedAttrName(InternalConstants.FACES_BACKING_ATTR);
        sh.ensureFailover(attr, this);
    }

    /**
     * Get the display name for the bean.  For FacesBackingBeans, this is simply the class name.
     */
    public String getDisplayName() {
        return getClass().getName();
    }

    /**
     * Reinitialize the bean for a new request.  Used by the framework; normally should not be called directly.
     */
    public void reinitialize() {
        super.reinitialize();

        if (_pageInputs == null) {
            Map map = InternalUtils.getActionOutputMap(false);
            if (map != null) _pageInputs = Collections.unmodifiableMap(map);
        }
        
        //
        // Initialize the page flow field.
        //
        Field pageFlowMemberField = getCachedInfo().getPageFlowMemberField();
        
        // TODO: should we add a compiler warning if this field isn't transient?  All this reinitialization logic is
        // for the transient case.
        if (fieldIsUninitialized(pageFlowMemberField)) {
            PageFlowController pfc = PageFlowUtils.getCurrentPageFlow();
            initializeField(pageFlowMemberField, pfc);
        }
        
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
                    SharedFlowController sf = name != null ? (SharedFlowController) sharedFlows.get(name) : null;

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
     * Get a page input that was passed from a Page Flow action as an "action output".
     *
     * @param pageInputName the name of the page input.  This is the same as the name of the "action output".
     * @return the value of the page input, or <code>null</code> if the given one does not exist.
     */
    protected Object getPageInput(String pageInputName) {
        return _pageInputs != null ? _pageInputs.get(pageInputName) : null;
    }

    /**
     * Get the map of all page inputs that was passed from a Page Flow action as "action outputs".
     *
     * @return a Map of page-input-name (String) to page-input-value (Object).
     */
    public Map getPageInputMap() {
        return _pageInputs;
    }

    private CachedFacesBackingInfo getCachedInfo() {
        ClassLevelCache cache = ClassLevelCache.getCache(getClass());
        CachedFacesBackingInfo info = (CachedFacesBackingInfo) cache.getCacheObject(CACHED_INFO_KEY);

        if (info == null) {
            info = new CachedFacesBackingInfo(getClass());
            cache.setCacheObject(CACHED_INFO_KEY, info);
        }

        return info;
    }

    /**
     * Callback that is invoked when this backing bean is restored as the page itself is restored through a
     * {@link org.apache.ti.pageflow.annotations.ti.forward &#64;ti.forward} or
     * {@link org.apache.ti.pageflow.annotations.ti.simpleAction &#64;ti.simpleAction} with
     * {@link org.apache.ti.pageflow.annotations.ti.forward#navigateTo() navigateTo}={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#currentPage ti.NavigateTo.currentPage}
     * or
     * {@link org.apache.ti.pageflow.annotations.ti.forward#navigateTo() navigateTo}={@link org.apache.ti.pageflow.annotations.ti.NavigateTo#currentPage ti.NavigateTo.previousPage}.
     */
    protected void onRestore() {
    }

    /**
     * Restore this bean (set it as the current one from some dormant state).  This is a framework-invoked method that
     * should not normally be called directly.
     */
    public void restore() {
        reinitialize();
        StorageHandler sh = Handlers.get().getStorageHandler();
        String attrName = InternalUtils.getScopedAttrName(InternalConstants.FACES_BACKING_ATTR);
        sh.setAttribute(attrName, this);
        Map newActionOutputs = InternalUtils.getActionOutputMap(false);
        if (newActionOutputs != null) _pageInputs = newActionOutputs;
        onRestore();
    }
}
