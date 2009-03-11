<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Struts JQuery Showcase</title>
</head>
<body>
<h2>Simple Form Examples</h2>
   <ul>
       <li>
           <s:url var="url" namespace="/form" action="SimpleFormPostNonAjaxResponse-input"/>
           <s:a href="%{#url}">Simple Form Post No Ajax</s:a>
       </li>
       <li>
           <s:url var="url" namespace="/form" action="SimpleFormGetNonAjaxResponse-input"/>
           <s:a href="%{#url}">Simple Form Get No Ajax</s:a>
       </li>
       <li>
           <s:url var="url" namespace="/form" action="SimpleFormPostAjaxResponse-input"/>
           <s:a href="%{#url}">Simple Form Post Ajax</s:a>
       </li>
       <li>
           <s:url var="url" namespace="/form" action="SimpleFormGetAjaxResponse-input"/>
           <s:a href="%{#url}">Simple Form Get Ajax</s:a>
       </li>
       <li>
           <s:url var="url" namespace="/form" action="FormWithResetGetReqAjaxResp-input"/>
           <s:a href="%{#url}">Form With Reset Get Ajax Response</s:a>
       </li>
       <li>
           <s:url var="url" namespace="/form" action="FormWithResetPostReqAjaxResp-input"/>
           <s:a href="%{#url}">Form With Reset Post Ajax Response</s:a>
       </li>
   </ul>
<h2>Form Examples Using jquery-simple Theme</h2>
   <ul>
       <li>
           <s:url var="url" namespace="/simple-theme" action="FormWithResetPostReqAjaxResp-input"/>
           <s:a href="%{#url}">Form With Reset Post Request Ajax Response (simple theme)</s:a>
       </li>
       <li>
           <s:url var="url" namespace="/simple-theme" action="SimpleFormPostAjaxResponse-input"/>
           <s:a href="%{#url}">Simple Form Post Ajax Response (simple theme)</s:a>
       </li>
       <li>
           <s:url var="url" namespace="/simple-theme" action="SimpleFormPostNonAjaxResponse-input"/>
           <s:a href="%{#url}">Simple Form Post Non-Ajax Response (simple theme)</s:a>
       </li>
   </ul>
<h2>Date Picker Examples</h2>
   <ul>
       <li>
           <s:url var="url" value="/form/date-input"/>
           <s:a href="%{#url}">Date Picker</s:a>
       </li>
       <li>
           <s:url var="url" value="/form/simple-form-with-date-picker-input"/>
           <s:a href="%{#url}">Form With a Date Picker, Validation & AJAX Response</s:a>
       </li>
   </ul>
</body>
</html>