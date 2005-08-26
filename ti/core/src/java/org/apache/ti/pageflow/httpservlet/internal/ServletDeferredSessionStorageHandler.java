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
package org.apache.ti.pageflow.httpservlet.internal;

import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.ti.pageflow.internal.DeferredSessionStorageHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class ServletDeferredSessionStorageHandler extends DeferredSessionStorageHandler {

    protected void raiseRemoveEvent(String attrName, Object value) {
        if (value instanceof HttpSessionBindingListener) {
            ServletWebContext webContext = (ServletWebContext) PageFlowActionContext.get().getWebContext();
            HttpSession session = webContext.getRequest().getSession();
            HttpSessionBindingEvent event = new SessionBindingEvent(session, attrName, value);
            ((HttpSessionBindingListener) value).valueUnbound(event);
        }
    }

    public Object getStorageLocation() {
        ServletWebContext webContext = (ServletWebContext) PageFlowActionContext.get().getWebContext();
        return webContext.getRequest().getSession();
    }

    private static final class SessionBindingEvent
            extends HttpSessionBindingEvent {

        public SessionBindingEvent(HttpSession httpSession, String attrName) {
            super(httpSession, attrName);
        }

        public SessionBindingEvent(HttpSession httpSession, String attrName, Object attrVal) {
            super(httpSession, attrName, attrVal);
        }
    }
}
