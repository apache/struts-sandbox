<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<html:xhtml/>
<html>
<head>
    <title><bean:message key="Logon.title"/></title>
</head>

<body>
<html:errors/>

<html:form action="/LogonPost">
    <table border="0" width="100%">

        <tr>
            <th align="right">
                <bean:message key="username.label"/>:
            </th>
            <td align="left">
                <html:text property="username" size="16" maxlength="18"/>
            </td>
        </tr>

        <tr>
            <th align="right">
                <bean:message key="password.label"/>:
            </th>
            <td align="left">
                <html:password property="password" size="16" maxlength="18"
                               redisplay="false"/>
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
            </td>
        </tr>

    </table>

</html:form>

</body>
</html>
