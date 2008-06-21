/*
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
package org.apache.struts2.jst;

import junit.framework.TestCase;
import com.mockobjects.dynamic.Mock;
import com.mockobjects.dynamic.C;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Map;
import java.util.Collections;

public class JSTResultTest extends TestCase {

    public void testResult() throws Exception {

        testTemplate("/hello.js", "hello");
        testTemplate("/hello-name.js", "hello bob", Collections.<String, Object>singletonMap("name", "bob"));
    }

    private void testTemplate(String templatePath, String response) throws IOException {
        testTemplate(templatePath, response, Collections.<String, Object>emptyMap());
    }

    private void testTemplate(String templatePath, String response, Map<String,Object> context) throws IOException {
        Mock mockServletContext = new Mock(ServletContext.class);
        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        Mock mockValueStack = new Mock(ValueStack.class);


        JSTResult result = new JSTResult((ServletContext) mockServletContext.proxy(), null);

        mockValueStack.matchAndReturn("findValue", C.args(C.eq("_MODIFIERS")), null);
        mockValueStack.matchAndReturn("findValue", C.args(C.eq("defined")), null);
        mockValueStack.matchAndReturn("findValue", C.args(C.eq("_OUT")), null);
        for (Map.Entry<String,Object> entry : context.entrySet()) {
            mockValueStack.expectAndReturn("findValue", C.args(C.eq(entry.getKey())), entry.getValue());
            mockValueStack.expectAndReturn("findValue", C.args(C.eq(entry.getKey())), entry.getValue());
        }
        mockActionInvocation.expectAndReturn("getStack", mockValueStack.proxy());

        mockServletContext.expectAndReturn("getResource", C.args(C.eq(templatePath)), getClass().getResource(templatePath));
        String output = result.processTemplate(templatePath, (ActionInvocation) mockActionInvocation.proxy());
        assertEquals(response, output);
        mockServletContext.verify();
    }

}
