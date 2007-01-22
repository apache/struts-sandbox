<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>
        <s:text name="login.title"/>
    </title>
</head>

<body onLoad="self.focus();document.Login_save.username.focus()">

<s:actionerror/>
<s:form action="Login_save" validate="true">
    <s:textfield key="username"/>

    <s:password key="password" showPassword="true"/>

    <s:submit key="button.save"/>

    <s:submit action="Login_cancel" key="button.cancel"
              onclick="form.onsubmit=null"/>
</s:form>

</body>
</html>
