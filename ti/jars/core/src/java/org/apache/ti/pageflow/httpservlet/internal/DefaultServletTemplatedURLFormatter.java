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
package org.apache.ti.pageflow.httpservlet.internal;

import org.apache.commons.chain.web.servlet.ServletWebContext;
import org.apache.ti.core.urls.MutableURI;
import org.apache.ti.core.urls.TemplatedURLFormatter;
import org.apache.ti.core.urls.URIContext;
import org.apache.ti.core.urltemplates.URLTemplate;
import org.apache.ti.core.urltemplates.URLTemplatesFactory;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;

import javax.servlet.http.HttpServletRequest;


/**
 * Default implementation of TemplatedURLFormatter for formatting URLs
 * based on templates from a URL template config file.
 * <p/>
 * <p/>
 * Used by URLRewriterService to apply any relevant templates to a URL,
 * after all other rewriting has been done on the URL.
 * </p>
 */
public class DefaultServletTemplatedURLFormatter extends TemplatedURLFormatter {

    /**
     * Format the given URL using a URL template, if defined in a URL
     * template config file. The {@link org.apache.ti.core.urls.URIContext}
     * encapsulates some additional data needed to write out the string form.
     * E.g. It defines if the &quot;&amp;amp;&quot; entity or the
     * '&amp;' character should be used to separate quary parameters.
     *
     * @param uri        the MutableURI to be formatted into a String.
     * @param key        key for the URL template type to use for formatting the URI
     * @param uriContext data required to write out the string form.
     * @return the URL as a <code>String</code>
     */
    public String getTemplatedURL(MutableURI uri, String key, URIContext uriContext) {
        // Look for the template config and get the right template.
        // If it is found, apply the value to the template.
        String result;
        URLTemplate template = null;
        URLTemplatesFactory factory = URLTemplatesFactory.getURLTemplatesFactory();

        if (factory != null) {
            String templateName = factory.getTemplateNameByRef(DEFAULT_TEMPLATE_REF, key);

            if (templateName != null) {
                template = factory.getURLTemplate(templateName);
            }
        }

        if (template != null) {
            result = formatURIWithTemplate(uri, uriContext, template);
        } else {
            // no template found, just return the uri as a String...
            result = uri.getURIString(uriContext);
        }

        return result;
    }

    private String formatURIWithTemplate(MutableURI uri, URIContext uriContext, URLTemplate template) {
        ServletWebContext webContext = (ServletWebContext) PageFlowActionContext.get().getWebContext();

        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort();
        HttpServletRequest request = webContext.getRequest();

        if (scheme == null || scheme.length() == 0) {
            scheme = request.getScheme();
        }

        if (host == null || host.length() == 0) {
            host = request.getServerName();
        }

        if (port < 0) {
            port = request.getServerPort();
        }

        template.substitute(TemplatedURLFormatter.SCHEME_TOKEN, scheme);
        template.substitute(TemplatedURLFormatter.DOMAIN_TOKEN, host);
        template.substitute(TemplatedURLFormatter.PORT_TOKEN, port);
        template.substitute(TemplatedURLFormatter.PATH_TOKEN, uri.getPath());

        String query;
        query = uri.getQuery(uriContext);

        if (query == null) {
            query = "";
        }

        template.substitute(TemplatedURLFormatter.QUERY_STRING_TOKEN, query);

        String fragment = uri.getFragment();

        if (fragment == null) {
            fragment = "";
        }

        template.substitute(TemplatedURLFormatter.FRAGMENT_TOKEN, fragment);

        return template.format();
    }
}
