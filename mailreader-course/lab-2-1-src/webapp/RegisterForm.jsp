<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="bean" uri="http://struts.apache.org/tags-bean" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>

<html:html>
<head>
    <!-- Lab 2-1: When task is Edit, change title -->
    <title>
        <c:if test="${RegisterForm.map.task == 'Create'}">
            <bean:message key="registration.title.create"/>
        </c:if>
        <c:if test="${RegisterForm.map.task == 'Edit'}">
            <bean:message key="registration.title.edit"/>
        </c:if>
    </title>
</head>

<body>

<html:errors/>

<html:form action="/RegisterSave">
    <html:hidden property="task"/>
    <table border="0" width="100%">

        <tr>
            <th align="right">
                Username:
            </th>
            <td align="left">
                <!-- Lab 2-1: When task is Edit, change username to readonly -->
                <c:if test="${RegisterForm.map.task == 'Create'}">
                    <html:text property="username" size="16" maxlength="16"/>
                </c:if>
                <c:if test="${RegisterForm.map.task == 'Edit'}">
                    <c:out value="${RegisterForm.map.username}"/>
                    <html:hidden property="username"/>
                </c:if>
            </td>
        </tr>

        <tr>
            <th align="right">
                Password:
            </th>
            <td align="left">
                <html:password property="password" size="16"
                               maxlength="16"/>
            </td>
        </tr>

        <tr>
            <th align="right">
                Confirm Password:
            </th>
            <td align="left">
                <html:password property="password2" size="16"
                               maxlength="16"/>
            </td>
        </tr>
        <tr>
            <th align="right">
                Fullname:
            </th>
            <td align="left">
                <html:text property="fullName" size="50"/>
            </td>
        </tr>

        <tr>
            <th align="right">
                From Address:
            </th>
            <td align="left">
                <html:text property="fromAddress" size="50"/>
            </td>
        </tr>

        <tr>
            <th align="right">
                Reply To Address:
            </th>
            <td align="left">
                <html:text property="replyToAddress" size="50"/>
            </td>
        </tr>

        <tr>
            <td align="right">
                <html:submit property="DO_SUBMIT">
                    <bean:message key="button.submit"/>
                </html:submit>
            </td>
            <td align="left">
                <html:reset property="DO_RESET">
                    <bean:message key="button.reset"/>
                </html:reset>
                &nbsp;
                <html:cancel/>
            </td>
        </tr>

    </table>
</html:form>

<!-- Lab 2-1: When task is Edit, display Subscriptions -->
<c:if test="${RegisterForm.map.task == 'Edit'}">
<div align="center">
    <h3><bean:message key="heading.subscriptions"/></h3>
</div>

<table border="1" width="100%">

    <tr>
        <th align="center" width="30%">
            <bean:message key="heading.host"/>
        </th>
        <th align="center" width="25%">
            <bean:message key="heading.user"/>
        </th>
        <th align="center" width="10%">
            <bean:message key="heading.type"/>
        </th>
        <th align="center" width="10%">
            <bean:message key="heading.autoConnect"/>
        </th>
        <th align="center" width="15%">
            <bean:message key="heading.action"/>
        </th>
    </tr>

    <c:forEach var="subscription" items="${user.subscriptions}">
    <tr>
        <td align="left">
            <bean:write name="subscription" property="host"/>
        </td>
        <td align="left">
            <bean:write name="subscription" property="username"/>
        </td>
        <td align="center">
            <bean:write name="subscription" property="type"/>
        </td>
        <td align="center">
            <bean:write name="subscription" property="autoConnect"/>
        </td>
        <td align="center">
            <html:link action="/DeleteSubscription"
                       paramName="subscription" paramId="host"
                       paramProperty="host">
                <bean:message key="registration.deleteSubscription"/>
            </html:link>
            &nbsp;
            <html:link action="/EditSubscription"
                       paramName="subscription" paramId="host"
                       paramProperty="host">
                <bean:message key="registration.editSubscription"/>
            </html:link>
        </td>
    </tr>
    </c:forEach>
    </c:if>
</body>
</html:html>
