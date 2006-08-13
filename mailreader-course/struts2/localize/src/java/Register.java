import com.opensymphony.xwork2.Action;
import org.apache.struts.apps.mailreader.dao.User;

public class Register extends MailReaderSupport {

    public String save()
            throws Exception {

        User user = findUser(getUsername(), getPassword());
        boolean haveUser = (user != null);

        if (haveUser) {
            addActionError(getText(ERROR_USERNAME_UNIQUE));
            return Action.INPUT;
        }

        user = createUser(getUsername(), getPassword());

        setUser(user);

        saveUser();

        return Action.SUCCESS;
    }

}
