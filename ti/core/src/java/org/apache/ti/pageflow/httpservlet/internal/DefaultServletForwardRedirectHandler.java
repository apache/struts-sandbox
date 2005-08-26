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

import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.internal.DefaultForwardRedirectHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DefaultServletForwardRedirectHandler extends DefaultForwardRedirectHandler {

    public void redirect(String uri) throws PageFlowException {
        try {
            WebContext webContext = PageFlowActionContext.get().getWebContext();
            assert webContext instanceof ServletWebContext : webContext.getClass().getName();
            ServletWebContext servletWebContext = (ServletWebContext) webContext;
            servletWebContext.getResponse().sendRedirect(uri);
        } catch (IOException e) {
            throw new PageFlowException(e);
        }
    }

    protected void doForward(String uri) throws PageFlowException {
        //
        // Note that we get a RequestDispatcher from the request, not from the ServletContext.
        // The request may be a ScopedRequest, which provides a special RequestDispatcher.
        //
        try {
            WebContext webContext = PageFlowActionContext.get().getWebContext();
            assert webContext instanceof ServletWebContext : webContext.getClass().getName();
            ServletWebContext servletWebContext = (ServletWebContext) webContext;
            HttpServletRequest request = servletWebContext.getRequest();
            request.getRequestDispatcher(uri).forward(request, servletWebContext.getResponse());
        } catch (IOException e) {
            throw new PageFlowException(e);
        } catch (ServletException e) {
            throw new PageFlowException(e);
        }
    }
}
