<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
  <head><title>UEL Showcase</title></head>
  <body>
    <ul>
        <li><s:a namespace="/survey" action="edit">Survey</s:a></li>
        <li><s:a namespace="/" action="uel-expressions">UEL Expressions</s:a></li>
    </ul>
  </body>
</html>