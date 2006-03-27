<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/webwork" prefix="saf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><saf:text name="logon.title"/></title>
    <link href="<saf:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body>

<saf:form method="POST" validate="true">
    <saf:textfield label="%{getText('prompt.username')}" name="username"/>

    <saf:textfield label="%{getText('prompt.password')}" name="password"/>

    <saf:submit value="%{getText('button.save')}"/>

    <saf:reset value="%{getText('button.reset')}"/>

    <saf:submit action="Welcome" value="%{getText('button.cancel')}"
               onclick="form.onsubmit=null"/>
</saf:form>

<jsp:include page="Footer.jsp"/>
</body>
</html>
