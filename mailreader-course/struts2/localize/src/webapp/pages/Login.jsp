<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><s:text name="login.title"/></title>
</head>

<body onLoad="self.focus();document.Login_save.username.focus()">

<s:actionerror/>
<s:form action="Login_save" validate="true">
    <s:textfield label="%{getText('username')}" name="username"/>

    <s:password label="%{getText('password')}" name="password" showPassword="true"/>

    <s:submit value="%{getText('button.save')}" name="Save"/>

    <s:submit action="Login_cancel" value="%{getText('button.cancel')}" name="Cancel"
              onclick="form.onsubmit=null"/>
</s:form>

</body>
</html>
