<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <s:if test="task=='Create'">
        <title>
            <s:text name="registration.title.create"/>
        </title>
    </s:if>
    <s:if test="task=='Edit'">
        <title>
            <s:text name="registration.title.edit"/>
        </title>
    </s:if>
</head>

<body onLoad="self.focus();document.Register_save.username.focus()">

<s:actionerror/>
<s:form action="Register_save" validate="true">
    <s:hidden name="task"/>
    <s:if test="task == 'Create'">
        <s:textfield key="username"/>
    </s:if>
    <s:else>
        <s:label key="username"/>
        <s:hidden name="username"/>
    </s:else>

    <s:password key="password"/>

    <s:password key="password2"/>

    <s:textfield key="fullName"/>

    <s:textfield key="fromAddress"/>

    <s:textfield key="replyToAddress"/>

    <s:if test="task == 'Create'">
        <s:submit key="button.save" action="Register_create"/>

        <s:reset key="button.reset"/>

        <s:submit action="Welcome" key="button.cancel"
                  onclick="form.onsubmit=null"/>
    </s:if>
    <s:else>
        <s:submit key="button.save"/>

        <s:reset key="button.reset"/>

        <s:submit action="Menu" key="button.cancel"
                  onclick="form.onsubmit=null"/>
    </s:else>

</s:form>

</body>
</html>
