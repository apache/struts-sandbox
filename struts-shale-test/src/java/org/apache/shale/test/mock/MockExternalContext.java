/*
 * Copyright 2004 The Apache Software Foundation.
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

package org.apache.shale.test.mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>Mock implementation of <code>ExternalContext</code>.</p>
 *
 * $Id$
 */

public class MockExternalContext extends ExternalContext {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a wrapper instance.</p>
     *
     * @param context <code>ServletContext</code> for this application
     * @param request <code>HttpServetRequest</code> for this request
     * @param response <code>HttpServletResponse</code> for this request
     */
    public MockExternalContext(ServletContext context,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        this.context = context;
        this.request = request;
        this.response = response;
        applicationMap = new MockApplicationMap(context);
        requestMap = new MockRequestMap(request);

    }
    

    // ----------------------------------------------------- Mock Object Methods


    // ------------------------------------------------------ Instance Variables


    private Map applicationMap = null;
    private ServletContext context = null;
    private HttpServletRequest request = null;
    private Map requestMap = null;
    private HttpServletResponse response = null;
    private Map sessionMap = null;


    // ------------------------------------------------- ExternalContext Methods


    public void dispatch(String requestURI)
      throws IOException, FacesException {

        throw new UnsupportedOperationException();

    }

    
    public String encodeActionURL(String sb) {

        throw new UnsupportedOperationException();

    }


    public String encodeNamespace(String aValue) {

        throw new UnsupportedOperationException();

    }


    public String encodeResourceURL(String sb) {

        throw new UnsupportedOperationException();

    }


    public Map getApplicationMap() {

        return this.applicationMap;

    }


    public String getAuthType() {

        return request.getAuthType();

    }


    public Object getContext() {

        return context;

    }
    
    
    public String getInitParameter(String name) {

        return context.getInitParameter(name);

    }


    public Map getInitParameterMap() {

        throw new UnsupportedOperationException();

    }


    public String getRemoteUser() {

        return request.getRemoteUser();

    }


    public Object getRequest() {

        return request;

    }
    
    
    public String getRequestContextPath() {

        return request.getContextPath();

    }


    public Map getRequestCookieMap() {

        throw new UnsupportedOperationException();

    }


    public Map getRequestHeaderMap() {

        throw new UnsupportedOperationException();

    }


    public Map getRequestHeaderValuesMap() {

        throw new UnsupportedOperationException();

    }


    public Locale getRequestLocale() {

        return request.getLocale();

    }
    

    public Iterator getRequestLocales() {

        return new LocalesIterator(request.getLocales());

    }
    

    public Map getRequestMap() {

        return requestMap;

    }


    public Map getRequestParameterMap() {

        throw new UnsupportedOperationException();

    }


    public Iterator getRequestParameterNames() {

        throw new UnsupportedOperationException();

    }


    public Map getRequestParameterValuesMap() {

        throw new UnsupportedOperationException();

    }


    public String getRequestPathInfo() {

        return request.getPathInfo();

    }


    public String getRequestServletPath() {

        return request.getServletPath();

    }


    public URL getResource(String path) throws MalformedURLException {

        throw new UnsupportedOperationException();

    }


    public InputStream getResourceAsStream(String path) {

        throw new UnsupportedOperationException();

    }


    public Set getResourcePaths(String path) {

        throw new UnsupportedOperationException();

    }


    public Object getResponse() {

        return response;

    }
    

    public Object getSession(boolean create) {

        return request.getSession(create);

    }
    

    public Map getSessionMap() {

        if (sessionMap == null) {
            HttpSession session = request.getSession(true);
            sessionMap = new MockSessionMap(session);
        }
        return sessionMap;

    }


    public java.security.Principal getUserPrincipal() {

        return request.getUserPrincipal();

    }


    public boolean isUserInRole(String role) {

        return request.isUserInRole(role);

    }


    public void log(String message) {

        context.log(message);

    }


    public void log(String message, Throwable throwable) {

        context.log(message, throwable);

    }


    public void redirect(String requestURI)
      throws IOException {

        throw new UnsupportedOperationException();

    }

    
    private class LocalesIterator implements Iterator {

	public LocalesIterator(Enumeration locales) {
	    this.locales = locales;
	}

	private Enumeration locales;

	public boolean hasNext() { return locales.hasMoreElements(); }

	public Object next() { return locales.nextElement(); }

	public void remove() { throw new UnsupportedOperationException(); }

    }


}
