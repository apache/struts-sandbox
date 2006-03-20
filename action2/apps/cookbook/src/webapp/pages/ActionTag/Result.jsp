<%@ taglib prefix="ww" uri="/webwork" %>
<html>
<head>
    <title>Cookbook - Action as Page Controller</title>
    <link rel="stylesheet" type="text/css" href="<ww:url value="/css/cookbook.css" />">
</head>

<body>
<ww:include value="/header-result.jsp"/>

<h1>Input Result</h1>

<table>
    <ww:label label="Favorite Language" name="favoriteLanguage"/>
    <ww:label label="Favorite Color" name="favoriteColor"/>
</table>

</body>
</html>
