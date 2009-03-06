<#--
/*
 * $Id: form-close-validate.ftl 720258 2008-11-24 19:05:16Z musachy $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<#if parameters.validate?default(true) == true>
<script type="text/javascript">
<#--TODO add tests for funky datatypes like Date obj returned from the datepicker
    TODO consider ids that contain a period... valid for struts, invalid for jquery
    -->
    function validateForm_${parameters.id}() {
        var formData = {'struts.enableJSONValidation': true};
        $("#${parameters.id}").find(":input").each( function() {
            formData[$(this).attr('name')] = $(this).val();
        });
<#--
        // var formData = $("#${parameters.id}").serialize() + '&struts.enableJSONValidation=true';

var debugStr = "";
jQuery.each(formData, function(i, val) {
    debugStr = debugStr + i + "=>" + val + ", ";
});
alert("formData = " + debugStr); -->

<#if parameters.method?contains("post") >
        $.post("${parameters.action}", formData, validateFormCb_${parameters.id} );
<#else>
        $.get("${parameters.action}", formData, validateFormCb_${parameters.id} );
</#if>
        return false;
    }

    function validateFormCb_${parameters.id}( responseText, textStatus ) {
        
        //clear previous validation errors, if any
        StrutsJQueryUtils.clearValidationErrors("${parameters.id}");

        //get errors from response
        var errorsObject = StrutsJQueryUtils.getValidationErrors(responseText);

        //show errors, if any
        if(errorsObject && errorsObject.fieldErrors) {
            StrutsJQueryUtils.showValidationErrors("${parameters.id}", errorsObject);
        }
        else {
<#if (parameters.ajaxResult?default(false) == true ) && parameters.method?contains("post")>
            var formData = { };
            $("#${parameters.id}").find(":input").each( function() {
                formData[$(this).attr('name')] = $(this).val();
            });
            $.post("${parameters.action}", formData, ${parameters.ajaxResultHandler} );
<#elseif (parameters.ajaxResult?default(false) == true )>
            var formData = { };
            $("#${parameters.id}").find(":input").each( function() {
                formData[$(this).attr('name')] = $(this).val();
            });
            $.get("${parameters.action}", formData, ${parameters.ajaxResultHandler} );
<#else>
            var form = document.getElementById("${parameters.id}");
            form.submit();
</#if>
        }
    }
</script>
</#if>