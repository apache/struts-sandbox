/*
 * Copyright 2004 The Apache Software Foundation.
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
 *
 * $Header:$
 */
package org.apache.ti.pageflow.handler;

import javax.security.auth.login.LoginException;
import java.security.Principal;


/**
 * Handler for login/logout/roles.
 */
public interface LoginHandler
        extends Handler {

    /**
     * Log in the given user.
     *
     * @param username the user to log in.
     * @param password the user's password.
     * @throws LoginException if the login fails.
     */
    public void login(String username, String password)
            throws LoginException;

    /**
     * Log out the current user.
     *
     * @param invalidateSessions if <code>true</code>, current sessions associated with the current
     *                           logged-in user will be invalidated.
     */
    public void logout(boolean invalidateSessions);


    /**
     * Tell whether the current user is in a given role.
     *
     * @param roleName the role to check.
     * @return <code>true</code> if there is a current logged-in user who is in the given role.
     */
    public boolean isUserInRole(String roleName);

    /**
     * Get the current user.
     *
     * @return a {@link Principal} that represents the current logged-in user, or <code>null</code> if there is no
     *         logged-in user.
     */
    public Principal getUserPrincipal();
}
