package mailreader2;

import org.apache.struts.apps.mailreader.dao.User;


/**
 * <p> Provide an Edit method for retrieving an existing user, and a Save
 * method for updating or inserting a user. </p>
 */
public final class Registration extends MailreaderSupport {

    /**
     * <p>Double check that there is not a valid User logon. </p>
     *
     * @return True if there is not a valid User logon
     */
    private boolean isCreating() {
        User user = getUser();
        return (null == user) || (null == user.getDatabase());
    }

    /**
     * <p> Retrieve User object to edit or null if User does not exist. </p>
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
     * <p> Insert or update a User object to the persistent store. </p>
     *
     * <p> If a User is not logged in, then a new User is created and
     * automatically logged in. Otherwise, the existing User is updated. </p>
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
