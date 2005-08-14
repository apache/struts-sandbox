/*
 * Copyright 2003,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ti.processor.chain.servlet;


import org.apache.ti.processor.chain.ChainRequestProcessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.*;
import javax.servlet.*;


/**
 * <p>Select the <code>Locale</code> to be used for this request.</p>
 *
 * @version $Rev: 169091 $ $Date: 2005-05-07 09:11:38 -0700 (Sat, 07 May 2005) $
 */

public class StackAwareRequest extends HttpServletRequestWrapper {


    private static final Log log = LogFactory.getLog(StackAwareRequest.class);

    private ServletRequest wrappedRequest;

    public StackAwareRequest(HttpServletRequest request) {
        super(request);
        wrappedRequest = request;
    }

    public Object getAttribute(String s) {
        Object attribute = super.getAttribute(s);
        if (attribute == null) {
            // If not found, then try the ValueStack
            OgnlValueStack stack = ActionContext.getContext().getValueStack();
            if (stack != null) {
                attribute = stack.findValue(s);
            }
        }
        return attribute;
    }
}

