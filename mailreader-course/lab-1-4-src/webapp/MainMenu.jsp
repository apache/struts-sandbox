<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html>
<head>
    <title><bean:message key="MainMenu.title"/></title>
</head>

<body>
<h3>Main Menu Options for <bean:write name="user"
                                      property="fullName"/></h3>
<ul>
    <li>[TODO] Edit Profile</li>
    <li>[TODO] Logout</li>
</ul>
</body>
</html>
