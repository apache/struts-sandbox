<%@ taglib prefix="sjx" uri="/struts-jquery-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Hello World!</title>
    <script type="text/javascript">
        function handleForm_indexForm() {
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
                var form = document.getElementById("indexForm");
                form.submit();
            }
        }
    </script>
    <sjx:head/>
</head>
<body>
<!-- TODO on sjx:form validate="true" will call handleForm_${formId} as a callback -->

<sjx:form theme="jquery-simple" id="indexForm" method="post" validate="true"
          action="SimpleFormPostNonAjaxResponse"
          ajaxResult="false">
<table>
    <tr>
        <td align="right">Message</td>
    </tr>
    <tr>
        <td><sjx:textfield key="msg"/></td>
    </tr>
    <tr>
        <td colspan="2" align="center"><sjx:submit/></td>
    </tr>
</table>
</sjx:form>
<div id="errors" style="color:red"></div>
</body>
</html>
