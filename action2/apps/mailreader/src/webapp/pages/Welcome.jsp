<%@taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><ww:text name="index.title"/></title>
    <link href="<ww:url value="/css/mailreader.css"/>" rel="stylesheet" type="text/css" />
</head>
<body>
<h3><ww:text name="index.heading"/></h3>

<ul>
    <li><a href="<ww:url action="Registration!edit"/>"><ww:text name="index.registration"/></a></li>
    <li><a href="<ww:url action="Logon!input"/>"><ww:text name="index.logon"/></a></li>
</ul>

<h3>Language Options</h3>

<hr/>

<ul>
    <li><a href="<ww:url action="Locale?language=en"/>">English</a></li>
    <li><a href="<ww:url action="Locale?language=ja"/>">Japanese</a></li>
    <li><a href="<ww:url action="Locale?language=ru"/>">Russian</a></li>
</ul>

<p><ww:i18n name="alternate">
    <img src="<ww:url><ww:text name="struts.logo.path"/></ww:url>" alt="<ww:text name="struts.logo.alt"/>" />
</ww:i18n></p>

<p><a href="<ww:url action="Tour" />"><ww:text name="index.tour"/></a></p>

</body>
</html>

