package org.apache.struts.apps.mailreader.course;

import org.apache.struts.apps.mailreader.dao.User;
import org.apache.struts.apps.mailreader.dao.UserDatabase;
import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.action.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.lang.reflect.InvocationTargetException;

/**
 * <p/>
 * Provide an Edit method for retrieving an existing user,
 * and a Save method for updating or inserting a user.
 * </p><p>
 * Both methods utilize a RegistrationForm to obtain or expose User details.
 * If Save is used to create a user,
 * additional validations ensure input is nominal.
 * When a user is created,
 * Save also handles the initial logon.
 * </p>
 */
public class RegisterAction extends Action {

    // ---- Constants ----

    /**
     * <p/>
     * The application scope attribute under which our user database
     * is stored.
     * </p>
     */
    public static final String DATABASE_KEY = "database";

    /**
     * <p/>
     * Name of fromAddress field ["fromAddress"].
     * </p>
     */
    public final static String FROM_ADDRESS = "fromAddress";

    /**
     * <p/>
     * Name of fullName field ["fullName"].
     * </p>
     */
    public final static String FULL_NAME = "fullName";

    /**
     * <p/>
     * Name of password field ["password"].
     * </p>
     */
    public static String PASSWORD = "password";

    /**
     * <p/>
     * Name of password confirmation field ["password2"].
     * </p>
     */
    public final static String PASSWORD2 = "password2";

    /**
     * <p/>
     * Name of replyToAddress field ["replyToAddress"].
     * </p>
     */
    public final static String REPLY_TO_ADDRESS = "replyToAddress";

    /**
     * <p/>
     * The token representing a "success" result for this application.
     * </p>
     */
    public static final String SUCCESS = "Success";

    /**
     * <p/>
     * The session scope attribute under which the User object
     * for the currently logged in user is stored.
     * </p>
     */
    public static final String USER_KEY = "user";

    /**
     * <p/>
     * Name of username field ["username"].
     * </p>
     */
    public static String USERNAME = "username";

    // ---- Private Methods ----

    /**
     * <p/>
     * The <code>Log</code> instance for this application.
     * </p>
     */
    private Log log = LogFactory.getLog(Constants.PACKAGE);

    /**
     * <p/>
     * The message prefix to use when populating a Registration Form.
     * </p>
     */
    final String LOG_REGISTRATION_POPULATE = "RegistrationForm.populate";

    /**
     * <p/>
     * Helper method to post error message when user already exists.
     * </p>
     *
     * @param username Existing username
     * @param errors   Our ActionMessages collection
     */
    private void errorUsernameUnique(String username,
                                     ActionMessages errors) {
        errors.add(
                USERNAME,
                new org.apache.struts.action.ActionMessage(
                        "error.username.unique", username));
    }


    /**
     * <p/>
     * Helper method to log event and cancel transaction.
     * </p>
     *
     * @param session Our HttpSession
     * @param method  Method being processed
     * @param key     Attrkibute to remove from session, if any
     */
    private void doCancel(HttpSession session, String method, String key) {
        if (log.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer(128);
            sb.append(Constants.LOG_CANCEL);
            sb.append(method);
            log.trace(sb.toString());
        }
        if (key != null) {
            session.removeAttribute(key);
        }
    }

    /**
     * <p/>
     * Verify input for creating a new user,
     * create the user, and process the login.
     * </p>
     *
     * @param form    The input form
     * @param request The HttpRequest being served
     * @param errors  The ActionMessages collection for any errors
     * @return A new User and empty Errors if create succeeds,
     *         or null and Errors if create fails
     */
    private User doCreateUser(
            ActionForm form,
            HttpServletRequest request,
            ActionMessages errors) {

        if (log.isTraceEnabled()) {
            log.trace(" Perform additional validations on Create");
        }

        UserDatabase database = doGetUserDatabase();
        String username = doGet(form, USERNAME);
        try {
            if (database.findUser(username) != null) {
                errorUsernameUnique(username, errors);
            }
        }
        catch (ExpiredPasswordException e) {
            errorUsernameUnique(username, errors);
            errors.add("errors.literal", new ActionMessage(e.getMessage()));
        }

        String password = doGet(form, PASSWORD);
        if ((password == null) || (password.length() < 1)) {
            errors.add(PASSWORD, new ActionMessage("error.password.required"));

            String password2 = doGet(form, PASSWORD2);
            if ((password2 == null) || (password2.length() < 1)) {
                errors.add(
                        PASSWORD2,
                        new ActionMessage("error.password2.required"));
            }
        }

        if (!errors.isEmpty()) {
            return null;
        }

        User user = database.createUser(username);

        // Log the user in
        HttpSession session = request.getSession();
        session.setAttribute(Constants.USER_KEY, user);
        if (log.isTraceEnabled()) {
            log.trace(
                    " User: '"
                            + user.getUsername()
                            + "' logged on in session: "
                            + session.getId());
        }

        return user;
    }

