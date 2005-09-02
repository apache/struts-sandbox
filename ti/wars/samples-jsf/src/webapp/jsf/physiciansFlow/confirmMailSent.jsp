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

<html>
    <head>
        <title>Mail Confirmation</title>
        <link rel="stylesheet" type="text/css" href="../style.css">
    </head>
    <body>
        <f:view>
        <f:loadBundle var="msgs" basename="org.apache.beehive.samples.netui.resources.jsf.physiciansFlow.messages" />
	    	<h:form>
            	<!-- navigation links -->
				<h:panelGrid cellspacing="0" cellpadding="0" width="90%" style="margin-bottom: 30; padding-bottom: 5px; border-bottom: 2px solid blue">
                    <f:attribute name="align" value="center"/>
                    <h:commandLink action="shared.home" value="Home" />
	        	</h:panelGrid>
	        	
            	<!-- message to user -->
				<h:panelGrid style="margin-bottom: 15" width="90%" >
                    <f:attribute name="align" value="center"/>

                    <h:panelGrid cellpadding="7">
                        <h:outputFormat value="#{msgs.mailConfirmation}">
                            <f:param value="#{pageInput.firstName}"/>
                            <f:param value="#{pageInput.lastName}"/>
                        </h:outputFormat>
                    </h:panelGrid>

                    <h:panelGrid cellpadding="7" columns="2">
                        <h:commandLink action="returnToPreviousPage" value="Return to Physician Details" />
                        <h:commandLink action="physicianSearch" value="Return to Search" />
                    </h:panelGrid>

	        	</h:panelGrid>
        	</h:form>
	        	
        	<!-- explanatory notes block -->
            <h:panelGrid width="90%" style="background-color: #EEF3FB; margin-bottom: 30; border:1px solid blue;" rendered="#{sharedFlow.shared.notesPreference}">
                <f:attribute name="align" value="center"/>
                <h:outputText value="#{msgs.notesHeading}" style="font-weight: bold"/>
                <h:outputText value="#{msgs.mailConfirmationPageNoteOne}"/>
            </h:panelGrid>
        </f:view>
    </body>
</html>

  
