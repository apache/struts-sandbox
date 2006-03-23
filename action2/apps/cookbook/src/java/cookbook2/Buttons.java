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

    public void setFord(boolean value) {
        if (value) return;
        setRecipient("Ford");
    }

    public void setMarvin(boolean value) {
        if (value) return;
        setRecipient("Marvin");
    }

    public void setTrillian(boolean value) {
        if (value) return;
        setRecipient("Trillian");
    }

}
