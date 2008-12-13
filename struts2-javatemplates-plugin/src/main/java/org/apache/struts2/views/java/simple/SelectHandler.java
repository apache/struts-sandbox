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

public class SelectHandler extends AbstractTagHandler implements TagGenerator {
    private Writer writer;

    @Override
    public void setup(TemplateRenderingContext context) {
        super.setup(context);
        this.writer = context.getWriter();
    }

    public void generate() throws IOException {
        Map<String,Object> params = context.getParameters();
        Attributes a = new Attributes();

        Object value = params.get("nameValue");

        a.addDefaultToEmpty("name", params.get("name"))
         .addIfExists("size", params.get("size"))
         .addIfExists("value", value, false)
         .addIfTrue("disabled", params.get("disabled"))
         .addIfTrue("readonly", params.get("readonly"))
         .addIfTrue("multiple", params.get("multiple"))
         .addIfExists("tabindex", params.get("tagindex"))
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

        super.end("select");
    }

    private void writeOption(String value, String text, boolean selected) throws IOException {
        writer.write("<option value=\"");
        writer.write(TextUtil.escapeHTML(value));
        writer.write("\"");
        if (selected)
            writer.write(" selected=\"selected\" ");
        writer.write(">");        
        writer.write(TextUtil.escapeHTML(text));
        writer.write("</option>");
    }
}
