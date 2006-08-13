import org.apache.struts.apps.mailreader.dao.User;

public class Register extends MailReaderSupport {

    public String save()
            throws Exception {

        User user = findUser(getUsername(), getPassword());
        boolean haveUser = (user != null);

        if (haveUser) {
            addActionError(ERROR_USERNAME_UNIQUE);
            return INPUT;
        }

        createUser(getUsername(), getPassword());

        saveUser();

        return SUCCESS;
    }

}
