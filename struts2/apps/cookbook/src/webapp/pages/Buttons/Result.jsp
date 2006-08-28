<%@ taglib prefix="ww" uri="/webwork" %>
<html>
<head>
    <title>Cookbook - Detecting Buttons</title>
    <link rel="stylesheet" type="text/css" href="<ww:url value="/css/cookbook.css" />">
</head>

<body>
<ww:include value="/header-result.jsp"/>

<h1>Input Result</h1>
<table>
    <ww:label label="Message" name="message"/>
    <ww:label label="Sent to" name="recipient"/>
</table>
</body>
</html>