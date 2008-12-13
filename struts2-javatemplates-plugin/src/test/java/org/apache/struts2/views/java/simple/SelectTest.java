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

import org.apache.struts2.components.TextField;
import org.apache.struts2.components.Select;
import org.easymock.EasyMock;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;

public class SelectTest extends AbstractTestCase {
    private Bean bean1;

    public void testRenderSelect() {
        SelectEx tag = new SelectEx(stack, request, response);
        tag.setName("name_");
        tag.setSize("10");
        tag.setDisabled("true");
        tag.setMultiple("true");
        tag.setTabindex("1");
        tag.setId("id_");
        tag.setCssClass("class");
        tag.setCssStyle("style");
        tag.setTitle("title");

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("select", context);
        String output = writer.getBuffer().toString();
        String expected = s("<select name='name_' size='10' tabindex='1' id='id_' class='class' style='style' title='title'></select>");
        assertEquals(expected, output);
    }

    public void testRenderSelectWithHeader() {
        SelectEx tag = new SelectEx(stack, request, response);

        tag.setList("%{{'key0', 'key1'}}");
        tag.setHeaderKey("%{'key0'}");
        tag.setHeaderValue("%{'val'}");

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("select", context);
        String output = writer.getBuffer().toString();
        String expected = s("<select name=''><option value='key0'>val</option></select>");
        assertEquals(expected, output);
    }

    public void testRenderSelectWithOptions() {
        SelectEx tag = new SelectEx(stack, request, response);

        tag.setList("%{list}");
        tag.setListKey("intField");
        tag.setListValue("stringField");

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("select", context);
        String output = writer.getBuffer().toString();
        String expected = s("<select name=''><option value='1'>val</option></select>");
        assertEquals(expected, output);
    }

    public void testRenderSelectWithMapOptions() {
        SelectEx tag = new SelectEx(stack, request, response);

        tag.setList("%{#{'key0' : 'val'}}");

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("select", context);
        String output = writer.getBuffer().toString();
        String expected = s("<select name=''><option value='key0'>val</option></select>");
        assertEquals(expected, output);
    }

    public void testRenderSelectWithOptionSelected() {
        SelectEx tag = new SelectEx(stack, request, response);

        tag.setList("%{list}");
        tag.setListKey("intField");
        tag.setListValue("stringField");
        tag.setValue("%{'1'}");

        tag.processParams();
        map.putAll(tag.getParameters());
        theme.renderTag("select", context);
        String output = writer.getBuffer().toString();
        String expected = s("<select name='' value='1'><option value='1' selected='selected'>val</option></select>");
        assertEquals(expected, output);
    }

    @Override
    protected void setUpStack() {
        super.setUpStack();
        bean1 = new Bean();
        bean1.setIntField(1);
        bean1.setStringField("val");


        expectFind("'key0'", String.class, "key0");
        expectFind("'key1'", String.class, "key1");
        expectFind("'val'", String.class, "val");
        expectFind("'val1'", String.class, "val1");
        expectFind("'1'", "1");
        expectFind("list", Arrays.asList(bean1));
        expectFind("key", "key0");
        expectFind("value", "val");
        expectFind("#{'key0' : 'val'}", new HashMap() {
            {
                put("key0", "val");
            }
        });

        expectFind("intField", 1);
        expectFind("stringField", "val");
    }

    class SelectEx extends Select {
        public SelectEx(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
            super(stack, request, response);
        }

        public void processParams() {
            //these methods are protected
            evaluateParams();
        }

        public boolean altSyntax() {
            return true;
        }
    }
}
