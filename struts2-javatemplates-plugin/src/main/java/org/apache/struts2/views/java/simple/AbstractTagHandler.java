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

import java.io.IOException;

import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.views.java.TagHandler;
import org.apache.struts2.views.java.Attributes;

public class AbstractTagHandler implements TagHandler {
    
    protected TagHandler nextTagHandler;
    protected TemplateRenderingContext context;

    public void characters(String text) throws IOException {
        if (nextTagHandler != null) {
            nextTagHandler.characters(text);
        }

    }

    public void end(String name) throws IOException {
        if (nextTagHandler != null) {
            nextTagHandler.end(name);
        }

    }

    public void setNext(TagHandler next) {
        this.nextTagHandler = next;
    }

    public void start(String name, Attributes a) throws IOException {
        if (nextTagHandler != null) {
            nextTagHandler.start(name, a);
        }

    }

    public void setup(TemplateRenderingContext context) {
        this.context = context;
    }

}
