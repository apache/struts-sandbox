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

import java.util.Arrays;
import java.util.List;

/**
 * Maintains optional deployment information about templates and the
 * URL template config file.
 *
 * @see URLTemplatesFactory
 * @deprecated As of v1m1. The preferred method is to use the URLTemplatesFactory
 *             directly. There should be an instance of a {@link URLTemplatesFactory}
 *             available as an attribute of the ServletContext.
 */
public class URLTemplateDescriptor {

    // Singleton instance
    private static URLTemplateDescriptor instance = new URLTemplateDescriptor();

    // Constants for URL template types
    public static final String DEFAULT_TEMPLATE = "default";
    public static final String SECURE_DEFAULT_TEMPLATE = "secure-default";
    public static final String ACTION_TEMPLATE = "action";
    public static final String SECURE_ACTION_TEMPLATE = "secure-action";
    public static final String RESOURCE_TEMPLATE = "resource";
    public static final String SECURE_RESOURCE_TEMPLATE = "secure-resource";
    public static final String RENDER_TEMPLATE = "render";
    public static final String SECURE_RENDER_TEMPLATE = "secure-render";

    // Tokens
    public static final String SCHEME_TOKEN = "{url:scheme}";
    public static final String DOMAIN_TOKEN = "{url:domain}";
    public static final String PORT_TOKEN = "{url:port}";
    public static final String PATH_TOKEN = "{url:path}";
    public static final String QUERY_STRING_TOKEN = "{url:queryString}";
    public static final String FRAGMENT_TOKEN = "{url:fragment}";

    private static final List KNOWN_TEMPLATE_TOKENS =
            Arrays.asList(new String[]{SCHEME_TOKEN, DOMAIN_TOKEN, PORT_TOKEN, FRAGMENT_TOKEN});

    private static final List REQUIRED_TEMPLATE_TOKENS =
            Arrays.asList(new String[]{PATH_TOKEN, QUERY_STRING_TOKEN});

    /**
     * Constructs an instance.
     */
    protected URLTemplateDescriptor() {
    }

    /**
     * Returns URL template given the name of the template.
     *
     * @param name name of the template
     * @return template
     */
    public URLTemplate getURLTemplate(String name) {
        URLTemplate urlTemplate = null;

        URLTemplatesFactory urlTemplatesFactory = URLTemplatesFactory.getURLTemplatesFactory();
        if (urlTemplatesFactory != null) {
            urlTemplate = urlTemplatesFactory.getURLTemplate(name);
        }

        return urlTemplate;
    }

    /**
     * Returns URL template name of the given type (by key).
     *
     * @param refGroupName name of a group of templates from the config file.
     * @param key          type of the template
     * @return template name
     */
    public String getURLTemplateRef(String refGroupName, String key) {
        String ref = null;

        URLTemplatesFactory urlTemplatesFactory = URLTemplatesFactory.getURLTemplatesFactory();
        if (urlTemplatesFactory != null) {
            ref = urlTemplatesFactory.getTemplateNameByRef(refGroupName, key);
        }

        return ref;
    }

    /**
     * Returns an instance of <code>URLTemplateDescriptor</code>.
     *
     * @return portal app descriptor
     */
    public static URLTemplateDescriptor getInstance() {
        return instance;
    }

    public synchronized void load() {
    }
}
