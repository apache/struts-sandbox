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

import org.apache.ti.pageflow.xwork.PageFlowActionContext;

import java.io.Serializable;

/**
 * Base class for previous-page and previous-action information.
 */
public abstract class PreviousInfo
        implements Serializable {

    private Object _formBean;
    private String _queryString;

    protected PreviousInfo() {
        PageFlowActionContext actionContext = PageFlowActionContext.get();
        _formBean = actionContext.getFormBean();
        _queryString = actionContext.getRequestQueryString();
    }

    /**
     * Get the form bean that was used to initialize the previous page or action.
     *
     * @return the previous Object instance, or <code>null</code> if there was none.
     */
    public Object getFormBean() {
        return _formBean;
    }

    /**
     * Get the query string from the previous page or action request.
     *
     * @return the previous query string, or <code>null</code> if there was none.
     */
    public String getQueryString() {
        return _queryString;
    }

    void setFormBean(Object formBean) {
        _formBean = formBean;
    }

    void setQueryString(String queryString) {
        _queryString = queryString;
    }
}
