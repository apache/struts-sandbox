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

<html>
    <head>
        <base href="<%= request.getScheme() %>://<%= request.getServerName() %>:<%= request.getServerPort() %><%= request.getRequestURI() %>">
    </head>

    <body>
        Clicking the link below launches the ChooseAirport nested page flow, which does one of two
        things:
        <ul>
            <li>
                Returns the 'chooseAirportDone' action, which carries a Results bean with it.  In
                this case, we forward to a results page to display the returned data.
            </li>
            <li>
                Returns the 'chooseAirportCancelled' action, in which case we just go back to the
                current page.
            </li>
        </ul>
        The "Your Name" field below is used to show that this page flow's state is preserved when
        you return from the nested flow.
        <br/>
        <br/>
        <hr/>
        <form action="chooseAirport.do" method="POST">
            Your Name: <input type="text" name="{pageFlow.yourName}"/>
            <br/>
            <input type="submit" value="Run the Choose Airport wizard"/>
        </form>
    </body>
</html>
