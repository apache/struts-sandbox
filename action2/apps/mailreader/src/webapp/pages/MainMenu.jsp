<%@taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><ww:text name="mainMenu.title"/></title>
    <link rel="stylesheet" type="text/css" href="base.css"/>
</head>

<body>
<h3><ww:text name="mainMenu.heading"/> <bean:write name="user"
                                                       property="fullName"/></h3>
<ul>
    <li><a href="<ww:url action="Registration!edit" />"> 
        <ww:text name="mainMenu.registration" />
    </a>
    </li>
    <li><a href="<ww:url action="Loggoff"/>">
        <ww:text name="mainMenu.logoff" />
    </a>
</ul>
</body>
</html>
