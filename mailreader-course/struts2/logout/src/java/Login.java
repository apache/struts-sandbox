import com.opensymphony.util.BeanUtils;
import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.apps.mailreader.dao.User;

public class Login extends MailReaderSupport {

    public String save() throws ExpiredPasswordException {

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
