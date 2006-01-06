<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:html>
    <head>
        <title>Register Form</title>
    </head>

    <body>
    <html:errors/>
    <html:form action="/RegisterSave">
        Username: <html:text property="username"/><br>
        <html:submit value="Register"/>
    </html:form>
    </body>
</html:html>
