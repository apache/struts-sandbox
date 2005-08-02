/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.ti.servlet;

import com.opensymphony.xwork.*;
import org.apache.ti.processor.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.*;
import org.apache.commons.chain.web.*;
import org.apache.commons.chain.web.servlet.*;

/**
 * Includes or forwards a view. There are three possible ways the result can be executed: <ul>
 * <p/>
 * <li>If we are in the scope of a JSP (a PageContext is available), PageContext's
 * {@link PageContext#include(String) include} method is called.</li>
 * <p/>
 * <li>If there is no PageContext and we're not in any sort of include (there is no
 * "javax.servlet.include.servlet_path" in the request attributes), then a call to
 * {@link RequestDispatcher#forward(javax.servlet.ServletRequest, javax.servlet.ServletResponse) forward}
 * is made.</li>
 * <p/>
 * <li>Otherwise, {@link RequestDispatcher#include(javax.servlet.ServletRequest, javax.servlet.ServletResponse) include}
 * is called.</li></ul>
 * <p/>
 * This result follows the same rules from {@link WebWorkResultSupport}.
 *
 * @author Patrick Lightbody
 * @see javax.servlet.RequestDispatcher
 */
public class ServletDispatcherResult extends BaseResult {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(ServletDispatcherResult.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Dispatches to the given location. Does its forward via a RequestDispatcher. If the
     * dispatch fails a 404 error will be sent back in the http response.
     *
     * @param finalLocation the location to dispatch to.
     * @param invocation    the execution state of the action
     * @throws Exception if an error occurs. If the dispatch fails the error will go back via the
     *                   HTTP request.
     */
    public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Forwarding to location " + finalLocation);
        }
        
        ServletWebContext ctx = (ServletWebContext) ControllerContext.getContext().getWebContext();

        //PageContext pageContext = ServletActionContext.getPageContext();

        //if (pageContext != null) {
        //    pageContext.include(finalLocation);
        //} else {
            HttpServletRequest request = ctx.getRequest();
            HttpServletResponse response = ctx.getResponse();
            RequestDispatcher dispatcher = request.getRequestDispatcher(finalLocation);

            // if the view doesn't exist, let's do a 404
            if (dispatcher == null) {
                response.sendError(404, "result '" + finalLocation + "' not found");

                return;
            }

            // If we're included, then include the view
            // Otherwise do forward 
            // This allow the page to, for example, set content type 
            if (!response.isCommitted() && (request.getAttribute("javax.servlet.include.servlet_path") == null)) {
                request.setAttribute("ti.view_uri", finalLocation);
                request.setAttribute("ti.request_uri", request.getRequestURI());

                dispatcher.forward(request, response);
            } else {
                dispatcher.include(request, response);
            }
        //}
    }
}
