<%@taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Source Code for Input Form</title>
<link rel="stylesheet" type="text/css" href="<ww:url value="/css/cookbook.css" />" >
</head>
<body>

<ww:url id="input_exe" action="Simple!input" />

<ww:a href="%{input_exe}">
    <img src="<ww:url value="/images/execute.gif"/>" alt="" hspace="4" border="0"  align="top" class="inline" />
</ww:a>

<a href="<ww:url value="/Home.jsp" />" >
    <img src="<ww:url value="/images/return.gif"/>" alt="" hspace="4" border="0"  align="top" class="inline" />
</a>

<h1>Source Code for Simple Input Form using Action Properties</h1>
<hr noshade="noshade"/>

<h2>Server Pages</h2>
<p><a href="<ww:url value="/View.jsp?src=/pages/Simple/Input.jsp"/>">Input.jsp</a></p>
<p><a href="<ww:url value="/View.jsp?src=/pages/Simple/Result.jsp"/>">Result.jsp</a></p>

<h2>Actions</h2>
<p><a href="<ww:url value="/View.jsp?src=/WEB-INF/src/java/cookbook/Simple.java"/>">Input.java</a></p>

<h2>Configuration files</h2>
<p><a href="<ww:url value="/View.jsp?src=/WEB-INF/classes/xwork-Simple.xml"/>">xwork-Input.xml</a></p>

<h2>Other source files</h2>
<p>None</p>

</body>
</html>