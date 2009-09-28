<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@taglib prefix="s" uri="/struts-tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
</head>

<body>
<s:actionerror/>
<s:actionmessage/>

<s:form namespace="/crud" action="person-submit" cssClass="section">
    <s:textfield label="Name" name="person.name"/>
    <s:textfield label="Coolness Level" name="person.coolness"/>
    <s:textfield label="Birth Date" name="person.birthDay"/>
    <s:submit/>
</s:form>

<table class="section">
    <tr>
        <td>ID</td>
        <td>Name</td>
        <td>Coolness Level</td>
        <td>Birthday</td>
        <td/>
    </tr>
    <s:iterator var="person" value="%{personList}">
        <tr>
            <td>${id}</td>
            <td>${name}</td>
            <td>${coolness}</td>
            <td><s:date name="birthDay" format="MM/dd/yyyy" /></td>
            <td>
                <s:a namespace="/crud" action="person-delete">
                    <s:param name="person.id" value="%{id}"/>
                    Delete
                </s:a>
            </td>
        </tr>
    </s:iterator>
</table>

<div class="doc-tabs">
	<ul>
		<li><a href="#tabs-1">Description</a></li>
		<li><a href="#tabs-2">Source</a></li>
	</ul>
	<div id="tabs-1">
        <p>This example shows how to use the Convention plugin with Spring and JPA to create a CRUD application</p>
        For more details:
        <ul>
            <li><a href="http://struts.apache.org/2.x/docs/convention-plugin.html">Convention Plugin</a></li>
            <li><a href="http://struts.apache.org/2.x/docs/spring-plugin.html">Spring Plugin</a></li>
            <li><a href="http://struts.apache.org/2.x/docs/struts-2-spring-2-jpa-ajax.html">Another CRUD example</a> </li>
        </ul>
	</div>
	<div id="tabs-2">
        <div class="doc-tabs">
            <ul>
                <li><a href="#src-tabs-1">PersonAction.java (action)</a></li>
                <li><a href="#src-tabs-2">PersonAction-validation.xml (validation)</a></li>
                <li><a href="#src-tabs-3">person-list.jsp (view)</a></li>
            </ul>
            <div id="src-tabs-1" class="src-java-tab">
                <s:action namespace="/source" name="get-class-source" executeResult="true" flush="false">
                    <s:param name="className" value="%{'org.apache.struts2.showcase.crud.actions.PersonAction'}"/>
                </s:action>
            </div>
            <div id="src-tabs-2" class="src-xml-tab">
                <s:action namespace="/source" name="get-xml-source" executeResult="true" flush="false">
                    <s:param name="path" value="%{'org/apache/struts2/showcase/crud/actions/PersonAction-validation.xml'}"/>
                </s:action>
            </div>
            <div id="src-tabs-3">
            </div>
        </div>
	</div>
</div>

</body>

</html>
