<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>
        <s:text name="message"/>
    </title>
</head>

<body>
<h2>
    <s:property value="message"/>
</h2>

<ul>
    <li>
        <s:url id="url" action="Hello_input"/>
        <s:a href="%{url}">
            <s:property value="prompt"/>
        </s:a>
    </li>
</ul>

<ul>
    <li>
        <s:url id="en" action="Hello_input">
            <s:param name="request_locale">en</s:param>
        </s:url>
        <s:a href="%{en}">English</s:a>
    </li>
    <li>
        <s:url id="es" action="Hello_input">
            <s:param name="request_locale">es</s:param>
        </s:url>
        <s:a href="%{es}">Español</s:a>
    </li>
</ul>

</body>
</html>
