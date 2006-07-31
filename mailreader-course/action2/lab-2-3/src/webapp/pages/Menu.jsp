<%@ taglib uri="/webwork" prefix="ww" %>
<html>
<head>
    <title>MailReader - Menu</title>
</head>

<body>
<h3>Menu Options for <ww:property value="user.fullName"/></h3>
<ul>
    <li><a href="<ww:url action="Register!input" />">
        Edit your registration profile
    </a>
    </li>
    <li><a href="<ww:url action="Logout"/>">
        Log out of MailReader application
    </a>
</ul>
</body>
</html>