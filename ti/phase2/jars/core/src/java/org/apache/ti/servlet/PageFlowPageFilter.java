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
package org.apache.ti.servlet;

import org.apache.ti.pageflow.internal.InternalConstants;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.internal.FileUtils;
import org.apache.ti.util.internal.ServletUtils;
import org.apache.ti.util.logging.Logger;
import org.apache.ti.processor.chain.pageflow.ShowView;
import org.apache.ti.processor.RequestProcessor;
import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;


/**
 * Base class for Servlet Filters that run before Page Flow page requests.
 */ 
public abstract class PageFlowPageFilter implements Filter
{
    private static final Logger _log = Logger.getInstance( PageFlowPageFilter.class );
    private static final String PREVENT_CACHE_ATTR = InternalConstants.ATTR_PREFIX + "preventCache";
    
    private RequestProcessor _requestProcessor;
    private ServletContext _servletContext;
    
    
    protected PageFlowPageFilter()
    {
    }
    
    public void init( FilterConfig filterConfig ) throws ServletException
    {
        _servletContext = filterConfig.getServletContext();
        ServletConfiguration servletConfiguration = ServletConfiguration.init(_servletContext);
        _requestProcessor = servletConfiguration.createRequestProcessor(_servletContext, "viewRequestProcessor", null);
    }
    
    protected abstract Set getValidFileExtensions();
    
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
            throws IOException, ServletException
    {
        if ( request instanceof HttpServletRequest && response instanceof HttpServletResponse )
        {
            HttpServletRequest httpRequest = ( HttpServletRequest ) request;
            HttpServletResponse httpResponse = ( HttpServletResponse ) response;
            
            //
            // Don't do the filter if the request is in error.
            //
            Object errStatusCode = request.getAttribute( "javax.servlet.error.status_code" );
            if ( errStatusCode != null )
            {
                if ( _log.isDebugEnabled() )
                {
                    _log.debug( "Request has error status code " + errStatusCode + ".  Skipping filter." );
                }
                
                chain.doFilter(request, response);
                return;           
            }
            
            //
            // If at an earlier stage in the request we determined that we should prevent caching,
            // actually write the appropriate headers to the response now.
            //
            if ( request.getAttribute( PREVENT_CACHE_ATTR ) != null ) ServletUtils.preventCache( httpResponse );
            
            String servletPath = httpRequest.getServletPath();
            String extension = FileUtils.getFileExtension( servletPath );
            Set validFileExtensions = getValidFileExtensions();
            
            if ( validFileExtensions != null && ! validFileExtensions.contains( extension ) )
            {
                if ( _log.isDebugEnabled() )
                {
                    _log.debug( "Path " + servletPath +
                                " does not have an appropriate file extension.  Skipping filter." );
                }
                
                chain.doFilter( request, response );
                return;
            }
            
            if ( _log.isDebugEnabled() ) _log.debug( "Filtering request for path " + servletPath );
            
            FilterChainViewRunner viewRunner = new FilterChainViewRunner(chain);
            viewRunner.activate();
            _requestProcessor.process(new ServletWebContext(_servletContext, httpRequest, httpResponse));
        }
        else
        {
            chain.doFilter( request, response );
        }
    }
    
    private static class FilterChainViewRunner extends ShowView.ViewRunner {
        
        private FilterChain _chain;
        
        public FilterChainViewRunner(FilterChain chain) {
            _chain = chain;
        }
        
        public void runView() throws PageFlowException {
            WebContext webContext = PageFlowActionContext.get().getWebContext();
            assert webContext instanceof ServletWebContext : webContext.getClass().getName();
            ServletWebContext servletWebContext = (ServletWebContext) webContext;
            
            try {
                _chain.doFilter(servletWebContext.getRequest(), servletWebContext.getResponse());
            } catch (ServletException e ) {
                throw new PageFlowException(e);
            } catch (IOException e ) {
                throw new PageFlowException(e);
            }
        }
    }
    
    /**
     * Make sure that when this page is rendered, it will set headers in the response to prevent caching.
     * Because these headers are lost on server forwards, we set a request attribute to cause the headers
     * to be set right before the page is rendered.
     */ 
    static void preventCache( HttpServletRequest request )
    {
        request.setAttribute( PREVENT_CACHE_ATTR, Boolean.TRUE );
    }
    
    public void destroy()
    {
        _requestProcessor.destroy();
        _requestProcessor = null;
    }
}
