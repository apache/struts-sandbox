<?xml version="1.0"?>
<html>
<head>
  <title><%=request.getAttribute("title")%></title>
</head>
<body>

  <h1><%=request.getAttribute("title")%></h1>
  <p>
  Congratulations!
  </p>

  <% java.util.Map form = (java.util.Map)request.getAttribute("form"); %>
  <table border="1">
  <tr>
      <th>First Name</th>
      <td><%=form.get("name")%></td>
    </tr>

   <tr>
      <th>Last Name</th>
      <td><%=form.get("lastname")%></td>
    </tr>

  <tr>
      <th>Middle Name</th>
      <td><%=form.get("middlename")%></td>
    </tr>

  <tr>
      <th>Favorite Sport</th>
      <td><%=form.get("sport")%></td>
    </tr>

  <tr>
      <th>Favorite Book</th>
      <td><%=form.get("book")%></td>
    </tr>
  </table>

  <form action="register.do" method="POST">
  <input type="hidden" name="contid" value='<%= request.getAttribute("contid") %>' />
  <input type="submit" name="prev" value="Previous" />
  </form>

  <a href="../../index.html">Return to index</a>  
</body>
</html>
