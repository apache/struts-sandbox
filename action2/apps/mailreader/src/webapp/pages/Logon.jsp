<%@ taglib uri="/webwork" prefix="a2" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><a2:text name="logon.title"/></title>
    <link href="<a2:url value="/css/mailreader.css"/>" rel="stylesheet" type="text/css"/>
</head>

<body>

<a2:form method="POST" validate="true">
    <a2:textfield label="%{getText('prompt.username')}" name="username"/>

    <a2:textfield label="%{getText('prompt.password')}" name="password"/>

    <a2:submit/>

    <a2:reset/>

    <a2:submit action="Welcome" value="%{getText('button.cancel')}" onclick="form.onsubmit=null"/>
</a2:form>

<jsp:include page="Footer.jsp"/>
</body>
</html>
