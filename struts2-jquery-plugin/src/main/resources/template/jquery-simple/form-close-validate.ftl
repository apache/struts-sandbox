<#--
/*
 * $Id$
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
<#if parameters.ajaxResult?default(true) == true>
<script type="text/javascript">
<#--TODO add tests for funky datatypes like Date obj returned from the datepicker
    TODO consider ids that contain a period... valid for struts, invalid for jquery
    -->
    function handleForm_${parameters.id}() {
        var formData = StrutsJQueryUtils.keyValueizeForm("${parameters.id}");
<#if parameters.method?contains("post") >
        $.post("${parameters.action}", formData, ${parameters.ajaxResultHandler} );
<#else>
        $.get("${parameters.action}", formData, ${parameters.ajaxResultHandler} );
</#if>
        return false;
    }
</script>
</#if>