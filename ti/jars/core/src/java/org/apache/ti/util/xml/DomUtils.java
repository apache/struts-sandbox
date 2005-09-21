/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.ti.util.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class exists simply because DOM is so inconvenient to use.</p>
 */
public final class DomUtils {
    /* do not construct */
    private DomUtils() {
    }

    /**
     * <p>Returns the first child element with the given name. Returns
     * <code>null</code> if not found.</p>
     *
     * @param parent parent element
     * @param name name of the child element
     * @return child element
     */
    public static Element getChildElementByName(Element parent, String name) {
        NodeList children = parent.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getTagName().equals(name)) {
                    return element;
                }
            }
        }

        return null;
    }

    /**
     * <p>Returns a list of child elements with the given
     * name. Returns an empty list if there are no such child
     * elements.</p>
     *
     * @param parent parent element
     * @param name name of the child element
     * @return child elements
     */
    public static List getChildElementsByName(Element parent, String name) {
        List elements = new ArrayList();

        NodeList children = parent.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getTagName().equals(name)) {
                    elements.add(element);
                }
            }
        }

        return elements;
    }

    /**
     * <p>Returns the text value of a child element. Returns
     * <code>null</code> if there is no child element found.</p>
     *
     * @param parent parent element
     * @param name name of the child element
     * @return text value
     */
    public static String getChildElementText(Element parent, String name) {
        // Get children
        List list = DomUtils.getChildElementsByName(parent, name);

        if (list.size() == 1) {
            Element child = (Element) list.get(0);

            StringBuffer buf = new StringBuffer();

            NodeList children = child.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);

                if ((node.getNodeType() == Node.TEXT_NODE) || (node.getNodeType() == Node.CDATA_SECTION_NODE)) {
                    Text text = (Text) node;
                    buf.append(text.getData().trim());
                }
            }

            return buf.toString();
        } else {
            return null;
        }
    }

    /**
     * <p>Returns the text value of a child element. Returns
     * <code>null</code> if there is no child element found.</p>
     *
     * @param element element
     * @return text value
     */
    public static String getElementText(Element element) {
        StringBuffer buf = new StringBuffer();

        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);

            if ((node.getNodeType() == Node.TEXT_NODE) || (node.getNodeType() == Node.CDATA_SECTION_NODE)) {
                Text text = (Text) node;
                buf.append(text.getData().trim());
            }
        }

        return buf.toString();
    }

    /**
     * <p>Returns an array of text values of a child element. Returns
     * <code>null</code> if there is no child element found.</p>
     *
     * @param parent parent element
     * @param name name of the child element
     * @return text value
     */
    public static String[] getChildElementTextArr(Element parent, String name) {
        // Get all the elements
        List children = getChildElementsByName(parent, name);

        String[] str = new String[children.size()];

        for (int i = 0; i < children.size(); i++) {
            Node child = (Node) children.get(i);

            StringBuffer buf = new StringBuffer();

            NodeList nodes = child.getChildNodes();

            for (int j = 0; j < nodes.getLength(); j++) {
                Node node = nodes.item(j);

                if ((node.getNodeType() == Node.TEXT_NODE) || (node.getNodeType() == Node.CDATA_SECTION_NODE)) {
                    Text text = (Text) node;
                    buf.append(text.getData().trim());
                }
            }

            str[i] = buf.toString();
        }

        return str;
    }

    /**
     * <p>Retutns the value of the named attribute of the given
     * element. If there is no such attribute, returns null.</p>
     *
     * @param element element
     * @param name name
     * @return value
     */
    public static String getAttributeValue(Element element, String name) {
        Attr attribute = element.getAttributeNode(name);

        if (attribute == null) {
            return null;
        } else {
            return attribute.getValue();
        }
    }
}
