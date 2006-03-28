<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/webwork" prefix="saf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <saf:if test="task=='Create'">
        <title><saf:text name="registration.title.create"/></title>
    </saf:if>
    <saf:if test="task=='Edit'">
        <title><saf:text name="registration.title.edit"/></title>
    </saf:if>
    <link href="<saf:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body>

<saf:form method="POST" validate="false">
    <saf:hidden name="task"/>
    <saf:if test="task == 'Create'">
        <saf:textfield label="%{getText('username')}" name="username"/>
    </saf:if>
    <saf:else>
        <saf:label label="%{getText('username')}" name="username"/>
        <saf:hidden name="username"/>
    </saf:else>

    <saf:textfield label="%{getText('password')}" name="password"/>

    <saf:textfield label="%{getText('password2')}" name="password2"/>

    <saf:textfield label="%{getText('fullName')}"
                   name="user.fullName"/>

    <saf:textfield label="%{getText('fromAddress')}"
                   name="user.fromAddress"/>

    <saf:textfield label="%{getText('replyToAddress')}"
                   name="user.replyToAddress"/>

    <saf:submit value="%{getText('button.save')}"/>

    <saf:reset value="%{getText('button.reset')}"/>

    <saf:if test="task == 'Create'">
        <saf:submit action="Welcome" value="%{getText('button.cancel')}"
                    onclick="form.onsubmit=null"/>
    </saf:if>
    <saf:else>
        <saf:submit action="MainMenu" value="%{getText('button.cancel')}"
                    onclick="form.onsubmit=null"/>
    </saf:else>

</saf:form>

<saf:if test="task == 'Edit'">
    <div align="center">
        <h3><saf:text name="heading.subscriptions"/></h3>
    </div>

    <table border="1" width="100%">

        <tr>
            <th align="center" width="30%">
                <saf:text name="heading.host"/>
            </th>
            <th align="center" width="25%">
                <saf:text name="heading.user"/>
            </th>
            <th align="center" width="10%">
                <saf:text name="heading.type"/>
            </th>
            <th align="center" width="10%">
                <saf:text name="heading.autoConnect"/>
            </th>
            <th align="center" width="15%">
                <saf:text name="heading.action"/>
            </th>
        </tr>

        <saf:iterator value="user.subscriptions">
            <tr>
                <td align="left">
                    <saf:property value="host"/>
                </td>
                <td align="left">
                    <saf:property value="username"/>
                </td>
                <td align="center">
                    <saf:property value="type"/>
                </td>
                <td align="center">
                    <saf:property value="autoConnect"/>
                </td>
                <td align="center">

                    <a href="<saf:url action="Subscription!delete"><saf:param name="host" value="host"/></saf:url>">
                        <saf:text name="registration.deleteSubscription"/>
                    </a>
                    &nbsp;
                    <a href="<saf:url action="Subscription!edit"><saf:param name="host" value="host"/></saf:url>">
                        <saf:text name="registration.editSubscription"/>
                    </a>

                </td>
            </tr>
        </saf:iterator>

    </table>

    <a href="<saf:url action="Subscription!input"/>"><saf:text
            name="registration.addSubscription"/></a>

</saf:if>

<jsp:include page="Footer.jsp"/>

</body>
</html>
