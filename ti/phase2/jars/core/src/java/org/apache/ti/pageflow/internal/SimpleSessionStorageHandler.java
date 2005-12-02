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

import org.apache.ti.pageflow.handler.StorageHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;

import java.util.Map;

/**
 * This storage handler simply puts/gets attributes in the session.  It does not do anything to support multiple
 * concurrent forwarded requests that are simultaneously modifying session data.
 *
 * @see DeferredSessionStorageHandler
 */
public class SimpleSessionStorageHandler
        extends DefaultHandler
        implements StorageHandler {

    public SimpleSessionStorageHandler() {
    }

    public void setAttribute(String attributeName, Object value) {
        Map sessionScope = PageFlowActionContext.get().getSession();
        sessionScope.put(attributeName, value);
    }

    public void removeAttribute(String attributeName) {
        Map sessionScope = PageFlowActionContext.get().getSession();
        sessionScope.remove(attributeName);
    }

    public Object getAttribute(String attributeName) {
        Map sessionScope = PageFlowActionContext.get().getSession();
        return sessionScope.get(attributeName);
    }

    public void ensureFailover(String attributeName, Object value) {
        AdapterManager.getContainerAdapter().ensureFailover(attributeName, value);
    }

    public boolean allowBindingEvent(Object event) {
        return true;
    }

    public Object getStorageLocation() {
        // TODO: create a Servlet version of this class that returns the actual session object
        return PageFlowActionContext.get().getSession();
    }

    public void applyChanges() {
    }
}
