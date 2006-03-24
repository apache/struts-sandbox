<%@ taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <ww:if test="task=='Create'">
        <title><ww:text name="subscription.title.create"/></title>
    </ww:if>
    <ww:if test="task=='Edit'">
        <title><ww:text name="subscription.title.edit"/></title>
    </ww:if>
    <ww:if test="task=='Edit'">
        <title><ww:text name="subscription.title.edit"/></title>
    </ww:if>
</head>

<body>
<ww:form method="POST" validate="true">
    <ww:hidden name="task"/>
    <ww:hidden name="username"/>

    <ww:label label="%{getText('prompt.username')}" name="user.username"/>

    <ww:if test="task == 'Create'">
        <ww:textfield label="%{getText('prompt.mailHostname')}" name="subscription.host"/>
    </ww:if>
    <ww:else>
        <ww:label label="%{getText('prompt.mailHostname')}" name="subscription.host"/>
        <ww:hidden name="subscription.host"/>
    </ww:else>

    <ww:textfield label="%{getText('prompt.mailUsername')}" name="subscription.username"/>

    <ww:textfield label="%{getText('prompt.mailPassword')}" name="subscription.password"/>

    <ww:select label="%{getText('prompt.mailServerType')}" name="subscription.type"
               list="servers"/>

    <ww:checkbox label="%{getText('prompt.autoConnect')}" name="subscription.autoConnect"/>

    <ww:if test="task == 'Delete'">
        <ww:submit value="%{getText('button.confirm')}"/>
        <ww:reset value="%{getText('button.reset')}"/>
    </ww:if>
    <ww:else>
        <ww:submit value="%{getText('button.save')}"/>
    </ww:else>

    <ww:submit action="MainMenu" value="%{getText('button.cancel')}" onclick="form.onsubmit=null"/>
</ww:form>

<jsp:include page="Footer.jsp"/>

</body>
</html>
