<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Struts JQuery Showcase</title>
</head>
<body>
   <ul>
       <li>
           <s:url var="url" namespace="/" action="SimpleFormPostNonAjaxResponse_input"/>
           <s:a href="%{#url}">Simple Form Post No Ajax</s:a>
       </li>
       <li>
           <s:url var="url" namespace="/" action="SimpleFormGetNonAjaxResponse_input"/>
           <s:a href="%{#url}">Simple Form Get No Ajax</s:a>
       </li>
       <li>
           <s:url var="url" namespace="/" action="SimpleFormPostAjaxResponse_input"/>
           <s:a href="%{#url}">Simple Form Post Ajax</s:a>
       </li>
       <li>
           <s:url var="url" namespace="/" action="SimpleFormGetAjaxResponse_input"/>
           <s:a href="%{#url}">Simple Form Get Ajax</s:a>
       </li>
   </ul>
</body>
</html>