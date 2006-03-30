<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/webwork" prefix="saf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title>Unexpected Error</title>
</head>

<body>
<h2>An unexpected error has occured</h2>

<p>
    Please report this error to your system administrator
    or appropriate technical support personnel.
    Thank you for your cooperation.
</p>

<hr/>

<h3>Error Message</h3>

<saf:actionerror />

<p>
    <saf:property value="%{exception.message}"/>
</p>

<hr/>

<h3>Technical Details</h3>

<p>
    <saf:property value="%{exceptionStack}"/>
</p>

<jsp:include page="Footer.jsp"/>

</body>
</html>
