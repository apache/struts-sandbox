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
package org.apache.ti.util.xml.validation.internal;

import org.apache.ti.util.xml.LocalFileEntityResolver;
import org.apache.ti.util.xml.validation.SchemaValidationException;
import org.apache.ti.util.xml.validation.SchemaValidator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DefaultSchemaValidator
        extends SchemaValidator {
    public void validate(InputStream xmlInputStream, String schemaResourcePath) {
        try {
            LocalFileEntityResolver entityResolver = new LocalFileEntityResolver(schemaResourcePath);
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaResourcePath);

            if (in == null) {
                in = DefaultSchemaValidator.class.getClassLoader().getResourceAsStream(schemaResourcePath);
            }

            if (in == null) {
                throw new SchemaValidationException("Could not parse document because schema was not found at " +
                                                    schemaResourcePath);
            }

            InputSource schemaInput = entityResolver.resolveLocalEntity(schemaResourcePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaInput);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);

            Validator handler = new Validator();
            builder.setErrorHandler(handler);
            builder.parse(xmlInputStream);

            SAXParseException e = handler.getException();

            if (e != null) {
                throw new SchemaValidationException("Error parsing document", e);
            }
        } catch (ParserConfigurationException e) {
            throw new SchemaValidationException("Error parsing document", e);
        } catch (SAXException e) {
            throw new SchemaValidationException("Error parsing document", e);
        } catch (IOException e) {
            throw new SchemaValidationException("Error parsing document", e);
        }
    }

    private static class Validator
            extends DefaultHandler {
        private SAXParseException _exception;

        public SAXParseException getException() {
            return _exception;
        }

        public void fatalError(SAXParseException e) throws SAXException {
            _exception = e;
        }

        public void error(SAXParseException e) throws SAXException {
            _exception = e;
        }
    }
}
