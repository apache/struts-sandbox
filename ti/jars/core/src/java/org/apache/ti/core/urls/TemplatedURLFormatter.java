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
package org.apache.ti.core.urls;

import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.schema.config.UrlConfig;
import org.apache.ti.util.config.ConfigUtil;
import org.apache.ti.util.internal.DiscoveryUtils;
import org.apache.ti.util.logging.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Used by URLRewriterService to apply any relevant templates to a URL,
 * after all other rewriting has been done on the URL.
 * <p/>
 * <p/>
 * Offers a mechanism for formatting URLs based on templates from a URL
 * template config file. A template is chosen from a reference group
 * based on the template type (key).
 * </p>
 * <p/>
 * <p/>
 * This class also contains the collection of known and required tokens
 * it expects within URL templates it handles.
 * </p>
 * <p/>
 * <p/>
 * In general, an implementation of this abstract class should be thread-safe,
 * not having state. If it is used as per-webapp default <code>TemplatedURLFormatter</code>,
 * defined in beehive-netui-config.xml (with a class name),
 * <code>URLRewriterService.getTemplatedURL()</code>
 * will share the same instance (from the application - see
 * {@link #getTemplatedURLFormatter}) for multiple simultaneous requests.
 * </p>
 */
public abstract class TemplatedURLFormatter {

    private static final Logger _log = Logger.getInstance(TemplatedURLFormatter.class);

    private static final String TEMPLATED_URL_FORMATTER_ATTR = "_netui:templatedURLFormatter";

    /**
     * The default name for a reference group.
     */
    public static final String DEFAULT_TEMPLATE_REF = "default-url-templates";

    // Base set of tokens
    /**
	 * @todo Finish documenting me!
     */
    public static final String SCHEME_TOKEN = "{url:scheme}";
    
    /**
	 * @todo Finish documenting me!
     */
    public static final String DOMAIN_TOKEN = "{url:domain}";
    
    /**
	 * @todo Finish documenting me!
     */
    public static final String PORT_TOKEN = "{url:port}";
    
    /**
	 * @todo Finish documenting me!
     */
    public static final String PATH_TOKEN = "{url:path}";
    
    /**
	 * @todo Finish documenting me!
     */
    public static final String QUERY_STRING_TOKEN = "{url:queryString}";
    
    /**
	 * @todo Finish documenting me!
     */
    public static final String FRAGMENT_TOKEN = "{url:fragment}";

    private List _knownTokens =
            Arrays.asList(new String[]{SCHEME_TOKEN, DOMAIN_TOKEN, PORT_TOKEN, FRAGMENT_TOKEN});

    private List requiredTokens =
            Arrays.asList(new String[]{PATH_TOKEN, QUERY_STRING_TOKEN});

    /**
     * Gets the TemplatedURLFormatter instance attribute of the application.
     *
     * @return the TemplatedURLFormatter instance from the application.
     */
    public static TemplatedURLFormatter getTemplatedURLFormatter() {
        return (TemplatedURLFormatter) PageFlowActionContext.get().getApplication().get(TEMPLATED_URL_FORMATTER_ATTR);
    }

    /**
	 * @todo Finish documenting me!
     * 
     * @param applicationScope
     * @param defaultFormatter
     * 
     * @return The {@link TemplatedURLFormatter} for this configuration.
     */
    public static TemplatedURLFormatter initApplication(Map applicationScope, TemplatedURLFormatter defaultFormatter) {
        // get the default template formatter class name from the config file
        TemplatedURLFormatter formatter = createTemplatedURLFormatter();
        
        // if there's no TemplatedURLFormatter in the config file, use our default impl.
        if (formatter == null) {
            formatter = defaultFormatter;
        }

        applicationScope.put(TEMPLATED_URL_FORMATTER_ATTR, formatter);
        return formatter;
    }

    private static TemplatedURLFormatter createTemplatedURLFormatter() {
        TemplatedURLFormatter formatter = null;
        
        // check for a default template formatter class name from the config file
        UrlConfig urlConfig = ConfigUtil.getConfig().getUrlConfig();
        if (urlConfig != null && urlConfig.isSetTemplatedUrlFormatterClass()) {
            String className = urlConfig.getTemplatedUrlFormatterClass();
            if (className != null) {
                className = className.trim();
                
                // create an instance of the def template formatter class
                ClassLoader cl = DiscoveryUtils.getClassLoader();

                try {
                    Class formatterClass = cl.loadClass(className);
                    if (!TemplatedURLFormatter.class.isAssignableFrom(formatterClass)) {
                        _log.error("The templated-url-formatter-class, " + className
                                + ", does not extend TemplatedURLFormatter.");
                    } else {
                        formatter = (TemplatedURLFormatter) formatterClass.newInstance();
                    }
                } catch (ClassNotFoundException e) {
                    _log.error("Could not find templated-url-formatter-class " + className, e);
                } catch (InstantiationException e) {
                    _log.error("Could not instantiate templated-url-formatter-class " + className, e);
                } catch (IllegalAccessException e) {
                    _log.error("Could not instantiate templated-url-formatter-class " + className, e);
                }
            }
        }

        return formatter;
    }

    /**
     * Returns the list of the known tokens (strings) that this URL template
     * formatter handles. These strings can be used for the template verification.
     * Tokens are expected to be qualified in braces. E.g. {url:path}
     *
     * @return the list of strings for the known tokens
     */
    public List getKnownTokens() {
        return _knownTokens;
    }

    /**
     * Returns the list of the tokens (strings) that this URL template
     * requires when formatting a URL with a template. These strings can
     * be used for the template verification. Tokens are expected to be
     * qualified in braces. E.g. {url:path}
     *
     * @return the list of strings for the known tokens
     */
    public List getRequiredTokens() {
        return requiredTokens;
    }

    /**
     * Format the given URL using a URL template, if defined in a URL
     * template config file. The {@link URIContext}
     * encapsulates some additional data needed to write out the string form.
     * E.g. It defines if the &quot;&amp;amp;&quot; entity or the
     * '&amp;' character should be used to separate quary parameters.
     *
     * @param uri        the MutableURI to be formatted into a String.
     * @param key        key for the URL template type to use for formatting the URI
     * @param uriContext data required to write out the string form.
     * @return the URL as a <code>String</code>
     */
    public abstract String getTemplatedURL(MutableURI uri, String key, URIContext uriContext);

    protected void setKnownTokens(List knownTokens) {
        _knownTokens = knownTokens;
    }

    protected void setRequiredTokens(List requiredTokens) {
        this.requiredTokens = requiredTokens;
    }
}
