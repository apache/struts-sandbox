package org.apache.struts2.json;

import org.apache.struts2.json.annotations.SMD;
import org.apache.struts2.json.annotations.SMDMethod;
import org.apache.struts2.json.annotations.SMDMethodParameter;

@SMD(objectName = "testaction", serviceType = "service", version = "10.0")
public class SMDActionTest2 {
    private boolean doSomethingInvoked;

    @SMDMethod
    public void add(@SMDMethodParameter(name = "a")
    int a, @SMDMethodParameter(name = "b")
    int b) {
    }

    @SMDMethod(name = "doSomethingElse")
    public void doSomething() {
        doSomethingInvoked = true;
    }

    @SMDMethod
    public Bean getBean() {
        Bean bean = new Bean();
        bean.setStringField("str");
        bean.setBooleanField(true);
        bean.setCharField('s');
        bean.setDoubleField(10.1);
        bean.setFloatField(1.5f);
        bean.setIntField(10);
        bean.setLongField(100);
        return bean;
    }

    public boolean isDoSomethingInvoked() {
        return doSomethingInvoked;
    }
}
