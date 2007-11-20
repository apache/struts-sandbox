<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title><s:text name="user.index.title"/></title>
</head>

<body>
<h3><s:text name="user.index.heading"/>
    <s:property value="user.fullName"/>
</h3>
<ul>
    <li><a href="<s:url action="update" method="input"/>">
        <s:text name="user.index.register"/>        
    </a>
    </li>
    <li><a href="<s:url action="logout"/>">
        <s:text name="user.index.logout"/>
    </a>
</ul>
</body>
</html>
