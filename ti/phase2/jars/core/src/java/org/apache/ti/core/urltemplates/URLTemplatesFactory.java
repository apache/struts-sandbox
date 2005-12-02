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

import org.apache.commons.chain.web.WebContext;

import org.apache.ti.core.factory.Factory;
import org.apache.ti.core.urls.TemplatedURLFormatter;
import org.apache.ti.pageflow.ContainerAdapter;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;
import org.apache.ti.util.SourceResolver;

import java.util.Collection;

/**
 * Access point to URL templates (an optional config file to help format
 * rewritten URLs) used by a {@link org.apache.ti.core.urls.TemplatedURLFormatter}
 * via the {@link org.apache.ti.core.urls.URLRewriterService}.
 */
public abstract class URLTemplatesFactory
        extends Factory {
    private static final String URL_TEMPLATE_FACTORY_ATTR = "_netui:urlTemplatesFactory";

    // Constants for URL template types
    public static final String DEFAULT_TEMPLATE = "default";
    public static final String SECURE_DEFAULT_TEMPLATE = "secure-default";
    public static final String ACTION_TEMPLATE = "action";
    public static final String SECURE_ACTION_TEMPLATE = "secure-action";
    public static final String RESOURCE_TEMPLATE = "resource";
    public static final String SECURE_RESOURCE_TEMPLATE = "secure-resource";
    public static final String RENDER_TEMPLATE = "render";
    public static final String SECURE_RENDER_TEMPLATE = "secure-render";

    /**
     * Default value for path from the web app to the URL templates.
     */
    public static final String DEFAULT_URL_TEMPLATE_CONFIG_FILE_PATH = "/WEB-INF/beehive-url-template-config.xml";

    // Path to the URL templates config file.
    protected String _configFilePath = DEFAULT_URL_TEMPLATE_CONFIG_FILE_PATH;

    // The known tokens (collection of String objects) in a valid template.
    protected Collection _knownTokens = null;

    // The required tokens (collection of String objects) in a valid template.
    protected Collection _requiredTokens = null;

    /**
     * Gets the URLTemplatesFactory instance attribute of the application.
     *
     * @return the URLTemplatesFactory instance from the application scope.
     */
    public static URLTemplatesFactory getURLTemplatesFactory() {
        return (URLTemplatesFactory) PageFlowActionContext.get().getApplication().get(URL_TEMPLATE_FACTORY_ATTR);
    }

    /**
     * Adds a given URLTemplatesFactory instance as an attribute of the application.
     */
    public static void initApplication(WebContext webContext, URLTemplatesFactory defaultFactory,
                                       TemplatedURLFormatter formatter, ContainerAdapter containerAdapter,
                                       SourceResolver sourceResolver) {
        // URLTemplatesFactory has not been initialized,
        // get a URLTemplatesFactory object from the containerAdapter.
        URLTemplatesFactory templatesFactory = createURLTemplatesFactory(defaultFactory, containerAdapter);

        // get the known/req tokens from the default formatter for the factory to use to verify templates
        templatesFactory.setKnownTokens(formatter.getKnownTokens());
        templatesFactory.setRequiredTokens(formatter.getRequiredTokens());
        templatesFactory.load(webContext, sourceResolver);
        webContext.getApplicationScope().put(URL_TEMPLATE_FACTORY_ATTR, templatesFactory);
    }

    /**
     * Get an uninitialized instance of a container specific URLTemplatesFactory
     * from the ContainerAdapter. If none exists, this returns an instance of a given default.
     * Caller should then set the known
     * and required tokens, call the {@link URLTemplatesFactory#load}
     * method and {@link URLTemplatesFactory#initApplication}.
     * <p/>
     * <p/>
     * IMPORTANT NOTE - Always try to get the application instance from the ServletContext
     * by calling {@link URLTemplatesFactory#getURLTemplatesFactory}.
     * Then, if a new URLTemplatesFactory must be created, call this method.
     * </p>
     *
     * @return a container specific implementation of URLTemplatesFactory, or
     *         {@link org.apache.ti.pageflow.internal.DefaultURLTemplatesFactory}.
     */
    private static URLTemplatesFactory createURLTemplatesFactory(URLTemplatesFactory defaultFactory,
                                                                 ContainerAdapter containerAdapter) {
        // get the URLTemplatesFactory from the containerAdapter.
        URLTemplatesFactory factory = (URLTemplatesFactory) containerAdapter.getFactory(URLTemplatesFactory.class, null, null);

        // if there's no URLTemplatesFactory, use our default impl.
        return (factory != null) ? factory : defaultFactory;
    }

    /**
     * Allow clients to set their own URL template config file name/path.
     *
     * @param configFilePath An absolute path from the web app root to the URL template config file.
     */
    public void setConfigFilePath(String configFilePath) {
        if (configFilePath == null) {
            throw new IllegalArgumentException("Config file path cannot be null.");
        }

        _configFilePath = configFilePath;
    }

    /**
     * Allow clients to define a set of known tokens for the
     * template verification. Tokens are expected to be qualified
     * in braces. E.g. {url:path}
     * <p/>
     * The template verification will ensure the known tokens in the
     * URL template conforms to a valid format.
     *
     * @param knownTokens The set of known tokens for a valid template.
     */
    public void setKnownTokens(Collection knownTokens) {
        _knownTokens = knownTokens;
    }

    /**
     * Allow clients to define a set of required tokens for the
     * template verification. Tokens are expected to be qualified
     * in braces. E.g. {url:path}
     * <p/>
     * The template verification will ensure the URL template conforms to
     * a valid format for known tokens and contains the required tokens.
     * </p>
     *
     * @param requiredTokens The set of required tokens in a valid template.
     */
    public void setRequiredTokens(Collection requiredTokens) {
        _requiredTokens = requiredTokens;
    }

    /**
     * Returns URL template given the name of the template.
     *
     * @param name name of the template
     * @return template
     */
    public abstract URLTemplate getURLTemplate(String name);

    /**
     * Returns URL template name of the given type (by key) from the
     * desired reference group.
     *
     * @param refGroupName name of a group of templates from the config file.
     * @param key          type of the template
     * @return template name
     */
    public abstract String getTemplateNameByRef(String refGroupName, String key);

    /**
     * Initialization method that parses the URL template config file to
     * get the URL templates and template reference groups.
     */
    protected abstract void load(WebContext webContext, SourceResolver sourceResolver);

    protected String getConfigFilePath() {
        return _configFilePath;
    }

    protected Collection getKnownTokens() {
        return _knownTokens;
    }

    protected Collection getRequiredTokens() {
        return _requiredTokens;
    }
}
