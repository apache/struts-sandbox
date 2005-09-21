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
package org.apache.ti.pageflow.internal;

import org.apache.commons.chain.web.WebContext;

import org.apache.ti.core.urltemplates.URLTemplate;
import org.apache.ti.core.urltemplates.URLTemplates;
import org.apache.ti.core.urltemplates.URLTemplatesFactory;
import org.apache.ti.util.SourceResolver;
import org.apache.ti.util.logging.Logger;
import org.apache.ti.util.xml.DomUtils;
import org.apache.ti.util.xml.XmlInputStreamResolver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Methods for configuring and retrieving the URLTemplate object.
 */
public class DefaultURLTemplatesFactory
        extends URLTemplatesFactory {
    private static final Logger _log = Logger.getInstance(DefaultURLTemplatesFactory.class);

    // Constants for schema elements
    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String TEMPLATE_NAME = "template-name";
    private static final String URL_TEMPLATE = "url-template";
    private static final String URL_TEMPLATE_REF = "url-template-ref";
    private static final String URL_TEMPLATE_REF_GROUP = "url-template-ref-group";
    private static final String VALUE = "value";
    private static final String CONFIG_SCHEMA = "org/apache/beehive/netui/core/urltemplates/schema/url-template-config.xsd";
    private static final XmlInputStreamResolver SCHEMA_RESOLVER = new XmlInputStreamResolver() {
        public String getResourcePath() {
            return CONFIG_SCHEMA;
        }

        public InputStream getInputStream() {
            return DefaultURLTemplatesFactory.class.getClassLoader().getResourceAsStream(getResourcePath());
        }
    };

    // The actual URL templates and template ref groups
    private URLTemplates _urlTemplates;

    /**
     * Returns URL template given the name of the template.
     *
     * @param name name of the template
     * @return template
     */
    public URLTemplate getURLTemplate(String name) {
        assert _urlTemplates != null : "The template config file has not been loaded.";

        if (_urlTemplates == null) {
            return null;
        }

        return _urlTemplates.getTemplate(name);
    }

    /**
     * Returns URL template name of the given type (by key) from the
     * desired reference group.
     *
     * @param refGroupName name of a group of templates from the config file.
     * @param key          type of the template
     * @return template name
     */
    public String getTemplateNameByRef(String refGroupName, String key) {
        assert _urlTemplates != null : "The template config file has not been loaded.";

        if (_urlTemplates == null) {
            return null;
        }

        String ref = _urlTemplates.getTemplateNameByRef(refGroupName, key);

        if (ref == null) {
            // If the template is a secure template, look for the secure default
            // before resolving to the default
            if (key.equals(URLTemplatesFactory.SECURE_RENDER_TEMPLATE) || key.equals(URLTemplatesFactory.SECURE_ACTION_TEMPLATE) ||
                    key.equals(URLTemplatesFactory.SECURE_RESOURCE_TEMPLATE)) {
                ref = _urlTemplates.getTemplateNameByRef(refGroupName, URLTemplatesFactory.SECURE_DEFAULT_TEMPLATE);
            }
        }

        return ref;
    }

    /**
    * Initialization method that parses the URL template config file to
    * get the URL templates and template reference groups.
    */
    public void load(WebContext webContext, SourceResolver sourceResolver) {
        _urlTemplates = new URLTemplates();

        InputStream xmlInputStream = null;
        InputStream xsdInputStream = null;

        try {
            URL url = sourceResolver.resolve(_configFilePath, webContext);

            if (url != null) {
                xmlInputStream = url.openStream();

                /* load the XSD input stream */
                xsdInputStream = SCHEMA_RESOLVER.getInputStream();

                final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
                final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
                final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setValidating(true);
                dbf.setNamespaceAware(true);
                dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                dbf.setAttribute(JAXP_SCHEMA_SOURCE, xsdInputStream);

                DocumentBuilder db = dbf.newDocumentBuilder();

                /* add an ErrorHandler that just logs validation problems */
                db.setErrorHandler(new ErrorHandler() {
                        public void warning(SAXParseException exception) {
                            _log.info("Validation warning validating config file \"" + _configFilePath +
                                      "\" against XML Schema \"" + SCHEMA_RESOLVER.getResourcePath());
                        }

                        public void error(SAXParseException exception) {
                            _log.error("Validation errors occurred parsing the config file \"" + _configFilePath +
                                       "\".  Cause: " + exception, exception);
                        }

                        public void fatalError(SAXParseException exception) {
                            _log.error("Validation errors occurred parsing the config file \"" + _configFilePath +
                                       "\".  Cause: " + exception, exception);
                        }
                    });

                db.setEntityResolver(new EntityResolver() {
                        public InputSource resolveEntity(String publicId, String systemId) {
                            if (systemId.endsWith("/url-template-config.xsd")) {
                                InputStream inputStream = DefaultURLTemplatesFactory.class.getClassLoader().getResourceAsStream(CONFIG_SCHEMA);

                                return new InputSource(inputStream);
                            } else {
                                return null;
                            }
                        }
                    });

                Document document = db.parse(xmlInputStream);
                Element root = document.getDocumentElement();
                loadTemplates(root);
                loadTemplateRefGroups(root);
            } else {
                if (_log.isInfoEnabled()) {
                    _log.info("Running without URL template descriptor, " + _configFilePath);
                }
            }
        } catch (ParserConfigurationException pce) {
            _log.error("Problem loading URL template descriptor file " + _configFilePath, pce);
        } catch (SAXException se) {
            _log.error("Problem parsing URL template descriptor in " + _configFilePath, se);
        } catch (IOException ioe) {
            _log.error("Problem reading URL template descriptor file " + _configFilePath, ioe);
        } finally {
            // Close the streams
            try {
                if (xmlInputStream != null) {
                    xmlInputStream.close();
                }
            } catch (Exception ignore) {
            }

            try {
                if (xsdInputStream != null) {
                    xsdInputStream.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    /**
    * Loads the templates from a URL template config document.
    *
    * @param parent
    */
    private void loadTemplates(Element parent) {
        // Load templates
        List templates = DomUtils.getChildElementsByName(parent, URL_TEMPLATE);

        for (int i = 0; i < templates.size(); i++) {
            Element template = (Element) templates.get(i);
            String name = getElementText(template, NAME);

            if (name == null) {
                _log.error("Malformed URL template descriptor in " + _configFilePath + ". The url-template name is missing.");

                continue;
            }

            String value = getElementText(template, VALUE);

            if (value == null) {
                _log.error("Malformed URL template descriptor in " + _configFilePath +
                           ". The url-template value is missing for template " + name);

                continue;
            }

            if (_log.isDebugEnabled()) {
                _log.debug("[URLTemplate] " + name + " = " + value);
            }

            URLTemplate urlTemplate = new URLTemplate(value);

            if (urlTemplate.verify(_knownTokens, _requiredTokens)) {
                _urlTemplates.addTemplate(name, urlTemplate);
            }
        }
    }

    /**
     * Loads the template reference groups from a URL template config document.
     *
     * @param parent
     */
    private void loadTemplateRefGroups(Element parent) {
        // Load template refs
        List templateRefGroups = DomUtils.getChildElementsByName(parent, URL_TEMPLATE_REF_GROUP);
        ;

        for (int i = 0; i < templateRefGroups.size(); i++) {
            Element refGroupElement = (Element) templateRefGroups.get(i);
            String refGroupName = getElementText(refGroupElement, NAME);

            if (refGroupName == null) {
                _log.error("Malformed URL template descriptor in " + _configFilePath +
                           ". The url-template-ref-group name is missing.");

                continue;
            }

            HashMap refGroup = new HashMap();
            List templateRefs = DomUtils.getChildElementsByName(refGroupElement, URL_TEMPLATE_REF);
            ;

            for (int j = 0; j < templateRefs.size(); j++) {
                Element templateRefElement = (Element) templateRefs.get(j);
                String key = getElementText(templateRefElement, KEY);

                if (key == null) {
                    _log.error("Malformed URL template descriptor in " + _configFilePath +
                               ". The url-template-ref key is missing in url-template-ref-group " + refGroupName);

                    continue;
                }

                String name = getElementText(templateRefElement, TEMPLATE_NAME);

                if (name != null) {
                    refGroup.put(key, name);

                    if (_log.isDebugEnabled()) {
                        _log.debug("[" + refGroupName + " URLTemplate] " + key + " = " + name);
                    }
                } else {
                    _log.error("Malformed URL template descriptor in " + _configFilePath +
                               ". The url-template-ref template-name is missing in url-template-ref-group " + refGroupName);
                }
            }

            if (refGroup.size() != 0) {
                _urlTemplates.addTemplateRefGroup(refGroupName, refGroup);
            }
        }
    }

    private String getElementText(Element parent, String elementName) {
        Element child = DomUtils.getChildElementByName(parent, elementName);

        if (child != null) {
            String text = DomUtils.getElementText(child);

            if (text != null) {
                text = text.trim();

                return (text.length() == 0) ? null : text;
            }
        }

        return null;
    }
}
