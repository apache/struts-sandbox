import com.opensymphony.util.BeanUtils;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.apps.mailreader.dao.Subscription;
import org.apache.struts.apps.mailreader.dao.User;
import org.apache.struts.apps.mailreader.dao.UserDatabase;
import org.apache.struts.apps.mailreader.dao.impl.memory.MemorySubscription;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class MailReaderSupport extends ActionSupport
        implements ApplicationAware, SessionAware {

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

    // ---- Subscription form properties ----

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String value) {
        host = value;
    }

    // ---- Task property (utilized by UI) ----

    private String task = null;

    public String getTask() {
        return task;
    }

    public void setTask(String value) {
        task = value;
    }

    // ---- Keys ----

    public static final String DATABASE_KEY = "database";

    public static final String SUBSCRIPTION_KEY = "subscription";

    public static final String USER_KEY = "user";

    public static final String HOST = "host";

    public static final String PASSWORD_MISMATCH_FIELD = "password";

    public static final String CANCEL = "cancel";

    public static final String CREATE = "Create";

    public static final String EDIT = "Edit";

    public static final String DELETE = "Delete";

    // ---- Message Keys ----

    public static final String ERROR_DATABASE_MISSING =
            "error.database.missing";

    public static final String ERROR_USERNAME_UNIQUE =
            "error.username.unique";

    public static final String ERROR_PASSWORD_MISMATCH =
            "error.password.mismatch";

    public static final String ERROR_PASSWORD_MATCH =
            "error.password.match";

    public static final String ERROR_HOST_UNIQUE
            = "That hostname is already defined";

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
            this.addActionError(getText(ERROR_DATABASE_MISSING));
        }
        return (UserDatabase) db;
    }

    public void setDatabase(UserDatabase database) {
        getApplication().put(DATABASE_KEY, database);
    }

    // ---- SessionAware ----

    /**
     * <p>Field to store session context, or its proxy.</p>
     */
    private Map session;

    /**
     * <p>Store a new session context.</p>
     *
     * @param value A Map representing session state
     */
    public void setSession(Map value) {
        session = value;
    }

    /**
     * <p>Provide session context.</p>
     *
     * @return session context
     */
    public Map getSession() {
        return session;
    }

    // ---- Subscription property ----

    public Subscription getSubscription() {
        return (Subscription) getSession().get(SUBSCRIPTION_KEY);
    }

    public void setSubscription(Subscription subscription) {
        getSession().put(SUBSCRIPTION_KEY, subscription);
    }


    public String getSubscriptionHost() {
        Subscription sub = getSubscription();
        if (null == sub) {
            return null;
        }
        return sub.getHost();
    }

    // ---- User property ----

    /**
     * <p>Provide reference to User object for authenticated user.</p>
     *
     * @return User object for authenticated user.
     */
    public User getUser() {
        return (User) getSession().get(USER_KEY);
    }

    /**
     * <p>Store new reference to User Object.</p>
     *
     * @param user User object for authenticated user
     */
    public void setUser(User user) {
        getSession().put(USER_KEY, user);
    }

    // ---- Database methods ----

    public User findUser(String username, String password)
            throws ExpiredPasswordException {

        User user = getDatabase().findUser(username);

        if ((user != null) && !user.getPassword().equals(password)) {
            user = null;
        }

        if (user == null) {
            addFieldError(PASSWORD_MISMATCH_FIELD,
                    getText(ERROR_PASSWORD_MISMATCH));
        }

        return user;

    }

    public User createUser(String username, String password) throws Exception {

        UserDatabase database = getDatabase();
        User user = database.findUser(username);

        if (user != null) {
            addActionError(getText(ERROR_USERNAME_UNIQUE));
            return null;
        }

        user = database.createUser(username);
        BeanUtils.setValues(user, this, null);

        return user;
    }

    public void saveUser() throws Exception {
        getDatabase().save();
    }

    /**
     * <p>Provide a "temporary" User Subscription object
     * that can be used tocapture input values.</p>
     */
    public void createInputSubscription() {
        Subscription sub = new MemorySubscription(getUser(), null);
        setSubscription(sub);
        setHost(sub.getHost());
    }

    public Subscription findSubscription(String host) {
        Subscription subscription;
        subscription = getUser().findSubscription(host);
        return subscription;
    }

    public Subscription findSubscription() {
        return findSubscription(getHost());
    }

    public void removeSubscription() {
        getUser().removeSubscription(getSubscription());
        getSession().remove(SUBSCRIPTION_KEY);
    }

    public void copySubscription(String host) {
        Subscription input = getSubscription();
        Subscription sub = createSubscription(host);
        if (null != sub) {
            BeanUtils.setValues(sub, input, null);
            setSubscription(sub);
            setHost(sub.getHost());
        }
    }

    public Subscription createSubscription(String host) {

        Subscription sub;

        sub = findSubscription(host);

        if (null != sub) {
            // FIXME - localization - "error.host.unique")
            addFieldError(HOST, ERROR_HOST_UNIQUE);
            return null;
        }

        return getUser().createSubscription(host);
    }

    // ---- Alias ----

    public String cancel() {
        return CANCEL;
    }

}
