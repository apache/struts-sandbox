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
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * <p>Mock implementation of <code>ServletConfig</code>.</p>
 *
 * $Id$
 */

public class MockServletConfig implements ServletConfig {


    // ------------------------------------------------------------ Constructors


    public MockServletConfig() {
    }


    public MockServletConfig(ServletContext context) {
        setServletContext(context);
    }


    // ----------------------------------------------------- Mock Object Methods


    public void addInitParameter(String name, String value) {

        parameters.put(name, value);

    }


    public void setServletContext(ServletContext context) {

        this.context = context;

    }


    // ------------------------------------------------------ Instance Variables


    private ServletContext context;
    private Hashtable parameters = new Hashtable();


    // --------------------------------------------------- ServletConfig Methods


    public String getInitParameter(String name) {

        return (String) parameters.get(name);

    }


    public Enumeration getInitParameterNames() {

        return parameters.keys();

    }


    public ServletContext getServletContext() {

        return this.context;

    }


    public String getServletName() {

        return "MockServlet";

    }


}
