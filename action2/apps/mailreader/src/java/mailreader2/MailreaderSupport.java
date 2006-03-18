/*
 * $Id: BaseAction.java 360442 2005-12-31 20:10:04Z husted $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mailreader2;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.webwork.interceptor.SessionAware;
import org.apache.struts.apps.mailreader.dao.User;
import org.apache.struts.apps.mailreader.dao.Subscription;
import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.apps.mailreader.dao.UserDatabase;

import java.util.Map;

/**
 * <p>
 * Base Action for MailreaderSupport application.
 * </p><p>
 * All the BaseAction helper methods are prefixed with "do"
 * so that they can be easily distinguished from Struts and Servlet API methods.
 * BaseAction subclasses may also have prive "do" helpers of their own.
 * </p><p>
 * Methods are kept in alphabetical order, to make them easier to find.
 * </p>
 *
 * @version $Rev: 360442 $ $Date: 2005-12-31 15:10:04 -0500 (Sat, 31 Dec 2005) $
 */
public abstract class MailreaderSupport extends ActionSupport implements SessionAware {

    // ---- SessionAware ----

    private Map session;

    public void setSession(Map session) {
        this.session = session;
    }

    public Map getSession() {
        return session;
    }

    // ---- Fields ----

    /**
     * <p>
     * Name of username field ["username"].
     * </p>
     */
    public static String USERNAME = "username";

    /**
     * <p>
     * Name of password field ["password"].
     * </p>
     */
    public static String PASSWORD = "password";

    /**
     * <p>
     * Name of task field ["task"].
     * </p>
     */
    public final static String TASK = "task";

    // ---- Protected Methods ----

    /**
     * <p>
     * Store User object in client session.
     * If user object is null, any existing user object is removed.
     * </p>
     *
     * @param user    The user object returned from the database
     */
    void doCacheUser(User user) {
        getSession().put(Constants.USER_KEY, user);
    }

    /**
     * <p>
     * Helper method to log event and cancel transaction.
     * </p>
     *
     * @param method  Method being processed
     * @param key     Attrkibute to remove from session, if any
     */
    protected void doCancel(String method, String key) {
        if (key != null) {
            getSession().remove(key);
        }
    }

    /**
     * <p>
     * Obtain the cached Subscription object, if any.
     * </p>
     *
     * @return Cached Subscription object or null
     */
    protected Subscription doGetSubscription() {
        return (Subscription) getSession().get(Constants.SUBSCRIPTION_KEY);
    }

    /**
     * <p>
     * Confirm user credentials. Post any errors and return User object
     * (or null).
     * </p>
     *
     * @param database Database in which to look up the user
     * @param username Username specified on the logon form
     * @param password Password specified on the logon form
     * @return Validated User object or null
     * @throws org.apache.struts.apps.mailreader.dao.ExpiredPasswordException
     *          to be handled by Struts exception
     *          processor via the action-mapping
     */
    User doGetUser(UserDatabase database, String username,
                   String password)
            throws ExpiredPasswordException {

        User user = null;
        if (database == null) {
            // FIXME: errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.database.missing"));
        } else {

            if (username.equals("Hermes")) {
                throw new ExpiredPasswordException("Hermes");
            }

            user = database.findUser(username);
            if ((user != null) && !user.getPassword().equals(password)) {
                user = null;
            }
            if (user == null) {
                // FIXME: errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.password.mismatch"));
            }
        }

        return user;
    }

    /**
     * <p>
     * Confirm user credentials. Post any errors and return User object
     * (or null).
     * </p>
     *
     * @param username Username specified on the logon form
     * @param password Password specified on the logon form
     * @return Validated User object or null
     * @throws org.apache.struts.apps.mailreader.dao.ExpiredPasswordException
     *          to be handled by Struts exception
     *          processor via the action-mapping
     */
    User doGetUser(String username,
                   String password)
            throws ExpiredPasswordException {

        return doGetUser(doGetUserDatabase(), username, password);
    }

    /**
     * <p>
     * Return a reference to the UserDatabase
     * or null if the database is not available.
     * </p>
     *
     * @return a reference to the UserDatabase or null if the database is not
     *         available
     */
    protected UserDatabase doGetUserDatabase() {
        return (UserDatabase) getSession().get(Constants.DATABASE_KEY);
    }

    /**
     * <p>
     * Helper method to obtain User form session (if any).
     * </p>
     *
     * @return User object, or null if there is no user.
     */
    protected User doGetUser() {
        return (User) getSession().get(Constants.USER_KEY);
    }

    /**
     * <p>
     * The <code>Log</code> instance for this application.
     * </p>
     */
    protected Log log = LogFactory.getLog(Constants.PACKAGE);

    /**
     * <p>
     * Persist the User object, including subscriptions, to the database.
     * </p>
     *
     * @param user Our User object
     * @throws javax.servlet.ServletException On any error
     */
    protected void doSaveUser(User user) throws ServletException {

        try {
            UserDatabase database = doGetUserDatabase();
            database.save();
        } catch (Exception e) {
            String message = Constants.LOG_DATABASE_SAVE_ERROR + user.getUsername();
            log.error(message, e);
            throw new ServletException(message, e);
        }
    }

}
