package actions;

import com.opensymphony.xwork2.ActionSupport;

public class Hello extends ActionSupport {

    /*
    public String execute() throws Exception {
        setMessage(getText(MESSAGE));
        return SUCCESS;
    }
    */

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
