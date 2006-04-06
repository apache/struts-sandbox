<%@ taglib uri="/webwork" prefix="ww" %>
<html>
<head>
    <title>Login</title>
</head>

<body onLoad="self.focus();document.Logon.username.focus()">

<ww:actionerror/>
<ww:form method="POST" validate="true">
    <ww:textfield label="UserName" name="username"/>

    <ww:password label="Password" name="password" showPassword="true"/>

    <ww:submit value="Save" name="Save"/>

    <ww:submit action="Login!cancel" value="Cancel" name="Cancel"
               onclick="form.onsubmit=null"/>
</ww:form>

</body>
</html>
