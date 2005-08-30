<%--
   Copyright 2004-2005 The Apache Software Foundation.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
       http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
  
   $Header:$
--%>
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="javax.security.auth.login.LoginException"%>


<html>
    <head>
        <base href="<%= request.getScheme() %>://<%= request.getServerName() %>:<%= request.getServerPort() %><%= request.getRequestURI() %>">
    </head>

    <body>
        <h3>Login Nested Page Flow</h3>

        <i>In this example, the only valid login is username 'good', password 'good'.</i>
        <br/>
        <br/>

        <span style="color:red;">
            <%--<netui:error key="javax.security.auth.login.LoginException"/>--%>

            <%
                // This is temporary -- no error tag or message bundle support.  Normally, the
                // exception would be mapped to a message.
                Throwable th = (Throwable) request.getAttribute(org.apache.ti.Globals.EXCEPTION_KEY);
            %>
            <%= th != null && th instanceof LoginException ? th.getClass().getName() + " : " + th.getMessage() : "" %>
        </span>

        <form action="login.do" method="POST">
            <table>
                <tr>
                    <td>username:</td>
                    <td><input type="text" name="{actionForm.username}"/></td>
                    <td><span style="color:red;"><%--<netui:error key="username"/>--%></span></td>
                </tr>
                <tr>
                    <td>password:</td>
                    <td><input type="password" name="{actionForm.password}" password="true"/></td>
                    <td><span style="color:red;"><%--<netui:error key="password"/>--%></span></td>
                </tr>
            </table>

            <br/>
            <input type="submit" value="submit"/>
            <input type="submit" value="cancel" name="actionOverride:cancel"/>
        </form>
    </body>
</html>

