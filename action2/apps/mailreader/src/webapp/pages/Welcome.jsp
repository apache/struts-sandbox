<%@ taglib uri="/webwork" prefix="t" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><t:text name="index.title"/></title>
    <link href="<t:url value="/css/mailreader.css"/>" rel="stylesheet" type="text/css"/>
</head>

<body>
<h3><t:text name="index.heading"/></h3>

<ul>
    <li><a href="<t:url action="Registration!input"/>"><t:text name="index.registration"/></a></li>
    <li><a href="<t:url action="Logon!input"/>"><t:text name="index.logon"/></a></li>
</ul>

<h3>Language Options</h3>

<hr/>

<ul>
    <li><a href="<t:url action="Locale?language=en"/>">English</a></li>
    <li><a href="<t:url action="Locale?language=ja"/>">Japanese</a></li>
    <li><a href="<t:url action="Locale?language=ru"/>">Russian</a></li>
</ul>

<p><t:i18n name="alternate">
    <img src="<t:url><t:text name="struts.logo.path"/></t:url>" alt="<t:text name="struts.logo.alt"/>"/>
</t:i18n></p>

<p><a href="<t:url action="Tour" />"><t:text name="index.tour"/></a></p>

</body>
</html>

