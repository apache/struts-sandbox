/*
 * $Id$
 *
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ti.config.mapper;

import java.util.Map;

/**
 * Simple class that holds the action mapping information used to invoke an
 * action. The name and namespace are required, but the params map
 * is optional, and as such may be null. If a params map is supplied,
 * it <b>must</b> be a mutable map, such as a HashMap.
 *
 * @author Patrick Lightbody
 */
public class ActionMapping {
    private String name;
    private String externalMapping;
    private String namespace;
    private Map params;

    public ActionMapping(String name, String namespace, String extMapping, Map params) {
        this.name = name;
        this.namespace = namespace;
        this.params = params;
        this.externalMapping = extMapping;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public Map getParams() {
        return params;
    }
    
    public String getExternalMapping() {
        return externalMapping;
    }
}
