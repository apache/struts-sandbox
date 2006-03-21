package mailreader2;

import com.opensymphony.util.BeanUtils;
import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.apps.mailreader.dao.User;
import org.apache.struts.apps.mailreader.dao.UserDatabase;
import org.apache.struts.apps.mailreader.dao.impl.memory.MemoryUser;


/**
 * <p> Provide an Edit method for retrieving an existing user, and a Save method for updating or inserting a user.
 * </p><p> Both methods utilize a RegistrationForm to obtain or expose User details. If Save is used to create a user,
 * additional validations ensure input is nominal. When a user is created, Save also handles the initial logon. </p>
 */
public final class Registration extends MailreaderSupport {

    /**
     * <p>The confirmation password input field.</p>
     */
    private String password2 = null;


    /**
     * @return Returns the confirmationpassword.
     */
    public String getPassword2() {
        return this.password2;
    }

    /**
     * @param password2 The confirmation password to set.
     */
    public void setPassword2(String password2) {
        this.password2 = password2;
    }


    /**
     * <p>The username input field.</p>
     */
    private String username = null;


    /**
     * @return Returns the username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    // ---- Private Methods ----

    /**
     * <p> Verify input for creating a new user, create the user, and process the login. </p>
     *
     * @return A new User and empty Errors if create succeeds, or null and Errors if create fails
     */
    private User createUser(String username, String password) {

        UserDatabase database = getDatabase();
        User user;

        try {

            user = findUser(username, password);
        }

        catch (ExpiredPasswordException e) {
            user = getUser(); // Just so that it is not null
        }

        if (user != null) {
            this.addFieldError("username", "error.username.unique");
            return null;
        }

        return database.createUser(username);
    }

    // ----- Public Methods ----


    private boolean isCreating() {
        User user = getUser();
        if (null == user) {
            return true;
        }
        return (null == user.getDatabase());
    }

    /**
     * <p> Retrieve the User object to edit or null if the User does not exist, and set an transactional token to later
     * detect multiple Save commands. </p>
     *
     * @return The "Success" result for this mapping
     * @throws Exception on any error
     */
    public String input() throws Exception {

        if (isCreating()) {
            User user = new MemoryUser(null, null);
            setUser(user);
            setTask(Constants.CREATE);
        } else {
            setTask(Constants.EDIT);
        }

        return INPUT;
    }

    /**
     * <p> Insert or update a User object to the persistent store. </p><p> If a User is not logged in, then a new User
     * is created and automatically logged in. Otherwise, the existing User is updated. </p>
     *
     * @return The "Success" result for this mapping
     * @throws Exception on any error
     */
    public String execute()
            throws Exception {

        boolean editing = Constants.EDIT.equals(getTask());
        // Double check for user and database
        editing = editing && (null != getUser()) && (null != getUser().getDatabase());

        User user;
        if (!editing) {
            User input = getUser();
            // Since user.username is immutable, we have to use a local property
            user = createUser(getUsername(), input.getPassword());
            BeanUtils.setValues(user, input, null);
        }

        saveUser();

        return SUCCESS;
    }

}
