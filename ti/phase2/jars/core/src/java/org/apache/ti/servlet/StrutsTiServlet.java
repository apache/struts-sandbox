/*
 * $Id$
 *
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.ti.servlet;

import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ti.processor.RequestProcessor;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p><strong>StrutsTiServlet</strong> is the entry point into Struts Ti.</p>
 * @version $Rev$ $Date$
 */
public class StrutsTiServlet extends HttpServlet {

    public static final String SERVLET_MAPPINGS_KEY = "servletMappings";


    protected static Log log = LogFactory.getLog(StrutsTiServlet.class);

    protected List servletMappings = new ArrayList();
    protected RequestProcessor processor = null;

    public void destroy() {

        processor.destroy();
        processor = null;
    }



    /**
     * <p>Initialize this servlet.  Most of the processing has been factored
     * into support methods so that you can override particular functionality
     * at a fairly granular level.</p>
     *
     * @exception ServletException if we cannot configure ourselves correctly
     */
    public void init() throws ServletException {
        super.init();

        initServlet();
        Map params = new HashMap();
        params.put(SERVLET_MAPPINGS_KEY, servletMappings);
        ServletConfiguration servletConfiguration = ServletConfiguration.init(getServletContext());
        processor = servletConfiguration.createRequestProcessor(getServletContext(), "actionRequestProcessor", params);
    }

    /**
     * <p>Perform the standard request processing for this request, and create
     * the corresponding response.</p>
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception ServletException if a servlet exception is thrown
     */
    public void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        processor.process(new ServletWebContext(getServletContext(), request, response));
    }


    /**
     * <p>Remember a servlet mapping from our web application deployment
     * descriptor, if it is for this servlet.</p>
     *
     * @param servletName The name of the servlet being mapped
     * @param urlPattern The URL pattern to which this servlet is mapped
     */
    public void addServletMapping(String servletName, String urlPattern) {

        String myServletName = getServletConfig().getServletName();

        boolean matches = servletName != null && servletName.equals(myServletName);
        if (log.isDebugEnabled()) {
            log.debug("Process servletName=" + servletName
                    + ", urlPattern=" + urlPattern 
                    + (matches ? "(is a match)" : "(not a match)"));
        }
		if (matches) {
            servletMappings.add(urlPattern);
        }

    }

    /**
     * <p>Initialize the servlet mapping under which our controller servlet
     * is being accessed.  This will be used in the <code>&html:form&gt;</code>
     * tag to generate correct destination URLs for form submissions.</p>
     *
     * @throws ServletException if error happens while scanning web.xml
     */
    protected void initServlet() throws ServletException {

        // Prepare a Digester to scan the web application deployment descriptor
        Digester digester = new Digester();
        digester.push(this);
        digester.setNamespaceAware(true);
        digester.setValidating(false);

        // Configure the processing rules that we need
        digester.addCallMethod("web-app/servlet-mapping",
                               "addServletMapping", 2);
        digester.addCallParam("web-app/servlet-mapping/servlet-name", 0);
        digester.addCallParam("web-app/servlet-mapping/url-pattern", 1);

        // Process the web application deployment descriptor
        if (log.isDebugEnabled()) {
            log.debug("Scanning web.xml for controller servlet mapping");
        }

        InputStream input =
            getServletContext().getResourceAsStream("/WEB-INF/web.xml");

        String err = "Unable to process web.xml";
        if (input == null) {
            throw new ServletException(err);
        }

        try {
            digester.parse(input);

        } catch (IOException e) {
            log.error(err, e);
            throw new ServletException(e);

        } catch (SAXException e) {
            log.error(err, e);
            throw new ServletException(e);

        } finally {
            try {
                input.close();
            } catch (IOException e) {
                log.error(err, e);
                throw new ServletException(e);
            }
        }
    }
}
