<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<html:html>
    <head>
        <title>Register Form</title>
    </head>

    <body>

    <html:errors/>

    <html:form action="/RegisterSave">
        <table border="0" width="100%">

            <tr>
                <th align="right">
                    Username:
                </th>
                <td align="left">
                    <html:text property="username" size="16" maxlength="16"/>
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

            <!-- Lab 1-2: Insert remaining fields from use case -->

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
                    <html:submit value="SAVE"/>
                </td>
                <td align="left">
                    <html:reset value="RESET"/>
                    &nbsp;
                    <html:cancel value="CANCEL"/>
                </td>
            </tr>

        </table>
    </html:form>

    </body>
</html:html>
