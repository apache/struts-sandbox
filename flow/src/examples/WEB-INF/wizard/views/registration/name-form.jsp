<?xml version="1.0"?>
<html>
<head>
  <title><%=request.getAttribute("title")%></title>
</head>
<body>

  <h1><%=request.getAttribute("title")%></h1>
  <p>
  Enter your name information:
  </p>

  <center style="color:red"><%=(request.getAttribute("errors") != null ? request.getAttribute("errors") : "")%></center>
  <form action="register.do" method="POST">

  <% java.util.Map form = (java.util.Map)request.getAttribute("form"); %>
  <table>
   <tr>
      <th>First Name</th>
      <td><input type="text" name="name" value="<%=(form.get("name") != null ? form.get("name") : "")%>"/></td>
    </tr>

   <tr>
      <th>Last Name</th>
      <td><input type="text" name="lastname" value="<%=(form.get("lastname") != null ? form.get("lastname") : "")%>"/></td>
    </tr>

   <tr>
      <th>Middle Name</th>
      <td><input type="text" name="middlename" value="<%=(form.get("middlename") != null ? form.get("middlename") : "")%>"/></td>
    </tr>
  </table>

  <input type="hidden" name="contid" value='<%= request.getAttribute("contid") %>' />
  <input type="submit" name="next" value="Next" />
  </form>

  <a href="../../index.html">Return to index</a>  
</body>
</html>
