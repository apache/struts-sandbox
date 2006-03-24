<%@ taglib uri="/webwork" prefix="a2" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><a2:text name="change.title"/></title>
    <link href="<a2:url value="/css/mailreader.css"/>" rel="stylesheet" type="text/css"/>
</head>

<body>

<p>
    <a2:text name="change.message"/>
</p>

<p>
    <a href="<a2:url action="Logon!input"/>">
        <a2:text name="change.try"/>
    </a>
</p>

</body>
</html>
