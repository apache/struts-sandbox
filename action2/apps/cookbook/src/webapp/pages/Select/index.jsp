<%@taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Source Code for Complex Input Form using Select Controls</title>
<link rel="stylesheet" type="text/css" href="<ww:url value="/css/cookbook.css" />" >
</head>
<body>

<a href="<ww:url action="Home" />">
    <img src="<ww:url value="/images/return.gif"/>" alt="Home" hspace="4" border="0"  align="top" class="inline" />
</a>

<ww:url id="Open" action="Open" namespace="/Select" />
<ww:a href="%{Open}">
    <img src="<ww:url value="/images/execute.gif"/>" alt="Open" hspace="4" border="0"  align="top" class="inline" />
</ww:a>

<h1>Source Code for Complex Input Form using Select Controls</h1>
<hr noshade="noshade"/>

<h2>Server Pages</h2>
<ww:url id="input" value="/View.jsp?src=/pages/Select/Select.jsp"/>
<ww:url id="result" value="/View.jsp?src=/pages/Select/Result.jsp"/>
<p><a href="<ww:property value="#input"/>">Select.jsp</a></p>
<p><a href="<ww:property value="#result"/>">Result.jsp</a></p>

<h2>Actions</h2>
<ww:url id="action" value="/View.jsp?src=/WEB-INF/src/java/cookbook/Select.java"/>
<p><a href="<ww:property value="#action"/>">Select.java</a></p>

<h2>Configuration files</h2>
<ww:url id="config" value="/View.jsp?src=/WEB-INF/classes/xwork-Select.xml"/>
<p><a href="<ww:property value="#config"/>">xwork-Select.xml</a></p>

<h2>Other source files</h2>
<p>None</p>

</body>
</html>