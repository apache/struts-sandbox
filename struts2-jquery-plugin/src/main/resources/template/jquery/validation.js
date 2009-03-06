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

function clearErrorMessages(form) {
    clearErrorMessagesJquery(form);
}

function clearErrorMessagesJquery(form) {
    $("#"+form).find("tr[errorFor]").remove();
}

function clearErrorLabels(form) {
    clearErrorLabelsJquery(form);
}

function clearErrorLabelsJquery(form) {
    $("#"+form).find(".errorLabel").removeClass("errorLabel");
}

function addError(e, errorText) {
    addErrorJquery(e, errorText);
}

function addErrorJquery(e, errorText) {

    $("#"+e).closest("tr").before(
      $(document.createElement("tr")).attr({errorFor:e}).html(
        $(document.createElement("td")).attr({colspan:2}).html(
          $(document.createElement("span")).addClass("errorMessage").html(errorText)
                )
            )
        );
    $("label[for="+ e +"]").addClass('errorLabel');
}
