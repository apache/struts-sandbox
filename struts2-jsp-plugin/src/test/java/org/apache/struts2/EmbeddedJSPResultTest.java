/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

public class EmbeddedJSPResultTest extends TestCase {
    private HttpServletRequest request;
    private MockHttpServletResponse response;
    private MockServletContext context;
    private EmbeddedJSPResult result;

    public void testSimple() throws Exception {
        result.setLocation("org/apache/struts2/simple0.jsp");
        result.execute(null);

        assertEquals("hello", response.getContentAsString());
    }

    public void testTag0() throws Exception {
        result.setLocation("org/apache/struts2/tag0.jsp");
        result.execute(null);

        assertEquals("Thissessionisnotsecure.OtherText", cleanup(response.getContentAsString()));
    }

    public void testIncludeSimple() throws Exception {
        result.setLocation("org/apache/struts2/includes0.jsp");
        result.execute(null);

        assertEquals("helloTest", cleanup(response.getContentAsString()));
    }

    public void testIncludeSimpleWithDirective() throws Exception {
        result.setLocation("org/apache/struts2/includes3.jsp");
        result.execute(null);

        assertEquals("helloTest", cleanup(response.getContentAsString()));
    }

    public void testIncludeWithSubdir() throws Exception {
        result.setLocation("org/apache/struts2/includes1.jsp");
        result.execute(null);

        assertEquals("subTest", cleanup(response.getContentAsString()));
    }

    public void testIncludeWithParam() throws Exception {
        result.setLocation("org/apache/struts2/includes2.jsp");
        result.execute(null);

        assertEquals("JGTest", cleanup(response.getContentAsString()));
    }

    public void testBroken0() throws Exception {
        try {
            result.setLocation("org/apache/struts2/broken0.jsp");
            result.execute(null);
            fail("should have failed with broken jsp");
        } catch (IllegalStateException ex) {
            //ok
        }
    }

    public void testJSTL() throws Exception {
           result.setLocation("org/apache/struts2/jstl.jsp");
           result.execute(null);

           assertEquals("XXXXXXXXXXX", cleanup(response.getContentAsString()));
       }


     public void testCachedInstances() throws InterruptedException {
        ServletCache cache = new ServletCache();
        Servlet servlet1 = cache.get("org/apache/struts2/simple0.jsp");
        Servlet servlet2 = cache.get("org/apache/struts2/simple0.jsp");

        assertSame(servlet1, servlet2);
    }

    private String cleanup(String str) {
        return str.replaceAll("\\s", "");
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        result = new EmbeddedJSPResult();

        request = EasyMock.createNiceMock(HttpServletRequest.class);
        response = new MockHttpServletResponse();
        context = new MockServletContext();

        final Map params = new HashMap();

        HttpSession session = EasyMock.createNiceMock(HttpSession.class);
        EasyMock.replay(session);

        EasyMock.expect(request.getSession()).andReturn(session).anyTimes();
        EasyMock.expect(request.getParameterMap()).andReturn(params).anyTimes();
        EasyMock.expect(request.getParameter("username")).andAnswer(new IAnswer<String>() {
            @Override
            public String answer() throws Throwable {
                return ((String[]) params.get("username"))[0];
            }
        });

        EasyMock.replay(request);

        ActionContext actionContext = new ActionContext(new HashMap<String, Object>());
        ActionContext.setContext(actionContext);
        actionContext.setParameters(params);
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ServletActionContext.setServletContext(context);

        //mock value stack
        Map stackContext = new HashMap();
        ValueStack valueStack = EasyMock.createNiceMock(ValueStack.class);
        EasyMock.expect(valueStack.getContext()).andReturn(stackContext).anyTimes();
        EasyMock.replay(valueStack);

        //mock converter
        XWorkConverter converter = new DummyConverter();

        //mock container
        Container container = EasyMock.createNiceMock(Container.class);
        EasyMock.expect(container.getInstance(XWorkConverter.class)).andReturn(converter).anyTimes();
        EasyMock.replay(container);
        stackContext.put(ActionContext.CONTAINER, container);
        actionContext.setContainer(container);

        actionContext.setValueStack(valueStack);

        //XWorkConverter conv = ((Container)stack.getContext().get(ActionContext.CONTAINER)).getInstance(XWorkConverter.class);

        if (JSPLoader.JSP_DIR.exists())
            FileUtils.forceDelete(JSPLoader.JSP_DIR);
    }
}

//converter has a protected default constructor...meh
class DummyConverter extends XWorkConverter {

}
