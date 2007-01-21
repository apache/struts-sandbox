<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head><title><s:text name="prompt"/></title></head>

<body>
<h2><s:text name="prompt"/></h2>

<p>
    <s:form action="Hello">
        <s:textfield name="message" label="%{getText('message')}"/>
        <s:submit/>
    </s:form>
</p>
</body>
</html>
