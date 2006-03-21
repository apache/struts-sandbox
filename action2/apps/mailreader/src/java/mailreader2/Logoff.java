package mailreader2;

public class Logoff extends MailreaderSupport {

    public String execute() {

        setUser(null);

        return SUCCESS;
    }
}
