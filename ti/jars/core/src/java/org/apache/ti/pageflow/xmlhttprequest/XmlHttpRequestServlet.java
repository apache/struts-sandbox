/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.ti.pageflow.xmlhttprequest;



/**
 * Servlet to handle XMLHttpRequests sent from pages.
 */
public class XmlHttpRequestServlet {

}

/* TODO: re-add backend XMLHttpRequest support
        extends HttpServlet
{
    private static final Logger logger = Logger.getInstance(XmlHttpRequestServlet.class);

    public void init() throws ServletException
    {
        RequestInterceptorContext.init();
        // TODO: does ErrorCRT really need to be an interceptor?
        RequestInterceptorContext.addInterceptor(new ErrorCRI());
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        //System.err.println("Inside the XmlHppRequestServlet:" + request.getRequestURI());

        // create an XML empty document, that isn't cached on the client
        response.setContentType("text/xml");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        
        ServletContext ctxt = getServletContext();
        RequestInterceptorContext context = new RequestInterceptorContext();
        List interceptors = context.getRequestInterceptors();

        // Register the default URLRewriter
        URLRewriterService.registerURLRewriter(0, new DefaultURLRewriter());

        try
        {
            Interceptors.doPreIntercept(context, interceptors);
        }
        catch (InterceptorException e)
        {
            throw new ServletException(e);
        }
        
        // Note that we're not worrying about post-intercept or whether any of the pre-interceptors cancelled the
        // request, since there's no further processing in the request. 
    }

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
        throws IOException, ServletException
    {
        doGet(request, response);
    }

    class ErrorCRI extends RequestInterceptor
    {
        public void preRequest(RequestInterceptorContext ctxt, InterceptorChain chain) throws InterceptorException
        {
            // Create the command by striping off the context path and the extension
            HttpServletRequest request = ctxt.getRequest();
            String cmd = request.getRequestURI();
            String ctxtPath = request.getContextPath();

            // catch any runtime errors here and return.
            try {
                cmd = cmd.substring(ctxtPath.length() + 1);
                int idx = cmd.lastIndexOf('.');
                if (idx != -1) {
                    cmd = cmd.substring(0, idx);
                }

                if ("netuiError".equals(cmd)) {
                    String error = request.getParameter("error");
                    logger.error("NetUI JavaScript Error:" + error);
                    System.err.println("NetUI JavaScript Error:" + error);
                }
            }
            catch (RuntimeException e) {
                logger.error("Runtime Error creating XmlRequestServlet Command:" + e.getClass().getName(),e);
            }

            chain.continueChain();
        }

        public void postRequest(RequestInterceptorContext context, InterceptorChain chain) throws InterceptorException
        {
            chain.continueChain();
        }
    }
}
*/
