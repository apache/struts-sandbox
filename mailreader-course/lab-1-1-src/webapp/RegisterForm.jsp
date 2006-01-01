<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html>
    <head>
        <title>Register Form</title>
    </head>

    <body>
    <html:form action="/RegisterSave">
        UserName: <html:text property="username"/><br>
        enter password: <html:password property="password"/><br>
        re-enter password: <html:password property="password2"/><br>
        <html:submit value="Register"/>
    </html:form>
    </body>
</html:html>
