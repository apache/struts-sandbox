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
package org.apache.ti.pageflow.internal;

import org.apache.ti.core.urls.MutableURI;
import org.apache.ti.core.urls.URIContext;
import org.apache.ti.schema.config.UrlConfig;
import org.apache.ti.util.config.ConfigUtil;

/**
 * Factory for the {@link URIContext} with the data needed to write out
 * a string form of a {@link MutableURI}.
 */
public final class URIContextFactory {

    /* do not construct */
    private URIContextFactory() {
    }

    /**
     * Get a URIContext. The context has data used to write a MutableURI
     * as a string. For example, it will indicate that the URI should be
     * written using the &quot;&amp;amp;&quot; entity, rather than the
     * character, '&amp;'. This returns the default context, but also
     * checks for any overriding setting in the NetUI config.
     *
     * @return the URIContext
     */
    public static final URIContext getInstance() {
        URIContext uriContext = MutableURI.getDefaultContext();
        UrlConfig urlConfig = ConfigUtil.getConfig().getUrlConfig();

        if (urlConfig != null && urlConfig.isSetHtmlAmpEntity()) {
            uriContext.setUseAmpEntity(urlConfig.getHtmlAmpEntity());
        }

        return uriContext;
    }

    /**
     * Get a URIContext. If it's for an XML document type, the context
     * will indicate that the URI should be written using the
     * &quot;&amp;amp;&quot; entity, rather than the character, '&amp;'.
     * If it's not for an XML doc type, then use the default context,
     * but check for any overriding setting in the NetUI config.
     *
     * @param forXML flag indicating that the URI is for an XML doc type
     * @return the URIContext
     */
    public static final URIContext getInstance(boolean forXML) {
        URIContext uriContext = null;

        if (forXML) {
            uriContext = new URIContext();
            uriContext.setUseAmpEntity(true);
        } else {
            uriContext = getInstance();
        }

        return uriContext;
    }
}
