<#--
/*
 * $Id: text.ftl 590812 2007-10-31 20:32:54Z apetrelli $
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
<div class="s2-jquery-datepicker">
<input type="text"<#rt/>
 name="struts.${parameters.name?default("")?html}"<#rt/>
<#if parameters.get("size")??>
 size="${parameters.get("size")?html}"<#rt/>
</#if>
<#if parameters.maxlength??>
 maxlength="${parameters.maxlength?html}"<#rt/>
</#if>
<#if parameters.displayValue??>
 value="<@s.property value="parameters.displayValue"/>"<#rt/>
</#if>
<#if parameters.disabled?default(false)>
 disabled="disabled"<#rt/>
</#if>
<#if parameters.readonly?default(false)>
 readonly="readonly"<#rt/>
</#if>
<#if parameters.tabindex??>
 tabindex="${parameters.tabindex?html}"<#rt/>
</#if>
<#if parameters.id??>
 id="${parameters.id?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/css.ftl" />
<#if parameters.title??>
 title="${parameters.title?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
<#include "/${parameters.templateDir}/simple/dynamic-attributes.ftl" />
/>
</div>
<input type="hidden" name="${parameters.name?default("")?html}" id="${parameters.id?html}_hidden"
<#if parameters.nameValue??>
value="<@s.property value="parameters.nameValue"/>"<#rt/>
</#if>
/>
<script type="text/javascript">
    $(function() {
        $("#${parameters.id?html}").datepicker({
            altField: "#${parameters.id?html}_hidden",
            altFormat: "yy-mm-dd'T'00:00:00",
            dateFormat : "${parameters.displayFormat?html}",
            <#if parameters.imageUrl??>
                buttonImage: "${parameters.imageUrl}",
            <#else>
                buttonImage: "${base}/struts/images/dateIcon.gif",
            </#if>
            buttonImageOnly: true,
            showOn: "both",
            buttonText: "${parameters.imageTooltip}"
        });
        <#if parameters.year?? && parameters.month?? && parameters.day??>
        $("#${parameters.id?html}").val($.datepicker.formatDate("${parameters.displayFormat?html}", new Date(${parameters.year?c}, ${parameters.month}, ${parameters.day})));
        </#if>
    });
</script>