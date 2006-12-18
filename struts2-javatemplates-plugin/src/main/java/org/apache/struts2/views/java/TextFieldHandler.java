/*
 * $Id: BaseTemplateEngine.java 471756 2006-11-06 15:01:43Z husted $
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
package org.apache.struts2.views.java;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.components.Property;
import org.apache.struts2.components.template.TemplateRenderingContext;

public class TextFieldHandler extends AbstractTagHandler implements TagGenerator {

    public void generate() throws IOException {
        Map<String,Object> params = context.getParameters();
        Attributes a = new Attributes();
        a.put("type", "text");
        
        a.addDefaultToEmpty("name", params.get("name"))
         .addIfExists("size", params.get("size"))
         .addIfExists("maxlength", params.get("maxlength"))
         .addIfExists("value", params.get("nameValue"), false)
         .addIfTrue("disabled", params.get("disabled"))
         .addIfTrue("readonly", params.get("readonly"))
         .addIfExists("tabindex", params.get("tagindex"))
         .addIfExists("id", params.get("id"))
         .addIfExists("class", params.get("cssClass"))
         .addIfExists("style", params.get("cssStyle"))
         .addIfExists("title", params.get("title"));
        super.start("input", a);
        super.end("input");
    }
    
    private String evalProperty(Object rawValue) {
        Property prop = new Property(context.getStack());
        prop.setValue(rawValue.toString());
        StringWriter writer = new StringWriter();
        prop.start(writer);
        return writer.toString();
    }
    
}
