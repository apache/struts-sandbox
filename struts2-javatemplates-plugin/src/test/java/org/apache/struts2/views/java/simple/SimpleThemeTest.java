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
import junit.framework.TestCase;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.easymock.EasyMock;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SimpleThemeTest extends TestCase {

    private SimpleTheme theme;

    private StringWriter writer;
    private Map map;

    private Template template;
    private Map stackContext;
    private ValueStack stack;
    private TemplateRenderingContext context;

    public void setUp() {
        theme = new SimpleTheme();
        writer = new StringWriter();
        map = new HashMap();

        template = org.easymock.classextension.EasyMock.createMock(Template.class);
        stack = EasyMock.createMock(ValueStack.class);
        stackContext = new HashMap();

        context = new TemplateRenderingContext(template, writer, stack, map, null);
        stackContext.put(Component.COMPONENT_STACK, new Stack());
    }

    public void prepareForTest() {
        EasyMock.expect(stack.getContext()).andReturn(stackContext).anyTimes();
        EasyMock.replay(stack);
        writer.getBuffer().delete(0, writer.getBuffer().length());
    }

    public void testRenderTextField() {
        prepareForTest();
        theme.renderTag("textfield", context);
        assertEquals("<input type=\"text\" name=\"\"></input>", writer.getBuffer().toString());

        EasyMock.reset(stack);
        prepareForTest();
        map.put("name", "name");
        map.put("accesskey", "accesskey");
        theme.renderTag("textfield", context);
        assertEquals("<input type=\"text\" name=\"name\" accesskey=\"accesskey\"></input>", writer.getBuffer().toString());

    }

    public void testRenderSelect() {
        prepareForTest();
        map.put("name", "choose");
        theme.renderTag("select", context);
        assertEquals("<select name=\"choose\"></select>", writer.getBuffer().toString());

        EasyMock.reset(stack);
        prepareForTest();
        map.put("multiple", true);
        theme.renderTag("select", context);
        assertEquals("<select name=\"choose\" multiple=\"multiple\"></select>", writer.getBuffer().toString());
    }

}
