<%@ taglib uri="/webwork" prefix="ui" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <ui:if test="task=='Create'">
        <title><ui:text name="subscription.title.create"/></title>
    </ui:if>
    <ui:if test="task=='Edit'">
        <title><ui:text name="subscription.title.edit"/></title>
    </ui:if>
    <ui:if test="task=='Delete'">
        <title><ui:text name="subscription.title.delete"/></title>
    </ui:if>
</head>

<body>
<ui:form method="POST" action="Subscription" validate="true">
    <ui:hidden name="task"/>
    <ui:label label="%{getText('prompt.username')}" name="user.username"/>

    <ui:if test="task == 'Create'">
        <ui:textfield label="%{getText('prompt.mailHostname')}" name="subscription.host"/>
    </ui:if>
    <ui:else>
        <ui:label label="%{getText('prompt.mailHostname')}" name="subscription.host"/>
        <ui:hidden name="subscription.host"/>
    </ui:else>

    <ui:textfield label="%{getText('prompt.mailUsername')}" name="subscription.username"/>

    <ui:textfield label="%{getText('prompt.mailPassword')}" name="subscription.password"/>

    <ui:select label="%{getText('prompt.mailServerType')}" name="subscription.type"
               list="types"/>

    <ui:checkbox label="%{getText('prompt.autoConnect')}" name="subscription.autoConnect"/>

    <ui:if test="task == 'Delete'">
        <ui:submit value="%{getText('button.confirm')}"/>
        <ui:reset value="%{getText('button.reset')}"/>
    </ui:if>
    <ui:else>
        <ui:submit value="%{getText('button.save')}"/>
    </ui:else>

    <ui:submit action="MainMenu" value="%{getText('button.cancel')}" onclick="form.onsubmit=null"/>
</ui:form>

<jsp:include page="Footer.jsp"/>

</body>
</html>
