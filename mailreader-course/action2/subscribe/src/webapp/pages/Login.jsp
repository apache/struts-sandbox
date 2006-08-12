<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><ww:text name="login.title"/></title>
</head>

<body onLoad="self.focus();document.Login.username.focus()">

<ww:actionerror/>
<ww:form method="POST" validate="true">
    <ww:textfield label="%{getText('username')}" name="username"/>

    <ww:password label="%{getText('password')}" name="password" showPassword="true"/>

    <ww:submit value="%{getText('button.save')}" name="Save"/>

    <ww:submit action="Login!cancel" value="%{getText('button.cancel')}" name="Cancel"
               onclick="form.onsubmit=null"/>
</ww:form>

</body>
</html>
