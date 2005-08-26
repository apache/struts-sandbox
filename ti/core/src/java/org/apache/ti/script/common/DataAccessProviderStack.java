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
package org.apache.ti.script.common;

import javax.servlet.jsp.JspContext;
import java.util.Stack;

public class DataAccessProviderStack {

    private static final String KEY = "org.apache.ti.script.common.DataAccessProviderStack";

    private Stack _stack = null;

    public static final void addDataAccessProvider(IDataAccessProvider provider, JspContext jspContext) {
        assert jspContext != null;

        DataAccessProviderBean bean = new DataAccessProviderBean(provider);

        Object val = jspContext.getAttribute(KEY);
        DataAccessProviderStack curStack = null;
        if (val == null) {
            curStack = new DataAccessProviderStack();

            jspContext.setAttribute(KEY, curStack);
        } else
            curStack = (DataAccessProviderStack) val;

        curStack.push(bean);

        jspContext.setAttribute("container", bean);

        return;
    }

    public static final DataAccessProviderBean removeDataAccessProvider(JspContext jspContext) {
        assert jspContext != null;

        Object val = jspContext.getAttribute(KEY);
        if (val != null) {
            DataAccessProviderStack curStack = (DataAccessProviderStack) val;
            DataAccessProviderBean lastTop = curStack.pop();

            if (!curStack.isEmpty())
                jspContext.setAttribute("container", curStack.peek());
            else
                jspContext.removeAttribute("container");

            return lastTop;
        }

        // todo: should this thrown an IllegalStateException?

        return null;
    }

    public DataAccessProviderStack() {
        _stack = new Stack();
    }

    public boolean isEmpty() {
        return _stack.empty();
    }

    public DataAccessProviderBean peek() {
        return (DataAccessProviderBean) _stack.peek();
    }

    public DataAccessProviderBean pop() {
        return (DataAccessProviderBean) _stack.pop();
    }

    public void push(DataAccessProviderBean bean) {
        _stack.push(bean);
    }

}
