<?xml version="1.0"?>
<html>
<head>
  <title><%=request.getAttribute("title")%></title>
</head>
<body>

  <h1><%=request.getAttribute("title")%></h1>
  <p>
  Enter your hobbies:
  </p>

  <center style="color:red"><%=(request.getAttribute("errors") != null ? request.getAttribute("errors") : "")%></center>

  <% java.util.Map form = (java.util.Map)request.getAttribute("form"); %>
  <form action="register.do" method="POST">
  <table>
   <tr>
      <th>Favorite Sport</th>
      <td><input type="text" name="sport" value="<%=(form.get("sport") != null ? form.get("sport") : "")%>"/></td>
    </tr>

   <tr>
      <th>Favorite Book</th>
      <td><input type="text" name="book" value="<%=(form.get("book") != null ? form.get("book") : "")%>"/></td>
    </tr>

  </table>

  <input type="hidden" name="contid" value='<%= request.getAttribute("contid") %>' />
  <input type="submit" name="prev" value="Previous" />
  <input type="submit" name="next" value="Next" />
  </form>

  <a href="../../index.html">Return to index</a>  
</body>
</html>
