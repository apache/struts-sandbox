<%@ taglib uri="/webwork" prefix="a2" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><a2:text name="index.title"/></title>
    <link href="<a2:url value="/css/mailreader.css"/>" rel="stylesheet" type="text/css"/>
</head>

<body>
<h3><a2:text name="index.heading"/></h3>

<ul>
    <li><a href="<a2:url action="Registration!input"/>"><a2:text name="index.registration"/></a></li>
    <li><a href="<a2:url action="Logon!input"/>"><a2:text name="index.logon"/></a></li>
</ul>

<h3>Language Options</h3>

<hr/>

<ul>
    <li><a href="<a2:url action="Locale?language=en"/>">English</a></li>
    <li><a href="<a2:url action="Locale?language=ja"/>">Japanese</a></li>
    <li><a href="<a2:url action="Locale?language=ru"/>">Russian</a></li>
</ul>

<p><a2:i18n name="alternate">
    <img src="<a2:url><a2:text name="struts.logo.path"/></a2:url>" alt="<a2:text name="struts.logo.alt"/>"/>
</a2:i18n></p>

<p><a href="<a2:url action="Tour" />"><a2:text name="index.tour"/></a></p>

</body>
</html>

