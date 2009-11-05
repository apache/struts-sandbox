<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head><title>UEL Expression</title></head>
  <body>
  <s:iterator begin="2" end="9" step="1" var="val">
      <s:property value="%{#val}"/>
  </s:iterator>
  </body>
</html>