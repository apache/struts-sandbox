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
  
   $Header:$
--%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="style.css" >
    </head>
    <body>
        <f:view>
        	<f:loadBundle var="msgs" basename="org.apache.beehive.samples.netui.resources.jsf.messages" />
            <h:form>
                <!-- heading -->
                <h:panelGrid columns="1" style="margin-bottom: 15px" >
                    <f:attribute name="align" value="center"/>
                    <h:panelGrid columns="1" >
                        <f:facet name="header" >
                            <h:outputText value="#{msgs.welcome}" style="font-size: 24; font-weight: bold" />
                        </f:facet>
                    </h:panelGrid>
                </h:panelGrid>

                <!-- intro -->
                <h:panelGrid cellspacing="20" columns="1" width="50%" style="margin-bottom: 15px; border: 1px solid blue; background-color: EEF3FB">
                    <f:attribute name="align" value="center"/>
                    <h:outputText value="#{msgs.intro}" />
                    <h:outputText value="#{msgs.homePageNotes}" />
                </h:panelGrid>

                <!-- link to physicians page flow -->
                <h:panelGrid columns="1" style="margin-bottom: 30px" >
                    <f:attribute name="align" value="center"/>
                    <h:commandLink action="locatePhysician" value="#{msgs.physicianSearchLinkText}" rendered="true"/>
                </h:panelGrid>
                
                <!-- preferences -->
                <h:panelGrid columns="1" style="border: solid 1px blue" cellpadding="5">
                    <f:attribute name="align" value="center"/>
                    <h:outputLabel value="#{msgs.notesPreferenceLabel}" for="notesPreference" styleClass="widgetLabel" />
                    <h:selectOneRadio value="#{backing.notesPreference}"  id="notesPreference" immediate="true" valueChangeListener="#{backing.notesPreferenceChange}" layout="pageDirection" >
                        <f:selectItems value="#{backing.notesPreferenceOptions}" id="notesPreferenceOptions" />
                    </h:selectOneRadio>
                </h:panelGrid>
            </h:form>
        </f:view>
    </body>
</html>

  
