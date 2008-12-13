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

import org.apache.struts2.views.java.TagGenerator;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.util.TextUtil;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.util.ContainUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.List;

import com.opensymphony.xwork2.util.ValueStack;

public class SelectHandler extends AbstractTagHandler implements TagGenerator {
    private Writer writer;

    @Override
    public void setup(TemplateRenderingContext context) {
        super.setup(context);
        this.writer = context.getWriter();
    }

    public void generate() throws IOException {
        Map<String, Object> params = context.getParameters();
        Attributes a = new Attributes();

        Object value = params.get("nameValue");

        a.addDefaultToEmpty("name", params.get("name"))
                .addIfExists("size", params.get("size"))
                .addIfExists("value", value, false)
                .addIfTrue("disabled", params.get("disabled"))
                .addIfTrue("readonly", params.get("readonly"))
                .addIfTrue("multiple", params.get("multiple"))
                .addIfExists("tabindex", params.get("tabindex"))
                .addIfExists("id", params.get("id"))
                .addIfExists("class", params.get("cssClass"))
                .addIfExists("style", params.get("cssStyle"))
                .addIfExists("title", params.get("title"));
        super.start("select", a);

        //options

        //header
        String headerKey = (String) params.get("headerKey");
        String headerValue = (String) params.get("headerValue");
        if (headerKey != null && headerValue != null) {
            boolean selected = ContainUtil.contains(value, params.get("headerKey"));
            writeOption(headerKey, headerValue, selected);
        }

        List list = (List) params.get("list");
        String listKey = (String) params.get("listKey");
        String listValue = (String) params.get("listValue");
        ValueStack stack = this.context.getStack();
        if (list != null) {
            for (Object item : list) {
                stack.push(item);

                //key
                Object itemKey = findValue(listKey != null ? listKey : "top");
                String itemKeyStr = itemKey != null ? itemKey.toString() : "";
                //value
                Object itemValue = findValue(listValue != null ? listValue : "top");
                String itemValueStr = itemValue != null ? itemValue.toString() : "";

                boolean selected = ContainUtil.contains(value, params.get(itemKey));
                writeOption(itemKeyStr, itemValueStr, selected);

                stack.pop();
            }
        }

        super.end("select");
    }

    private void writeOption(String value, String text, boolean selected) throws IOException {
        Attributes attrs = new Attributes();
        attrs.addIfExists("value", value)
                .addIfTrue("selected", selected);
        start("option", attrs);
        characters(text);
        end("option");
    }
}
