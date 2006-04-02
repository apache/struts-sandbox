/*
 *  Copyright 1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.struts.flow.portlet;

import org.apache.struts.flow.core.*;
import javax.portlet.*;
import org.apache.commons.chain.web.WebContext;
import org.apache.commons.chain.web.portlet.PortletWebContext;
import org.apache.struts.flow.core.javascript.fom.FOM_Flow;

import java.util.Map;

/**
 *  Access to Portlet resources
 *
 * @jsname portlet
 */
public class Portlet {
    
    protected FOM_Flow flow = null;
    protected static final Logger logger = Factory.getLogger();

    public Portlet() {
        throw new IllegalStateException("Cannot create new Struts object in a flow script");
    }
            

    /**  Constructor for the JSLog object */
    public Portlet(FOM_Flow flow) {
        this.flow = flow;
    }
    
    private PortletWebContext getContext() {
        WebContext ctx = flow.getWebContext();
        if (ctx instanceof PortletWebContext) {
            return (PortletWebContext)ctx;
        } else {
            throw new IllegalStateException("The web context must be the PortletWebContext");
        }
    }
    
    /**
     *  Gets a map of request parameters as Strings
     */
    public Map getParam() {
        return getContext().getParam();
    }
    
    /**
     *  Gets a map of request parameters as String arrays
     */
    public Map getParamValues() {
        return getContext().getParamValues();
    }
    
    /**
     *  Gets a map of request attributes
     */
    public Map getRequestScope() {
        return getContext().getRequestScope();
    }
    
    /**
     *  Gets a map of session attributes
     */
    public Map getSessionScope() {
        return getContext().getSessionScope();
    }
    
    /**
     *  Gets a map of application attributes
     */
    public Map getApplicationScope() {
        return getContext().getApplicationScope();
    }
    
    /**
     *  Gets the portlet request
     */
    public PortletRequest getRequest() {
        return getContext().getRequest();
    }
    
    /**
     *  Gets the portlet context
     */
    public PortletContext getPortletContext() {
        return getContext().getContext();
    }
}

