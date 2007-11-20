<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title><s:text name="index.login.title"/></title>
</head>

<body>

<s:actionerror/>
<s:form action="login">
    <s:textfield key="user.username"/>

    <s:password key="user.password1" showPassword="true"/>

    <s:submit key="button.update"/>

    <s:submit key="button.cancel" method="cancel" onclick="form.onsubmit=null"/>
</s:form>

</body>
<script src="assets/focus-first-input.js" type="text/javascript"></script>
</html>
