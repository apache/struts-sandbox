package org.apache.ti.util.config.bean;


/**
 *
 */
public class SharedFlowRefConfig {
    private String _name;
    private String _type;

    public SharedFlowRefConfig(String name, String type) {
        _name = name;
        _type = type;
    }

    public String getName() {
        return _name;
    }

    public String getType() {
        return _type;
    }
}
