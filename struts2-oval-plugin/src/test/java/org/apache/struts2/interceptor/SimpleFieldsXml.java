package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionSupport;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

public class SimpleFieldsXml extends ActionSupport {
    @NotNull
    private String firstName;

    @NotEmpty
    private String lastName = "";

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
