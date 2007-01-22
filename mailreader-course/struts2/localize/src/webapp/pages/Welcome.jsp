<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>
        <s:text name="index.title"/>
    </title>
</head>

<body>
<h3>
    <s:text name="index.heading"/>
</h3>

<ul>
    <li><a href="<s:url action="Register"/>">
        <s:text
                name="index.registration"/>
    </a></li>
    <li><a href="<s:url action="Login"/>">
        <s:text
                name="index.login"/>
    </a></li>
</ul>

<h3>Language Options</h3>
<ul>
    <li>
        <s:url id="en" action="Welcome">
            <s:param name="request_locale">en</s:param>
        </s:url>
        <s:a href="%{en}">English</s:a>
    </li>
    <li>
        <s:url id="ja" action="Welcome">
            <s:param name="request_locale">ja</s:param>
        </s:url>
        <s:a href="%{ja}">Japanese</s:a>
    </li>
    <li>
        <s:url id="ru" action="Welcome">
            <s:param name="request_locale">ru</s:param>
        </s:url>
        <s:a href="%{ru}">Russian</s:a>
    </li>
</ul>

</body>
</html>

