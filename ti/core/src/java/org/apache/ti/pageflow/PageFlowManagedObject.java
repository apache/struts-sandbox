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
import org.apache.ti.pageflow.xwork.PageFlowAction;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.logging.Logger;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Base class for Page Flow managed objects (like page flows and JavaServer Faces backing beans).
 */
public abstract class PageFlowManagedObject
        implements Serializable, HttpSessionBindingListener {

    private static final long serialVersionUID = 1;
    private static final Logger _log = Logger.getInstance(PageFlowManagedObject.class);

    /**
     * Creation time.  This is non-transient, so it gets replicated in a cluster.
     */
    private long _createTime;

    protected PageFlowManagedObject() {
        _createTime = System.currentTimeMillis();
    }

    /**
     * Reinitialize the object for a new request.  Used by the framework; normally should not be called directly.
     */
    public void reinitialize() {
    }

    /**
     * Initialize after object creation.  This is a framework-invoked method; it should not normally be called directly.
     */
    public synchronized void create()
            throws Exception {
        reinitialize();
        // TODO: re-add Controls support
//        JavaControlUtils.initJavaControls( this );
        onCreate();
    }

    /**
     * Internal destroy method that is invoked when this object is being removed from the session.  This is a
     * framework-invoked method; it should not normally be called directly.
     */
    void destroy() {
        onDestroy();
        // TODO: re-add Controls support
//        JavaControlUtils.uninitJavaControls( this );
    }

    /**
     * Create-time callback.  Occurs after internal initialization (e.g., Control fields) is done.
     *
     * @throws Exception
     */
    protected void onCreate()
            throws Exception {
    }

    /**
     * Callback that occurs when this object is "destroyed", i.e., removed from the session.
     */
    protected void onDestroy() {
    }

    /**
     * Callback when this object is added to the user session.
     */
    public void valueBound(HttpSessionBindingEvent event) {
    }

    /**
     * Callback when this object is removed from the user session.  Causes {@link #onDestroy} to be called.  This is a
     * framework-invoked method that should not normally be called indirectly.
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        if (Handlers.get().getStorageHandler().allowBindingEvent(event)) {
            destroy();
        }
    }

    /**
     * Remove this instance from the session.
     */
    protected abstract void removeFromSession();

    /**
     * Store this object in the user session, in the appropriate place.  Used by the framework; normally should not be
     * called directly.
     */
    public abstract void persistInSession();

    /**
     * Ensures that any changes to this object will be replicated in a cluster (for failover),
     * even if the replication scheme uses a change-detection algorithm that relies on
     * HttpSession.setAttribute to be aware of changes.  Note that this method is used by the framework
     * and does not need to be called explicitly in most cases.
     */
    public abstract void ensureFailover();

    /**
     * Get the display name for this managed object.
     */
    public abstract String getDisplayName();

    /**
     * Tell whether the given Field is uninitialized.
     *
     * @return <code>true</code> if the field is non-<code>null</code> and its value is <code>null</code>.
     */
    protected boolean fieldIsUninitialized(Field field) {
        try {
            return field != null && field.get(this) == null;
        } catch (IllegalAccessException e) {
            _log.error("Error initializing field " + field.getName() + " in " + getDisplayName(), e);
            return false;
        }
    }

    /**
     * Initialize the given field with an instance.  Mainly useful for the error handling.
     */
    protected void initializeField(Field field, Object instance) {
        if (instance != null) {
            if (_log.isTraceEnabled()) {
                _log.trace("Initializing field " + field.getName() + " in " + getDisplayName() + " with " + instance);
            }

            try {
                field.set(this, instance);
            } catch (IllegalArgumentException e) {
                _log.error("Could not set field " + field.getName() + " on " + getDisplayName() +
                        "; instance is of type " + instance.getClass().getName() + ", field type is "
                        + field.getType().getName());
            } catch (IllegalAccessException e) {
                _log.error("Error initializing field " + field.getName() + " in " + getDisplayName(), e);
            }
        }
    }

    /**
     * Get the time at which this object was created.
     *
     * @return the system time, in milliseconds, at which this object was created.
     */
    public long getCreateTime() {
        return _createTime;
    }

    /**
     * Get the current Struts ActionConfig, which is information from the Struts-XML &lt;action&gt;
     * tag that corresponds to the current action being executed.  This call is only valid
     * during {@link FlowController#execute} (where any user action method is invoked), and during the lifecycle
     * methods {@link FlowController#beforeAction} and {@link FlowController#afterAction}.
     *
     * @return the current Struts ActionConfig.
     * @throws IllegalStateException if this method is invoked outside of action method
     *                               execution (i.e., outside of the call to {@link FlowController#execute}, and outside of
     *                               {@link FlowController#onCreate}, {@link FlowController#beforeAction}, {@link FlowController#afterAction}.
     */
    protected static PageFlowAction getAction() {
        return getContext().getAction();
    }

    protected static PageFlowActionContext getContext() {
        return PageFlowActionContext.get();
    }

    protected static Map getRequestScope() {
        return getContext().getRequestScope();
    }

    protected static Map getSessionScope() {
        return getContext().getSession();
    }
}