    /**
     * <p/>
     * Helper method to obtain User form session (if any).
     * </p>
     *
     * @param session Our HttpSession
     * @return User object, or null if there is no user.
     */
    private User doGetUser(HttpSession session) {
        return (User) session.getAttribute(Constants.USER_KEY);
    }

    /**
     * <p/>
     * Confirm user credentials. Post any errors and return User object
     * (or null).
     * </p>
     *
     * @param database Database in which to look up the user
     * @param username Username specified on the logon form
     * @param password Password specified on the logon form
     * @param errors   ActionMessages queue to passback errors
     * @return Validated User object or null
     * @throws org.apache.struts.apps.mailreader.dao.ExpiredPasswordException
     *          to be handled by Struts exception
     *          processor via the action-mapping
     */
    private User doGetUser(UserDatabase database, String username,
                           String password, ActionMessages errors)
            throws ExpiredPasswordException {

        User user = null;
        if (database == null) {
            errors.add(
                    ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("error.database.missing"));
        } else {

            if (username.equals("Hermes")) {
                throw new ExpiredPasswordException("Hermes");
            }

            user = database.findUser(username);
            if ((user != null) && !user.getPassword().equals(password)) {
                user = null;
            }
            if (user == null) {
                errors.add(
                        ActionMessages.GLOBAL_MESSAGE,
                        new ActionMessage("error.password.mismatch"));
            }
        }

        return user;
    }

    /**
     * <p/>
     * Return a reference to the UserDatabase
     * or null if the database is not available.
     * </p>
     *
     * @return a reference to the UserDatabase or null if the database is not
     *         available
     */
    private UserDatabase doGetUserDatabase() {
        return (UserDatabase) servlet.getServletContext().getAttribute(
                DATABASE_KEY);
    }

    /**
     * <p/>
     * Log a "processing" message for an Action.
     * </p>
     *
     * @param mapping Our ActionMapping
     * @param method  Name of method being processed
     */
    private void doLogProcess(ActionMapping mapping, String method) {
        if (log.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer(128);
            sb.append(" ");
            sb.append(mapping.getPath());
            sb.append(":");
            sb.append(Constants.LOG_PROCESSING);
            sb.append(method);
            log.debug(sb.toString());
        }
    }

