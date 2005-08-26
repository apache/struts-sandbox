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

import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.ti.pageflow.PageFlowException;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

public class ServletPageFlowResult /*extends PageFlowResult*/ {

    private static final Logger _log = Logger.getInstance(ServletPageFlowResult.class);
    
    /* TODO: re-add this
    protected boolean changeScheme( String webappRelativeURI, String scheme, int port, 
    FlowControllerHandlerContext context )
    throws URISyntaxException, IOException, ServletException
    {
    if ( port == -1 )
    {
    if ( _log.isWarnEnabled() )
    {
    _log.warn( "Could not change the scheme to " + scheme + " because the relevant port was not provided "
    + "by the ContainerAdapter." );
    return false;
    }
    }
    
    //
    // First put all request attributes into the session, so they can be added to the
    // redirected request.
    //
    Map attrs = new HashMap();
    String queryString = null;
    ServletContext servletContext = getServletContext();
    HttpServletRequest request = ( ( RequestContext ) context ).getHttpRequest();
    
    for ( Enumeration e = request.getAttributeNames(); e.hasMoreElements(); )
    {
    String name = ( String ) e.nextElement();
    attrs.put( name, request.getAttribute( name ) );
    }
    
    if ( ! attrs.isEmpty() )
    {
    String hash = Integer.toString( request.hashCode() );
    String key = makeRedirectedRequestAttrsKey( webappRelativeURI, hash );
    request.getSession().setAttribute( key, attrs );
    queryString = URLRewriterService.getNamePrefix( servletContext, request, REDIRECT_REQUEST_ATTRS_PARAM )
    + REDIRECT_REQUEST_ATTRS_PARAM + '=' + hash;
    }
    
    
    //
    // Now do the redirect.
    //
    URI redirectURI = new URI( scheme, null, request.getServerName(), port,
    request.getContextPath() + webappRelativeURI,
    queryString, null );
    
    ForwardRedirectHandler fwdRedirectHandler = _handlers.getForwardRedirectHandler();
    fwdRedirectHandler.redirect( context, redirectURI.toString() );
    
    if ( _log.isDebugEnabled() )
    {
    _log.debug( "Redirected to " + redirectURI );
    }
    
    return true;
    }
    */
    
    protected void doForward(String path)
            throws PageFlowException {
        boolean securityRedirected = false;
        
        /* TODO: re-add
        //
        // As in the TilesRequestProcessor.doForward(), if the response has already been commited,
        // do an include instead.
        //
        if ( response.isCommitted() )
        {
            doInclude( path, request, response );
            return;
        }
        
        PageFlowActionContext actionContext = PageFlowActionContext.getContext();        
        FlowController fc = actionContext.getFlowController();
        FlowControllerHandlerContext context = new FlowControllerHandlerContext( fc );
        
        PageflowConfig pageflowConfig = ConfigUtil.getConfig().getPageflowConfig();
        boolean secureForwards = pageflowConfig != null && pageflowConfig.getEnsureSecureForwards();
        
        if ( secureForwards )
        {
            SecurityProtocol sp = PageFlowUtils.getSecurityProtocol( path);
            
            if ( ! sp.equals( SecurityProtocol.UNSPECIFIED ) )
            {
                try
                {
                    if ( request.isSecure() )
                    {
                        if ( sp.equals( SecurityProtocol.UNSECURE ) )
                        {
                            int listenPort = _containerAdapter.getListenPort( request );
                            securityRedirected = changeScheme( path, SCHEME_UNSECURE, listenPort, context );
                        }
                    }
                    else
                    {
                        if ( sp.equals( SecurityProtocol.SECURE ) )
                        {
                            int secureListenPort = _containerAdapter.getSecureListenPort( request );
                            securityRedirected = changeScheme( path, SCHEME_SECURE, secureListenPort, context );
                        }
                    }
                }
                catch ( URISyntaxException e )
                {
                    _log.error( "Bad forward URI " + path, e );
                }
            }
        }
        
        if ( ! securityRedirected )
        {
            super.doForward( path );
        }
        */
    }

