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
  <h:outputText        value="#{messages['usecases.title']}"/>
</title>
</head>
<body>

 <h:form                   id="usecasesForm">

  <h:panelGrid        columns="1">

    <f:facet             name="header">
      <h:panelGroup>
        <h:outputText   value="#{messages['prompt.username']}"/>
        <h:outputText   value="#{user.username}"
                     rendered="#{!(empty user)}"/>
        <h:outputText   value="----"
                     rendered="#{empty user}"/>
      </h:panelGroup>
    </f:facet>

    <h:commandLink     action="locale$select">
      <h:outputText     value="#{messages['usecases.locale']}"/>
    </h:commandLink>

    <h:commandLink     action="#{logon$dialog.enter}"
                     rendered="#{empty user}">
      <h:outputText     value="#{messages['usecases.logon']}"/>
    </h:commandLink>

    <h:commandLink     action="#{logon$dialog.edit}"
                     rendered="#{!(empty user)}">
      <h:outputText     value="#{messages['usecases.edit']}"/>
    </h:commandLink>

    <h:commandLink     action="#{logon$dialog.logoff}"
                     rendered="#{!(empty user)}">
      <h:outputText     value="#{messages['usecases.logoff']}"/>
    </h:commandLink>

    <f:facet             name="footer">
      <h:panelGroup>
        <h:outputText   value="#{messages['prompt.locale']}"/>
        <h:outputText   value="#{view.locale}"/>
      </h:panelGroup>
    </f:facet>

  </h:panelGrid>

 </h:form>

</body>
</html>
</f:view>
