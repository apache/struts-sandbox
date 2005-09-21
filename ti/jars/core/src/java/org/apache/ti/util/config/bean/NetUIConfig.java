/**
 Copyright 2004 The Apache Software Foundation.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 $Header:$
 */
package org.apache.ti.util.config.bean;


/**
 *
 */
public class NetUIConfig {
    private PageFlowActionInterceptorsConfig _pageFlowActionInterceptors;
    private PageFlowHandlersConfig _pageFlowHandlers;
    private PageFlowConfig _pageFlowConfig;
    private PageFlowFactoriesConfig _pageFlowFactories;
    private SharedFlowRefConfig[] _sharedFlowRef;
    private RequestInterceptorsConfig _requestInterceptors;
    private JspTagConfig _jspTagConfig;
    private PrefixHandlerConfig[] _prefixHandlers;
    private ExpressionLanguagesConfig _expressionLanguages;
    private IteratorFactoryConfig[] _iteratorFactories;
    private TypeConverterConfig[] _typeConverters;
    private UrlConfig _urlConfig;

    public NetUIConfig(PageFlowActionInterceptorsConfig pageFlowActionInterceptors, PageFlowHandlersConfig pageFlowHandlers,
                       PageFlowConfig pageFlowConfig, PageFlowFactoriesConfig pageFlowFactories,
                       SharedFlowRefConfig[] sharedFlowRef, RequestInterceptorsConfig requestInterceptors,
                       JspTagConfig jspTagConfig, PrefixHandlerConfig[] prefixHandlers,
                       ExpressionLanguagesConfig expressionLanguages, IteratorFactoryConfig[] iteratorFactories,
                       TypeConverterConfig[] typeConverters, UrlConfig urlConfig) {
        _pageFlowActionInterceptors = pageFlowActionInterceptors;
        _pageFlowHandlers = pageFlowHandlers;
        _pageFlowConfig = pageFlowConfig;
        _pageFlowFactories = pageFlowFactories;
        _sharedFlowRef = sharedFlowRef;
        _requestInterceptors = requestInterceptors;
        _jspTagConfig = jspTagConfig;
        _prefixHandlers = prefixHandlers;
        _expressionLanguages = expressionLanguages;
        _iteratorFactories = iteratorFactories;
        _typeConverters = typeConverters;
        _urlConfig = urlConfig;
    }

    public PageFlowActionInterceptorsConfig getPageFlowActionInterceptors() {
        return _pageFlowActionInterceptors;
    }

    public PageFlowHandlersConfig getPageFlowHandlers() {
        return _pageFlowHandlers;
    }

    public PageFlowConfig getPageFlowConfig() {
        return _pageFlowConfig;
    }

    public PageFlowFactoriesConfig getPageFlowFactories() {
        return _pageFlowFactories;
    }

    public SharedFlowRefConfig[] getSharedFlowRefs() {
        return _sharedFlowRef;
    }

    public RequestInterceptorsConfig getRequestInterceptors() {
        return _requestInterceptors;
    }

    public JspTagConfig getJspTagConfig() {
        return _jspTagConfig;
    }

    public PrefixHandlerConfig[] getPrefixHandlers() {
        return _prefixHandlers;
    }

    public ExpressionLanguagesConfig getExpressionLanguages() {
        return _expressionLanguages;
    }

    public IteratorFactoryConfig[] getIteratorFactories() {
        return _iteratorFactories;
    }

    public TypeConverterConfig[] getTypeConverters() {
        return _typeConverters;
    }

    public UrlConfig getUrlConfig() {
        return _urlConfig;
    }
}
