<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <s:if test="task=='Create'">
        <title><s:text name="registration.title.create"/></title>
    </s:if>
    <s:if test="task=='Edit'">
        <title><s:text name="registration.title.edit"/></title>
    </s:if>
</head>

<body onLoad="self.focus();document.Register.username.focus()">

<s:actionerror/>
<s:form method="POST" validate="true">
    <s:hidden name="task"/>
    <s:if test="task == 'Create'">
        <s:textfield label="%{getText('username')}" name="username"/>
    </s:if>
    <s:else>
        <s:label label="%{getText('username')}" name="username"/>
        <s:hidden name="username"/>
    </s:else>

    <s:password label="%{getText('password')}" name="password"/>

    <s:password label="%{getText('password2')}" name="password2"/>

    <s:textfield label="%{getText('fullName')}"
                  name="fullName"/>

    <s:textfield label="%{getText('fromAddress')}"
                  name="fromAddress"/>

    <s:textfield label="%{getText('replyToAddress')}"
                  name="replyToAddress"/>

    <s:if test="task == 'Create'">
        <s:submit value="%{getText('button.save')}" action="RegisterCreate"/>

        <s:reset value="%{getText('button.reset')}"/>

        <s:submit action="Welcome" value="%{getText('button.cancel')}"
                   onclick="form.onsubmit=null"/>
    </s:if>
    <s:else>
        <s:submit value="%{getText('button.save')}" action="Register"/>

        <s:reset value="%{getText('button.reset')}"/>

        <s:submit action="Menu" value="%{getText('button.cancel')}"
                   onclick="form.onsubmit=null"/>
    </s:else>

</s:form>

</body>
</html>
