<%@taglib prefix="ww" uri="/webwork" %>
<html>
    <head>
        <title>Cookbook - Simple Input Form</title>
        <ww:head/>
    </head>
    <body>
        <ww:form action="SimpleResult">
            <ww:textfield label="Please enter your name" name="name" />
            <ww:submit />
        </ww:form>
    </body>
</html>