    /**
     * <p/>
     * Helper method to populate the User object from the input form.
     * </p>
     *
     * @param user User object to populate
     * @param form Form with incoming values
     * @throws ServletException On any error
     */
    private void doPopulate(User user, ActionForm form)
            throws ServletException {

        if (log.isTraceEnabled()) {
            log.trace(Constants.LOG_POPULATE_USER + user);
        }

        try {
            String oldPassword = user.getPassword();
            PropertyUtils.copyProperties(user, form);
            String password = doGet(form, PASSWORD);
            if ((password == null)
                    || (password.length() < 1)) {

                user.setPassword(oldPassword);
            }

        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t == null) {
                t = e;
            }

            log.error(LOG_REGISTRATION_POPULATE, t);
            throw new ServletException(LOG_REGISTRATION_POPULATE, t);

        } catch (Throwable t) {
            log.error(LOG_REGISTRATION_POPULATE, t);
            throw new ServletException(LOG_REGISTRATION_POPULATE, t);
        }
    }

    /**
     * <p/>
     * Persist the User object, including subscriptions, to the database.
     * </p>
     *
     * @param user Our User object
     * @throws javax.servlet.ServletException On any error
     */
    private void doSaveUser(User user) throws ServletException {

        final String LOG_DATABASE_SAVE_ERROR =
                " Unexpected error when saving User: ";

        try {
            UserDatabase database = doGetUserDatabase();
            database.save();
        } catch (Exception e) {
            String message = LOG_DATABASE_SAVE_ERROR + user.getUsername();
            log.error(message, e);
            throw new ServletException(message, e);
        }
    }

    // ---- Protected methods ----

    /**
     * <p/>
     * Return the mapping labeled "success"
     * or null if there is no such mapping.
     * </p>
     *
     * @param mapping Our ActionMapping
     * @return Return the mapping named "success" or null if there is no such
     *         mapping.
     */
    protected ActionForward doFindSuccess(ActionMapping mapping) {
        return mapping.findForward(SUCCESS);
    }


    /**
     * <p/>
     * Helper method to fetch a String property from a DynaActionForm.
     * </p>
     * <p/>
     * Values are returned trimmed of leading and trailing whitespace.
     * Zero-length strings are returned as null.
     * </p>
     *
     * @param form     Our DynaActionForm
     * @param property The name of the property
     * @return The value or null if an error occurs
     */
    protected String doGet(ActionForm form, String property) {
        String initial;
        try {
            initial = (String) PropertyUtils.getSimpleProperty(form, property);
        } catch (Throwable t) {
            initial = null;
        }
        String value = null;
        if ((initial != null) && (initial.length() > 0)) {
            value = initial.trim();
            if (value.length() == 0) {
                value = null;
            }
        }
        return value;
    }

    /**
     * <p/>
     * Confirm user credentials. Post any errors and return User object
     * (or null).
     * </p>
     *
     * @param username Username specified on the logon form
     * @param password Password specified on the logon form
     * @param errors   ActionMessages queue to passback errors
     * @return Validated User object or null
     * @throws org.apache.struts.apps.mailreader.dao.ExpiredPasswordException
     *          to be handled by Struts exception
     *          processor via the action-mapping
     */
    protected User doGetUser(String username,
                             String password, ActionMessages errors)
            throws ExpiredPasswordException {

        return doGetUser(doGetUserDatabase(), username, password, errors);
    }

    /**
     * <p/>
     * Save any errors and forward to the Input result.
     * </p>
     *
     * @param mapping Our ActionMapping
     * @param request Our HttpServletRequest
     * @param errors  Our ActionMessages collectoin
     * @return The InputForward for this mappintg
     */
    protected ActionForward doInputForward(ActionMapping mapping,
                                         HttpServletRequest request,
                                         ActionMessages errors) {
        this.saveErrors(request, errors);
        return (mapping.getInputForward());
    }

    // ----- Public Methods ----

    /**
     * <p/>
     * Insert or update a User object to the persistent store.
     * </p><p>
     * If a User is not logged in,
     * then a new User is created and automatically logged in.
     * Otherwise, the existing User is updated.
     * </p>
     *
     * @param mapping  Our ActionMapping
     * @param form     Our ActionForm
     * @param request  Our HttpServletRequest
     * @param response Our HttpServletResponse
     * @return The "Success" result for this mapping
     * @throws Exception on any error
     */
    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        final String method = Constants.SAVE;
        doLogProcess(mapping, method);

        HttpSession session = request.getSession();
        if (isCancelled(request)) {
            doCancel(session, method, Constants.SUBSCRIPTION_KEY);
            return doFindSuccess(mapping);
        }

        ActionMessages errors = new ActionMessages();
        if (!errors.isEmpty()) {
            return doInputForward(mapping, request, errors);
        }

        User user = doGetUser(session);
        if (user == null) {
            user = doCreateUser(form, request, errors);
            if (!errors.isEmpty()) {
                return doInputForward(mapping, request, errors);
            }
        }

        doPopulate(user, form);
        doSaveUser(user);

        return doFindSuccess(mapping);
    }
}
