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

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.shale.test.base.AbstractJsfTestCase;

/**
 * <p>Simple unit tests for Mock Objects that have behavior.</p>
 */

public class MockObjectsTestCase extends AbstractJsfTestCase {


    // ------------------------------------------------------------ Constructors


    // Construct a new instance of this test case.
    public MockObjectsTestCase(String name) {
        super(name);
    }


    // ---------------------------------------------------- Overall Test Methods


    // Set up instance variables required by this test case.
    public void setUp() {

        super.setUp();

        // Set up Servlet API Objects
        servletContext.addInitParameter("appParamName", "appParamValue");
        servletContext.setAttribute("appScopeName", "appScopeValue");
        servletContext.setAttribute("sameKey", "sameKeyAppValue");
        session.setAttribute("sesScopeName", "sesScopeValue");
        session.setAttribute("sameKey", "sameKeySesValue");
        request.setAttribute("reqScopeName", "reqScopeValue");
        request.setAttribute("sameKey", "sameKeyReqValue");
        request.setAttribute("test", new TestMockBean());

    }


    // Return the tests included in this test case.
    public static Test suite() {

        return (new TestSuite(MockObjectsTestCase.class));

    }


    // Tear down instance variables required by this test case.
    public void tearDown() {


        super.tearDown();

    }


    // ------------------------------------------------------ Instance Variables


    // ------------------------------------------------- Individual Test Methods


    public void testMethodBindingGetTypePositive() throws Exception {

        Class argsString[] = new Class[] { String.class };
        Class argsNone[] = new Class[0];

        checkMethodBindingGetType("test.getCommand", argsNone, String.class);
        checkMethodBindingGetType("test.setCommand", argsString,  null);
        checkMethodBindingGetType("test.getInput", argsNone, String.class);
        checkMethodBindingGetType("test.setInput", argsString, null);
        checkMethodBindingGetType("test.getOutput", argsNone, String.class);
        checkMethodBindingGetType("test.setOutput", argsString, null);
        checkMethodBindingGetType("test.combine", argsNone, String.class);

    }


    public void testMethodBindingInvokePositive() throws Exception {

        TestMockBean bean = (TestMockBean) request.getAttribute("test");
        MethodBinding mb = null;
        Class argsString[] = new Class[] { String.class };
        Class argsNone[] = new Class[0];
        assertEquals("::", bean.combine());

        mb = application.createMethodBinding("test.setCommand", argsString);
        mb.invoke(facesContext, new String[] { "command" });
        assertEquals("command", bean.getCommand());
        mb = application.createMethodBinding("test.setInput", argsString);
        mb.invoke(facesContext, new String[] { "input" });
        assertEquals("input", bean.getInput());
        mb = application.createMethodBinding("test.setOutput", argsString);
        mb.invoke(facesContext, new String[] { "output" });
        assertEquals("output", bean.getOutput());
        mb = application.createMethodBinding("test.combine", null);
        assertEquals("command:input:output", bean.combine());
        assertEquals("command:input:output", mb.invoke(facesContext, null));

    }


    // Positive tests for ValueBinding.getValue()
    public void testValueBindingGetValuePositive() throws Exception {

        // Implicit search
        checkValueBindingGetValue("appScopeName", "appScopeValue");
        checkValueBindingGetValue("sesScopeName", "sesScopeValue");
        checkValueBindingGetValue("reqScopeName", "reqScopeValue");
        checkValueBindingGetValue("sameKey", "sameKeyReqValue"); // Req scope

        // Explicit scope search
        checkValueBindingGetValue("applicationScope.appScopeName",
                                  "appScopeValue");
        checkValueBindingGetValue("applicationScope.sameKey",
                                  "sameKeyAppValue");
        checkValueBindingGetValue("sessionScope.sesScopeName",
                                  "sesScopeValue");
        checkValueBindingGetValue("sessionScope.sameKey",
                                  "sameKeySesValue");
        checkValueBindingGetValue("requestScope.reqScopeName",
                                  "reqScopeValue");
        checkValueBindingGetValue("requestScope.sameKey",
                                  "sameKeyReqValue");

    }


