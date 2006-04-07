import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.apps.mailreader.dao.User;

public class Login extends MailReaderSupport {

    public String execute() throws ExpiredPasswordException {

        User user = findUser(getUsername(), getPassword());

        if (user != null) {
            setUser(user);
        }

        if (hasErrors()) {
            return INPUT;
        }

        return SUCCESS;

    }

}
