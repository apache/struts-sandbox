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
package org.apache.struts2.views.java.simple;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.TextParseUtil;
import junit.framework.TestCase;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.TextField;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.views.util.ContextUtil;
import org.easymock.EasyMock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TextFieldTest extends AbstractTestCase {

    public void testRenderTextField() {
        TestFieldEx tag = new TestFieldEx(stack, request, response);

        tag.setName("name");
        tag.setValue("val1");
        tag.setSize("10");
        tag.setMaxlength("11");
        tag.setDisabled("true");
        tag.setReadonly("true");
        tag.setTabindex("1");
        tag.setId("id1");
        tag.setCssClass("class1");
        tag.setCssStyle("style1");
        tag.setTitle("title");


        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("textfield", context);
        String output = writer.getBuffer().toString();
        String expected = s("<input type='text' name='name' size='10' maxlength='11' value='val1' tabindex='1' id='id1' class='class1' style='style1' title='title'></input>");
        assertEquals(expected, output);
    }

    public void testRenderTextFieldScriptingAttrs() {
        TestFieldEx tag = new TestFieldEx(stack, request, response);

        applyScriptingAttrs(tag);

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("textfield", context);
        String output = writer.getBuffer().toString();

        assertScriptingAttrs(output);
    }

     public void testRenderTextFieldCommonAttrs() {
        TestFieldEx tag = new TestFieldEx(stack, request, response);

        applyCommonAttrs(tag);

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("textfield", context);
        String output = writer.getBuffer().toString();

        assertCommongAttrs(output);
    }

    class TestFieldEx extends TextField {
        public TestFieldEx(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
            super(stack, request, response);
        }

        public void processParams() {
            //these methods are protected 
            evaluateParams();
            evaluateExtraParams();
        }

        public boolean altSyntax() {
            return true;
        }

        protected Object findValue(String expr, Class toType) {
            return doFindValue(expr, toType);
        }
    }

}
