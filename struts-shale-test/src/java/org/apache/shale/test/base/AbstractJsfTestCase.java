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

package org.apache.shale.test.base;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.component.UIViewRoot;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.shale.test.mock.MockApplication;
import org.apache.shale.test.mock.MockApplicationFactory;
import org.apache.shale.test.mock.MockExternalContext;
import org.apache.shale.test.mock.MockFacesContext;
import org.apache.shale.test.mock.MockHttpServletRequest;
import org.apache.shale.test.mock.MockHttpServletResponse;
import org.apache.shale.test.mock.MockHttpSession;
import org.apache.shale.test.mock.MockLifecycle;
import org.apache.shale.test.mock.MockRenderKit;
import org.apache.shale.test.mock.MockRenderKitFactory;
import org.apache.shale.test.mock.MockServletConfig;
import org.apache.shale.test.mock.MockServletContext;

/**
 * <p>Abstract JUnit test case base class, which sets up the JavaServer Faces
 * mock object environment for a particular simulated request.  The following
 * protected variables are initialized in the <code>setUp()</code> method, and
 * cleaned up in the <code>tearDown()</code> method:</p>
 * <ul>
 * <li><code>application</code> (<code>MockApplication</code>)</li>
 * <li><code>config</code> (<code>MockServletConfig</code>)</li>
 * <li><code>externalContext</code> (<code>MockExternalContext</code>)</li>
 * <li><code>facesContext</code> (<code>MockFacesContext</code>)</li>
 * <li><code>lifecycle</code> (<code>MockLifecycle</code>)</li>
 * <li><code>request</code> (<code>MockHttpServletRequest</code></li>
 * <li><code>response</code> (<code>MockHttpServletResponse</code>)</li>
 * <li><code>servletContext</code> (<code>MockServletContext</code>)</li>
 * <li><code>session</code> (<code>MockHttpSession</code>)</li>
 * </ul>
 *
 * <p>In addition, appropriate factory classes will have been registered with
 * <code>javax.faces.FactoryFinder</code> for <code>Application</code> and
 * <code>RenderKit</code> instances.  The created <code>FacesContext</code>
 * instance will also have been registered in the apppriate thread local
 * variable, to simulate what a servlet container would do.</p>
 */

public abstract class AbstractJsfTestCase extends TestCase {


    // ------------------------------------------------------------ Constructors


    // Construct a new instance of this test case.
    public AbstractJsfTestCase(String name) {
        super(name);
    }


    // ---------------------------------------------------- Overall Test Methods


    // Set up instance variables required by this test case.
    public void setUp() {

        // Set up Servlet API Objects
        servletContext = new MockServletContext();
        config = new MockServletConfig(servletContext);
        session = new MockHttpSession();
        request = new MockHttpServletRequest(session);
        response = new MockHttpServletResponse();

        // Set up JSF API Objects
	FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY,
				 "org.apache.shale.test.mock.MockApplicationFactory");
	FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY,
				 "org.apache.shale.test.mock.MockRenderKitFactory");

        externalContext =
            new MockExternalContext(servletContext, request, response);
        lifecycle = new MockLifecycle();
        facesContext = new MockFacesContext(externalContext, lifecycle);
	UIViewRoot root = new UIViewRoot();
	root.setViewId("/viewId");
        facesContext.setViewRoot(root);
        ApplicationFactory applicationFactory = (ApplicationFactory)
            FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        application = (MockApplication) applicationFactory.getApplication();
        facesContext.setApplication(application);
        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = new MockRenderKit();
        try {
            renderKitFactory.addRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT,
                                          renderKit);
        } catch (IllegalArgumentException e) {
            ;
        }

    }


    // Return the tests included in this test case.
    public static Test suite() {

        return (new TestSuite(AbstractJsfTestCase.class));

    }


    // Tear down instance variables required by this test case.
    public void tearDown() {

        application = null;
        config = null;
        externalContext = null;
        facesContext = null;
        lifecycle = null;
        request = null;
        response = null;
        servletContext = null;
        session = null;

    }


    // ------------------------------------------------------ Instance Variables


    // Mock object instances for our tests
    protected MockApplication         application = null;
    protected MockServletConfig       config = null;
    protected MockExternalContext     externalContext = null;
    protected MockFacesContext        facesContext = null;
    protected MockLifecycle           lifecycle = null;
    protected MockHttpServletRequest  request = null;
    protected MockHttpServletResponse response = null;
    protected MockServletContext      servletContext = null;
    protected MockHttpSession         session = null;


}
