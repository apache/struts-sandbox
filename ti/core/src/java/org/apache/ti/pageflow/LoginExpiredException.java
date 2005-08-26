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
package org.apache.ti.pageflow;


/**
 * Exception thrown when {@link NotLoggedInException} would be thrown, <i>and</i> when the
 * current HttpServletRequest refers to a session that no longer exists.
 */
public class LoginExpiredException
        extends NotLoggedInException {

    public LoginExpiredException(FlowController fc) {
        super(fc);
    }

    public String[] getMessageParts() {
        return new String[]
        {
            "action ",
            " on Page Flow ",
            " requires a current user, but there is no logged-in user.  This may be due to an expired session."
        };
    }
}
