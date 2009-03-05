/*
 * $Id: $
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

var StrutsJQueryUtils = {};

// gets an object with validation errors from string returned by 
// the ajaxValidation interceptor
StrutsJQueryUtils.getValidationErrors = function(data) {
  if(data.indexOf("/* {") == 0) {
    return eval("( " + data.substring(2, data.length - 2) + " )");
  } else {
    return null;
  }  
};

StrutsJQueryUtils.clearValidationErrors = function(formId) {
  clearErrorMessages(formId);
};  

// shows validation errors using functions from xhtml/validation.js
// or css_xhtml/validation.js
StrutsJQueryUtils.showValidationErrors = function(form, errors) {
  StrutsJQueryUtils.clearValidationErrors(form, errors);
    
  if(errors.fieldErrors) {
    for(var fieldName in errors.fieldErrors) {
      for(var i = 0; i < errors.fieldErrors[fieldName].length; i++) {
        addErrorJquery( $("#"+form+" > input[name="+fieldName+"]"), 
                 errors.fieldErrors[fieldName][i]);
      }
    }
  }
};

StrutsJQueryUtils.firstElement  = function(parentNode, tagName) {
  var node = parentNode.firstChild;
  while(node && node.nodeType != 1){
    node = node.nextSibling;
  }
  if(tagName && node && node.tagName && node.tagName.toLowerCase() != tagName.toLowerCase()) {
    node = StrutsJQueryUtils.nextElement(node, tagName);
  }
  return node;  
};

StrutsJQueryUtils.nextElement = function(node, tagName) {
  if(!node) { return null; }
  do {
    node = node.nextSibling;
  } while(node && node.nodeType != 1);

  if(node && tagName && tagName.toLowerCase() != node.tagName.toLowerCase()) {
    return StrutsJQueryUtils.nextElement(node, tagName);
  }
  return node;  
};

StrutsJQueryUtils.previousElement = function(node, tagName) {
  if(!node) { return null; }
  if(tagName) { tagName = tagName.toLowerCase(); }
  do {
    node = node.previousSibling;
  } while(node && node.nodeType != 1);
  
  if(node && tagName && tagName.toLowerCase() != node.tagName.toLowerCase()) {
    return StrutsJQueryUtils.previousElement(node, tagName);
  }
  return node;  
};

StrutsJQueryUtils.addOnLoad = function(func) {
  $().ready(func);
};

StrutsJQueryUtils.addEventListener = function(element, name, observer, capture) {
  if (element.addEventListener) {
    element.addEventListener(name, observer, false);
  } else if (element.attachEvent) {
    element.attachEvent('on' + name, observer);
  }
};
