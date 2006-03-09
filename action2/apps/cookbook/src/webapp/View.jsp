<%@taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>View Source</title>
<link rel="stylesheet" type="text/css" href="<ww:url value="css/example.css"/>">
</head>
<body>
<p><strong>Viewing: </strong>
    <ww:property value="#parameters['src']"/>
<hr noshade="noshade" />
<pre>
     <ww:include value="#parameters['src']" />
</pre>
</body>
</html>
