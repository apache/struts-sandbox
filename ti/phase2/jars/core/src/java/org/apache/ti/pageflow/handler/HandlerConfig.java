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
package org.apache.ti.pageflow.handler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for encapsulating configuration properties associated with a {@link Handler}.
 */
public class HandlerConfig
        implements Serializable {

    private Map/*< String, String >*/ _customProperties = new HashMap/*< String, String >*/();
    private String _handlerClass;

    public HandlerConfig(String handlerClass) {
        _handlerClass = handlerClass;
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

    public String getHandlerClass() {
        return _handlerClass;
    }
}
