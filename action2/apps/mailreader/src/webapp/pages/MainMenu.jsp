<%@ taglib uri="/webwork" prefix="a2" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><a2:text name="mainMenu.title"/></title>
    <link href="<a2:url value="/css/mailreader.css"/>" rel="stylesheet" type="text/css"/>
</head>

<body>
<h3><a2:text name="mainMenu.heading"/> <a2:property value="user.fullName"/></h3>
<ul>
    <li><a href="<a2:url action="Registration!input" />">
        <a2:text name="mainMenu.registration"/>
    </a>
    </li>
    <li><a href="<a2:url action="Logoff"/>">
        <a2:text name="mainMenu.logoff"/>
    </a>
</ul>
</body>
</html>
