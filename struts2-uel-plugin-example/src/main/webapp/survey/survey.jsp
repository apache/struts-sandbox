<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
    <title>Survey</title>
</head>

<body>
<h3>Please fill our Survey</h3>

<s:fielderror />

<p>
    <s:form action="save" method="post">
        <s:textfield label="First Name" name="#{surveyBean.firstName}" />
        <s:textfield label="Last Name" name="#{surveyBean.lastName}" />
        <s:textfield label="Age" name="#{surveyBean.age}" />
        <s:textfield label="Birthday" name="#{surveyBean.birthdate}" />
        <s:submit value="Submit"/>
    </s:form>
</p>
</body>
</html>
