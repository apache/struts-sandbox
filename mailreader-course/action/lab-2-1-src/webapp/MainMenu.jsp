<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="bean" uri="http://struts.apache.org/tags-bean" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<html>
<head>
    <title><bean:message key="MainMenu.title"/></title>
</head>

<body>
<h3>Main Menu Options for <c:out value="${user.map.fullName}"/></h3>
<ul>
    <li><html:link action="/RegisterEdit">Edit Profile</html:link></li>
    <li>[TODO] Logout</li>
</ul>
</body>
</html>
