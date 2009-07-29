package org.apache.struts2.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Bean {
    private String stringField;
    private int intField;
    private boolean booleanField;
    private char charField;
    private long longField;
    private float floatField;
    private double doubleField;
    private Object objectField;
    private byte byteField;
    private AnEnum enumField;
    private AnEnumBean enumBean;
    private BigDecimal bigDecimal;
    private BigInteger bigInteger;

    /**
     * @return the byteField
     */
    public byte getByteField() {
        return this.byteField;
    }

    /**
     * @param byteField
     *            the byteField to set
     */
    public void setByteField(byte byteField) {
        this.byteField = byteField;
    }

    public boolean isBooleanField() {
        return this.booleanField;
    }

    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
    }

    public char getCharField() {
        return this.charField;
    }

    public void setCharField(char charField) {
        this.charField = charField;
    }

    public double getDoubleField() {
        return this.doubleField;
    }

    public void setDoubleField(double doubleField) {
        this.doubleField = doubleField;
    }

    public float getFloatField() {
        return this.floatField;
    }

    public void setFloatField(float floatField) {
        this.floatField = floatField;
    }

    public int getIntField() {
        return this.intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public long getLongField() {
        return this.longField;
    }

    public void setLongField(long longField) {
        this.longField = longField;
    }

    public Object getObjectField() {
        return this.objectField;
    }

    public void setObjectField(Object objectField) {
        this.objectField = objectField;
    }

    public String getStringField() {
        return this.stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public AnEnum getEnumField() {
        return enumField;
    }

    public void setEnumField(AnEnum enumField) {
        this.enumField = enumField;
    }

    public AnEnumBean getEnumBean() {
        return enumBean;
    }

    public void setEnumBean(AnEnumBean enumBean) {
        this.enumBean = enumBean;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }

    public void setBigInteger(BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }
}
