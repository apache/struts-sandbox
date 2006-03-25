<%@ taglib uri="/webwork" prefix="a2" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <a2:if test="task=='Create'">
        <title><a2:text name="registration.title.create"/></title>
    </a2:if>
    <a2:if test="task=='Edit'">
        <title><a2:text name="registration.title.edit"/></title>
    </a2:if>
</head>

<body>

<a2:form method="POST" validate="false">
    <a2:hidden name="task"/>
    <a2:if test="task == 'Create'">
        <a2:textfield label="%{getText('prompt.username')}" name="username"/>
    </a2:if>
    <a2:else>
        <a2:label label="%{getText('prompt.username')}" name="username"/>
        <a2:hidden name="username"/>
    </a2:else>

    <a2:textfield label="%{getText('prompt.password')}" name="password"/>

    <a2:textfield label="%{getText('prompt.password2')}" name="password2"/>

    <a2:textfield label="%{getText('prompt.fullName')}" name="user.fullName"/>

    <a2:textfield label="%{getText('prompt.fromAddress')}" name="user.fromAddress"/>

    <a2:textfield label="%{getText('prompt.replyToAddress')}" name="user.replyToAddress"/>

    <a2:submit/>

    <a2:reset/>

    <a2:if test="task == 'Create'">
        <a2:submit action="Welcome" value="%{getText('button.cancel')}" onclick="form.onsubmit=null"/>
    </a2:if>
    <a2:else>
        <a2:submit action="MainMenu" value="%{getText('button.cancel')}" onclick="form.onsubmit=null"/>
    </a2:else>

</a2:form>

<a2:if test="task == 'Edit'">
    <div align="center">
        <h3><a2:text name="heading.subscriptions"/></h3>
    </div>

    <table border="1" width="100%">

        <tr>
            <th align="center" width="30%">
                <a2:text name="heading.host"/>
            </th>
            <th align="center" width="25%">
                <a2:text name="heading.user"/>
            </th>
            <th align="center" width="10%">
                <a2:text name="heading.type"/>
            </th>
            <th align="center" width="10%">
                <a2:text name="heading.autoConnect"/>
            </th>
            <th align="center" width="15%">
                <a2:text name="heading.action"/>
            </th>
        </tr>

        <a2:iterator value="user.subscriptions">
            <tr>
                <td align="left">
                    <a2:property value="host"/>
                </td>
                <td align="left">
                    <a2:property value="username"/>
                </td>
                <td align="center">
                    <a2:property value="type"/>
                </td>
                <td align="center">
                    <a2:property value="autoConnect"/>
                </td>
                <td align="center">

                    <a href="<a2:url action="Subscription!delete"><a2:param name="host" value="host"/></a2:url>">
                        <a2:text name="registration.deleteSubscription"/>
                    </a>
                    &nbsp;
                    <a href="<a2:url action="Subscription!edit"><a2:param name="host" value="host"/></a2:url>">
                        <a2:text name="registration.editSubscription"/>
                    </a>

                </td>
            </tr>
        </a2:iterator>

    </table>

    <a href="<a2:url action="Subscription!input"/>"><a2:text name="registration.addSubscription"/></a>

</a2:if>

<jsp:include page="Footer.jsp"/>

</body>
</html>