    /**
     * An opportunity to process a page forward in a different way than performing a server forward.  The default
     * implementation looks for a file on classpath called
     * META-INF/pageflow-page-servlets/<i>path-to-page</i>.properties (e.g.,
     * "/META-INF/pageflow-page-servlets/foo/bar/hello.jsp.properties").  This file contains mappings from
     * <i>platform-name</i> (the value returned by {@link org.apache.ti.pageflow.ContainerAdapter#getPlatformName}) to the name of a Servlet
     * class that will process the page request.  If the current platform name is not found, the value "default" is
     * tried.  An example file might look like this:
     * <pre>
     *     tomcat=org.apache.jsp.foo.bar.hello_jsp
     *     default=my.servlets.foo.bar.hello
     * </pre>
     *
     * @param pagePath the webapp-relative path to the page, e.g., "/foo/bar/hello.jsp"
     * @return <code>true</code> if the method handled the request, in which case it should not be forwarded.
     */
    protected boolean processPageForward(String pagePath)
            throws PageFlowException {
        /* TODO: re-add this
        Class pageServletClass = ( Class ) _pageServletClasses.get( pagePath );
        
        if ( pageServletClass == null )
        {
            pageServletClass = Void.class;
            ClassLoader cl = DiscoveryUtils.getClassLoader();
            String path = "META-INF/pageflow-page-servlets" + pagePath + ".properties";
            InputStream in = cl.getResourceAsStream( path );
            
            if ( in != null )
            {
                String className = null;
                
                try
                {
                    Properties props = new Properties();
                    props.load( in );
                    className = props.getProperty( _containerAdapter.getPlatformName() );
                    if ( className == null ) className = props.getProperty( "default" );
                    
                    if ( className != null )
                    {
                        pageServletClass = cl.loadClass( className );
                        
                        if ( Servlet.class.isAssignableFrom( pageServletClass ) )
                        {
                            if ( _log.isInfoEnabled() )
                            {
                                _log.info( "Loaded page Servlet class " + className + " for path " + pagePath );
                            }
                        }
                        else
                        {
                            pageServletClass = Void.class;
                            _log.error( "Page Servlet class " + className + " for path " + pagePath
                                    + " does not extend " + Servlet.class.getName() );
                        }
                    }
                }
                catch ( IOException e )
                {
                    _log.error( "Error while reading " + path, e );
                }
                catch ( ClassNotFoundException e )
                {
                    _log.error( "Error while loading page Servlet class " + className, e );
                }
            }
            
            _pageServletClasses.put( pagePath, pageServletClass );
        }
        
        if ( pageServletClass.equals( Void.class ) )
        {
            return false;
        }
        
        try
        {
            Servlet pageServlet = ( Servlet ) pageServletClass.newInstance();
            pageServlet.init( new PageServletConfig( pagePath ) );
            _pageServletFilter.doFilter( request, response, new PageServletFilterChain( pageServlet ) );
            return true;
        }
        catch ( InstantiationException e )
        {
            _log.error( "Error while instantiating page Servlet of type " + pageServletClass.getName(), e );
        }
        catch ( IllegalAccessException e )
        {
            _log.error( "Error while instantiating page Servlet of type " + pageServletClass.getName(), e );
        }
        
        */
        return false;
    }

    /**
     * Used by {@link org.apache.ti.pageflow.PageFlowRequestProcessor#processPageForward} to run a page Servlet.
     */
    private static class PageServletFilterChain implements FilterChain {

        private Servlet _pageServlet;

        public PageServletFilterChain(Servlet pageServlet) {
            _pageServlet = pageServlet;
        }

        public void doFilter(ServletRequest request, ServletResponse response)
                throws IOException, ServletException {
            _pageServlet.service(request, response);
        }
    }

    /**
     * Used by {@link org.apache.ti.pageflow.PageFlowRequestProcessor#processPageForward} to initialize a page Servlet.
     */
    private class PageServletConfig implements ServletConfig {

        private String _pagePath;

        public PageServletConfig(String pagePath) {
            _pagePath = pagePath;
        }

        public String getServletName() {
            return _pagePath;
        }

        public ServletContext getServletContext() {
            ServletWebContext webContext = (ServletWebContext) PageFlowActionContext.get().getWebContext();
            return webContext.getContext();
        }

        public String getInitParameter(String s) {
            return null;
        }

        public Enumeration getInitParameterNames() {
            return Collections.enumeration(Collections.EMPTY_LIST);
        }
    }

}
