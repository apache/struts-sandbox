<%@ taglib uri="/webwork" prefix="a2" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <a2:if test="task=='Create'">
        <title><a2:text name="subscription.title.create"/></title>
    </a2:if>
    <a2:if test="task=='Edit'">
        <title><a2:text name="subscription.title.edit"/></title>
    </a2:if>
    <a2:if test="task=='Edit'">
        <title><a2:text name="subscription.title.edit"/></title>
    </a2:if>
</head>

<body>
<a2:form method="POST" validate="true">
    <a2:hidden name="task"/>
    <a2:hidden name="username"/>

    <a2:label label="%{getText('prompt.username')}" name="user.username"/>

    <a2:if test="task == 'Create'">
        <a2:textfield label="%{getText('prompt.mailHostname')}" name="subscription.host"/>
    </a2:if>
    <a2:else>
        <a2:label label="%{getText('prompt.mailHostname')}" name="subscription.host"/>
        <a2:hidden name="subscription.host"/>
    </a2:else>

    <a2:textfield label="%{getText('prompt.mailUsername')}" name="subscription.username"/>

    <a2:textfield label="%{getText('prompt.mailPassword')}" name="subscription.password"/>

    <a2:select label="%{getText('prompt.mailServerType')}" name="subscription.type"
               list="servers"/>

    <a2:checkbox label="%{getText('prompt.autoConnect')}" name="subscription.autoConnect"/>

    <a2:if test="task == 'Delete'">
        <a2:submit value="%{getText('button.confirm')}"/>
        <a2:reset value="%{getText('button.reset')}"/>
    </a2:if>
    <a2:else>
        <a2:submit value="%{getText('button.save')}"/>
    </a2:else>

    <a2:submit action="MainMenu" value="%{getText('button.cancel')}" onclick="form.onsubmit=null"/>
</a2:form>

<jsp:include page="Footer.jsp"/>

</body>
</html>
