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
package org.apache.ti.pageflow.interceptor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to hold configuration parameters for registered {@link Interceptor}s.
 */
public class InterceptorConfig
        implements Serializable {

    private String _interceptorClass;
    private Map/*< String, String >*/ _customProperties = new HashMap/*< String, String >*/();

    protected InterceptorConfig() {
    }

    protected InterceptorConfig(String interceptorClass) {
        _interceptorClass = interceptorClass;
    }

    public String getInterceptorClass() {
        return _interceptorClass;
    }

    public void setInterceptorClass(String interceptorClass) {
        _interceptorClass = interceptorClass;
    }

    public Map/*< String, String >*/ getCustomProperties() {
        return _customProperties;
    }

    void addCustomProperty(String name, String value) {
        _customProperties.put(name, value);
    }

    public String getCustomProperty(String name) {
        return (String) _customProperties.get(name);
    }
}
