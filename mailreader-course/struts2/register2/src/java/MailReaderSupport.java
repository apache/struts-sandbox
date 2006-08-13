import com.opensymphony.util.BeanUtils;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.apps.mailreader.dao.User;
import org.apache.struts.apps.mailreader.dao.UserDatabase;
import org.apache.struts2.interceptor.ApplicationAware;

import java.util.Map;

public class MailReaderSupport extends ActionSupport implements ApplicationAware {

    // ---- Register form properties ----

    private String username = null;

    public String getUsername() {
        return username;
    }

    public void setUsername(String value) {
        username = value;
    }

    private String password = null;

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        password = value;
    }

    private String password2 = null;

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String value) {
        password2 = value;
    }

    private String fullName = null;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String value) {
        fullName = value;
    }

    private String fromAddress = null;

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String value) {
        fromAddress = value;
    }

    private String replyToAddress = null;

    public String getReplyToAddress() {
        return replyToAddress;
    }

    public void setReplyToAddress(String value) {
        replyToAddress = value;
    }

    // ---- Keys ----

    public static final String DATABASE_KEY = "database";

    public static final String USER_KEY = "user";

    // ---- Messages ----

    public static final String ERROR_DATABASE_MISSING =
            "Database is missing";

    public static String ERROR_USERNAME_UNIQUE =
            "That username is already in use - please select another";

    // ---- ApplicationAware ----

    private Map application;

    public void setApplication(Map value) {
        application = value;
    }

    public Map getApplication() {
        return application;
    }

    // ---- Database property ----

    public UserDatabase getDatabase() {
        Object db = getApplication().get(DATABASE_KEY);
        if (db == null) {
            this.addActionError(ERROR_DATABASE_MISSING);
        }
        return (UserDatabase) db;
    }

    public void setDatabase(UserDatabase database) {
        getApplication().put(DATABASE_KEY, database);
    }

    // ---- Database methods ----

    public User findUser(String username, String password)
            throws ExpiredPasswordException {
        return getDatabase().findUser(username);
    }

    public User createUser(String username, String password) throws Exception {

        UserDatabase database = getDatabase();
        User user = database.findUser(username);

        if (user != null) {
            addActionError(ERROR_USERNAME_UNIQUE);
            return null;
        }

        user = database.createUser(username);
        BeanUtils.setValues(user, this, null);

        return user;
    }

    public void saveUser() throws Exception {
        getDatabase().save();
    }

}
