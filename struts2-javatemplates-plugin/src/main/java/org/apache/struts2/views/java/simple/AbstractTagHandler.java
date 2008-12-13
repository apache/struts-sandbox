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
import java.util.Map;

import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.views.java.TagHandler;
import org.apache.struts2.views.java.Attributes;
import org.apache.struts2.views.util.ContextUtil;
import org.apache.struts2.StrutsConstants;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.ActionContext;

public class AbstractTagHandler implements TagHandler {

    protected TagHandler nextTagHandler;
    protected TemplateRenderingContext context;
    protected boolean altSyntax;

    public void characters(String text) throws IOException {
        characters(text, true);
    }

    public void characters(String text, boolean encode) throws IOException {
        if (nextTagHandler != null) {
            nextTagHandler.characters(text, encode);
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
        this.altSyntax = ContextUtil.isUseAltSyntax(context.getStack().getContext());
        processParams();
    }

    protected void processParams() {
    }

    protected String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    protected Object findValue(String expr) {
        if (expr == null) {
            return null;
        }

        ValueStack stack = context.getStack();
        if (altSyntax) {
            // does the expression start with %{ and end with }? if so, just cut it off!
            if (expr.startsWith("%{") && expr.endsWith("}")) {
                expr = expr.substring(2, expr.length() - 1);
            }
        }

        return stack.findValue(expr);
    }

    private Object findValue(String expr, Class toType) {
        ValueStack stack = context.getStack();

        if (altSyntax && toType == String.class) {
            return TextParseUtil.translateVariables('%', expr, stack);
        } else {
            if (altSyntax) {
                // does the expression start with %{ and end with }? if so, just cut it off!
                if (expr.startsWith("%{") && expr.endsWith("}")) {
                    expr = expr.substring(2, expr.length() - 1);
                }
            }

            return stack.findValue(expr, toType);
        }
    }
}
