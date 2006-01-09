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
package org.apache.struts.apps.mailreader.course;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.apps.mailreader.dao.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p/>
 * Validate a user logon.
 * </p>
 *
 * @version $Rev: 360442 $ $Date: 2005-12-31 15:10:04 -0500 (Sat, 31 Dec 2005) $
 */
public final class LogonAction extends RegisterAction {


    /**
     * <p/>
     * Store User object in client session.
     * If user object is null, any existing user object is removed.
     * </p>
     *
     * @param request The request we are processing
     * @param user    The user object returned from the database
     */
    protected void doCacheUser(HttpServletRequest request, User user) {

        HttpSession session = request.getSession();
        session.setAttribute(USER_KEY, user);
    }

    /**
     * <p/>
     * Use "username" and "password" fields from ActionForm to retrieve a User
     * object from the database. If credentials are not valid, or database
     * has disappeared, post error messages and forward to input.
     * </p>
     *
     * @param mapping  The ActionMapping used to select this instance
     * @param form     The optional ActionForm bean for this request (if any)
     * @param request  The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @throws Exception if the application business logic throws
     *                   an exception
     */
    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        // Retrieve user
        String username = doGet(form, USERNAME);
        String password = doGet(form, PASSWORD);
        ActionMessages errors = new ActionMessages();
        User user = doGetUser(username, password, errors);

        // Report back any errors, and exit if any
        if (!errors.isEmpty()) {
            return doInputForward(mapping, request, errors);
        }

        // Cache user object in session to signify logon
        doCacheUser(request, user);

        // Done
        return doFindSuccess(mapping);

    }

}
