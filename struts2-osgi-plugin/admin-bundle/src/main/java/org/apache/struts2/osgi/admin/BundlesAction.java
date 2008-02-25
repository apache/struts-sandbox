package org.apache.struts2.osgi.admin;

import org.apache.struts2.dispatcher.DefaultActionSupport;

public class BundlesAction extends DefaultActionSupport {

    private String id;

    public String index() {
        return SUCCESS;
    }

    public String view() {
        return SUCCESS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
