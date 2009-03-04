<%@ taglib prefix="sjx" uri="/struts-jquery-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Hello World!</title>
<sjx:head />
    <script type="text/javascript">
        function handleAjaxResponse(data, textStatus) {
            $("#messages").append(data + "<br />\n");
        }
    </script>
</head>
<body>
<sjx:form id="indexForm" method="post" validate="true"
          namespace="/form"
          action="SimpleFormPostAjaxResponse"
          ajaxResult="true" ajaxResultHandler="handleAjaxResponse" >
<sjx:textfield key="msg" />
<sjx:submit />
</sjx:form>
<div id="messages"></div>
</body>
</html>
