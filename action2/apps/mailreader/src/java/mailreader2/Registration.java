package mailreader2;

import org.apache.struts.apps.mailreader.dao.User;


/**
 * <p> Provide an Edit method for retrieving an existing user, and a Save method for updating or inserting a user.
 * </p><p> Both methods utilize a RegistrationForm to obtain or expose User details. If Save is used to create a user,
 * additional validations ensure input is nominal. When a user is created, Save also handles the initial logon. </p>
 */
public final class Registration extends MailreaderSupport {

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
            createInputUser();
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

        if (creating) {
            copyUser(getUsername(), getPassword());
        }

        saveUser();

        return SUCCESS;
    }

}
