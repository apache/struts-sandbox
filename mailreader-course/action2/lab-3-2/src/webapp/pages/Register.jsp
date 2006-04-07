<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <ww:if test="task=='Create'">
        <title><ww:text name="registration.title.create"/></title>
    </ww:if>
    <ww:if test="task=='Edit'">
        <title><ww:text name="registration.title.edit"/></title>
    </ww:if>
</head>

<body onLoad="self.focus();document.Register.username.focus()">

<ww:actionerror/>
<ww:form method="POST" validate="true">
    <ww:hidden name="task"/>
    <ww:if test="task == 'Create'">
        <ww:textfield label="%{getText('username')}" name="username"/>
    </ww:if>
    <ww:else>
        <ww:label label="%{getText('username')}" name="username"/>
        <ww:hidden name="username"/>
    </ww:else>

    <ww:password label="%{getText('password')}" name="password"/>

    <ww:password label="%{getText('password2')}" name="password2"/>

    <ww:textfield label="%{getText('fullName')}"
                   name="user.fullName"/>

    <ww:textfield label="%{getText('fromAddress')}"
                   name="user.fromAddress"/>

    <ww:textfield label="%{getText('replyToAddress')}"
                   name="user.replyToAddress"/>

    <ww:if test="task == 'Create'">
        <ww:submit value="%{getText('button.save')}" action="RegisterSave"/>

        <ww:reset value="%{getText('button.reset')}"/>

        <ww:submit action="Welcome" value="%{getText('button.cancel')}"
                    onclick="form.onsubmit=null"/>
    </ww:if>
    <ww:else>
        <ww:submit value="%{getText('button.save')}" action="Register"/>

        <ww:reset value="%{getText('button.reset')}"/>

        <ww:submit action="Menu" value="%{getText('button.cancel')}"
                    onclick="form.onsubmit=null"/>
    </ww:else>

</ww:form>

</body>
</html>
