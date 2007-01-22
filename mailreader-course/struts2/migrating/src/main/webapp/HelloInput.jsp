<%@ taglib prefix="bean" uri="http://struts.apache.org/tags-bean" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<html>
<head>
    <title>
        <bean:message key="prompt"/>
    </title>
</head>

<body>
<h2>
    <bean:message key="prompt"/>
</h2>
<html:errors/>
<html:form action="/Hello">
    <table>
        <tr>
            <td>
                <bean:message key="message"/>
            </td>
            <td>
                <html:text property="message"/>
            </td>
        </tr>
        <td colspan="2">
            <html:submit/>
        </td>
    </table>
</html:form>
</body>
</html>
