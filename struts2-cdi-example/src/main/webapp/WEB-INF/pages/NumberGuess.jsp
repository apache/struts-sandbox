<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Numberguess</title>
</head>

<body>
<h2>Numberguess Game - Struts 2 CDI Example</h2>

<h3>I've picked a number between <s:property value="game.smallest"/> and <s:property value="game.biggest"/>.
    You have <s:property value="game.remainingGuesses"/>remaining guesses.</h3>

<s:form action="guess">
    <s:textfield name="game.guess" label="Your Guess"/>
    <s:submit/>
</s:form>
<p/>
<s:actionerror/>

</body>
</html>
