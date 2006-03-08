package cookbook2;

import com.opensymphony.xwork.ActionSupport;

public class Simple extends ActionSupport {

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

    public String input() throws Exception {
        name = "glock";
        return SUCCESS;
    }

    public String result() throws Exception {
        return SUCCESS;
    }
}
