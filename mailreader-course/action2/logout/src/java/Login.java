import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.apps.mailreader.dao.User;
import com.opensymphony.util.BeanUtils;

public class Login extends MailReaderSupport {

    public String execute() throws ExpiredPasswordException {

        User user = findUser(getUsername(), getPassword());

        if (user != null) {
            setUser(user);
            BeanUtils.setValues(this, user, null);
        }

        if (hasErrors()) {
            return INPUT;
        }

        return SUCCESS;

    }

}
