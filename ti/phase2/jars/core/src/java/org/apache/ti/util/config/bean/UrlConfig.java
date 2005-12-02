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
public class UrlConfig {
    private static final boolean DEFAULT_URL_ENCODE_URLS = true;
    private static final boolean DEFAULT_HTML_AMP_ENTITY = true;
    private static final String DEFAULT_TEMPLATED_URL_FORMATTER_CLASS = "org.apache.ti.pageflow.internal.DefaultTemplatedURLFormatter";
    private boolean _urlEncodeUrls;
    private boolean _htmlAmpEntity = DEFAULT_HTML_AMP_ENTITY;
    private String _templatedUrlFormatterClass;

    public UrlConfig() {
        _urlEncodeUrls = DEFAULT_URL_ENCODE_URLS;
        _htmlAmpEntity = DEFAULT_HTML_AMP_ENTITY;
        _templatedUrlFormatterClass = DEFAULT_TEMPLATED_URL_FORMATTER_CLASS;
    }

    public UrlConfig(Boolean urlEncodeUrls, Boolean htmlAmpEntity, String templatedUrlFormatterClass) {
        this();

        if (urlEncodeUrls != null) {
            _urlEncodeUrls = urlEncodeUrls.booleanValue();
        }

        if (htmlAmpEntity != null) {
            _htmlAmpEntity = htmlAmpEntity.booleanValue();
        }

        if (templatedUrlFormatterClass != null) {
            _templatedUrlFormatterClass = templatedUrlFormatterClass;
        }
    }

    public boolean isUrlEncodeUrls() {
        return _urlEncodeUrls;
    }

    public boolean isHtmlAmpEntity() {
        return _htmlAmpEntity;
    }

    public String getTemplatedUrlFormatterClass() {
        return _templatedUrlFormatterClass;
    }
}
