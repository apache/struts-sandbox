/*
 * $Id: LogonAction.java 360442 2005-12-31 20:10:04Z husted $
 *
 * Copyright 2000-2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mailreader2;

import org.apache.struts.apps.mailreader.dao.ExpiredPasswordException;
import org.apache.struts.apps.mailreader.dao.User;

/**
 * <p> Validate a user logon. </p>
 *
 * @version $Rev: 360442 $ $Date: 2005-12-31 15:10:04 -0500 (Sat, 31 Dec 2005) $
 */
public final class Logon extends MailreaderSupport {

    // -------------------------------------------------------------- Properties

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

    /**
     * <p> Use "username" and "password" fields to retrieve a User object from the database. If credentials are not
     * valid, or database has disappeared, post error messages and forward to input. </p>
     */
    public String execute() throws ExpiredPasswordException {

        User user = findUser(getUsername(), getPassword());

        if (user != null) {
            setUser(user);
        }

        if (this.hasErrors()) {
            return INPUT;
        }

        return SUCCESS;

    }

}
