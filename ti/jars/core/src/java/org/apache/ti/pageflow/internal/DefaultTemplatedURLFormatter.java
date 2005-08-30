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

import org.apache.ti.core.urls.MutableURI;
import org.apache.ti.core.urls.TemplatedURLFormatter;
import org.apache.ti.core.urls.URIContext;


/**
 * Default implementation of TemplatedURLFormatter for formatting URLs
 * based on templates from a URL template config file.
 * <p/>
 * <p/>
 * Used by URLRewriterService to apply any relevant templates to a URL,
 * after all other rewriting has been done on the URL.
 * </p>
 */
public class DefaultTemplatedURLFormatter extends TemplatedURLFormatter {

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
        return uri.getURIString(uriContext);
    }
}

