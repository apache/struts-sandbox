<?xml version="1.0"?>
<html>
<head>
  <title>Struts Flow number guessing game - Remote edition</title>
    <script type="text/javascript">
  <!--
function cheat() {
    dojo.io.bind({
        url:  'play.do?FlowCall=cheat&contid=<%= request.getAttribute("contid") %>',
        type: "text/javascript",
        load: function(type, data, evt) {
            eval("data = "+data);
            alert("The secret number is "+data.secret+". After applying a penalty, you have guessed "+data.guesses+" times");
        }
    });
}
    -->
  </script>
  <script type="text/javascript" src="../dojo-io.js"></script>
</head>
<body>

  <h1>Guess the Number Between 1 and 10</h1>
  
  <h2><%= request.getAttribute("hint") %></h2>
  
  <h3>You've guessed <%= request.getAttribute("guesses") %> times.</h3>
  
  <form method="post" action="play.do">
    <input type="hidden" name="contid" value='<%= request.getAttribute("contid") %>' />
    <input type="text" name="guess"/>
    <input type="submit"/>
    <input type="button" onclick="cheat()" value="Cheat" />
  </form>
  
  <a href="../../index.html">Return to index</a>  
</body>
</html>
