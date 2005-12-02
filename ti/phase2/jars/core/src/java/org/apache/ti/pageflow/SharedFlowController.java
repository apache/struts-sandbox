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
import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.xwork.PageFlowResult;
import org.apache.ti.util.internal.cache.ClassLevelCache;

/**
 * <p/>
 * Base "shared flow" class for controller logic, exception handlers, and state that can be shared by any number of page
 * flows.  A shared flow is <i>not</i> a page flow; it is used by page flows, but never becomes the "current page flow"
 * (see {@link PageFlowController} for information on page flows and the "current page flow").
 * The class is configured through the
 * {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller} annotation.
 * </p>
 * <p/>
 * <p/>
 * A shared flow comes into existance in one of two ways:
 * <ul>
 * <li>
 * A page flow is hit, and the page flow refers to the shared flow in its
 * {@link org.apache.ti.pageflow.annotations.ti.controller#sharedFlowRefs sharedFlowRefs}
 * annotation attribute, or
 * </li>
 * <li>
 * Any page flow is hit, and the <code>&lt;default-shared-flow-refs&gt;</code> element in
 * /WEB-INF/struts-ti-config.xml declares that this shared flow will be used by all page flows in the web
 * application.
 * </li>
 * </ul>
 * When a shared flow is created, it is stored in the user session.  It is only removed through a call to
 * {@link #remove} or through a call to {@link PageFlowUtils#removeSharedFlow}.
 * </p>
 * <p/>
 * <p/>
 * Shared flow actions are defined with <i>action methods</i> or <i>action annotations</i> that determine the next URI
 * to be displayed, after optionally performing arbitrary logic.  A page or page flow can raise a shared flow action
 * using the pattern <code>"</code><i>shared-flow-name</i><code>.</code><i>action-name</i><code>"</code>.  The shared
 * flow name is the one chosen by the page flow
 * in {@link org.apache.ti.pageflow.annotations.ti.sharedFlowRef#name name}
 * on {@link org.apache.ti.pageflow.annotations.ti.sharedFlowRef &#64;ti.sharedFlowRef}.
 * </p>
 * <p/>
 * <p/>
 * A referenced shared flow gets the chance to handle any uncaught page flow exception.  It declares its exception
 * handling through {@link org.apache.ti.pageflow.annotations.ti.controller#handleExceptions handleExceptions}
 * on {@link org.apache.ti.pageflow.annotations.ti.controller &#64;ti.controller}.
 * </p>
 * <p/>
 * <p/>
 * Properties in the current shared flow instance can be accessed from JSP 2.0-style expressions like this one:
 * <code>${sharedFlow.</code><i>sharedFlowName</i><code>.someProperty}</code>.
 * </p>
 * <p/>
 * <p/>
 * There may only be one shared flow in any package.
 * </p>
 *
 * @see PageFlowController
 */
public abstract class SharedFlowController
        extends FlowController
        implements PageFlowConstants {
    private static final String CACHED_INFO_KEY = "cachedInfo";

    /**
     * Get the namespace for actions in this shared flow.
     *
     * @return the namespace for actions in this shared flow.
     */
    public String getNamespace() {
        ClassLevelCache cache = ClassLevelCache.getCache(getClass());
        String namespace = (String) cache.getCacheObject(CACHED_INFO_KEY);

        if (namespace == null) {
            String className = getClass().getName();
            int lastDot = className.lastIndexOf('.');
            assert lastDot != -1 : className;
            className = className.substring(0, lastDot);
            namespace = '-' + className.replace('.', '/');
            cache.setCacheObject(CACHED_INFO_KEY, namespace);
        }

        return namespace;
    }

    /**
     * Store this object in the user session, in the appropriate place.  Used by the framework; normally should not be
     * called directly.
     */
    public void persistInSession() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        sh.setAttribute(InternalConstants.SHARED_FLOW_ATTR_PREFIX + getClass().getName(), this);
    }

    /**
     * Ensures that any changes to this object will be replicated in a cluster (for failover),
     * even if the replication scheme uses a change-detection algorithm that relies on
     * HttpSession.setAttribute to be aware of changes.  Note that this method is used by the framework
     * and does not need to be called explicitly in most cases.
     */
    public void ensureFailover() {
        StorageHandler sh = Handlers.get().getStorageHandler();
        sh.ensureFailover(InternalConstants.SHARED_FLOW_ATTR_PREFIX + getClass().getName(), this);
    }

    /**
     * Get the path for addressing this object within an application.
     *
     * @return <code>null</code>, since a shared flow is not addressable directly.
     */
    public String getPath() {
        return null;
    }

    /**
     * Get the display name.  The display name for a shared flow is simply the class name.
     *
     * @return the name of the shared flow class.
     */
    public String getDisplayName() {
        return getClass().getName();
    }

    /**
     * Store information about recent pages displayed.  This is a framework-invoked method that should not normally be
     * called directly.
     */
    public void savePreviousPageInfo(PageFlowResult result, Forward fwd, Object form) {
        //
        // Special case: if the given forward has a path to a page in the current pageflow, let that pageflow save
        // the info on this page.  Otherwise, don't ever save any info on what we're forwarding to.
        //
        if ((result != null) && result.isPath()) // i.e., it's a straight forward to a path, not a navigateTo, etc.
         {
            PageFlowController currentJpf = PageFlowUtils.getCurrentPageFlow();

            if (currentJpf != null) {
                if (result.getLocation().startsWith(currentJpf.getNamespace())) {
                    currentJpf.savePreviousPageInfo(result, fwd, form);
                }
            }
        }
    }

    /**
     * Store information about the most recent action invocation.  This is a framework-invoked method that should not
     * normally be called directly
     */
    void savePreviousActionInfo() {
        //
        // Save this previous-action info in the *current page flow*.
        //
        PageFlowController currentJpf = PageFlowUtils.getCurrentPageFlow();

        if (currentJpf != null) {
            currentJpf.savePreviousActionInfo();
        }
    }

    /**
     * Remove this instance from the session.  When inside a shared flow action, {@link #remove} may be called instead.
     */
    protected synchronized void removeFromSession() {
        PageFlowUtils.removeSharedFlow(getClass().getName());
    }
}
