<?xml version="1.0"?>
<html>
<head>
  <title>Struts Flow number guessing game</title>
</head>
<body>

  <h1>Guess the Number Between 1 and 10</h1>
  
  <h2><%= request.getAttribute("hint") %></h2>
  
  <h3>You've guessed <%= request.getAttribute("guesses") %> times.</h3>
  
  <form method="post" action="play.do">
    <input type="hidden" name="contid" value='<%= request.getAttribute("contid") %>' />
    <input type="text" name="guess"/>
    <input type="submit"/>
  </form>
  <a href="../../index.html">Return to index</a>  
</body>
</html>
