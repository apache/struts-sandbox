<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Survey</title>
</head>

<body>
<h3>Please fill out Survey</h3>
<p>
  <s:form action="SurveySave" method="post">
  	<s:textfield label="First Name" name="surveyBean.firstName"></s:textfield>
  	<s:textfield label="Last Name" name="surveyBean.lastName"></s:textfield>
  	<s:textfield label="Age" name="surveyBean.age"></s:textfield>
  	<s:textfield label="Birthday" name="surveyBean.birthdate"></s:textfield>
  	<s:submit value="Submit"></s:submit>
  </s:form>
</p>
</body>
</html>
