<%@ page language="java" contentType="text/html;charset=UTF-8"%>

<html>
    <body>
        <h3><%= request.getRequestURI() %></h3>
        <a href="someAction.do">action someAction</a>
        <br/>
        <a href="throw1.do">throw IntentionalException</a>
        <br/>
        <a href="throw2.do">throw ArithmeticException</a>
    </body>
</html>

