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
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <p>Mock implementation of <code>Servlet</code>.</p>
 *
 * $Id$
 */

public class MockServlet implements Servlet {


    // ------------------------------------------------------------ Constructors


    public MockServlet() {
    }


    public MockServlet(ServletConfig config) throws ServletException {
        init(config);
    }


    // ----------------------------------------------------- Mock Object Methods


    public void setServletConfig(ServletConfig config) {

        this.config = config;

    }


    // ------------------------------------------------------ Instance Variables


    private ServletConfig config;


    // --------------------------------------------------------- Servlet Methods


    public void destroy() {
    }


    public ServletConfig getServletConfig() {

        return this.config;

    }


    public String getServletInfo() {

        return "MockServlet";

    }


    public void init(ServletConfig config) throws ServletException {

        this.config = config;

    }



    public void service(ServletRequest request, ServletResponse response)
      throws IOException, ServletException {

        throw new UnsupportedOperationException();

    }


}
