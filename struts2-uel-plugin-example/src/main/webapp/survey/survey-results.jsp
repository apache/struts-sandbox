<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Survey</title>
</head>

<body>
<h3>Results</h3>
First Name: <s:property value="surveyBean.firstName"/><br/>
Last Name: <s:property value="surveyBean.lastName"/><br/>
Age: <s:property value="surveyBean.age"/><br/>
Birthday: <s:property value="surveyBean.birthdate"/><br/>
</body>
</html>
