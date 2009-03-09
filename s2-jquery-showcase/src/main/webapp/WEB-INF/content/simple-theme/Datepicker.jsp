<%@ taglib prefix="sjx" uri="/struts-jquery-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Date Picker</title>
    <sjx:head/>
</head>
<body>
<s:form id="indexForm" method="post" namespace="/form" action="date" theme="simple">
    <sjx:datepicker name="date" displayFormat="mm dd yy" theme="jquery-simple"/>
    <s:submit />
</s:form>
</body>
</html>