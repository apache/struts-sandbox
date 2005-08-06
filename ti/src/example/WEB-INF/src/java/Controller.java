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


import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import java.util.Map;
import ttt.Game;

public class Controller {

    private String name;

    public void setName(String name) {
        this.name = name;
    }    

    /** @ti.action */
    public String index() {
        System.out.println("index called");
        System.out.println("hello "+name);
        
        return Action.SUCCESS;
    }   

   
    //    /** @ti.action */
    //    public String login() {
    //        return Action.SUCCESS;
    //    }
    //
    //    /**
    //     * @ti.action 
    //     * @ti.validateRequired userName "User name is required"
    //     * @ti.validateRequired password "Password is required"
    //     *
    //     * @ti.forward name="success" type="redirect" location="index"
    //     * @ti.forward name="error" type="action" location="login"
    //     */
    //    public String processLogin() {
    //        ActionContext ctx = ActionContext.getContext();
    //        Map params = ctx.getParameters();
    //        String userName = (String)params.get("userName");
    //        String password = (String)params.get("password");
    //
    //        UserManager mgr = new UserManager();
    //        if (mgr.isValid(userName, password)) {
    //            return Action.SUCCESS;
    //        } else {
    //            ActionContext.getContext().put("error", "Invalid login");
    //            return Action.ERROR;
    //        }    
    //    }
    //
    //    /**
    //     * Demonstrates login action with POJO form
    //     * @ti.action 
    //     */
    //    public String processLoginWithForm(LoginForm form) {
    //        // do something
    //        return Action.SUCCESS;
    //    }
    // 
    //    /**
    //     * POJO form with validation annotations on fields.
    //     */
    //    public static final class LoginForm {
    //        
    //        private String userName;
    //        private String password;
    //
    //        public void setUserName(String name) {
    //            this.userName = name;
    //        }
    //
    //        public void setPassword(String val) {
    //            this.password = val;
    //        }
    //
    //        /**
    //         * @ti.validateRequired "User name is required"
    //         */
    //        public String getUserName() {
    //            return this.userName;
    //        }
    //
    //        /**
    //         * @ti.validateRequired "Password is required"
    //         */
    //        public String getPassword() {
    //            return this.password;
    //        }
    //    }   
    //    
    //    static class UserManager {
    //        public boolean isValid(String username, String password) {
    //            return (username.equals("test") && password.equals("test"));
    //        }
    //    }    
    //       
         
}   
