<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
	<head>
	    <title><s:text name="index.title"/></title>
	</head>
	
	<body>
	<h3><s:text name="index.heading"/></h3>
	
	<ul>
	    <li><a href="<s:url namespace="/user" action="create" method="input" />"><s:text name="index.register"/></a></li>	   
	    <li><a href="<s:url namespace="/user" action="login" method="input"/>"><s:text name="index.login"/></a></li>
	</ul>
	
	<h3>Language Options</h3>
	<ul>
	    <li>
	        <s:url id="en" action="index">
	            <s:param name="request_locale">en</s:param>
	        </s:url>
	        <s:a href="%{en}">English</s:a>
	    </li>
	    <li>
	        <s:url id="ja" action="index">
	            <s:param name="request_locale">ja</s:param>
	        </s:url>
	        <s:a href="%{ja}">Japanese</s:a>
	    </li>
	    <li>
	        <s:url id="ru" action="index">
	            <s:param name="request_locale">ru</s:param>
	        </s:url>
	        <s:a href="%{ru}">Russian</s:a>
	    </li>
	</ul>
	
	</body>
</html>
