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
package org.apache.ti.pageflow.internal;

import org.apache.ti.pageflow.ContainerAdapter;
import org.apache.ti.pageflow.handler.StorageHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * This alternate session storage handler does not write any attribute into the session until the very end of a chain
 * of forwarded requests (i.e., not even at the end of an inner forwarded request).  This allows it to handle multiple
 * concurrent forwarded requests, each of which is modifying the same data, in a more reasonable way.  Basically,
 * each request works in its own snapshot of the session, and the last one to commit is the one whose snapshot wins.
 * This is a better alternative thatn allowing them to interfere with each other in the middle of the request chain.
 */
public class DeferredSessionStorageHandler
        extends DefaultHandler
        implements StorageHandler {

    private static final String CHANGELIST_ATTR = InternalConstants.ATTR_PREFIX + "changedAttrs";
    private static final String FAILOVER_MAP_ATTR = InternalConstants.ATTR_PREFIX + "failoverAttrs";

    private static ThreadLocal _isCommittingChanges =
            new ThreadLocal() {
                public Object initialValue() {
                    return Boolean.FALSE;
                }
            };

    public DeferredSessionStorageHandler() {
    }

    protected void raiseRemoveEvent(String attrName, Object value) {
    }

    public void setAttribute(String attrName, Object value) {
        Map requestScope = PageFlowActionContext.get().getOuterRequestScope();
        Object currentValue = requestScope.get(attrName);
        
        //
        // Emulate a setAttribute on the session: if the value is an HttpSessionBindingListener, invoke its
        // valueUnbound().  Note that we don't currently care about calling valueBound().
        //
        if (currentValue != null && currentValue != value) raiseRemoveEvent(attrName, value);

        requestScope.put(attrName, value);
        getChangedAttributesList(requestScope, true, false).add(attrName);

        HashMap failoverAttrs = getFailoverAttributesMap(requestScope, false, false);
        if (failoverAttrs != null) failoverAttrs.remove(attrName);
    }

    public void removeAttribute(String attrName) {
        Map requestScope = PageFlowActionContext.get().getOuterRequestScope();
        Object currentValue = requestScope.get(attrName);
        
        //
        // Emulate a removeAttribute on the session: if the value is an HttpSessionBindingListener, invoke its
        // valueUnbound().
        //
        if (currentValue != null) raiseRemoveEvent(attrName, currentValue);

        requestScope.remove(attrName);
        getChangedAttributesList(requestScope, true, false).add(attrName);

        HashMap failoverAttrs = getFailoverAttributesMap(requestScope, false, false);
        if (failoverAttrs != null) failoverAttrs.remove(attrName);
    }

    public Object getAttribute(String attributeName) {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        Map requestScope = actionContext.getOuterRequestScope();
        Object val = requestScope.get(attributeName);
        if (val != null) return val;
        //
        // If the attribute isn't present in the request and is in the list of changed attrs, then it was removed.
        // Don't fall back to the session attribute in that case.
        //
        HashSet changedAttrs = getChangedAttributesList(requestScope, false, false);
        if (changedAttrs != null && changedAttrs.contains(attributeName)) return null;
        
        
        //
        // Get the attribute out of the session, and put it into the request.  Until applyChanges is called, this is
        // the value we'll use.
        //
        Map sessionScope = actionContext.getSession();
        if (sessionScope != null) {
            val = sessionScope.get(attributeName);
            if (val != null) requestScope.put(attributeName, val);
        }

        return val;
    }

    public void applyChanges() {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        Map outerRequestScope = actionContext.getOuterRequestScope();
        HashSet changedAttrs = getChangedAttributesList(outerRequestScope, false, true);

        if (changedAttrs != null) {
            Map sessionScope = actionContext.getSession();
            
            //
            // Go through each changed attribute, and either write it to the session or remove it to the session,
            // depending on whether or not it exists in the request.
            //
            for (Iterator i = changedAttrs.iterator(); i.hasNext();) {
                String attrName = (String) i.next();
                Object val = outerRequestScope.get(attrName);

                if (val != null) {
                    //
                    // Write it to the session, but only if the current value isn't already this value.
                    //
                    Object currentValue = sessionScope.get(attrName);

                    if (currentValue != val) {
                        //
                        // This ThreadLocal value allows others (e.g., an HttpSessionBindingListener like
                        // PageFlowManagedObject) that we're in the middle of committing changes to the session.
                        //
                        _isCommittingChanges.set(Boolean.TRUE);

                        try {
                            sessionScope.put(attrName, val);
                        } finally {
                            _isCommittingChanges.set(Boolean.FALSE);
                        }
                    }
                } else {
                    //
                    // This ThreadLocal value allows others (e.g., an HttpSessionBindingListener like
                    // PageFlowManagedObject) that we're in the middle of committing changes to the session.
                    //
                    _isCommittingChanges.set(Boolean.TRUE);

                    try {
                        sessionScope.remove(attrName);
                    } finally {
                        _isCommittingChanges.set(Boolean.FALSE);
                    }
                }
            }
        }
        
        
        //
        // Now go through the attributes we need to ensure-failover on.
        //
        HashMap failoverAttrs = getFailoverAttributesMap(outerRequestScope, false, true);

        if (failoverAttrs != null) {
            ContainerAdapter sa = AdapterManager.getContainerAdapter();

            for (Iterator i = failoverAttrs.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                sa.ensureFailover((String) entry.getKey(), entry.getValue());
            }
        }
    }

    public void ensureFailover(String attributeName, Object value) {
        Map requestScope = PageFlowActionContext.get().getOuterRequestScope();
        getFailoverAttributesMap(requestScope, true, false).put(attributeName, value);
    }

    private static HashSet getChangedAttributesList(Map requestScope, boolean create, boolean remove) {
        HashSet set = (HashSet) requestScope.get(CHANGELIST_ATTR);

        if (set == null && create) {
            set = new HashSet();
            requestScope.put(CHANGELIST_ATTR, set);
        }

        if (set != null && remove) requestScope.remove(CHANGELIST_ATTR);

        return set;
    }

    private static HashMap getFailoverAttributesMap(Map requestScope, boolean create, boolean remove) {
        HashMap map = (HashMap) requestScope.get(FAILOVER_MAP_ATTR);

        if (map == null && create) {
            map = new HashMap();
            requestScope.put(FAILOVER_MAP_ATTR, map);
        }

        if (map != null && remove) requestScope.remove(FAILOVER_MAP_ATTR);

        return map;
    }

    public boolean allowBindingEvent(Object event) {
        return !((Boolean) _isCommittingChanges.get()).booleanValue();
    }

    public Object getStorageLocation() {
        return PageFlowActionContext.get().getSession();
    }
}
