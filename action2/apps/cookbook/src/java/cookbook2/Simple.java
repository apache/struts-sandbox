package cookbook2;

import com.opensymphony.xwork.ActionSupport;

public class Simple extends ActionSupport {

    private String name;

    public void setName(String value) {
        name = value;
    }

    public String getName() {
        return this.name;
    }


    public String execute() {

        if (this.hasErrors()) {
            return INPUT;
        }

        return SUCCESS;
    }

}
