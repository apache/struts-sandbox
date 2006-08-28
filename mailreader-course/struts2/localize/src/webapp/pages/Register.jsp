<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><s:text name="registration.title.create"/></title>
</head>

<body onLoad="self.focus();document.Register_save.username.focus()">

<s:actionerror/>
<s:form action="Register_save" validate="true">

    <s:textfield label="%{getText('username')}" name="username"/>

    <s:password label="%{getText('password')}" name="password"/>

    <s:password label="%{getText('password2')}" name="password2"/>

    <s:textfield label="%{getText('fullName')}" name="fullName"/>

    <s:textfield label="%{getText('fromAddress')}" name="fromAddress"/>

    <s:textfield label="%{getText('replyToAddress')}" name="replyToAddress"/>

    <s:submit value="%{getText('button.save')}" name="Save"/>

    <s:submit action="Register_cancel" value="%{getText('button.cancel')}" name="Cancel"
              onclick="form.onsubmit=null"/>

</s:form>


</body>
</html>
