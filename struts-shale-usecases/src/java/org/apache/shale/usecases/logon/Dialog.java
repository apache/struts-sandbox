/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

package org.apache.shale.usecases.logon;

import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shale.usecases.logic.LogonLogic;
import org.apache.shale.usecases.model.User;
import org.apache.shale.usecases.view.BaseDialogController;
import org.apache.shale.util.Messages;

/**
 * <p><code>DialogController</code> for the logon dialog use case.  This
 * dialog has supports alternate entry points:</p>
 * <ul>
 * <li><code>enter()</code> - Normal entry point when the user selects an
 *     option to log on.  If a "remember me" cookie was included with this
 *     request, and the cookie identifies a valid user, bypass the normal
 *     username/password dialog and return an authenticated user (this mode
 *     also exits the dialog).  Otherwise, initiate a username/password dialog.
 * <li><code>edit()</code> - Alternate entry point when a logged on user
 *     selects an option to edit his or her profile information.</li>
 * </ul>
 *
 * <p>It also supports alternative exit points:</p>
 * <ul>
 * <li><code>cancel()</code> - Remove the currently logged on user (if any)
 *     and call <code>exit()</code>.</li>
 * <li><code>exit()</code> - Return "authenticated" or "unauthenticated"
 *     depending on whether there is a valid logged on user or not.</li>
 * <li><code>finish()</code> - Finish the creation or editing of a user
 *     profile, log the new user on (if creating), and call <code>exit()</code>.
 *     </li>
 * <li><code>logoff()</code> - Synonym for <code>cancel()</code>.</li>
 * <li><code>logon()</code> - If there is a valid "remember me" cookie present,
 *     log the corresponding user on and call <code>exit()</code>.  Otherwise,
 *     do not exit this dialog; instead, proceed to the logon view.</li>
 * </ul>
 *
 * $Id$
 */
public class Dialog extends BaseDialogController {
    
    
    // ------------------------------------------------------ Manifest Constants


    /**
     * <p>Logical outcome indicating that a user was successfully authenticated,
     * and that a corresponding {@link User} object has been placed in
     * session scope under the specified key.</p>
     */
    static final String AUTHENTICATED = "logon$authenticated";


    /**
     * <p>Name of the HTTP cookie in which we store "remember me" credentials.</p>
     */
    static final String COOKIE_NAME = "remember_me";


    /**
     * <p>Managed bean name under which the <em>Dialog</em> instance is
     * stored.</p>
     */
    static final String DIALOG_BEAN = "logon$dialog";


    /**
     * <p>Managed bean name under which the business logic bean instance
     * for this dialog is stored.</p>
     */
    static final String LOGIC_BEAN = "logon$logic";


    /**
     * <p>Logical outcome indicating that the user should be presented with
     * the username/password logon page.</p>
     */
    static final String LOGON = "logon$logon";


    /**
     * <p>Logical outcome indicating that the user should be presented with
     * the first profile page.</p>
     */
    static final String PROFILE1 = "logon$profile1";


    /**
     * <p>Logical outcome indicating that the user should be presented with
     * the second profile page.</p>
     */
    static final String PROFILE2 = "logon$profile2";


    /**
     * <p>Logical outcome indicating that the user should be presented with
     * the third profile page.</p>
     */
    static final String PROFILE3 = "logon$profile3";


    /**
     * <p>Logical outcome indicating that user authentication did not succeed,
     * and that no {@link User} object exists in session scope under the
     * specified key.</p>
     */
    static final String UNAUTHENTICATED = "logon$unauthenticated";


    /**
     * <p>Session scope attribute key under which the authenticated
     * {@link User} (if any) is stored.</p>
     */
    static final String USER = "user";


    // -------------------------------------------------------- Static Variables


    /**
     * <p>Log instance for this class.</p>
     */
    private static final Log log = LogFactory.getLog(Dialog.class);


    /**
     * <p>Message resources for this application.</p>
     */
    private static Messages messages =
      new Messages("org.apache.shale.usecases.view.Bundle");


    // --------------------------------------------------- Configured Properties


