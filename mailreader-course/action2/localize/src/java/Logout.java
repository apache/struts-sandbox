public class Logout extends MailReaderSupport {

    public String execute() {

        setUser(null);

        return SUCCESS;
    }
}
