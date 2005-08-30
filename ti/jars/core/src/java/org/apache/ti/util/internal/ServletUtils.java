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
package org.apache.ti.util.internal;

import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintStream;
import java.util.Enumeration;

public class ServletUtils {

    /**
     * Print parameters and attributes in the given request.
     *
     * @param request the current HttpServletRequest.
     * @param output  a PrintStream to which to output request parameters and request/session
     *                attributes; if <code>null</null>, <code>System.err</code> is used.
     */
    public static void dumpRequest(ServletRequest request, PrintStream output) {
        if (output == null) {
            output = System.err;
        }

        output.println("*** ServletRequest " + request);

        if (request instanceof HttpServletRequest) {
            output.println("        uri = " + ((HttpServletRequest) request).getRequestURI());
        }

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            output.println("            parameter " + name + " = " + request.getParameter(name));
        }

        for (Enumeration e = request.getAttributeNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            output.println("            attribute " + name + " = " + request.getAttribute(name));
        }

        if (request instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest) request).getSession(false);

            if (session != null) {
                for (Enumeration e = session.getAttributeNames(); e.hasMoreElements();) {
                    String name = (String) e.nextElement();
                    output.println("            session attribute " + name + " = " + session.getAttribute(name));
                }
            }
        }
    }

    /**
     * Print attributes in the given ServletContext.
     *
     * @param context the current ServletContext.
     * @param output  a PrintStream to which to output ServletContext attributes; if <code>null</null>,
     *                <code>System.err</code> is used.
     */
    public static void dumpServletContext(ServletContext context, PrintStream output) {
        if (output == null) {
            output = System.err;
        }

        output.println("*** ServletContext " + context);

        for (Enumeration e = context.getAttributeNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            output.println("            attribute " + name + " = " + context.getAttribute(name));
        }
    }

    /**
     * Set response headers to prevent caching of the response by the browser.
     */
    public static void preventCache(HttpServletResponse httpResponse) {
        httpResponse.setHeader("Pragma", "No-cache");
        httpResponse.setHeader("Cache-Control", "no-cache,no-store,max-age=0");
        httpResponse.setDateHeader("Expires", 1);
    }
    
    /*
    public static void writeHtml(WebContext webContext, String html, boolean preventCache)
    {
        response.setContentType( "text/html;charset=UTF-8" );
        response.getWriter().println( html );        
    }
    */
    
    public static boolean isSessionExpired(WebContext webContext) {
        // TODO: make this class (or part of it) into a spring bean (SessionUtils or something like that)
        if (webContext instanceof ServletWebContext) {
            HttpServletRequest request = ((ServletWebContext) webContext).getRequest();
            String requestedSessionID = request.getRequestedSessionId();

            if (requestedSessionID != null) {
                HttpSession session = request.getSession(false);
                return session == null || !requestedSessionID.equals(session.getId());
            }
        }

        return false;
    }

    /**
     * Get the base filename of the given URI.
     *
     * @param uri the URI from which to get the base filename.
     * @return a String containing everything after the last slash of the given URI.
     */
    public static String getBaseName(String uri) {
        int lastSlash = uri.lastIndexOf('/');
        assert lastSlash != -1 : uri;
        assert lastSlash < uri.length() - 1 : "URI must not end with a slash: " + uri;
        return uri.substring(lastSlash + 1);
    }

    /**
     * Get the directory path of the given URI.
     *
     * @param uri the URI from which to get the directory path.
     * @return a String containing everything before the last slash of the given URI.
     */
    public static String getDirName(String uri) {
        int lastSlash = uri.lastIndexOf('/');
        assert lastSlash != -1 : uri;
        assert uri.length() > 1 : uri;
        assert lastSlash < uri.length() - 1 : "URI must not end with a slash: " + uri;
        return uri.substring(0, lastSlash);
    }
}
