<?xml version="1.0"?>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects/>

  <h1>Guess the Number Between 1 and 10</h1>

  <h2><%= request.getAttribute("hint") %></h2>
  
  <h3>You've guessed <%= request.getAttribute("guesses") %> times.</h3>
   
<portlet:renderURL var="submitUrl"> 
  <portlet:param name="contid" value="<%= String.valueOf(request.getAttribute("contid")) %>"/> 
</portlet:renderURL>

<form action="<%=submitUrl%>"> 
    <input type="text" name="guess"/>
    <input type="submit"/>
  </form>
