<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>MailReader - Menu</title>
</head>

<body>
<h3>Main Menu Options for
    <s:property value="fullName"/>
</h3>
<ul>
    <li><a href="<s:url action="Register" />">
        Edit your registration profile
    </a>
    </li>
    <li><a href="<s:url action="Logout"/>">
        Log out of MailReader application
    </a>
</ul>
</body>
</html>
