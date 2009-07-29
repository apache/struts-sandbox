package org.apache.struts2.json;

/**
 * A more complicated Enum with additional properties.
 */
public enum AnEnumBean {

    One("A", "B"), Two("C", "D"), Three("E", "F");

    private String propA;
    private String propB;

    AnEnumBean(String propA, String propB) {
        this.propA = propA;
        this.propB = propB;
    }

    public String getPropA() {
        return propA;
    }

    public String getPropB() {
        return propB;
    }
}
