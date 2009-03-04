<%@ taglib prefix="sjx" uri="/struts-jquery-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Hello World!</title>
<sjx:head />
</head>
<body>
<sjx:form id="indexForm" method="get" validate="true"
          action="SimpleFormPostNonAjaxResponse"
          ajaxResult="false" >
<sjx:textfield key="msg" />
<sjx:submit />
</sjx:form>
</body>
</html>
