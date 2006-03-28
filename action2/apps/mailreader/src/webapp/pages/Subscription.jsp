<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/webwork" prefix="saf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <saf:if test="task=='Create'">
        <title><saf:text name="subscription.title.create"/></title>
    </saf:if>
    <saf:if test="task=='Edit'">
        <title><saf:text name="subscription.title.edit"/></title>
    </saf:if>
    <saf:if test="task=='Delete'">
        <title><saf:text name="subscription.title.delete"/></title>
    </saf:if>
    <link href="<saf:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body onLoad="self.focus();document.Subscription.username.focus()">

<saf:actionerror/>
<saf:form method="POST" action="SubscriptionSave" validate="false">
    <saf:hidden name="task"/>
    <saf:label label="%{getText('username')}" name="user.username"/>

    <saf:if test="task == 'Create'">
        <saf:textfield label="%{getText('mailHostname')}" name="host"/>
    </saf:if>
    <saf:else>
        <saf:label label="%{getText('mailHostname')}" name="host"/>
        <saf:hidden name="host"/>
    </saf:else>

    <saf:if test="task == 'Delete'">
        <saf:label label="%{getText('mailUsername')}"
                   name="subscription.username"/>
        <saf:label label="%{getText('mailPassword')}"
                   name="subscription.password"/>
        <saf:label label="%{getText('mailServerType')}"
                   name="subscription.type"/>
        <saf:label label="%{getText('autoConnect')}"
                   name="subscription.autoConnect"/>
        <saf:submit value="%{getText('button.confirm')}"/>
    </saf:if>
    <saf:else>
        <saf:textfield label="%{getText('mailUsername')}"
                       name="subscription.username"/>
        <saf:textfield label="%{getText('mailPassword')}"
                       name="subscription.password"/>
        <saf:select label="%{getText('mailServerType')}"
                    name="subscription.type" list="types"/>
        <saf:checkbox label="%{getText('autoConnect')}"
                      name="subscription.autoConnect"/>
        <saf:submit value="%{getText('button.save')}"/>
        <saf:reset value="%{getText('button.reset')}"/>
    </saf:else>

    <saf:submit action="Registration!input"
                value="%{getText('button.cancel')}"
                onclick="form.onsubmit=null"/>
</saf:form>

<jsp:include page="Footer.jsp"/>

</body>
</html>
