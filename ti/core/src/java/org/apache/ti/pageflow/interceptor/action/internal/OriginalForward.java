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
package org.apache.ti.pageflow.interceptor.action.internal;

import org.apache.ti.pageflow.interceptor.action.InterceptorForward;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class OriginalForward
        extends InterceptorForward {

    private Map _savedAttrs;
    private boolean _restoreQueryString = false;

    private static abstract class AttributeWrapper
            implements Serializable {

        private static final long serialVersionUID = 1;

        public abstract Object get();
    }

    private static final class TransientAttributeWrapper
            extends AttributeWrapper {

        private transient Object _object;

        public TransientAttributeWrapper(Object object) {
            _object = object;
        }

        public Object get() {
            return _object;
        }
    }

    private static final class SerializableAttributeWrapper
            extends AttributeWrapper {

        private Object _object;

        public SerializableAttributeWrapper(Object object) {
            _object = object;
        }

        public Object get() {
            return _object;
        }
    }

    public OriginalForward() {
        super();
        saveRequestAttrs();
        
        // By default, we do restore the original query string.
        setRestoreQueryString(true);
    }

    private void saveRequestAttrs() {
        _savedAttrs = new HashMap();

        PageFlowActionContext actionContext = PageFlowActionContext.get();
        Map requestScope = actionContext.getRequestScope();

        for (Iterator i = requestScope.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String attrName = (String) entry.getKey();
            Object attrVal = entry.getValue();

            if (attrVal instanceof Serializable) {
                _savedAttrs.put(attrName, new SerializableAttributeWrapper(attrVal));
            } else {
                _savedAttrs.put(attrName, new TransientAttributeWrapper(attrVal));
            }
        }

        setQueryString(actionContext.getRequestQueryString());
    }

    public void rehydrateRequest() {
        //
        // Restore the request attributes.
        //
        if (_savedAttrs != null) {
            PageFlowActionContext actionContext = PageFlowActionContext.get();
            Map requestScope = actionContext.getRequestScope();

            for (Iterator i = _savedAttrs.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                String attrName = (String) entry.getKey();
                if (requestScope.get(attrName) == null) {
                    Object value = ((AttributeWrapper) entry.getValue()).get();
                    if (value != null) requestScope.put(attrName, value);
                }
            }
        }
        
        //
        // Restore the query string.
        //
        if (doesRestoreQueryString()) {
            String queryString = getQueryString();

            if (queryString != null && queryString.length() > 0) {
                assert queryString.charAt(0) == '?';
                String path = getPath();
                if (path.indexOf('?') != -1) {
                    path += '&' + queryString.substring(1);
                } else {
                    path += queryString;
                }

                setPath(path);
            }
        }
    }

    public boolean doesRestoreQueryString() {
        return _restoreQueryString;
    }

    public void setRestoreQueryString(boolean restoreQueryString) {
        _restoreQueryString = restoreQueryString;
    }
}
