/* 
 * Copyright 2004-2005 The Apache Software Foundation. 
 * 
 * Licensed under the Apache License , Version 2.0 (the "License" );
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing , software 
 * distributed under the License is distributed on an "AS IS" BASIS ,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND , either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 * $Header:$ 
 */ 
package pageflow.loginexample.loginflow ;

import javax.security.auth.login.LoginException ;

 
import org.apache.ti.pageflow.PageFlowController ;
import org.apache.ti.pageflow.Forward ;

/**
 * @ti.controller nested="true"
 * @ti.simpleAction name="cancel" returnAction="loginCancel"
 * @ti.handleException type="LoginException" path="begin.jsp"
 * @ti.messageBundle bundlePath="org.apache.beehive.samples.netui.resources.loginexample.messages"
 */ 
public class Controller extends PageFlowController 
{
    /**
     * @ti.action
     * @ti.forward name="success" returnAction="loginSuccess"
     * @ti.validationErrorForward name="failure" path="begin.jsp"
     */ 
    public Forward login ( LoginForm form )
        throws LoginException 
    {
        // This ultimately calls login on org.apache.beehive.samples.netui.login.ExampleLoginHandler. 
        login ( form.getUsername (), form.getPassword () );
        return new Forward ( "success" );
    }

    public static class LoginForm implements java.io.Serializable 
    {
        private String _username ;
        private String _password ;

        /**
           // We could have also used the 'displayName' attribute -- a hardcoded string or a 
           // JSP 2.0-style expression.
         * @ti.validatableProperty displayNameKey="displaynames.username"
         * @ti.validateRequired
         * @ti.validateMinLength chars="4"
         */ 
        public String getUsername ()
        {
            return _username ;
        }

        public void setUsername ( String username )
        {
            _username = username ;
        }

        /**
         * @ti.validatableProperty displayNameKey="displaynames.password"
         * @ti.validateRequired
         * @ti.validateMinLength chars="4"
         */ 
        public String getPassword ()
        {
            return _password ;
        }

        public void setPassword ( String password )
        {
            _password = password ;
        }
    }
}
