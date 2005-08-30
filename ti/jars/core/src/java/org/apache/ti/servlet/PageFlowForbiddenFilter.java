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

import org.apache.ti.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * Servlet Filter that sends a specified error code on the response.  Used to prevent Java source from being displayed
 * in the browser, in the case where it is mixed in with web content, and the web content directory itself is being
 * deployed.
 */ 
public class PageFlowForbiddenFilter
        implements Filter
{
    private static final Logger _log = Logger.getInstance( PageFlowForbiddenFilter.class );
    private static final int DEFAULT_RESPONSE_CODE = HttpServletResponse.SC_FORBIDDEN;
    
    private int _responseCode = DEFAULT_RESPONSE_CODE;
    
    public void init( FilterConfig filterConfig ) throws ServletException
    {
        String responseCodeStr = filterConfig.getInitParameter( "response-code" );
        
        if ( responseCodeStr != null )
        {
            try
            {
                _responseCode = Integer.parseInt( responseCodeStr );
            }
            catch ( NumberFormatException e )
            {
                _log.error( "Could not parse response-code \"" + responseCodeStr + "\" for Servlet Filter "
                            + PageFlowForbiddenFilter.class.getName(), e );
            }
        }
    }

    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain )
            throws IOException, ServletException
    {
        if ( ! ( servletResponse instanceof HttpServletResponse ) )
        {
            _log.error( "Servlet Filter " + PageFlowForbiddenFilter.class.getName() + " used against a non-HTTP response: "
                        + servletResponse.getClass().getName() );
            return;
        }
        
        if ( _log.isInfoEnabled() )
        {
            _log.info( "Request for " + ( ( HttpServletRequest ) servletRequest ).getServletPath() + " handled by "
                       + PageFlowForbiddenFilter.class.getName() );
        }
        
        ( ( HttpServletResponse ) servletResponse ).sendError( _responseCode );
    }

    public void destroy()
    {
    }
}
