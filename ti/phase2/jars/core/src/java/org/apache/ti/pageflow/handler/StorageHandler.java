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
package org.apache.ti.pageflow.handler;


/**
 * Handler for storing Page Flow objects.
 */
public interface StorageHandler
        extends Handler {

    /**
     * Set a named attribute.
     *
     * @param attributeName the name of the attribute to set.
     * @param value         the attribute value.
     */
    public void setAttribute(String attributeName, Object value);

    /**
     * Remove a named attribute.
     *
     * @param attributeName the name of the attribute to remove.
     */
    public void removeAttribute(String attributeName);

    /**
     * Get a named attribute.
     *
     * @param attributeName the name of the attribute to get.
     * @return the attribute, or <code>null</code> if there is no such named attribute.
     */
    public Object getAttribute(String attributeName);

    /**
     * Ensure that the given named attribute is replicated in a cluster for session failover, if appropriate.
     *
     * @param attributeName the name of the attribute for which failover should be ensured.
     * @param value         the value of the attribute for which failover should be ensured.
     */
    public void ensureFailover(String attributeName, Object value);

    /**
     * Tell whether a given binding event should be allowed to occur.  This is mainly useful in cases when this
     * handler writes data to some underlying storage (like the <code>HttpSession</code>) at some time other than
     * when {@link #setAttribute} is called, in which case a binding event would be misleading.  Only
     * {@link org.apache.ti.pageflow.PageFlowManagedObject}s pay attention to this.
     *
     * @param event the binding event, e.g., <code>javax.servlet.http.HttpSessionBindingEvent</code>
     * @return <code>true</code> if the event should be processed.
     */
    public boolean allowBindingEvent(Object event);

    public Object getStorageLocation();

    /**
     * Apply any deferred changes, at the end of a chain of requests.
     */
    public void applyChanges();
}
