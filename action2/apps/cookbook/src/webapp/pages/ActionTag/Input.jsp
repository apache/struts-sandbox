<%@ taglib prefix="ww" uri="/webwork" %>

<html>
<head>
    <title>CookBook - Action as Page Controller</title>
    <link rel="stylesheet" type="text/css" href="<ww:url value="/css/cookbook.css" />">
</head>

<body>

<ww:include value="/header-result.jsp"/>

<h1>Action as Page Controller</h1>

<p>
    The controls on this page is each populated by its own action
    and formatted on its own server page fragment.
    Accordingly, each control could be re-used on any number of pages.
</p>

<ww:form method="POST">

    <ww:action name="languages" namespace="/ActionTag" executeResult="true"/>

    <ww:action name="colors" namespace="/ActionTag" executeResult="true"/>

    <ww:submit/>

</ww:form>

</body>
</html>
