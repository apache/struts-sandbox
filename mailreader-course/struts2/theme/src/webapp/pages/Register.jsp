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

<body>

<ww:actionerror/>
<ww:form method="POST" validate="true">
    <ww:token/>
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
                   name="fullName"/>

    <ww:textfield label="%{getText('fromAddress')}"
                   name="fromAddress"/>

    <ww:textfield label="%{getText('replyToAddress')}"
                   name="replyToAddress"/>

    <ww:if test="task == 'Create'">
        <ww:submit value="%{getText('button.save')}" action="RegisterCreate"/>

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

                    <a href="<ww:url action="Subscribe!delete"><ww:param name="host" value="host"/></ww:url>">
                        <ww:text name="registration.deleteSubscription"/>
                    </a>
                    &nbsp;
                    <a href="<ww:url action="Subscribe!edit"><ww:param name="host" value="host"/></ww:url>">
                        <ww:text name="registration.editSubscription"/>
                    </a>

                </td>
            </tr>
        </ww:iterator>

    </table>

    <a href="<ww:url action="Subscribe!input"/>"><ww:text
            name="registration.addSubscription"/></a>

</ww:if>

</body>
</html>