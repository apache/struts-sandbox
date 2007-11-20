<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
        <title>
            <s:text name="subscription.title.delete"/>
        </title>
</head>

<body>

<s:actionerror/>
<s:form validate="true">
    <s:hidden name="input"/>
    <s:hidden name="subscription"/>
    <s:label key="user.username" name="subscription.user.username"/>
    <s:label key="subscription.username"/>
    <s:label key="subscription.password"/>
    <s:label key="subscription.protocol" name="subscription.protocol.description"/>
    <s:label key="subscription.autoConnect"/>
    <s:submit key="button.confirm" action="delete" />
    <s:submit key="button.cancel" action="delete" method="cancel" onclick="form.onsubmit=null"/>
</s:form>
  <script src="<s:url value="/assets/focus-first-input.js"/>" type="text/javascript"></script>
</body>
</html>
