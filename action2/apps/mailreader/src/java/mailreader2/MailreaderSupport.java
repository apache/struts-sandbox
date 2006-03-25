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

import com.opensymphony.util.BeanUtils;
import com.opensymphony.webwork.interceptor.ApplicationAware;
import com.opensymphony.webwork.interceptor.SessionAware;
import com.opensymphony.xwork.ActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.apps.mailreader.dao.Subscription;
import org.apache.struts.apps.mailreader.dao.User;
import org.apache.struts.apps.mailreader.dao.UserDatabase;
import org.apache.struts.apps.mailreader.dao.impl.memory.MemorySubscription;
import org.apache.struts.apps.mailreader.dao.impl.memory.MemoryUser;

import java.util.Map;

/**
 * <p> Base Action for MailreaderSupport application. </p>
 *
 * <p> Note that this class does NOT implement model driven because of issues with the pre-existing model. The
 * MailReader DAO does not provide a setter for username and does not provide a default constructor, making it difficult
 * to use as a POJO or to extend. As an alternative, the username and password properties are provided on the Action and
 * then passed to the user class as needed. </p>
 *
 * @version $Rev: 360442 $ $Date: 2005-12-31 15:10:04 -0500 (Sat, 31 Dec 2005) $
 */
public class MailreaderSupport extends ActionSupport implements SessionAware, ApplicationAware {

    // ---- ApplicationAware ----

    private Map application;

    public void setApplication(Map application) {
        this.application = application;
    }

    public Map getApplication() {
        return this.application;
    }

    // ---- SessionAware ----

    private Map session;

    public void setSession(Map session) {
        this.session = session;
    }

    public Map getSession() {
        return session;
    }

    // ---- Task property (utilized by UI) ----

    /**
     * <p>The task input field.</p>
     */
    private String task = null;


    /**
     * @return Returns the task.
     */
    public String getTask() {
        return this.task;
    }

    /**
     * @param task The task to set.
     */
    public void setTask(String task) {
        this.task = task;
    }

    // ---- Host property ----

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String value) {
        host = value;
    }

    // ---- Password property ----

    /**
     * <p>The password input field.</p>
     */
    private String password = null;


    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    // ---- Password2 property (confirmation) ----

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

    // ---- Username property ----

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

    // ---- Database property ----

    /**
     * <p> Return a reference to the UserDatabase or null if the database is not available. </p>
     *
     * @return a reference to the UserDatabase or null if the database is not available
     */
    public UserDatabase getDatabase() {
        Object db = getApplication().get(Constants.DATABASE_KEY);
        if (db == null) {
            this.addActionError("error.database.missing");
        }
        return (UserDatabase) db;
    }

    public void setDatabase(UserDatabase database) {
        getApplication().put(Constants.DATABASE_KEY, database);
    }

    // ---- User property ----

    public User getUser() {
        return (User) getSession().get(Constants.USER_KEY);
    }

    public void setUser(User user) {
        getSession().put(Constants.USER_KEY, user);
    }

    public User findUser(String username, String password) throws ExpiredPasswordException {
        // FIXME: Stupid hack to compensate for inadequate DAO layer
        if ("Hermes".equals(username)) {
            throw new ExpiredPasswordException("Hermes");
        }

        User user = getDatabase().findUser(username);
        if ((user != null) && !user.getPassword().equals(password)) {
            user = null;
        }
        if (user == null) {
            this.addFieldError("password", "error.password.mismatch");
        }
        return user;
    }

    /**
     * <p> The <code>Log</code> instance for this application. </p>
     */
    protected Log log = LogFactory.getLog(Constants.PACKAGE);

    /**
     * <p> Persist the User object, including subscriptions, to the database. </p>
     *
     * @throws javax.servlet.ServletException On any error
     */
    public void saveUser() throws Exception {
        try {
            getDatabase().save();
        } catch (Exception e) {
            String message = Constants.LOG_DATABASE_SAVE_ERROR + getUser().getUsername();
            log.error(message, e);
            throw new Exception(message, e);
        }
    }

    public void createInputUser() {
        User user = new MemoryUser(null, null);
        setUser(user);
    }

    /**
     * <p> Verify input for creating a new user, create the user, and process the login. </p>
     *
     * @return A new User and empty Errors if create succeeds, or null and Errors if create fails
     */
    public User createUser(String username, String password) {

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

    // Since user.username is immutable, we have to use some local properties
    public void copyUser(String _username, String _password) {
        User input = getUser();
        input.setPassword(_password);
        User user = createUser(_username, _password);
        if (null != user) {
            BeanUtils.setValues(user, input, null);
            setUser(user);
        }
    }

    // ---- Subscription property ----

    /**
     * <p> Obtain the cached Subscription object, if any. </p>
     *
     * @return Cached Subscription object or null
     */
    public Subscription getSubscription() {
        return (Subscription) getSession().get(Constants.SUBSCRIPTION_KEY);
    }

    public void setSubscription(Subscription subscription) {
        getSession().put(Constants.SUBSCRIPTION_KEY, subscription);
    }

    /**
     * <p> Obtain subscription matching host for our User, or return null if not found. </p>
     *
     * @return The matching Subscription or null
     */
    public Subscription findSubscription(String host) {

        Subscription subscription;

        try {
            subscription = getUser().findSubscription(host);
        }
        catch (NullPointerException e) {
            subscription = null;
        }

        return subscription;
    }

    public Subscription findSubscription() {

        return findSubscription(getHost());
    }

    public void createInputSubscription() {
        Subscription sub = new MemorySubscription(getUser(), null);
        setSubscription(sub);
        setHost(sub.getHost());
    }

    public Subscription createSubscription(String host) {

        Subscription sub;

        sub = findSubscription(host);

        if (null != sub) {
            this.addFieldError("host", "error.host.unique");
            return null;
        }

        return getUser().createSubscription(host);
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

    public void removeSubscription() throws Exception {
        getUser().removeSubscription(getSubscription());
        getSession().remove(Constants.SUBSCRIPTION_KEY);
    }

    public String getSubscriptionHost() {
        Subscription sub = getSubscription();
        if (null == sub) {
            return null;
        }
        return sub.getHost();
    }
}
