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

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class XmlModelWriter {

    private Document _doc;
    private String _systemID;
    private String _publicID;

    public XmlModelWriter(File starterFile, String rootName, String publicID, String systemID, String headerComment)
            throws XmlModelWriterException, IOException {
        _systemID = systemID;
        _publicID = publicID;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            if (starterFile != null && starterFile.canRead()) {
                _doc = db.parse(starterFile);
            } else {
                DOMImplementation impl = db.getDOMImplementation();
                DocumentType docType = impl.createDocumentType(rootName, _publicID, _systemID);
                _doc = impl.createDocument(null, rootName, docType);
            }

            if (headerComment != null) {
                Element root = _doc.getDocumentElement();
                Comment comment = _doc.createComment(headerComment);
                root.insertBefore(comment, root.getFirstChild());
            }
        }
        catch (ParserConfigurationException e) {
            throw new XmlModelWriterException(e);
        }
        catch (SAXException e) {
            throw new XmlModelWriterException(e);
        }
    }

    public void write(Writer writer)
            throws XmlModelWriterException, IOException {
        try {
            DOMSource domSource = new DOMSource(_doc);
            StreamResult streamResult = new StreamResult(writer);
            //TransformerFactory tf = TransformerFactory.newInstance();
            // TODO: how to make the above statement work when running under ant?  Causes the following exception:
            // javax.xml.transform.TransformerFactoryConfigurationError: Provider for javax.xml.transform.TransformerFactory cannot be found
            TransformerFactory tf = new TransformerFactoryImpl();
            tf.setAttribute("indent-number", new Integer(2));
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, _publicID);
            serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, _systemID);
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.transform(domSource, streamResult);
        }
        catch (TransformerException e) {
            throw new XmlModelWriterException(e);
        }
        finally {
            writer.close();
        }
    }

    public final Document getDocument() {
        return _doc;
    }

    public final Element addElement(Element parent, String tagName) {
        Element element = _doc.createElement(tagName);
        parent.appendChild(element);
        return element;
    }

    public final void addComment(Element parent, String commentText) {
        if (commentText != null) {
            Comment comment = _doc.createComment(commentText);
            parent.appendChild(comment);
        }
    }
}
