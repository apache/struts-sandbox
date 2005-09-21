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

import org.apache.ti.compiler.internal.LocalFileEntityResolver;

import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlModelWriter {
    private static final int INDENT_LEN = 2;
    private Document _doc;
    private String _rootName;
    private String _systemID;
    private String _publicID;

    public XmlModelWriter(File starterFile, String rootName, String publicID, String systemID, String headerComment)
            throws XmlModelWriterException, IOException {
        assert rootName != null;
        _rootName = rootName;
        _systemID = systemID;
        _publicID = publicID;

        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            builder.setEntityResolver(LocalFileEntityResolver.getInstance());

            if ((starterFile != null) && starterFile.canRead()) {
                _doc = builder.parse(starterFile);
            } else {
                DOMImplementation impl = builder.getDOMImplementation();
                DocumentType docType = impl.createDocumentType(_rootName, _publicID, _systemID);
                _doc = impl.createDocument(null, _rootName, docType);
            }

            if (headerComment != null) {
                Element root = _doc.getDocumentElement();
                Comment comment = _doc.createComment(headerComment);
                root.insertBefore(comment, root.getFirstChild());
            }
        } catch (ParserConfigurationException e) {
            throw new XmlModelWriterException(e);
        } catch (SAXException e) {
            throw new XmlModelWriterException(e);
        }
    }

    /**
     * Write the XML to a stream, without using standard APIs.  This appears to be about ten times as fast (for our
     * simple purposes) as using a Transformer ({@link #write}), and it avoids JDK and environment issues with obtaining
     * a TransformerFactory.
     */
    public void simpleFastWrite(Writer out) throws IOException {
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        if ((_publicID != null) && (_systemID != null)) {
            out.write("<!DOCTYPE ");
            out.write(_rootName);
            out.write(" PUBLIC \"");
            out.write(_publicID);
            out.write("\" \"");
            out.write(_systemID);
            out.write("\">\n");
        }

        writeElement(out, _doc.getDocumentElement(), 0);
    }

    /**
     * Write the XML to a stream, using standard APIs.  This appears to be about ten times slower than our custom writer
     * ({@link #simpleFastWrite}).
     */
    public void write(Writer out) throws XmlModelWriterException, IOException {
        try {
            DOMSource domSource = new DOMSource(_doc);
            StreamResult streamResult = new StreamResult(out);
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", new Integer(INDENT_LEN));

            Transformer serializer = tf.newTransformer();

            if (_publicID != null) {
                serializer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, _publicID);
            }

            if (_systemID != null) {
                serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, _systemID);
            }

            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            throw new XmlModelWriterException(e);
        }
    }

    public final Document getDocument() {
        return _doc;
    }

    public final Element getRootElement() {
        return _doc.getDocumentElement();
    }

    public final Element addElement(Element parent, String tagName) {
        Element element = _doc.createElement(tagName);
        parent.appendChild(element);

        return element;
    }

    public final Element addElementWithText(Element parent, String tagName, String text) {
        Element element = addElement(parent, tagName);
        Text textNode = _doc.createTextNode(text);
        element.appendChild(textNode);

        return element;
    }

    public final void addComment(Element parent, String commentText) {
        if (commentText != null) {
            Comment comment = _doc.createComment(commentText);
            parent.appendChild(comment);
        }
    }

    private static void doIndent(Writer out, int indent)
            throws IOException {
        for (int i = 0; i < indent; ++i) {
            out.write(' ');
        }
    }

    private static void writeElement(Writer out, Element element, int indent)
            throws IOException {
        doIndent(out, indent);
        out.write('<');
        out.write(element.getTagName());

        NamedNodeMap attrs = element.getAttributes();

        for (int i = 0, len = attrs.getLength(); i < len; ++i) {
            Node attr = attrs.item(i);
            out.write(' ');
            out.write(attr.getNodeName());
            out.write("=\"");
            out.write(attr.getNodeValue());
            out.write('"');
        }

        String textContent = XmlElementSupport.getTextContent(element);
        NodeList children = element.getChildNodes();
        int childCount = children.getLength();

        if (textContent != null) {
            out.write('>');
            out.write(textContent);
            out.write("</");
            out.write(element.getTagName());
            out.write(">\n");
        } else if (childCount > 0) {
            out.write(">\n");

            for (int i = 0; i < childCount; ++i) {
                Node child = children.item(i);

                if (child instanceof Comment) {
                    writeComment(out, (Comment) child, indent + INDENT_LEN);
                } else if (child instanceof Text) {
                    assert XmlElementSupport.isWhiteSpace(child.getNodeValue()) : "expected only whitespace: " +
                    child.getNodeValue();
                } else {
                    assert child instanceof Element : child.getClass().getName();
                    writeElement(out, (Element) child, indent + INDENT_LEN);
                }
            }

            doIndent(out, indent);
            out.write("</");
            out.write(element.getTagName());
            out.write(">\n");
        } else {
            out.write("/>\n");
        }
    }

    private static void writeComment(Writer out, Comment comment, int indent)
            throws IOException {
        doIndent(out, indent);
        out.write("<!--");
        out.write(comment.getNodeValue());
        out.write("-->\n");
    }
}