    // Positive tests for ValueBinding.putValue()
    public void testValueBindingPutValuePositive() throws Exception {

        ValueBinding vb = null;

        // New top-level variable
        assertNull(request.getAttribute("newSimpleName"));
        assertNull(session.getAttribute("newSimpleName"));
        assertNull(servletContext.getAttribute("newSimpleName"));
        vb = application.createValueBinding("newSimpleName");
        vb.setValue(facesContext, "newSimpleValue");
        assertEquals("newSimpleValue", request.getAttribute("newSimpleName"));
        assertNull(session.getAttribute("newSimpleName"));
        assertNull(servletContext.getAttribute("newSimpleName"));

        // New request-scope variable
        assertNull(request.getAttribute("newReqName"));
        assertNull(session.getAttribute("newReqName"));
        assertNull(servletContext.getAttribute("newReqName"));
        vb = application.createValueBinding("requestScope.newReqName");
        vb.setValue(facesContext, "newReqValue");
        assertEquals("newReqValue", request.getAttribute("newReqName"));
        assertNull(session.getAttribute("newReqName"));
        assertNull(servletContext.getAttribute("newReqName"));

        // New session-scope variable
        assertNull(request.getAttribute("newSesName"));
        assertNull(session.getAttribute("newSesName"));
        assertNull(servletContext.getAttribute("newSesName"));
        vb = application.createValueBinding("sessionScope.newSesName");
        vb.setValue(facesContext, "newSesValue");
        assertNull(request.getAttribute("newSesName"));
        assertEquals("newSesValue", session.getAttribute("newSesName"));
        assertNull(servletContext.getAttribute("newSesName"));

        // New application-scope variable
        assertNull(request.getAttribute("newAppName"));
        assertNull(session.getAttribute("newAppName"));
        assertNull(servletContext.getAttribute("newAppName"));
        vb = application.createValueBinding("applicationScope.newAppName");
        vb.setValue(facesContext, "newAppValue");
        assertNull(request.getAttribute("newAppName"));
        assertNull(session.getAttribute("newAppName"));
        assertEquals("newAppValue", servletContext.getAttribute("newAppName"));

        // Old top-level variable (just created)
        assertEquals("newSimpleValue", request.getAttribute("newSimpleName"));
        assertNull(session.getAttribute("newSimpleName"));
        assertNull(servletContext.getAttribute("newSimpleName"));
        vb = application.createValueBinding("newSimpleName");
        vb.setValue(facesContext, "newerSimpleValue");
        assertEquals("newerSimpleValue", request.getAttribute("newSimpleName"));
        assertNull(session.getAttribute("newSimpleName"));
        assertNull(servletContext.getAttribute("newSimpleName"));

        // Old hierarchically found variable
        assertEquals("sameKeyAppValue", servletContext.getAttribute("sameKey"));
        assertEquals("sameKeySesValue", session.getAttribute("sameKey"));
        assertEquals("sameKeyReqValue", request.getAttribute("sameKey"));
        vb = application.createValueBinding("sameKey");
        vb.setValue(facesContext, "sameKeyNewValue");
        assertEquals("sameKeyAppValue", servletContext.getAttribute("sameKey"));
        assertEquals("sameKeySesValue", session.getAttribute("sameKey"));
        assertEquals("sameKeyNewValue", request.getAttribute("sameKey"));


    }


    // --------------------------------------------------------- Private Methods


    private void checkMethodBindingGetType(String ref, Class params[],
                                           Class expected) throws Exception {

        MethodBinding mb = application.createMethodBinding(ref, params);
        assertNotNull("MethodBinding[" + ref + "] exists", mb);
        assertEquals("MethodBinding[" + ref + "] type",
                     expected,
                     mb.getType(facesContext));

    }


    private void checkValueBindingGetValue(String ref, Object expected) {

        ValueBinding vb = application.createValueBinding(ref);
        assertNotNull("ValueBinding[" + ref + "] exists", vb);
        assertEquals("ValueBinding[" + ref + "] value",
                     expected,
                     vb.getValue(facesContext));

    }


}
