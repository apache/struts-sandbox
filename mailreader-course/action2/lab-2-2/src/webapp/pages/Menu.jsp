<%@ taglib uri="/webwork" prefix="ww" %>
<html>
<head>
    <title>Menu</title>
</head>

<body>
<h3>Main Menu Options for <ww:property value="fullName"/></h3>
<ul>
    <li><a href="<ww:url action="Registration!input" />">
        Edit your registration profile
    </a>
    </li>
    <li><a href="<ww:url action="Logoff"/>">
        Log off MailReader
    </a>
</ul>
</body>
</html>
