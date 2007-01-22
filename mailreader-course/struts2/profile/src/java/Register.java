import com.opensymphony.util.BeanUtils;
import org.apache.struts.apps.mailreader.dao.User;

public class Register extends MailReaderSupport {

    public String input() throws Exception {

        User user = getUser();
        boolean editing = (null != user);

        if (editing) {
            setTask(EDIT);
            BeanUtils.setValues(this, user, null);
            setPassword(null);
        } else {
            setTask(CREATE);
        }

        return INPUT;
    }

    public String save()
            throws Exception {

        User user = getUser();
        boolean editing = (null != user);

        if (editing) {
            // FIXME: Any way to call the RegisterCreate validators from here?
            String newPassword = getPassword();
            boolean changing = ((null != newPassword) && (newPassword.length() > 0));
            if (!changing) {
                setPassword(user.getPassword());
            } else {
                String confirmPassword = getPassword2();
                boolean matches = ((null != confirmPassword)
                        && (confirmPassword.equals(newPassword)));
                if (matches) {
                    user.setPassword(newPassword);
                } else {
                    addActionError(getText(ERROR_PASSWORD_MATCH));
                    return INPUT;
                }
            }
            BeanUtils.setValues(user, this, null);
        }

        if (!editing) {
            user = findUser(getUsername(), getPassword());
            boolean haveUser = (user != null);

            if (haveUser) {
                addActionError(getText(ERROR_USERNAME_UNIQUE));
                return INPUT;
            }

            user = createUser(getUsername(), getPassword());
            setUser(user);
        }

        saveUser();

        return SUCCESS;
    }
}
