<%@ taglib prefix="ww" uri="/webwork" %>
<html>
<head>
    <title>Cookbook - Input Form using a POJO</title>
    <link rel="stylesheet" type="text/css" href="<ww:url value="/css/cookbook.css" />">
</head>

<body>
<ww:include value="/header-result.jsp"/>

<h1>Input Result</h1>
<table>
    <ww:label label="First Name" name="firstname"/>
    <ww:label label="Last Name" name="lastname"/>
    <ww:label label="Telephone" name="extension"/>
    <ww:label label="User Name" name="username"/>
    <ww:label label="Hired" name="hired"/>
    <ww:label label="Hours" name="hours"/>
    <ww:label label="Editor?" name="editor"/>
</table>
</body>
</html>