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
package org.apache.ti.core.urltemplates;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The key class to get information from url-template-config.
 */
public class URLTemplates implements Serializable {

    private HashMap/*< String, URLTemplate >*/ _templates = new HashMap/*< String, URLTemplate >*/();
    private HashMap/*< String, Map< String, String > >*/ _templateRefGroups =
            new HashMap/*< String, Map< String, String > >*/();

    /**
     * Add a template from url-template-config by name.
     *
     * @param templateName the name of the template.
     * @param template     the template to add.
     */
    public void addTemplate(String templateName, URLTemplate template) {
        if (templateName == null || templateName.length() == 0) {
            throw new IllegalArgumentException("Template name cannot be null or empty.");
        }

        if (template == null) {
            throw new IllegalArgumentException("URLTemplate cannot be null.");
        }

        _templates.put(templateName, template);
    }

    /**
     * Retrieve a template from url-template-config by name.
     * Always returns a copy of a URLTemplate with the same
     * parsed template data but its own cleared set of
     * token values for the substitue() methods.
     * This allows multiple client requests access to
     * the same parsed template structure, without requiring
     * it to be parsed for each request.
     *
     * @param templateName the name of the template.
     * @return a URLTemplate copy with its own empty map for storing
     *         token replacement values.
     */
    public URLTemplate getTemplate(String templateName) {
        URLTemplate template = (URLTemplate) _templates.get(templateName);
        if (template == null) return null;

        return new URLTemplate(template);
    }

    /**
     * Add a template reference group from url-template-config by name.
     *
     * @param refGroupName     the name of the template reference group.
     * @param templateRefGroup the template reference group.
     */
    public void addTemplateRefGroup(String refGroupName, Map/*< String, String >*/ templateRefGroup) {
        if (refGroupName == null || refGroupName.length() == 0) {
            throw new IllegalArgumentException("Template Reference Group name cannot be null or empty.");
        }

        if (templateRefGroup == null || templateRefGroup.size() == 0) {
            throw new IllegalArgumentException("Template Reference Group cannot be null or empty.");
        }

        _templateRefGroups.put(refGroupName, templateRefGroup);
    }

    /**
     * Retrieve a template name from a reference group in url-template-config.
     *
     * @param refGroupName the name of the template reference group.
     * @param key          the key to the particular template reference in the group.
     * @return a template name from the reference group.
     */
    public String getTemplateNameByRef(String refGroupName, String key) {
        String templateName = null;
        Map/*< String, String >*/ templateRefGroup = (Map) _templateRefGroups.get(refGroupName);
        if (templateRefGroup != null) {
            templateName = (String) templateRefGroup.get(key);
        }

        return templateName;
    }
}
