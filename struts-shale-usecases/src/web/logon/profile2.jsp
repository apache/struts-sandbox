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
  <h:outputText        value="#{messages['logon.title.2']}"/>
</title>
</head>
<body>

  <h:form                  id="profile2Form">

    <h:panelGrid      columns="3">

      <f:facet           name="header">
        <h:messages
                   globalOnly="true"/>
      </f:facet>

      <%-- fullName --%>

      <h:outputLabel      for="fullName">
        <h:outputText   value="#{messages['prompt.fullName']}"/>
      </h:outputLabel>

      <h:inputText         id="fullName"
                     required="true"
                        value="#{logon$dialog.fullName}"/>

      <h:message          for="fullName"/>

      <%-- emailAddress --%>

      <h:outputLabel      for="emailAddress">
        <h:outputText   value="#{messages['prompt.emailAddress']}"/>
      </h:outputLabel>

      <h:inputText         id="emailAddress"
                     required="true"
                        value="#{logon$dialog.emailAddress}"/>

      <h:message          for="emailAddress"/>

      <%-- actions --%>

      <s:token             id="token"/>

      <h:panelGroup>
        <h:commandButton   id="next"
                       action="#{logon$profile2.next}"
                        value="#{messages['label.next']}"/>
        <h:commandButton   id="previous"
                       action="#{logon$profile2.previous}"
                        value="#{messages['label.previous']}"/>
        <h:commandButton   id="finish"
                       action="#{logon$profile2.finish}"
                        value="#{messages['label.finish']}"/>
        <h:commandButton   id="cancel"
                       action="#{logon$profile2.cancel}"
                    immediate="true"
                        value="#{messages['label.cancel']}"/>
      </h:panelGroup>

      <h:message          for="token"/>

    </h:panelGrid>

  </h:form>

</body>
</html>
</f:view>
