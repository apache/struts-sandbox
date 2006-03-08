<%@taglib prefix="ww" uri="/webwork" %>
<html>
    <head>
        <title>Cookbook - Input Form</title>
        <ww:head/>
    </head>
    <body>
        <ww:form action="InputResult">
            <ww:textfield label="Please enter your name" name="name" />
            <ww:submit />
        </ww:form>
    </body>
</html>