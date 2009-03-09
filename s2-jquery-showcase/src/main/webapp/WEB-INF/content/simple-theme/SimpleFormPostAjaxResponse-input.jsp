<%@ taglib prefix="sjx" uri="/struts-jquery-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Hello World!</title>
<sjx:head />
    <script type="text/javascript">
        function handleAjaxResponse(responseText, textStatus) {

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
                var formData = StrutsJQueryUtils.keyValueizeForm("indexForm");
                formData["struts.enableJSONValidation"] = false;
                $.post("/s2-jquery-showcase/simple-theme/SimpleFormPostAjaxResponse", formData, handleFormCb);
            }
        }

        function handleFormCb(responseText, textStatus) {
            $("#messages").append(responseText + "<br />\n");
        }
    </script>
</head>
<body>
<sjx:form theme="jquery-simple" id="indexForm" method="post" validate="true"
          namespace="/simple-theme" action="SimpleFormPostAjaxResponse"
          ajaxResult="true" ajaxResultHandler="handleAjaxResponse" >
<sjx:textfield key="msg" />
    <input type="hidden" id="struts.enableJSONValidation" name="struts.enableJSONValidation" value="true" />
<sjx:submit />
</sjx:form>
<div id="messages"></div>
<div id="errors" style="color:red"></div>
</body>
</html>
