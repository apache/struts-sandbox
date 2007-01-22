<%@ taglib prefix="bean" uri="http://struts.apache.org/tags-bean" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<html>
<head>
    <title>
        <bean:message key="message"/>
    </title>
</head>

<body>
<h2>
    <bean:write name="HelloForm" property="message"/>
</h2>

<ul>
    <li>
        <html:link action="/HelloInput">
            <bean:message key="prompt"/>
        </html:link>
    </li>
</ul>

<ul>
    <li>
        <html:link action="/Locale?language=en">English</html:link>
    </li>
    <li>
        <html:link action="/Locale?language=es">Español</html:link>
    </li>

</ul>

</body>
</html>
