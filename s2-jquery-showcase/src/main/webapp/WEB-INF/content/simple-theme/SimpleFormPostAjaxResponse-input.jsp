<%@ taglib prefix="sjx" uri="/struts-jquery-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Hello World!</title>
<sjx:head />
    <script type="text/javascript">
        function handleAjaxResponse(data, textStatus) {
            var formData = StrutsJQueryUtils.keyValueizeForm("indexForm");
            formData['struts.enableJSONValidation'] = true;
            $.post("/s2-jquery-showcase/form/SimpleFormPostNonAjaxResponse", formData, handleFormCb_indexForm);
            return false;
        }

        function handleFormCb_indexForm(responseText, textStatus) {

            //clear previous validation errors, if any
            $("#errors").empty();

            //get errors from response
            var errorsObject = StrutsJQueryUtils.getValidationErrors(responseText);

            //show errors, if any
            if (errorsObject && errorsObject.fieldErrors) {
                for (var fieldName in errorsObject.fieldErrors) {
                    for (var i = 0; i < errorsObject.fieldErrors[fieldName].length; i++) {
                        $("#errors").html(
                                $("#errors").html() + "<br/>" + errorsObject.fieldErrors[fieldName][i]
                                );
                    }
                }
            }
            else {
                $("#messages").append(responseText + "<br />\n");
                // alert(textStatus);
            }
        }
    </script>
</head>
<body>
<sjx:form theme="jquery-simple" id="indexForm" method="post" validate="true"
          namespace="/form" action="SimpleFormPostAjaxResponse"
          ajaxResult="true" ajaxResultHandler="handleAjaxResponse" >
<sjx:textfield key="msg" />
<sjx:submit />
</sjx:form>
<div id="messages"></div>
<div id="errors" style="color:red"></div>
</body>
</html>
