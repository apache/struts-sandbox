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
import org.apache.ti.schema.urltemplates.UrlTemplateConfigDocument;
import org.apache.ti.schema.urltemplates.UrlTemplateConfigDocument.UrlTemplateConfig;
import org.apache.ti.schema.urltemplates.UrlTemplateDocument;
import org.apache.ti.schema.urltemplates.UrlTemplateRefDocument;
import org.apache.ti.schema.urltemplates.UrlTemplateRefGroupDocument;
import org.apache.ti.util.SourceResolver;
import org.apache.ti.util.internal.InternalStringBuilder;
import org.apache.ti.util.logging.Logger;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Methods for configuring and retrieving the URLTemplate object.
 */
public class DefaultURLTemplatesFactory extends URLTemplatesFactory {

    private static final Logger _log = Logger.getInstance(DefaultURLTemplatesFactory.class);

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
            if (key.equals(URLTemplatesFactory.SECURE_RENDER_TEMPLATE) ||
                    key.equals(URLTemplatesFactory.SECURE_ACTION_TEMPLATE) ||
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
    protected void load(WebContext webContext, SourceResolver sourceResolver) {
        String configFilePath = getConfigFilePath();

        try {
            URL url = sourceResolver.resolve(configFilePath, webContext);
            if (url != null) {
                _urlTemplates = getTemplatesFromConfig(url);
            } else {
                // No descriptor
                _urlTemplates = new URLTemplates();

                if (_log.isInfoEnabled()) {
                    InternalStringBuilder message = new InternalStringBuilder();
                    message.append("Running without URL template descriptor, ");
                    message.append(configFilePath);
                    _log.info(message.toString());
                }
            }
        } catch (XmlException xe) {
            // Bad descriptor
            _log.error("Malformed URL template descriptor in " + configFilePath, xe);
        } catch (IOException ioe) {
            // Bad descriptor
            _log.error("Problem parsing URL template descriptor in " + configFilePath, ioe);
        } catch (Exception e) {
            // Bad descriptor
            _log.error("Problem loading URL template descriptor file " + configFilePath, e);
        }
    }

    /**
     * Loads the templates from a URL template config document.
     *
     * @param url the URL to load the config file.
     * @return The URL templates found in the config document.
     */
    protected URLTemplates getTemplatesFromConfig(URL url) throws XmlException, IOException {
        URLTemplates urlTemplates = new URLTemplates();

        UrlTemplateConfig urlTemplateConfig = UrlTemplateConfigDocument.Factory.parse(url).getUrlTemplateConfig();

        // Load templates
        UrlTemplateDocument.UrlTemplate[] templates = urlTemplateConfig.getUrlTemplateArray();
        String configFilePath = getConfigFilePath();
        for (int i = 0; i < templates.length; i++) {
            String name = templates[i].getName();
            if (name != null) {
                name = name.trim();
            } else {
                _log.error("Malformed URL template descriptor in " + configFilePath
                        + ". The url-template name is missing.");
                continue;
            }

            String value = templates[i].getValue();
            if (value != null) {
                value = value.trim();
                if (_log.isDebugEnabled()) {
                    _log.debug("[URLTemplate] " + name + " = " + value);
                }
                URLTemplate urlTemplate = new URLTemplate(value);
                urlTemplate.verify(getKnownTokens(), getRequiredTokens());
                urlTemplates.addTemplate(name, urlTemplate);
            } else {
                _log.error("Malformed URL template descriptor in " + configFilePath
                        + ". The url-template value is missing.");
            }
        }

        // Load template refs
        UrlTemplateRefGroupDocument.UrlTemplateRefGroup[] templateRefGroups = urlTemplateConfig.getUrlTemplateRefGroupArray();
        for (int i = 0; i < templateRefGroups.length; i++) {
            HashMap refGroup = new HashMap();
            String refGroupName = templateRefGroups[i].getName();
            if (refGroupName != null) {
                refGroupName = refGroupName.trim();
            } else {
                _log.error("Malformed URL template descriptor in " + configFilePath
                        + ". The url-template-ref-group name is missing.");
                continue;
            }

            UrlTemplateRefDocument.UrlTemplateRef[] templateRefs = templateRefGroups[i].getUrlTemplateRefArray();
            for (int j = 0; j < templateRefs.length; j++) {
                String key = templateRefs[j].getKey();

                if (key != null) {
                    key = key.trim();
                } else {
                    _log.error("Malformed URL template descriptor in " + configFilePath
                            + ". The url-template-ref key is missing.");
                    continue;
                }

                String name = templateRefs[j].getTemplateName();
                if (name != null) {
                    name = name.trim();
                    refGroup.put(key, name);
                    if (_log.isDebugEnabled()) {
                        _log.debug("[" + refGroupName + " URLTemplate] " + key + " = " + name);
                    }
                } else {
                    _log.error("Malformed URL template descriptor in " + configFilePath
                            + ". The url-template-ref template-name is missing.");
                }
            }

            if (refGroup.size() != 0) {
                urlTemplates.addTemplateRefGroup(refGroupName, refGroup);
            }
        }

        return urlTemplates;
    }
}
