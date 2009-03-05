/*
 * $Id: validation.js 692578 2008-09-05 23:30:16Z davenewton $
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

function clearErrorMessages(form) {
    clearErrorMessagesJquery(form);
}

function clearErrorMessagesJquery(form) {

    $("#"+form+" > tr[errorFor!=null]").remove();
}

function clearErrorLabels(form) {
    clearErrorLabelsJquery(form);
}

function clearErrorLabelsJquery(form) {
    $("#"+form+"> .errorLabel").addClass("label").removeClass("errorLabel");
}

function addError(e, errorText) {
    addErrorJquery(e, errorText);
}

function addErrorJquery(e, errorText) {
    try {
        var row = (e.type ? e : e[0]);
        while(row.nodeName.toUpperCase() != "TR") {
            row = row.parentNode;
        }
        var table = row.parentNode;
        var error = document.createTextNode(errorText);
        var tr = document.createElement("tr");
        var td = document.createElement("td");
        var span = document.createElement("span");
        td.align = "center";
        td.valign = "top";
        td.colSpan = 2;
        span.setAttribute("class", "errorMessage");
        span.setAttribute("className", "errorMessage"); //ie hack cause ie does not support setAttribute
        span.appendChild(error);
        td.appendChild(span);
        tr.appendChild(td);
        tr.setAttribute("errorFor", e.id);
        table.insertBefore(tr, row);

        // update the label too
        //if labelposition is 'top' the label is on the row above
        var labelRow = row.cells.length > 1 ? row : StrutsUtils.previousElement(tr, "tr");
        var label = labelRow.cells[0].getElementsByTagName("label")[0];
        if (label) {
            label.setAttribute("class", "errorLabel");
            label.setAttribute("className", "errorLabel"); //ie hack cause ie does not support setAttribute
        }
    } catch (e) {
        alert(e);
    }
}
