<%@ taglib uri="/webwork" prefix="af" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><af:text name="change.title"/></title>
    <link href="<af:url value="/css/mailreader.css"/>" rel="stylesheet" type="text/css"/>
</head>

<body>

<p>
    <af:text name="change.message"/>
</p>

<p>
    <a href="<af:url action="Logon!input"/>">
        <af:text name="change.try"/>
    </a>
</p>

</body>
</html>
