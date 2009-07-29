package org.apache.struts2.json;

public class TestAction4 {
    private Bean bean;

    public Bean getBean() {
        if (this.bean == null)
            this.bean = new Bean();
        return this.bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
    }
}
