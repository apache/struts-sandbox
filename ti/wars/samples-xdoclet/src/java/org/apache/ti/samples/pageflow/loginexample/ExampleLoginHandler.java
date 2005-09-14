/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
package org.apache.ti.samples.pageflow.loginexample;

import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.util.Map;

import org.apache.ti.pageflow.handler.BaseHandler;
import org.apache.ti.pageflow.handler.LoginHandler;
import org.apache.ti.pageflow.xwork.PageFlowActionContext;

public class ExampleLoginHandler
    extends BaseHandler
    implements LoginHandler
{
    private static class UserPrincipal
        implements Principal
    {
        public String getName()
        {
            return "good";
        }
    }

    public void login( String username, String password )
        throws LoginException
    {
        if ( username.equals("good") && password.equals("good") )
        {
            Map sessionScope = PageFlowActionContext.get().getSessionScope();
            sessionScope.put("_principal", new UserPrincipal());
        }
        else
        {
            throw new LoginException( username );
        }
    }

    public void logout( boolean invalidateSessions )
    {
        PageFlowActionContext.get().getSessionScope().remove("_principal");
    }

    public boolean isUserInRole(String roleName)
    {
        return false;
    }

    public Principal getUserPrincipal()
    {
        return (Principal) PageFlowActionContext.get().getSessionScope().get("_principal");
    }
}
