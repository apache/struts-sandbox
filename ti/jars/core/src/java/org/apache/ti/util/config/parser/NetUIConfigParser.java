/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
package org.apache.ti.util.config.parser;

import org.apache.ti.util.config.ConfigInitializationException;
import org.apache.ti.util.config.bean.BindingContextConfig;
import org.apache.ti.util.config.bean.CustomPropertyConfig;
import org.apache.ti.util.config.bean.DocType;
import org.apache.ti.util.config.bean.ExpressionLanguageConfig;
import org.apache.ti.util.config.bean.ExpressionLanguagesConfig;
import org.apache.ti.util.config.bean.GlobalPageFlowActionInterceptorConfig;
import org.apache.ti.util.config.bean.HandlerConfig;
import org.apache.ti.util.config.bean.IdJavascript;
import org.apache.ti.util.config.bean.InterceptorConfig;
import org.apache.ti.util.config.bean.IteratorFactoryConfig;
import org.apache.ti.util.config.bean.JspTagConfig;
import org.apache.ti.util.config.bean.ModuleConfigLocatorConfig;
import org.apache.ti.util.config.bean.MultipartHandler;
import org.apache.ti.util.config.bean.NetUIConfig;
import org.apache.ti.util.config.bean.PageFlowActionInterceptorsConfig;
import org.apache.ti.util.config.bean.PageFlowConfig;
import org.apache.ti.util.config.bean.PageFlowFactoriesConfig;
import org.apache.ti.util.config.bean.PageFlowFactoryConfig;
import org.apache.ti.util.config.bean.PageFlowHandlersConfig;
import org.apache.ti.util.config.bean.PerActionInterceptorConfig;
import org.apache.ti.util.config.bean.PerPageFlowActionInterceptorConfig;
import org.apache.ti.util.config.bean.PrefixHandlerConfig;
import org.apache.ti.util.config.bean.PreventCache;
import org.apache.ti.util.config.bean.RequestInterceptorsConfig;
import org.apache.ti.util.config.bean.SharedFlowRefConfig;
import org.apache.ti.util.config.bean.SimpleActionInterceptorConfig;
import org.apache.ti.util.config.bean.TypeConverterConfig;
import org.apache.ti.util.config.bean.UrlConfig;
import org.apache.ti.util.logging.Logger;
import org.apache.ti.util.xml.DomUtils;
import org.apache.ti.util.xml.XmlInputStreamResolver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class NetUIConfigParser {
    private static final Logger LOGGER = Logger.getInstance(NetUIConfigParser.class);
    private static final String DEFAULT_CONFIG = "org/apache/ti/util/config/internal/struts-ti-config-default.xml";
    private static final String CONFIG_SCHEMA = "org/apache/ti/util/config/schema/struts-ti-config.xsd";
    private static final XmlInputStreamResolver SCHEMA_RESOLVER = new XmlInputStreamResolver() {
        public String getResourcePath() {
            return CONFIG_SCHEMA;
        }

        public InputStream getInputStream() {
            return NetUIConfigParser.class.getClassLoader().getResourceAsStream(getResourcePath());
        }
    };

    private static final XmlInputStreamResolver DEFAULT_CONFIG_RESOLVER = new XmlInputStreamResolver() {
        public String getResourcePath() {
            return DEFAULT_CONFIG;
        }

        public InputStream getInputStream() {
            return NetUIConfigParser.class.getClassLoader().getResourceAsStream(getResourcePath());
        }
    };

    public NetUIConfig parse(final XmlInputStreamResolver xmlResolver) {
        NetUIConfig configBean = null;
        InputStream xmlInputStream = null;
        XmlInputStreamResolver theXmlResolver = xmlResolver;

        try {
            /* use the default XmlInputStream */
            if (theXmlResolver == null) {
                theXmlResolver = DEFAULT_CONFIG_RESOLVER;
            }

            xmlInputStream = theXmlResolver.getInputStream();

            /* the default XmlInputStream could not provide a valid InputStream; try the default */
            if (xmlInputStream == null) {
                theXmlResolver = DEFAULT_CONFIG_RESOLVER;
                xmlInputStream = theXmlResolver.getInputStream();

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Loading the default NetUI config file.  The runtime will be configured " +
                                "with a set of minimum parameters.");
                }

                /* todo: should this throw an exception? */
                if (xmlInputStream == null) {
                    throw new ConfigInitializationException("The NetUI runtime could not find the default config file.  " +
                                                            "The webapp may not function properly.");
                }
            }

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("NetUIConfigParser -- load config: " + theXmlResolver.getResourcePath());
            }

            configBean = parse(theXmlResolver.getResourcePath(), xmlInputStream);
        } catch (IOException e) {
            LOGGER.error("Could not open stream for " + theXmlResolver.getResourcePath(), e);
        } finally {
            try {
                if (xmlInputStream != null) {
                    xmlInputStream.close();
                }
            } catch (IOException ignore) {
            }
        }

        return configBean;
    }

    private NetUIConfig parse(final String resourcePath, final InputStream is) {
        assert is != null;

        NetUIConfig netuiConfig = null;
        InputStream xsdInputStream = null;

        try {
            /* parse the config document */
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
            db.setErrorHandler(new ErrorHandler() {
                    public void warning(SAXParseException exception) {
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("Validation warning validating config file \"" + resourcePath +
                                        "\" against XML Schema \"" + SCHEMA_RESOLVER.getResourcePath());
                        }
                    }

                    public void error(SAXParseException exception) {
                        throw new ConfigInitializationException("Validation errors occurred parsing the config file \"" +
                                                                resourcePath + "\".  Cause: " + exception, exception);
                    }

                    public void fatalError(SAXParseException exception) {
                        throw new ConfigInitializationException("Validation errors occurred parsing the config file \"" +
                                                                resourcePath + "\".  Cause: " + exception, exception);
                    }
                });

            db.setEntityResolver(new EntityResolver() {
                    public InputSource resolveEntity(String publicId, String systemId) {
                        if (systemId.endsWith("/struts-ti-config.xsd")) {
                            InputStream inputStream = NetUIConfigParser.class.getClassLoader().getResourceAsStream(CONFIG_SCHEMA);

                            return new InputSource(inputStream);
                        } else {
                            return null;
                        }
                    }
                });

            Document document = db.parse(is);

            PageFlowActionInterceptorsConfig pfActionInterceptorsConfig = parsePfActionInterceptorsConfig(document);
            PageFlowHandlersConfig pfHandlersConfig = parsePfHandlersConfig(document);
            PageFlowConfig pfConfig = parsePfConfig(document);
            PageFlowFactoriesConfig pfFactoriesConfig = parsePfFactoriesConfig(document);
            SharedFlowRefConfig[] sharedFlowRefConfigs = parseSharedFlowRefConfigs(document);
            RequestInterceptorsConfig requestInterceptorsConfig = parseRequestInterceptorsConfig(document);

            JspTagConfig jspTagConfig = parseJspTagConfig(document);
            ExpressionLanguagesConfig elConfig = parseExpressionLanguageConfig(document);
            TypeConverterConfig[] typeConvertersConfig = parseTypeConvertersConfig(document);
            UrlConfig urlConfig = parseUrlConfig(document);
            IteratorFactoryConfig[] iteratorFactories = parseIteratorFactoryConfig(document);
            PrefixHandlerConfig[] prefixHandlers = parsePrefixHandlerConfig(document);

            netuiConfig = new NetUIConfig(pfActionInterceptorsConfig, pfHandlersConfig, pfConfig, pfFactoriesConfig,
                                          sharedFlowRefConfigs, requestInterceptorsConfig, jspTagConfig, prefixHandlers,
                                          elConfig, iteratorFactories, typeConvertersConfig, urlConfig);
        } catch (ParserConfigurationException e) {
            throw new ConfigInitializationException("Error occurred parsing the config file \"" + resourcePath + "\"", e);
        } catch (IOException e) {
            throw new ConfigInitializationException("Error occurred parsing the config file \"" + resourcePath + "\"", e);
        } catch (SAXException e) {
            throw new ConfigInitializationException("Error occurred parsing the config file \"" + resourcePath + "\"", e);
        } finally {
            try {
                if (xsdInputStream != null) {
                    xsdInputStream.close();
                }
            } catch (IOException e) {
            }
        }

        return netuiConfig;
    }

    private static final PageFlowActionInterceptorsConfig parsePfActionInterceptorsConfig(Document document) {
        final Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "pageflow-action-interceptors");

        if (elem == null) {
            return null;
        }

        /* global */
        Element globalElem = DomUtils.getChildElementByName(elem, "global");
        GlobalPageFlowActionInterceptorConfig global = null;
        InterceptorConfig[] globalInterceptorConfigs = null;
        SimpleActionInterceptorConfig[] globalSimpleActionInterceptorConfig = null;

        if (globalElem != null) {
            globalSimpleActionInterceptorConfig = parseSimpleActionInterceptorConfigs(DomUtils.getChildElementsByName(globalElem,
                                                                                                                      "simple-action-interceptor"));
            globalInterceptorConfigs = parseInterceptorConfigs(DomUtils.getChildElementsByName(globalElem, "action-interceptor"));
        }

        global = new GlobalPageFlowActionInterceptorConfig(globalSimpleActionInterceptorConfig, globalInterceptorConfigs);

        /* per page flow */
        PerPageFlowActionInterceptorConfig[] perPageFlow = null;
        NodeList perJpfList = elem.getElementsByTagName("per-pageflow");

        if ((perJpfList != null) && (perJpfList.getLength() > 0)) {
            perPageFlow = new PerPageFlowActionInterceptorConfig[perJpfList.getLength()];

            for (int i = 0; i < perJpfList.getLength(); i++) {
                Element perJpfElem = (Element) perJpfList.item(i);

                PerActionInterceptorConfig[] perActionInterceptorConfigs = null;
                NodeList perAction = perJpfElem.getElementsByTagName("per-action");

                if ((perAction != null) && (perAction.getLength() > 0)) {
                    perActionInterceptorConfigs = new PerActionInterceptorConfig[perAction.getLength()];

                    for (int j = 0; j < perAction.getLength(); j++) {
                        perActionInterceptorConfigs[j] = new PerActionInterceptorConfig(DomUtils.getChildElementText((Element) perAction.item(j),
                                                                                                                     "action-name"),
                                                                                        parseSimpleActionInterceptorConfigs(DomUtils.getChildElementsByName((Element) perAction.item(j),
                                                                                                                                                            "simple-action-interceptor")),
                                                                                        parseInterceptorConfigs(DomUtils.getChildElementsByName((Element) perAction.item(j),
                                                                                                                                                "action-interceptor")));
                    }
                }

                perPageFlow[i] = new PerPageFlowActionInterceptorConfig(DomUtils.getChildElementText(perJpfElem, "pageflow-uri"),
                                                                        parseSimpleActionInterceptorConfigs(DomUtils.getChildElementsByName(perJpfElem,
                                                                                                                                            "simple-action-interceptor")),
                                                                        parseInterceptorConfigs(DomUtils.getChildElementsByName(perJpfElem,
                                                                                                                                "action-interceptor")),
                                                                        perActionInterceptorConfigs);
            }
        }

        return new PageFlowActionInterceptorsConfig(global, perPageFlow);
    }

    private static final SimpleActionInterceptorConfig[] parseSimpleActionInterceptorConfigs(List list) {
        if ((list == null) || (list.size() == 0)) {
            return null;
        }

        SimpleActionInterceptorConfig[] simpleActionInterceptorConfigs = new SimpleActionInterceptorConfig[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Boolean afterAction = null;

            String tmp = DomUtils.getChildElementText((Element) list.get(i), "after-action");

            if (tmp != null) {
                afterAction = new Boolean(tmp);
            }

            simpleActionInterceptorConfigs[i] = new SimpleActionInterceptorConfig(afterAction,
                                                                                  DomUtils.getChildElementText((Element) list.get(i),
                                                                                                               "intercept-path"));
        }

        return simpleActionInterceptorConfigs;
    }

    private static final PageFlowHandlersConfig parsePfHandlersConfig(Document document) {
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "pageflow-handlers");

        if (elem == null) {
            return null;
        }

        return new PageFlowHandlersConfig(parseHandlerConfig(elem.getElementsByTagName("action-forward-handler")),
                                          parseHandlerConfig(elem.getElementsByTagName("exceptions-handler")),
                                          parseHandlerConfig(elem.getElementsByTagName("forward-redirect-handler")),
                                          parseHandlerConfig(elem.getElementsByTagName("login-handler")),
                                          parseHandlerConfig(elem.getElementsByTagName("storage-handler")),
                                          parseHandlerConfig(elem.getElementsByTagName("reloadable-class-handler")),
                                          parseHandlerConfig(elem.getElementsByTagName("module-registration-handler")),
                                          parseHandlerConfig(elem.getElementsByTagName("annotation-handler")));
    }

    private static final PageFlowConfig parsePfConfig(Document document) {
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "pageflow-config");

        if (elem == null) {
            return new PageFlowConfig();
        }

        PageFlowConfig pfConfig = null;

        Boolean enableSelfNesting = null;
        Boolean ensureSecureForwards = null;
        Boolean throwSessionExpiredException = null;
        Integer maxForwardsPerRequest = null;
        Integer maxNestingStackDepth = null;
        MultipartHandler mpHandler = null;
        PreventCache preventCache = null;
        ModuleConfigLocatorConfig[] moduleConfigLocators = null;

        String tmp = null;

        tmp = DomUtils.getChildElementText(elem, "enable-self-nesting");

        if (tmp != null) {
            enableSelfNesting = new Boolean(tmp);
        }

        tmp = DomUtils.getChildElementText(elem, "ensure-secure-forwards");

        if (tmp != null) {
            ensureSecureForwards = new Boolean(tmp);
        }

        tmp = DomUtils.getChildElementText(elem, "throw-session-expired-exception");

        if (tmp != null) {
            throwSessionExpiredException = new Boolean(tmp);
        }

        tmp = DomUtils.getChildElementText(elem, "max-forwards-per-request");

        if (tmp != null) {
            maxForwardsPerRequest = new Integer(Integer.parseInt(tmp));
        }

        tmp = DomUtils.getChildElementText(elem, "max-nesting-stack-depth");

        if (tmp != null) {
            maxNestingStackDepth = new Integer(Integer.parseInt(tmp));
        }

        tmp = DomUtils.getChildElementText(elem, "multipart-handler");

        if (tmp != null) {
            if (tmp.equals("disabled")) {
                mpHandler = MultipartHandler.DISABLED;
            } else if (tmp.equals("disk")) {
                mpHandler = MultipartHandler.DISK;
            } else if (tmp.equals("memory")) {
                mpHandler = MultipartHandler.MEMORY;
            }
        }

        tmp = DomUtils.getChildElementText(elem, "prevent-cache");

        if (tmp != null) {
            if (tmp.equals("always")) {
                preventCache = PreventCache.ALWAYS;
            } else if (tmp.equals("default")) {
                preventCache = PreventCache.DEFAULT;
            } else if (tmp.equals("inDevMode")) {
                preventCache = PreventCache.IN_DEV_MODE;
            }
        }

        moduleConfigLocators = parseModuleConfigLocators(DomUtils.getChildElementByName(elem, "module-config-locators"));

        pfConfig = new PageFlowConfig(enableSelfNesting, ensureSecureForwards, throwSessionExpiredException,
                                      maxForwardsPerRequest, maxNestingStackDepth, mpHandler, preventCache, moduleConfigLocators);

        return pfConfig;
    }

    private static final ModuleConfigLocatorConfig[] parseModuleConfigLocators(Element element) {
        if (element == null) {
            return null;
        }

        NodeList list = element.getElementsByTagName("module-config-locator");

        if ((list == null) || (list.getLength() == 0)) {
            return null;
        }

        ModuleConfigLocatorConfig[] mclConfig = new ModuleConfigLocatorConfig[list.getLength()];

        for (int i = 0; i < list.getLength(); i++) {
            mclConfig[i] = new ModuleConfigLocatorConfig(DomUtils.getChildElementText((Element) list.item(i), "locator-class"),
                                                         DomUtils.getChildElementText((Element) list.item(i), "description"));
        }

        return mclConfig;
    }

    private static final PageFlowFactoriesConfig parsePfFactoriesConfig(Document document) {
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "pageflow-factories");

        if (elem == null) {
            return null;
        }

        PageFlowFactoryConfig pfFactory = parsePageFlowFactoryConfig(DomUtils.getChildElementByName(elem, "flowcontroller-factory"));
        PageFlowFactoryConfig fbbFactoyr = parsePageFlowFactoryConfig(DomUtils.getChildElementByName(elem,
                                                                                                     "faces-backing-bean-factory"));

        return new PageFlowFactoriesConfig(pfFactory, fbbFactoyr);
    }

    private static final SharedFlowRefConfig[] parseSharedFlowRefConfigs(Document document) {
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "default-shared-flow-refs");

        if (elem == null) {
            return null;
        }

        NodeList list = elem.getElementsByTagName("shared-flow-ref");

        if ((list == null) || (list.getLength() == 0)) {
            return null;
        }

        SharedFlowRefConfig[] sharedFlowRefConfigs = new SharedFlowRefConfig[list.getLength()];

        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            sharedFlowRefConfigs[i] = new SharedFlowRefConfig(DomUtils.getChildElementText((Element) node, "name"),
                                                              DomUtils.getChildElementText((Element) node, "type"));
        }

        return sharedFlowRefConfigs;
    }

    private static final RequestInterceptorsConfig parseRequestInterceptorsConfig(Document document) {
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "request-interceptors");

        if (elem == null) {
            return null;
        }

        RequestInterceptorsConfig requestInterceptorsConfig = null;
        Element global = DomUtils.getChildElementByName(elem, "global");

        if (global == null) {
            return null;
        }

        InterceptorConfig[] interceptorConfigs = parseInterceptorConfigs(DomUtils.getChildElementsByName(global,
                                                                                                         "request-interceptor"));

        if (interceptorConfigs != null) {
            requestInterceptorsConfig = new RequestInterceptorsConfig(interceptorConfigs);
        }

        return requestInterceptorsConfig;
    }

    private static final JspTagConfig parseJspTagConfig(Document document) {
        DocType docType = null;
        IdJavascript idJavascript = null;
        String treeImageLocation = null;

        String tmp = null;
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "jsp-tag-config");

        if (elem == null) {
            return new JspTagConfig();
        }

        tmp = DomUtils.getChildElementText(elem, "doctype");

        if (tmp != null) {
            if (tmp.equals("html4-loose")) {
                docType = DocType.HTML4_LOOSE;
            } else if (tmp.equals("html4-loose-quirks")) {
                docType = DocType.HTML4_LOOSE_QUIRKS;
            } else if (tmp.equals("xhtml1-transitional")) {
                docType = DocType.XHTML1_TRANSITIONAL;
            }
        }

        tmp = DomUtils.getChildElementText(elem, "id-javascript");

        if (tmp != null) {
            if (tmp.equals("default")) {
                idJavascript = IdJavascript.DEFAULT;
            } else if (tmp.equals("legacy")) {
                idJavascript = IdJavascript.LEGACY;
            } else if (tmp.equals("legacyOnly")) {
                idJavascript = IdJavascript.LEGACY_ONLY;
            }
        }

        treeImageLocation = DomUtils.getChildElementText(elem, "tree-image-location");

        return new JspTagConfig(docType, idJavascript, treeImageLocation);
    }

    private static final PrefixHandlerConfig[] parsePrefixHandlerConfig(Document document) {
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "prefix-handlers");

        if (elem == null) {
            return null;
        }

        NodeList list = elem.getElementsByTagName("prefix-handler");

        if ((list == null) || (list.getLength() == 0)) {
            return null;
        }

        PrefixHandlerConfig[] prefixHandlers = new PrefixHandlerConfig[list.getLength()];

        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            prefixHandlers[i] = new PrefixHandlerConfig(DomUtils.getChildElementText((Element) node, "name"),
                                                        DomUtils.getChildElementText((Element) node, "handler-class"));
        }

        return prefixHandlers;
    }

    private static final ExpressionLanguagesConfig parseExpressionLanguageConfig(Document document) {
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "expression-languages");

        if (elem == null) {
            return null;
        }

        String defaultLanguage = DomUtils.getChildElementText(elem, "default-language");
        ExpressionLanguageConfig[] elConfigs = null;

        NodeList list = elem.getElementsByTagName("expression-language");

        if ((list != null) && (list.getLength() > 0)) {
            elConfigs = new ExpressionLanguageConfig[list.getLength()];

            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);

                BindingContextConfig[] bindingContextConfig = null;
                Node bindingContexts = DomUtils.getChildElementByName((Element) node, "binding-contexts");

                if (bindingContexts != null) {
                    NodeList bcList = ((Element) bindingContexts).getElementsByTagName("binding-context");

                    if ((bcList != null) && (bcList.getLength() > 0)) {
                        bindingContextConfig = new BindingContextConfig[bcList.getLength()];

                        for (int j = 0; j < bcList.getLength(); j++) {
                            bindingContextConfig[j] = new BindingContextConfig(DomUtils.getChildElementText((Element) bcList.item(j),
                                                                                                            "name"),
                                                                               DomUtils.getChildElementText((Element) bcList.item(j),
                                                                                                            "factory-class"));
                        }
                    }
                }

                elConfigs[i] = new ExpressionLanguageConfig(DomUtils.getChildElementText((Element) node, "name"),
                                                            DomUtils.getChildElementText((Element) node, "factory-class"),
                                                            bindingContextConfig);
            }
        }

        return new ExpressionLanguagesConfig(defaultLanguage, elConfigs);
    }

    private static final TypeConverterConfig[] parseTypeConvertersConfig(Document document) {
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "type-converters");

        if (elem == null) {
            return null;
        }

        NodeList list = elem.getElementsByTagName("type-converter");

        if ((list == null) || (list.getLength() == 0)) {
            return null;
        }

        TypeConverterConfig[] typeConverterConfig = new TypeConverterConfig[list.getLength()];

        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            typeConverterConfig[i] = new TypeConverterConfig(DomUtils.getChildElementText((Element) node, "type"),
                                                             DomUtils.getChildElementText((Element) node, "converter-class"));
        }

        return typeConverterConfig;
    }

    private static final IteratorFactoryConfig[] parseIteratorFactoryConfig(Document document) {
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "iterator-factories");

        if (elem == null) {
            return null;
        }

        NodeList list = elem.getElementsByTagName("iterator-factory");

        if ((list == null) || (list.getLength() == 0)) {
            return null;
        }

        IteratorFactoryConfig[] iteratorFactoryConfig = new IteratorFactoryConfig[list.getLength()];

        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            iteratorFactoryConfig[i] = new IteratorFactoryConfig(DomUtils.getChildElementText((Element) node, "name"),
                                                                 DomUtils.getChildElementText((Element) node, "factory-class"));
        }

        return iteratorFactoryConfig;
    }

    private static final UrlConfig parseUrlConfig(Document document) {
        Element elem = DomUtils.getChildElementByName(document.getDocumentElement(), "url-config");

        if (elem == null) {
            return new UrlConfig();
        }

        Boolean urlEncodeUrls = null;
        Boolean htmlAmpEntity = null;
        String templatedUrlFormatterClass = null;

        String tmp = null;

        tmp = DomUtils.getChildElementText(elem, "url-encode-urls");

        if (tmp != null) {
            urlEncodeUrls = new Boolean(tmp);
        }

        tmp = DomUtils.getChildElementText(elem, "html-amp-entity");

        if (tmp != null) {
            htmlAmpEntity = new Boolean(tmp);
        }

        templatedUrlFormatterClass = DomUtils.getChildElementText(elem, "templated-url-formatter-class");

        return new UrlConfig(urlEncodeUrls, htmlAmpEntity, templatedUrlFormatterClass);
    }

    /* -----------------------------------------------------------------------------------

       Utilities used to parse reused NetUI config types

       ----------------------------------------------------------------------------------
     */
    private static final HandlerConfig[] parseHandlerConfig(NodeList list) {
        if ((list == null) || (list.getLength() == 0)) {
            return null;
        }

        HandlerConfig[] handlerConfigs = new HandlerConfig[list.getLength()];

        for (int i = 0; i < handlerConfigs.length; i++) {
            handlerConfigs[i] = new HandlerConfig(DomUtils.getChildElementText((Element) list.item(i), "handler-class"),
                                                  parseCustomProperties(((Element) list.item(i)).getElementsByTagName("custom-property")));
        }

        return handlerConfigs;
    }

    private static final InterceptorConfig[] parseInterceptorConfigs(List list) {
        if ((list == null) || (list.size() == 0)) {
            return null;
        }

        InterceptorConfig[] interceptorConfigs = new InterceptorConfig[list.size()];

        for (int i = 0; i < list.size(); i++) {
            interceptorConfigs[i] = new InterceptorConfig(DomUtils.getChildElementText((Element) list.get(i), "interceptor-class"),
                                                          parseCustomProperties(((Element) list.get(i)).getElementsByTagName("custom-property")));
        }

        return interceptorConfigs;
    }

    private static final CustomPropertyConfig[] parseCustomProperties(NodeList customProperties) {
        if ((customProperties == null) || (customProperties.getLength() == 0)) {
            return null;
        }

        CustomPropertyConfig[] cpConfig = new CustomPropertyConfig[customProperties.getLength()];

        for (int i = 0; i < cpConfig.length; i++) {
            cpConfig[i] = new CustomPropertyConfig(DomUtils.getChildElementText((Element) customProperties.item(i), "name"),
                                                   DomUtils.getChildElementText((Element) customProperties.item(i), "value"));
        }

        return cpConfig;
    }

    private static final PageFlowFactoryConfig parsePageFlowFactoryConfig(Node node) {
        if (node != null) {
            return new PageFlowFactoryConfig(DomUtils.getChildElementText((Element) node, "factory-class"),
                                             parseCustomProperties((((Element) node).getElementsByTagName("custom-property"))));
        } else {
            return null;
        }
    }
}
