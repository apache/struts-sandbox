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

<body>

<ww:form method="POST" validate="true">
    <ww:hidden name="task"/>
    <ww:if test="task == 'Create'">
        <ww:textfield label="%{getText('prompt.username')}" name="username"/>
    </ww:if>
    <ww:else>
        <ww:label label="%{getText('prompt.username')}" name="username"/>
        <ww:hidden name="username"/>
    </ww:else>

    <ww:textfield label="%{getText('prompt.password')}" name="password"/>

    <ww:textfield label="%{getText('prompt.password2')}" name="password2"/>

    <ww:textfield label="%{getText('prompt.fullName')}" name="user.fullName"/>

    <ww:textfield label="%{getText('prompt.fromAddress')}" name="user.fromAddress"/>

    <ww:textfield label="%{getText('prompt.replyToAddress')}" name="user.replyToAddress"/>

    <ww:submit/>

    <ww:reset/>

    <ww:if test="task == 'Create'">
        <ww:submit action="Welcome" value="%{getText('button.cancel')}" onclick="form.onsubmit=null"/>
    </ww:if>
    <ww:else>
        <ww:submit action="MainMenu" value="%{getText('button.cancel')}" onclick="form.onsubmit=null"/>
    </ww:else>

</ww:form>

<ww:if test="task == 'Edit'">
    <div align="center">
        <h3><ww:text name="heading.subscriptions"/></h3>
    </div>

    <table border="1" width="100%">

        <tr>
            <th align="center" width="30%">
                <ww:text name="heading.host"/>
            </th>
            <th align="center" width="25%">
                <ww:text name="heading.user"/>
            </th>
            <th align="center" width="10%">
                <ww:text name="heading.type"/>
            </th>
            <th align="center" width="10%">
                <ww:text name="heading.autoConnect"/>
            </th>
            <th align="center" width="15%">
                <ww:text name="heading.action"/>
            </th>
        </tr>

        <ww:iterator value="user.subscriptions">
            <tr>
                <td align="left">
                    <ww:property value="host"/>
                </td>
                <td align="left">
                    <ww:property value="username"/>
                </td>
                <td align="center">
                    <ww:property value="type"/>
                </td>
                <td align="center">
                    <ww:property value="autoConnect"/>
                </td>
                <td align="center">

                    <a href="<ww:url action="Subscription!delete"><ww:param name="host" value="#host"/></ww:url>">
                        <ww:text name="registration.deleteSubscription"/>
                    </a>
                    &nbsp;
                    <a href="<ww:url action="Subscription!edit"><ww:param name="host" value="#host"/></ww:url>">
                        <ww:text name="registration.editSubscription"/>
                    </a>

                </td>
            </tr>
        </ww:iterator>

    </table>

    <ww:action name="Subscription.edit"><ww:text name="registration.addSubscription"/></ww:action>
</ww:if>

<jsp:include page="Footer.jsp"/>

</body>
</html>
