package org.apache.struts2.uelplugin;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

@Conversion
public class TestAction extends ActionSupport {
    private TestObject object;
    private int bar;

    public int getBar() {
        return bar;
    }

    public void setBar(int bar) {
        this.bar = bar;
    }

    private String converted;

    public String getConverted() {
        return converted;
    }

    public void setConverted(String converted) {
        this.converted = converted;
    }

    public TestObject getObject() {
        return object;
    }

    public void setObject(TestObject object) {
        this.object = object;
    }
}
