<%@page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="s" uri="http://struts.apache.org/shale/core" %>

<%--

 Copyright 2004-2005 The Apache Software Foundation.
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

--%>

<f:view>
<%@include              file="../messages.jspf"%>
<html>
<head>
<title>
  <h:outputText        value="#{messages['logon.title']}"/>
</title>
</head>
<body>

  <h:form                  id="logonForm">

    <h:panelGrid      columns="3">

      <f:facet           name="header">
        <h:messages
                   globalOnly="true"/>
      </f:facet>

      <%-- "remember" --%>

      <h:outputText     value=""
                     rendered="#{logon$dialog.rememberMe}"/>

      <h:panelGroup  rendered="#{logon$dialog.rememberMe}">
        <h:selectBooleanCheckbox
                           id="remember"
                        value="#{logon$dialog.remember}"/>
        <h:outputLabel    for="remember">
          <h:outputText value="#{messages['prompt.remember']}"/>
        </h:outputLabel>
      </h:panelGroup>

      <h:outputText     value=""
                     rendered="#{logon$dialog.rememberMe}"/>

      <%-- username --%>

      <h:outputLabel      for="username">
        <h:outputText   value="#{messages['prompt.username']}"/>
      </h:outputLabel>

      <h:inputText         id="username"
                     required="true"
                        value="#{logon$dialog.username}"/>

      <h:message          for="username"/>

      <%-- password --%>

      <h:outputLabel      for="password">
        <h:outputText   value="#{messages['prompt.password']}"/>
      </h:outputLabel>

      <h:inputSecret       id="password"
                     required="true"
                        value="#{logon$dialog.password}"/>

      <h:message          for="password"/>

      <%-- actions --%>

      <h:outputText     value=""/>

      <h:panelGroup>
        <h:commandButton   id="logon"
                       action="#{logon$logon.logon}"
                        value="#{messages['label.logon']}"/>
        <h:commandLink     id="create"
                       action="#{logon$logon.create}"
                    immediate="true">
          <h:outputText value="#{messages['logon.create']}"/>
        </h:commandLink>
      </h:panelGroup>

    </h:panelGrid>

  </h:form>

</body>
</html>
</f:view>
