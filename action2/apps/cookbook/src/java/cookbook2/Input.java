package cookbook2;

import com.opensymphony.xwork.ActionSupport;

public class Input extends ActionSupport {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }
}