    /**
     * <p>Flag indicating that a confirmation message should be sent to a
     * prospetive new user, and a response received, before the new account
     * is activated.</p>
     */
    private boolean confirmation = false;
    public boolean isConfirmation() { return this.confirmation; }
    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }


    /**
     * <p>Flag indicating that we are creating a new profile, versus editing
     * an existing one.</p>
     */
    private boolean creating = true;
    public boolean isCreating() { return this.creating; }
    public void setCreating(boolean creating) { this.creating = creating; }


    /**
     * <p>Flag indicating that "remember me" cookies should be sent if
     * requested by the user, and recognized on subsequent logon attempts
     * if returned by the client.</p>
     */
    private boolean rememberMe = false;
    public boolean isRememberMe() { return this.rememberMe; }
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }


    /**
     * <p>Session scope attribute key under which a {@link User} instance
     * for an authenticated user will be stored.</p>
     */
    private String userKey = "user";
    public String getUserKey() { return this.userKey; }
    public void setUserKey(String userKey) { this.userKey = userKey; }


    // -------------------------------------------------- Input Field Properties


    /**
     * <p>Category identifiers for the categories selected by this user
     */
    private int categories[] = new int[0];
    public int[] getCategories() { return this.categories; }
    public void setCategories(int categories[]) { this.categories = categories; }


    /**
     * <p>Email address for this user.</p>
     */
    private String emailAddress = null;
    public String getEmailAddress() { return this.emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }


    /**
     * <p>Full name for this user.</p>
     */
    private String fullName = null;
    public String getFullName() { return this.fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }


    /**
     * <p>Password entered by the user.</p>
     */
    private String password = null;
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }


    /**
     * <p>Confirmation password entered by the user.</p>
     */
    private String password2 = null;
    public String getPassword2() { return this.password2; }
    public void setPassword2(String password2) { this.password2 = password2; }


    /**
     * <p>Flag indicating that the user wants to use the "remember me"
     * capability.</p>
     */
    private boolean remember = false;
    public boolean isRemember() { return this.remember; }
    public void setRemember(boolean remember) { this.remember = remember; }


    /**
     * <p>Username entered by the user.</p>
     */
    private String username = null;
    public String getUsername() { return this.username; }
    public void setUsername(String username) { this.username = username; }


    // ------------------------------------------------ DialogController Methods


    /**
     * <p>Abnormal exit action for this dialog.  Do not perform any updates,
     * remove this instance from session scope, and return "authenticated"
     * or "unauthenticated" as appropriate.</p>
     */
    public String cancel() {

        if (log.isDebugEnabled()) {
            log.debug("cancel()");
        }

        return exit();

    }


    /**
     * <p>Action to initiate creation of a new user profile.</p>
     */
    public String create() {

        if (log.isDebugEnabled()) {
            log.debug("create()");
        }

        setCreating(true);

        setCategories(new int[0]);
        setEmailAddress(null);
        setFullName(null);
        setPassword(null);
        setPassword2(null);
        setUsername(null);

        return PROFILE1;

    }


    /**
     * <p>Alternate entry action for the "edit profile" mode of this dialog.
     * User must already be logged on in order to edit their profile.</p>
     */
    public String edit() {

        if (log.isDebugEnabled()) {
            log.debug("edit()");
        }

        setCreating(false);

        User user = (User) getBean(USER);
        setCategories(user.getCategories());
        setEmailAddress(user.getEmailAddress());
        setFullName(user.getFullName());
        setPassword(null);
        setPassword2(null);
        setUsername(user.getUsername());

        return PROFILE1;

    }


    /**
     * <p>Entry action for the "logon" model of this dialog.  If "remember me"
     * functionality is in place, check for a valid cookie and (if found):</p>
     * <ul>
     * <li>Record a successful logon for the corresponding user.</li>
     * <li>Return outcome <code>authenticated</code>.</li>
     * </ul>
     * <p>Otherwise, return outcome <code>logon</code> to initiate
     * the logon dialog.</p>
     */
    public String enter() {

        if (log.isDebugEnabled()) {
            log.debug("enter(rememberMe=" + isRememberMe() + ")");
        }

        // Perform "remember me" processing if requested
        if (isRememberMe()) {
            User user = verify();
            if (user != null) {
                if (log.isDebugEnabled()) {
                    log.debug("enter(username=" + user.getUsername() + ") was remembered");
                }
                FacesContext.getCurrentInstance().getExternalContext().
                  getSessionMap().put(USER, user);
                return exit();
            }
        }

        // Initialize entry fields on the logon dialog
        if (log.isTraceEnabled()) {
            log.trace("enter() --> logon dialog");
        }
        setUsername(null);
        setPassword(null);
        setRemember(false);
        return LOGON;

    }


    /**
     * <p>Normal exit action for this dialog.  Return outcome
     * <code>authenticated</code> if logon was successful; else
     * return outcome <code>unauthenticated</code>.</p>
     */
    public String exit() {

        // Retrieve the session attributes for this request
        Map map = FacesContext.getCurrentInstance().getExternalContext().
          getSessionMap();
        if (log.isDebugEnabled()) {
            log.debug("exit(authenticated=" + map.containsKey(USER) + ")");
        }


        // Remove this dialog bean from session scope
        map.remove(DIALOG_BEAN);

        // Return an outcome based on our authenticated user state
        if (map.get(USER) != null) {
            return AUTHENTICATED;
        } else {
            return UNAUTHENTICATED;
        }

    }


    /**
     * <p>Alternate exit action for this dialog.  Finish up processing
     * the creation or editing a user profile, log the newly created user
     * on (if confirmed), remove this instance from session scope, and return
     * outcome <code>authenticated</code>.</p>
     */
    public String finish() {

        if (log.isDebugEnabled()) {
            log.debug("finish()");
        }

        // Perform overall validations (in case "Finish" was pressed early)
        ; // FIXME - finish() overall validations

        // Create or acquire our User instance
        LogonLogic logic = (LogonLogic) getBean(LOGIC_BEAN);
        User user = null;
        if (creating) {
            user = logic.createUser();
        } else {
            user = (User) getBean(USER);
        }

        // Update to reflect changes during this dialog
        user.setCategories(getCategories());
        if (creating) {
            user.setConfirmed(!isConfirmation());
        }
        user.setEmailAddress(getEmailAddress());
        user.setFullName(getFullName());
        if ((getPassword() != null) && (getPassword().length() > 0)) {
            user.setPassword(getPassword());
        }
        user.setUsername(getUsername());

        // Persist the changes made during this dialog
        if (creating) {
            logic.insertUser(user);
        } else {
            logic.updateUser(user);
        }

        // Log in a new user if already confirmed
        // Otherwise, send the confirmation email
        if (creating) {
            if (user.isConfirmed()) {
                FacesContext.getCurrentInstance().getExternalContext().
                  getSessionMap().put(USER, user);
            } else {
                confirm(user);
            }
        }

        // Return outcome based on logged-in state
        return exit();

    }


    /**
     * <p>Alternate exit action for this dialog.  Remove the currently
     * logged on user (if any), remove this instance from session scope,
     * and return outcome <code>unauthenticated</code>.</p>
     */
    public String logoff() {

        if (log.isDebugEnabled()) {
            log.debug("logoff()");
        }

        FacesContext.getCurrentInstance().getExternalContext().
          getSessionMap().remove(USER);
        return cancel();

    }


    /**
     * <p>Alternate exit action for this dialog.
     * Validate the credentials entered by the user.  If they are invalid,
     * queue an error message and redisplay the current view.  Otherwise,
     * cache the authenticated {@link User} instance, set a "remember me"
     * cookie (if requested), remove this instance from session scope,
     * and return outcome <code>authenticated</code>.</p>
     */
    public String logon() {

        if (log.isDebugEnabled()) {
            log.debug("logon()");
        }

        // Process a successful authentication
        LogonLogic logic = (LogonLogic) getBean(LOGIC_BEAN);
        User user = logic.authenticate(username, password);
        if (user != null) {
            if (user.isConfirmed()) {
                // Confirmed user, log him/her on
                FacesContext.getCurrentInstance().getExternalContext().
                  getSessionMap().put(USER, user);
                if (isRememberMe()) {
                    if (isRemember()) {
                        remember(user);
                    } else {
                        forget(user);
                    }
                }
            } else {
                // Unconfirmed user, tell him/her to reply to the email
                FacesMessage message =
                  new FacesMessage(messages.getMessage("logon.unconfirmed"));
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            return exit();
        }

        // On unsuccessful authentication, tell the user to try again
        FacesMessage message =
          new FacesMessage(messages.getMessage("logon.mismatch"));
        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext.getCurrentInstance().addMessage(null, message);
        return null;

    }


    // --------------------------------------------------------- Private Methods


    /**
     * <p>Send a confirmation email to the email address for the specified
     * user.  When the user replies to this message, this {@link User} will
     * become confirmed and eligible to sign on.</p>
     *
     * @param user {@link User} to confirm
     */
    private void confirm(User user) {

        if (log.isTraceEnabled()) {
            log.trace("confirm(" + user.getUsername() + ")");
        }

        ; // FIXME - confirm()

    }


    /**
     * <p>Remove any existing "remember me" cookie that was included.
     * FIXME - forget() dependency on Servlet API.</p>
     *
     * @param user {@link User} to be forgotten
     */
    private void forget(User user) {

        if (log.isTraceEnabled()) {
            log.trace("forget(" + user.getUsername() + ")");
        }

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request =
          (HttpServletRequest) context.getExternalContext().getRequest();
        Cookie cookie =
          new Cookie(Dialog.COOKIE_NAME, "");
        cookie.setDomain(request.getServerName());
        cookie.setMaxAge(0); // Delete immediately
        cookie.setPath(request.getContextPath());
        HttpServletResponse response =
          (HttpServletResponse) context.getExternalContext().getResponse();
        response.addCookie(cookie);

    }


    /**
     * <p>Create a "remember me" cookie and include it in this response.
     * FIXME - remember() dependency on Servlet API.</p>
     */
    private void remember(User user) {

        if (log.isTraceEnabled()) {
            log.trace("remember(" + user.getUsername() + ")");
        }
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request =
          (HttpServletRequest) context.getExternalContext().getRequest();
        Cookie cookie =
          new Cookie(Dialog.COOKIE_NAME, "" + user.getId()); // FIXME - more secure mechanism needed
        // cookie.setDomain(request.getServerName());
        cookie.setMaxAge(60 * 60 * 24 * 365); // One year
        cookie.setPath(request.getContextPath());
        HttpServletResponse response =
          (HttpServletResponse) context.getExternalContext().getResponse();
        response.addCookie(cookie);

    }


    /**
     * <p>Verify the {@link User} specified by any "remember me" cookie that
     * is included with this request.  If there is no such user, return
     * <code>null</code> instead.</p>
     */
    private User verify() {

        // Locate the "remember me" cookie (if any)
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request =
          (HttpServletRequest) context.getExternalContext().getRequest();
        Cookie cookie = null;
        Cookie cookies[] = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if (COOKIE_NAME.equals(cookies[i].getName())) {
                cookie = cookies[i];
                break;
            }
        }
        if (cookie == null) {
            if (log.isTraceEnabled()) {
                log.trace("verify(" + "NONE" + ")");
            }
            return null;
        }

        // Locate the corresponding valid user (if any) and return it
        int id = 0;
        try {
            id = Integer.parseInt(cookie.getValue());
        } catch (NumberFormatException e) {
            if (log.isTraceEnabled()) {
                log.trace("verify(" + "INVALID=" + cookie.getValue() + ")");
            }
            return null;
        }
        LogonLogic logic = (LogonLogic) getBean(LOGIC_BEAN);
        User user = logic.findUser(id);
        if (log.isTraceEnabled()) {
            if (user != null) {
                log.trace("verify(" + user.getUsername() + ")");
            } else {
                log.trace("verify(" + "MISSING=" + id + ")");
            }
        }
        return user;

    }


}
