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
