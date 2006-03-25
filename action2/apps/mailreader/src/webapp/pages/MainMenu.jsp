<%@ taglib uri="/webwork" prefix="saf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><saf:text name="mainMenu.title"/></title>
    <link href="<saf:url value="/css/mailreader.css"/>" rel="stylesheet" type="text/css"/>
</head>

<body>
<h3><saf:text name="mainMenu.heading"/> <saf:property value="user.fullName"/></h3>
<ul>
    <li><a href="<saf:url action="Registration!input" />">
        <saf:text name="mainMenu.registration"/>
    </a>
    </li>
    <li><a href="<saf:url action="Logoff"/>">
        <saf:text name="mainMenu.logoff"/>
    </a>
</ul>
</body>
</html>
