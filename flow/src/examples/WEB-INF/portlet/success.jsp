<?xml version="1.0"?>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
  <h1>Success!</h1>
   
  <h2>The number was: <%= request.getAttribute("random") %></h2>
  
  <h3>It took you <%= request.getAttribute("guesses") %> tries.</h3>
  
 <portlet:renderURL var="url" /> 
  <p><a href="<%=url%>">Play again</a></p>


