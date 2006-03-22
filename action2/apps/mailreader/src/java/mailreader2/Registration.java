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
        return (null == user) || (null == user.getDatabase());
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
            setUsername(getUser().getUsername());
            setPassword(getUser().getPassword());
            setPassword2(getUser().getPassword());
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

        boolean creating = Constants.CREATE.equals(getTask());
        creating = creating && isCreating(); // trust but verify

        User user;
        if (creating) {
            User input = getUser();
            // Since user.username is immutable, we have to use some local properties
            user = createUser(getUsername(), getPassword());
            input.setPassword(getPassword());
            BeanUtils.setValues(user, input, null);
            setUser(user);
        }

        saveUser();

        return SUCCESS;
    }

}
