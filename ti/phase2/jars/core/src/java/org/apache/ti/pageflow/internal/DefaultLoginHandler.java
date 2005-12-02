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
package org.apache.ti.pageflow.internal;

import org.apache.ti.pageflow.handler.LoginHandler;

import javax.security.auth.login.LoginException;
import java.security.Principal;


/**
 * Implements default J2EE web-tier login handling.
 */
public abstract class DefaultLoginHandler
        extends DefaultHandler
        implements LoginHandler {

    public void login(String username, String password)
            throws LoginException {
        AdapterManager.getContainerAdapter().login(username, password);
    }

    public void logout(boolean invalidateSessions) {
        AdapterManager.getContainerAdapter().logout(invalidateSessions);
    }

    public abstract boolean isUserInRole(String roleName);

    public abstract Principal getUserPrincipal();
}
