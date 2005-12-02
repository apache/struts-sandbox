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
package org.apache.ti.pageflow.internal.annotationreader;

import org.apache.ti.util.logging.Logger;
import org.apache.ti.util.xml.DomUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class ProcessedAnnotationParser {
    private static final Logger _log = Logger.getInstance(ProcessedAnnotationParser.class);
    private static final String ANNOTATED_ELEMENT = "annotated-element";
    private static final String ANNOTATION = "annotation";
    private static final String ANNOTATION_ATTRIBUTE = "annotation-attribute";
    private static final String ANNOTATION_NAME = "annotation-name";
    private static final String ATTRIBUTE_NAME = "attribute-name";
    private static final String ATTRIBUTE_STRING_VALUE = "string-value";
    private static final String ATTRIBUTE_VALUE = "annotation-value";
    private static final String ELEMENT_NAME = "element-name";
    private static final String TYPE_NAME = "type-name";

    /* do not construct */
    private ProcessedAnnotationParser() {
    }

    public static ProcessedAnnotations parse(final String annotationsXml, final InputStream is) {
        assert is != null;

        ProcessedAnnotations processedAnnotations = null;

        try {
            /* parse the config document */
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(is);
            Element root = document.getDocumentElement();
            String typeName = getElementText(root, TYPE_NAME);
            assert typeName != null : "Missing the following element: " + TYPE_NAME;

            Map annotatedElements = parseAnnotatedElements(root);
            processedAnnotations = new ProcessedAnnotations(typeName, annotatedElements);
        } catch (ParserConfigurationException pce) {
            _log.error("Error occurred while parsing annotations XML file " + annotationsXml, pce);
        } catch (SAXException saxe) {
            _log.error("Error occurred while parsing annotations XML file " + annotationsXml, saxe);
        } catch (IOException ioe) {
            _log.error("Error occurred while parsing annotations XML file " + annotationsXml, ioe);
        }

        return processedAnnotations;
    }

    private static final Map parseAnnotatedElements(Element parent) {
        if (parent == null) {
            return null;
        }

        List list = DomUtils.getChildElementsByName(parent, ANNOTATED_ELEMENT);

        if ((list == null) || (list.size() == 0)) {
            return null;
        }

        HashMap annotatedElements = new HashMap();

        for (int i = 0; i < list.size(); i++) {
            Element elem = (Element) list.get(i);
            String name = getElementText(elem, ELEMENT_NAME);
            assert name != null : "Missing the following element: " + ELEMENT_NAME;

            ProcessedAnnotation[] annotations = parseProcessedAnnotations(elem, ANNOTATION);
            assert annotations != null : "Missing the following element: " + ANNOTATION;

            annotatedElements.put(name, annotations);
        }

        return annotatedElements;
    }

    private static final ProcessedAnnotation[] parseProcessedAnnotations(Element parent, String nodeName) {
        if (parent == null) {
            return null;
        }

        List list = DomUtils.getChildElementsByName(parent, nodeName);

        if ((list == null) || (list.size() == 0)) {
            return null;
        }

        ProcessedAnnotation[] annotations = new ProcessedAnnotation[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Element elem = (Element) list.get(i);
            String name = getElementText(elem, ANNOTATION_NAME);
            assert name != null : "Missing the following element: " + ANNOTATION_NAME;

            AnnotationAttribute[] attributes = parseAnnotationAttribute(elem);
            annotations[i] = new ProcessedAnnotation(name, attributes);
        }

        return annotations;
    }

    private static final AnnotationAttribute[] parseAnnotationAttribute(Element parent) {
        if (parent == null) {
            return null;
        }

        List list = DomUtils.getChildElementsByName(parent, ANNOTATION_ATTRIBUTE);

        if ((list == null) || (list.size() == 0)) {
            return null;
        }

        AnnotationAttribute[] attributes = new AnnotationAttribute[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Element elem = (Element) list.get(i);
            String name = getElementText(elem, ATTRIBUTE_NAME);
            assert name != null : "Missing the following element: " + ATTRIBUTE_NAME;

            String value = getElementText(elem, ATTRIBUTE_STRING_VALUE);

            if (value != null) {
                attributes[i] = new AnnotationAttribute(name, value);
            } else {
                ProcessedAnnotation[] annotations = parseProcessedAnnotations(elem, ATTRIBUTE_VALUE);
                attributes[i] = new AnnotationAttribute(name, annotations);
            }
        }

        return attributes;
    }

    private static String getElementText(Element parent, String elementName) {
        Element child = DomUtils.getChildElementByName(parent, elementName);

        if (child != null) {
            String text = DomUtils.getElementText(child);

            if (text != null) {
                return (text.length() == 0) ? null : text;
            }
        }

        return null;
    }
}
