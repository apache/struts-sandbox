<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>MailReader - Login</title>
</head>

<body onLoad="self.focus();document.Login_save.username.focus()">

<s:actionerror/>
<s:form action="Login_save" validate="true">
    <s:textfield label="Username" name="username"/>

    <s:password label="Password" name="password" showPassword="true"/>

    <s:submit value="Save" name="Save"/>

    <s:submit action="Login_cancel" value="Cancel" name="Cancel"
              onclick="form.onsubmit=null"/>
</s:form>

</body>
</html>
