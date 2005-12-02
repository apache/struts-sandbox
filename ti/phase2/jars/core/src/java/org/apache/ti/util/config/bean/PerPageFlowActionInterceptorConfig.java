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
public class PerPageFlowActionInterceptorConfig {
    private String _pageflowUri;
    private SimpleActionInterceptorConfig[] _simpleActionInterceptors;
    private InterceptorConfig[] _actionInterceptors;
    private PerActionInterceptorConfig[] _perActionInterceptors;

    public PerPageFlowActionInterceptorConfig(String pageflowUri, SimpleActionInterceptorConfig[] simpleActionInterceptors,
                                              InterceptorConfig[] actionInterceptors,
                                              PerActionInterceptorConfig[] perActionInterceptors) {
        _pageflowUri = pageflowUri;
        _simpleActionInterceptors = simpleActionInterceptors;
        _actionInterceptors = actionInterceptors;
        _perActionInterceptors = perActionInterceptors;
    }

    public String getPageFlowUri() {
        return _pageflowUri;
    }

    public SimpleActionInterceptorConfig[] getSimpleActionInterceptors() {
        return _simpleActionInterceptors;
    }

    public InterceptorConfig[] getActionInterceptors() {
        return _actionInterceptors;
    }

    public PerActionInterceptorConfig[] getPerActionInterceptors() {
        return _perActionInterceptors;
    }
}
