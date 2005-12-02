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
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
       <title>Physician Results with Details</title>
        <link rel="stylesheet" type="text/css" href="../style.css">
    </head>
    <body>
        <f:view>
	        <f:loadBundle var="msgs" basename="org.apache.beehive.samples.netui.resources.jsf.physiciansFlow.messages" />
			<h:form>
            	<!-- navigation links -->
				<h:panelGrid columns="2" cellpadding="0" width="90%" style="margin-bottom: 30; padding-bottom: 5px; border-bottom: 2px solid blue" >
                    <f:attribute name="align" value="center"/>
                    <h:panelGrid>
                        <f:attribute name="align" value="left"/>
                        <h:commandLink action="shared.home" value="Home" />
                    </h:panelGrid>
	        	</h:panelGrid>

            	<!-- message to user -->
				<h:panelGrid style="margin-bottom: 15" >
                    <f:attribute name="align" value="center"/>
                    <h:outputText value="#{msgs.detailedResults}" styleClass="infoMessage" />
	        	</h:panelGrid>

                <!-- detailed search results -->
                <h:dataTable value="#{pageFlow.results}" var="physician" rendered="#{pageFlow.results != null}" styleClass="resultsTable" rowClasses="oddRow, evenRow" id="searchResults" cellpadding="7px" style="margin-bottom: 30">
                    <h:column>
                        <h:panelGrid columns="2" cellpadding="7">
                            <f:facet name="header">
                                    <h:outputText value="Dr. #{physician.firstName}   #{physician.lastName}" />
                            </f:facet>
                            <h:outputText value="#{msgs.specialtyColumnLabel}: " />
                            <h:outputText value="#{physician.specialty}" />
                            <h:outputText value="#{msgs.genderColumnLabel}: " />
                            <h:outputText value="#{physician.gender}" />
                            <h:outputText value="#{msgs.cityColumnLabel}: " />
                            <h:outputText value="#{physician.city}" />
                            <h:outputText value="#{msgs.schoolColumnLabel}: " />
                            <h:outputText value="#{physician.school}" />
                            <h:outputText value="#{msgs.hospitalsColumnLabel}: " />
                            <h:panelGroup>
                                <c:forEach items="${physician.hospitalAffiliations}" var="hospital">
                                    <f:verbatim>
                                        <c:out value="${hospital}" />
                                        <br>
                                    </f:verbatim>
                                </c:forEach>
                            </h:panelGroup>
                        </h:panelGrid>
                    </h:column>
                    <h:column>
                        <h:panelGrid columns="1" cellpadding="7">
                            <f:facet name="header">
                                <h:outputText value="#{msgs.bioHeading}" />
                            </f:facet>
                            <!-- disabled because this is for output text only -->
                            <h:inputTextarea value="#{physician.bio}" cols="40" rows="10" disabled="false"/>
                        </h:panelGrid>
                    </h:column>
                </h:dataTable>

                <h:panelGrid>
                    <f:attribute name="align" value="center"/>
                    <h:commandLink action="physicianSearch" value="Return to Search" />
                </h:panelGrid>

            </h:form>

        	<!-- explanatory notes block -->
            <h:panelGrid width="90%" cellpadding="5" style="background-color: #EEF3FB; margin-bottom: 30; border:1px solid blue;" rendered="#{sharedFlow.shared.notesPreference}">
                <f:attribute name="align" value="center"/>
                <h:outputText value="#{msgs.notesHeading}" style="font-weight: bold" />
                <h:outputText value="#{msgs.detailedResultsPageNoteOne}" rendered="#{sharedFlow.shared.notesPreference}" />
            </h:panelGrid>
        </f:view>
    </body>
</html>

  
