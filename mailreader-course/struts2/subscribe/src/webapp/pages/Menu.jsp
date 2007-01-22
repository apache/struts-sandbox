<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>
        <s:text name="menu.title"/>
    </title>
</head>

<body>
<h3>
    <s:text name="menu.heading"/>
    <s:property value="user.fullName"/>
</h3>
<ul>
    <li><a href="<s:url action="Register" />">
        <s:text name="menu.registration"/>
    </a>
    </li>
    <li><a href="<s:url action="Logout"/>">
        <s:text name="menu.logout"/>
    </a>
</ul>
</body>
</html>
