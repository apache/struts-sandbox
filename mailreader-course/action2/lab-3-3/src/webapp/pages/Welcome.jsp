<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><ww:text name="index.title"/></title>
</head>

<body>
<h3>MailReader Options</h3>

<ul>
    <li><a href="<ww:url action="Register!input"/>"><ww:text
            name="index.registration"/></a></li>
    <li><a href="<ww:url action="Login!input"/>"><ww:text
            name="index.login"/></a></li>
</ul>

<h3>Language Options</h3>
<ul>
    <li><a href="<ww:url action="Welcome?request_locale=en"/>">English</a></li>
    <li><a href="<ww:url action="Welcome?request_locale=ja"/>">Japanese</a></li>
    <li><a href="<ww:url action="Welcome?request_locale=ru"/>">Russian</a></li>
</ul>

<hr />

<ww:form>

<ww:doubleselect
    label="doubleselect test1"
    name="menu"
    list="{'fruit','other'}"
    doubleName="dishes"
    doubleList="top == 'fruit'
      ? {'apple', 'orange'} :   {'monkey', 'chicken'}" />
</ww:form>
</body>
</html>

