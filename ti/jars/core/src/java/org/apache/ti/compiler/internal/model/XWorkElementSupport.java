/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Header:$
 */
package org.apache.ti.compiler.internal.model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class XWorkElementSupport {

    private XWorkModuleConfigModel _parentApp;
    private String _comment;

    public XWorkElementSupport(XWorkModuleConfigModel parentApp) {
        _parentApp = parentApp;
    }

    protected XWorkModuleConfigModel getParentApp() {
        return _parentApp;
    }

    public void setComment(String comment) {
        _comment = comment;
    }

    public String getComment() {
        return _comment;
    }

    public static String getAttr(Node node, String name) {
        Node attr = node.getAttributes().getNamedItem(name);
        return (attr != null ? attr.getNodeValue() : null);
    }

    public static boolean getAttrBool(Node node, String name) {
        String val = getAttr(node, name);
        return (val != null && val.equalsIgnoreCase("true"));  // NOI18N
    }

    protected final void setParentApp(XWorkModuleConfigModel parentApp) {
        _parentApp = parentApp;
    }

    protected final void addParam(XmlModelWriter xw, Element parentElement, String name, String value) {
        if (value != null) {
            Element element = xw.addElement(parentElement, "param");
            element.setAttribute("name", name);
            element.appendChild(xw.getDocument().createTextNode(value));
        }
    }

    protected final void addParam(XmlModelWriter xw, Element parentElement, String name, int value) {
        addParam(xw, parentElement, name, new Integer(value).toString());
    }

    protected final void addParam(XmlModelWriter xw, Element parentElement, String name, boolean value) {
        addParam(xw, parentElement, name, Boolean.toString(value));
    }
}
