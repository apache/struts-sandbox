package cookbook2;

import com.opensymphony.xwork.ActionSupport;

public class Buttons extends ActionSupport {

    private String recipient = "Nobody";

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String value) {
        recipient = value;
    }

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        message = value;
    }

    public void setFord(String value) {
        setRecipient("Ford");
    }

    public void setMarvin(String value) {
        setRecipient("Marvin");
    }

    public void setTrillian(String value) {
        setRecipient("Trillian");
    }

}
