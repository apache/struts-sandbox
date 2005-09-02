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
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
    <head>
        <title>Physician Search</title>
        <link rel="stylesheet" type="text/css" href="../style.css" >
    </head>
    <body>
        <f:view>
            <f:loadBundle var="msgs" basename="org.apache.beehive.samples.netui.resources.jsf.physiciansFlow.messages" />
            <h:form>
                <!-- navigation links -->
                <h:panelGrid columns="1" cellspacing="0" cellpadding="0" width="90%" style="margin-bottom: 30; padding-bottom: 5px; border-bottom: 2px solid blue" >
                    <f:attribute name="align" value="center"/>
                    <h:commandLink action="shared.home" value="Home" />
                </h:panelGrid>
                                
                <!-- the search form -->
                <h:panelGrid columns="2" width="470" cellpadding="7" style="margin-bottom: 30; border: solid 1px blue" >
                    <f:attribute name="align" value="center"/>

                    <h:outputLabel value="#{msgs.physicianTypeLabel}" for="physicianType" styleClass="widgetLabel" />
                    <h:selectOneRadio value="#{backing.physicianType}"  id="physicianType" immediate="true" valueChangeListener="#{backing.physicianTypeChange}" onclick="onChange=this.form.submit();" layout="pageDirection" >
                        <f:selectItems value="#{backing.physicianTypes}" id="physicianTypes" />
                    </h:selectOneRadio>

                    <h:outputLabel value="#{msgs.specialistTypeLabel}" for="specialistType "styleClass="widgetLabel" />
                    <h:selectOneMenu value="#{backing.criteria.specialty}" disabled="#{backing.specialistsDisabled}" id="specialistType" styleClass="selectMenu" >
                        <f:selectItems value="#{backing.specialistTypes}" id="specialistTypes" />
                    </h:selectOneMenu>

                    <h:outputLabel value="#{msgs.cityLabel}" for="city" styleClass="widgetLabel" />

                    <h:panelGrid columns="2">
                        <h:selectOneMenu value="#{backing.criteria.city}" id="city" required="true" styleClass="selectMenu" >
                            <f:selectItems value="#{backing.cities}" id="cities" />
                        </h:selectOneMenu>
                        <h:message for="city" styleClass="validationMessage" style="color:#ff0000;" />
                    </h:panelGrid>

                    <h:outputLabel value="#{msgs.resultsFormatTypeLabel}" for="resultFormatType" styleClass="widgetLabel" />
                    <h:selectOneRadio value="#{backing.resultFormatType}"  id="resultFormatType" layout="pageDirection" >
                        <f:selectItems value="#{backing.resultFormatTypes}" id="resultFormatTypes" />
                    </h:selectOneRadio>

                    <!-- here we submit a form to an action handler in the backing file -->
                    <h:commandButton action="#{backing.execute}" styleClass="submitButton" id="searchButton" value="Search"  />
                    <f:facet name="td"/>
                </h:panelGrid>
                
                <!-- message to user -->
                <h:panelGrid style="margin-bottom: 15" >
                    <f:attribute name="align" value="center"/>
                    <h:outputText value="#{msgs.searchResultsYes}" styleClass="infoMessage" rendered="#{pageFlow.results.rowCount > 0}" />
                    <h:outputText value="#{msgs.searchResultsNo}" styleClass="infoMessage" rendered="#{pageFlow.results.rowCount == 0}" />           
                </h:panelGrid>
                
                <!-- search results -->
                <h:dataTable value="#{pageFlow.results}" var="physician" rendered="#{pageFlow.results != null}" styleClass="resultsTable" rowClasses="oddRow, evenRow" id="searchResults" cellpadding="7px" >
                    <h:column>
                        <f:facet name="header" >
                            <h:outputText value="#{msgs.specialtyColumnLabel}" />
                        </f:facet>
                        <h:outputText value="#{physician.specialty}" />
                    </h:column>
                    <h:column>
                        <f:facet name="header" >
                            <h:outputText value="#{msgs.firstNameColumnLabel}" />
                        </f:facet>
                        <h:outputText value="#{physician.firstName}" />
                    </h:column>
                    <h:column>
                        <f:facet name="header" >
                            <h:commandLink action="#{backing.sortByLastName}" immediate="true" >
                                <h:outputText value="#{msgs.lastNameColumnLabel}" />
                            </h:commandLink>
                        </f:facet>
                        <h:outputText value="#{physician.lastName}" />
                    </h:column>
                    <h:column>
                        <f:facet name="header" >
                            <h:commandLink action="#{backing.sortByGender}" immediate="true" >
                                <h:outputText value="#{msgs.genderColumnLabel}" />
                            </h:commandLink>
                        </f:facet>
                        <h:outputText value="#{physician.gender}" />
                    </h:column>
                    <h:column>
                        <f:facet name="header" >
                            <h:outputText value="#{msgs.cityColumnLabel}" />
                        </f:facet>
                        <h:outputText value="#{physician.city}" />
                    </h:column>
                    <h:column>
                        <f:facet name="header" >
                            <h:outputText value="#{msgs.detailsColumnLabel}" />
                        </f:facet>
                        <!-- Here we call a page flow action directly -->
                        <h:commandLink value="#{msgs.detailLabel}" action="physicianDetail" >
                            <f:param name="physicianId" value="#{physician.id}" />
                         </h:commandLink>
                    </h:column>
                </h:dataTable>
            </h:form>
                
            <!-- explanatory notes block -->
            <h:panelGrid width="90%" cellpadding="5" style="background-color: #EEF3FB; margin-bottom: 30; border:1px solid blue;" rendered="#{sharedFlow.shared.notesPreference}">
                <h:outputText value="#{msgs.notesHeading}" style="font-weight: bold"/>

                <h:outputText value="#{msgs.searchPageNoteOne}"/>
                <f:verbatim>
                <pre>
        @ti.commandHandler(
            raiseActions = {
                @ti.raiseAction(action="displayPhysiciansWithDetail", outputFormBean="searchForm"),
                @ti.raiseAction(action="displayPhysiciansAbbreviated", outputFormBean="searchForm")
            }
        )
                </pre>
                </f:verbatim>


                <h:outputText value="#{msgs.searchPageNoteTwo}"/>
                <f:verbatim>
                <pre>
        &lt;h:commandLink value="#{msgs.detailLabel}" action="physicianDetail" &gt;
            &lt;f:param name="physicianId" value="#{physician.id}" &#047;&gt;
        &lt;&#047;h:commandLink&gt;
                </pre>
                </f:verbatim>


                <h:outputText value="#{msgs.searchPageNoteThree}"/>

                <h:outputText value="#{msgs.searchPageNoteFour}"/>

                <h:outputText value="#{msgs.searchPageNoteFive}"/>
            </h:panelGrid>

        </f:view>
    </body>
</html>
