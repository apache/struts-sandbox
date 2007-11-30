/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package action.user;

import org.apache.struts2.config.ParentPackage;

import entity.user.User;
import entity.user.UserService;
import entity.user.UserServiceImpl;

@ParentPackage("entity-default")
public class Index extends action.Index {

    // ---- STATICS ----

    static final String ERROR_FROM_ADDRESS_FORMAT = "error.fromAddress.format";

    static final String ERROR_PASSWORD_MATCH = "error.password.match";

    static final String ERROR_PASSWORD_LENGTH = "error.password.length";

    static final String ERROR_REPLY_TO_ADDRESS_FORMAT = "error.replyToAddress.format";

    static final String ERROR_CREDENTIALS_MISMATCH = "error.password.mismatch";

    static final String ERROR_USERNAME_UNIQUE = "error.username.unique";

    static final String PASSWORD_MISMATCH_FIELD = "password";

    // ---- PROPERTIES ----

    private UserService manager;

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User value) {
        user = value;
    }

    // ---- METHODS ----

    protected void authenticate() throws Exception {
        String username = getUser().getUsername();
        String password = getUser().getPassword1();

        User foundValue = manager.findByName(username);
        if ((foundValue != null) && !foundValue.getPassword().equals(password)) {
            foundValue = null;
        }

        boolean isFound = (foundValue != null);
        if (isFound) {
            setUser(foundValue);
            login();
        } else {
            addFieldError(PASSWORD_MISMATCH_FIELD,
                    getText(ERROR_CREDENTIALS_MISMATCH));
        }
    }

    protected void create() throws Exception {
        String username = getUser().getUsername();
        String password = getUser().getPassword1();

        User foundValue = manager.findByName(username);
        boolean isNameInUse = (foundValue != null);
        if (isNameInUse) {
            addActionError(getText(ERROR_USERNAME_UNIQUE));
        } else {
            User newValue = getUser();
            newValue.setPassword(password);
            // Let other details carryover
            manager.create(newValue);
            login();
        }
    }

    @SuppressWarnings("unchecked")
    protected void login() {
        getProfile().put(USER, getUser());
    }

    @SuppressWarnings("unchecked")
    protected void logout() {
        getProfile().put(USER, null);
    }

    protected void update() throws Exception {
        User result = getUser();
        boolean exists = (null != manager.find(result.getId()));
        if (exists) {
            manager.update(result);
        } else {
            addActionError(getText(ERROR_CREDENTIALS_MISMATCH));
        }
    }

    protected boolean validatePasswordChange() {
        String newPassword = getUser().getPassword1();
        boolean changing = ((null != newPassword) && (newPassword.length() > 0));
        if (changing) {
            String confirmPassword = getUser().getPassword2();
            boolean matches = ((null != confirmPassword) && (confirmPassword
                    .equals(newPassword)));
            if (matches) {
                getUser().setPassword(newPassword);
            } else {
                addActionError(getText(ERROR_PASSWORD_MATCH));
            }
        }
        return !hasErrors();
    }

    /**
     * <p>
     * Instantiate default instance.
     * </p>
     */
    public Index() {
        manager = new UserServiceImpl();
    }

    /**
     * <p>
     * Instantiate instance using specified <code>IUserManager</code>.
     * </p>
     * 
     * @param manager
     *            IUserManager instance
     */
    public Index(UserService manager) {
        this.manager = manager;
    }

}
