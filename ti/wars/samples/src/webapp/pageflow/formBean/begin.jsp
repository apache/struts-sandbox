<%@ page language="java" contentType="text/html;charset=UTF-8"%>

<html>
    <body>
        <h3><%= request.getRequestURI() %></h3>

        <form action="submit.do" method="POST">
            foo: <input name="{actionForm.foo}" type="text"/>
            <br/>
            <input type="submit" value="submit"/>
        </form>
    </body>
</html>

