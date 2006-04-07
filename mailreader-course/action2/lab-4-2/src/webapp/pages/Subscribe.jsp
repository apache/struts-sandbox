<%@ page contentType="text/html; charset=UTF-8" %>
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
    <ww:if test="task=='Delete'">
        <title><ww:text name="subscription.title.delete"/></title>
    </ww:if>
    <link href="<ww:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body onLoad="self.focus();document.Subscribe.username.focus()">

<ww:actionerror/>
<ww:form method="POST" action="SubscribeSave" validate="true">
    <ww:token/>
    <ww:hidden name="task"/>
    <ww:label label="%{getText('username')}" name="user.username"/>

    <ww:if test="task == 'Create'">
        <ww:textfield label="%{getText('mailHostname')}" name="host"/>
    </ww:if>
    <ww:else>
        <ww:label label="%{getText('mailHostname')}" name="host"/>
        <ww:hidden name="host"/>
    </ww:else>

    <ww:if test="task == 'Delete'">
        <ww:label label="%{getText('mailUsername')}"
                   name="subscription.username"/>
        <ww:label label="%{getText('mailPassword')}"
                   name="subscription.password"/>
        <ww:label label="%{getText('mailServerType')}"
                   name="subscription.type"/>
        <ww:label label="%{getText('autoConnect')}"
                   name="subscription.autoConnect"/>
        <ww:submit value="%{getText('button.confirm')}"/>
    </ww:if>
    <ww:else>
        <ww:textfield label="%{getText('mailUsername')}"
                       name="subscription.username"/>
        <ww:textfield label="%{getText('mailPassword')}"
                       name="subscription.password"/>
        <ww:select label="%{getText('mailServerType')}"
                    name="subscription.type" list="types"/>
        <ww:checkbox label="%{getText('autoConnect')}"
                      name="subscription.autoConnect"/>
        <ww:submit value="%{getText('button.save')}"/>
        <ww:reset value="%{getText('button.reset')}"/>
    </ww:else>

    <ww:submit action="Register!input"
                value="%{getText('button.cancel')}"
                onclick="form.onsubmit=null"/>
</ww:form>

<jsp:include page="Footer.jsp"/>

</body>
</html>
