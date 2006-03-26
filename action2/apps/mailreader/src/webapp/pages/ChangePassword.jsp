<%@ taglib uri="/webwork" prefix="saf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><saf:text name="change.title"/></title>
    <link href="<saf:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body>

<p>
    <saf:text name="change.message"/>
</p>

<p>
    <a href="<saf:url action="Logon!input"/>">
        <saf:text name="change.try"/>
    </a>
</p>

</body>
</html>
