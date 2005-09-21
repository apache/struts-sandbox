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

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.ArrayList;

public abstract class XmlElementSupport {
    private String _comment;

    public void setComment(String comment) {
        _comment = comment;
    }

    public final void writeXML(XmlModelWriter xw, Element element) {
        if (_comment != null) {
            xw.addComment(element, ' ' + _comment + ' ');
        }

        writeToElement(xw, element);
    }

    protected abstract void writeToElement(XmlModelWriter xw, Element element);

    protected final void setElementAttributeMayBeEmpty(Element element, String attrName, String value) {
        if (value != null) {
            String existingAttr = getElementAttribute(element, attrName);

            if ((existingAttr == null) || (existingAttr.length() == 0)) {
                element.setAttribute(attrName, value);
            }
        }
    }

    protected final void setElementAttribute(Element element, String attrName, String value) {
        if ((value != null) && (value.length() > 0)) {
            String existingAttr = getElementAttribute(element, attrName);

            if ((existingAttr == null) || (existingAttr.length() == 0)) {
                element.setAttribute(attrName, value);
            }
        }
    }

    protected final void setElementAttribute(Element element, String attrName, Boolean value) {
        if (value != null) {
            String existingAttr = getElementAttribute(element, attrName);

            if ((existingAttr == null) || (existingAttr.length() == 0)) {
                element.setAttribute(attrName, value.toString());
            }
        }
    }

    /**
     * Gets the attribute value, or <code>null</code> (unlike <code>Element.getAttribute</code>).
     */
    protected String getElementAttribute(Element element, String attrName) {
        Attr attr = element.getAttributeNode(attrName);

        return (attr != null) ? attr.getValue() : null;
    }

    protected final void setElementAttribute(Element element, String attrName, boolean value) {
        if (value) {
            String existingAttr = getElementAttribute(element, attrName);

            if (existingAttr == null) {
                element.setAttribute(attrName, Boolean.toString(value));
            }
        }
    }

    protected final Element findChildElement(XmlModelWriter xw, Element parent, String childName, boolean createIfNotPresent) {
        return findChildElement(xw, parent, childName, null, null, createIfNotPresent);
    }

    protected final Element findChildElementWithChildText(XmlModelWriter xw, Element parent, String childName,
                                                          String childSubElementName, String textValue, boolean createIfNotPresent) {
        Element[] matchingChildren = getChildElements(parent, childName);

        for (int i = 0; i < matchingChildren.length; i++) {
            Element childSubElement = findChildElement(xw, matchingChildren[i], childSubElementName, false);

            if (childSubElement != null) {
                String text = getTextContent(childSubElement);

                if (textValue.equals(text)) {
                    return childSubElement;
                }
            }
        }

        if (createIfNotPresent) {
            Element newChild = xw.addElement(parent, childName);
            xw.addElementWithText(newChild, childSubElementName, textValue);

            return newChild;
        }

        return null;
    }

    protected final Element findChildElement(XmlModelWriter xw, Element parent, String childName, String keyAttributeName,
                                             String keyAttributeValue, boolean createIfNotPresent) {
        NodeList childNodes = parent.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node node = childNodes.item(i);

            if (node instanceof Element) {
                Element childElement = (Element) node;

                if (childName.equals(childElement.getTagName())) {
                    // If there's no target key attribute to match, just return the element.
                    if (keyAttributeName == null) {
                        return childElement;
                    }

                    // Return the element if the key attribute values match (or if both are null).
                    String childElementAttributeValue = getElementAttribute(childElement, keyAttributeName);

                    if (((keyAttributeValue == null) && (childElementAttributeValue == null)) ||
                            ((keyAttributeValue != null) && keyAttributeValue.equals(childElementAttributeValue))) {
                        return childElement;
                    }
                }
            }
        }

        if (createIfNotPresent) {
            Element newChild = xw.getDocument().createElement(childName);
            parent.appendChild(newChild);

            if ((keyAttributeName != null) && (keyAttributeValue != null)) {
                newChild.setAttribute(keyAttributeName, keyAttributeValue);
            }

            return newChild;
        }

        return null;
    }

    protected Element[] getChildElements(Element element, String nameFilter) {
        NodeList children = element.getChildNodes();
        ArrayList list = new ArrayList(children.getLength());

        for (int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);

            if (node instanceof Element) {
                if ((nameFilter == null) || nameFilter.equals(((Element) node).getTagName())) {
                    list.add(node);
                }
            }
        }

        return (Element[]) list.toArray(new Element[list.size()]);
    }

    public static boolean isWhiteSpace(String s) {
        for (int j = 0; j < s.length(); ++j) {
            if (!Character.isWhitespace(s.charAt(j))) {
                return false;
            }
        }

        return true;
    }

    public static String getTextContent(Element element) // TODO: move to a utils class, so XmlModelWriter is independentf
     {
        NodeList children = element.getChildNodes();
        String retVal = null;

        for (int i = 0, len = children.getLength(); i < len; ++i) {
            Node child = children.item(i);

            if (!(child instanceof Text)) {
                return null;
            }

            String text = child.getNodeValue();

            if (!isWhiteSpace(text)) {
                if (retVal != null) {
                    return null;
                }

                retVal = text;
            }
        }

        return retVal;
    }
}
