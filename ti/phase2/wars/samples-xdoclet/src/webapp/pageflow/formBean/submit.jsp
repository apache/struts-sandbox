<%@ page language="java" contentType="text/html;charset=UTF-8"%>

<html>
    <body>
        <h3><%= request.getRequestURI() %></h3>

        result: <b>${pageInput.result}</b>
        <br/>
        <a href="begin.do">start over</a>
    </body>
</html>

