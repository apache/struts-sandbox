package actions;

import org.apache.struts.validator.ValidatorForm;

public class HelloForm extends ValidatorForm {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
