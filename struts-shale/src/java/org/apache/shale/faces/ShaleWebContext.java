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
 *
 * $Id$
 */

package org.apache.shale.faces;

import javax.faces.context.FacesContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.chain.web.servlet.ServletWebContext;

/**
 * <p>Commons Chain <code>Context</code> implementation for Shale.</p>
 */
public class ShaleWebContext extends ServletWebContext {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a new context instance with the specified parameters.
     *
     * @param context <code>ServletContext</code> for this application
     * @param request <code>HttpServetRewquest</code> for this request
     * @param response <code>HttpServletResponse</code> for this request
     * @param chain <code>FilterChain</code> for this request
     */
    public ShaleWebContext(ServletContext context,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           FilterChain chain) {

        super(context, request, response);
        this.chain = chain;

    }
    

    // ------------------------------------------------------ Instance Variables


    /**
     * <p>The <code>FilterChain</code> for the current request.
     */
    private FilterChain chain = null;


    /**
     * <p>The <code>FacesContext</code> for the current request (if any).</p>
     */
    private FacesContext facesContext = null;


    // -------------------------------------------------------------- Properties


    /**
     * <p>Return the <code>FilterChain</code> for the current request.</p>
     */
    public FilterChain getChain() {
        return this.chain;
    }


    /**
     * <p>Return the <code>FacesContext</code> for the current request, if any;
     * otherwise, return <code>null</code>.</p>
     */
    public FacesContext getFacesContext() {
        return this.facesContext;
    }


    /**
     * <p>Set the <code>FacesContext</code> for the current request.  This
     * method should only be called by the framework, once it is determined
     * that this is actually a JSF request.</p>
     *
     * @param facesContext The <code>FacesContext</code> for this request
     */
    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }


    /**
     * <p>Set the <code>HttpServletRequest</code> that will be passed on to the
     * remainder of the filter chain.  If this is different from the request
     * originally used to create this context, it must be an implementation of
     * <code>HttpServletRequestWrapper</code> that wraps the original request.</p>
     *
     * <p><strong>NOTE</strong> - Setting ths property is only useful if it
     * occurs in the preprocessing portion of the chain, before the filter
     * chain has been invoked.</p>
     *
     * @param request The request or request wrapper to pass on
     */
    public void setRequest(HttpServletRequest request) {
        if (!(request instanceof HttpServletRequestWrapper)) {
            // FIXME - i18n
            throw new IllegalArgumentException("Must be an HttpServletRequestWrapper");
        }
        initialize(getContext(), request, getResponse());
    }


    /**
     * <p>Set the <code>HttpServletResponse</code> that will be passed on to the
     * remainder of the filter chain.  If this is different from the response
     * originally used to create this context, it must be an implementation of
     * <code>HttpServletResponseWrapper</code> that wraps the original request.</p>
     *
     * <p><strong>NOTE</strong> - Setting ths property is only useful if it
     * occurs in the preprocessing portion of the chain, before the filter
     * chain has been invoked.</p>
     *
     * @param response The response or response wrapper to pass on
     */
    public void setResponse(HttpServletResponse response) {
        if (!(response instanceof HttpServletResponseWrapper)) {
            // FIXME - i18n
            throw new IllegalArgumentException("Must be an HttpServletResponseWrapper");
        }
        initialize(getContext(), getRequest(), response);
    }


}
