<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><ww:text name="menu.title"/></title>
</head>

<body>
<h3><ww:text name="menu.heading"/> <ww:property value="user.fullName"/></h3>
<ul>
    <li><a href="<ww:url action="Register!input" />">
        <ww:text name="menu.registration"/>
    </a>
    </li>
    <li><a href="<ww:url action="Logout"/>">
        <ww:text name="menu.logout"/>
    </a>
</ul>
</body>
</html>
