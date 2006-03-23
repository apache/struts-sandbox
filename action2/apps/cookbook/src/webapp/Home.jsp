<%@ taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Struts Action 2 Cookbook</title>

    <link href="<ww:url value="/css/cookbook.css"/>" rel="stylesheet" type="text/css"/>
</head>

<body>
<h2>Struts Action 2 Cookbook -- Examples with Code</h2>

<p>
    This is a collection of working examples which demonstrate how to code
    some common web application use cases.
</p>

<p>
    To navigate your way through the examples, the following icons will help:
</p>
<table border="0" cellspacing="5" width="85%">
    <tr valign="top">
        <td width="30"><ww:include value="/icon-open.jsp"/></td>
        <td>Execute the example</td>
    </tr>
    <tr valign="top">
        <td><ww:include value="/icon-view.jsp"/></td>
        <td>View the source code for the example</td>
    </tr>
    <tr valign="top">
        <td width="30"><ww:include value="/icon-home.jsp"/></td>
        <td>Return to this screen</td>
    </tr>
</table>
<br/>
<table width="85%" border="0" cellpadding="2" cellspacing="5">

<tr valign="top">
    <td>Hello World</td>
    <td>

        <ww:url id="Hello_Open" action="Open" namespace="/Hello"/>
        <ww:a href="%{Hello_Open}">
            <ww:include value="/icon-open.jsp"/>
        </ww:a>
        <ww:a href="%{Hello_Open}">Execute</ww:a>

    </td>
    <td>

        <ww:url id="Hello_View" action="View" namespace="/Hello"/>
        <ww:a href="%{Hello_View}">
            <ww:include value="/icon-view.jsp"/>
        </ww:a>
        <ww:a href="%{Hello_View}">
            View Source
        </ww:a>

    </td>
</tr>


<tr valign="top">
    <td>Simple Input Form using Action Properties</td>
    <td>

        <ww:url id="Simple_Open" action="Open!input" namespace="/Simple"/>
        <ww:a href="%{Simple_Open}">
            <ww:include value="/icon-open.jsp"/>
        </ww:a>
        <ww:a href="%{Simple_Open}">Execute</ww:a>

    </td>
    <td>

        <ww:url id="Simple_View" action="View" namespace="/Simple"/>
        <ww:a href="%{Simple_View}">
            <ww:include value="/icon-view.jsp"/>
        </ww:a>
        <ww:a href="%{Simple_View}">
            View Source
        </ww:a>

    </td>
</tr>


<tr valign="top">
    <td>Input Form using a POJO</td>
    <td>

        <ww:url id="Pojo_Open" action="Open!input" namespace="/Pojo"/>
        <ww:a href="%{Pojo_Open}">
            <ww:include value="/icon-open.jsp"/>
        </ww:a>
        <ww:a href="%{Pojo_Open}">Execute</ww:a>

    </td>
    <td>

        <ww:url id="Pojo_View" action="View" namespace="/Pojo"/>
        <ww:a href="%{Pojo_View}">
            <ww:include value="/icon-view.jsp"/>
        </ww:a>
        <ww:a href="%{Pojo_View}">
            View Source
        </ww:a>

    </td>
</tr>

<tr valign="top">
    <td>Complex Input Form using Select Controls</td>
    <td>

        <ww:url id="Select_Open" action="Open!input" namespace="/Select"/>
        <ww:a href="%{Select_Open}">
            <ww:include value="/icon-open.jsp"/>
        </ww:a>
        <ww:a href="%{Select_Open}">Execute</ww:a>

    </td>
    <td>

        <ww:url id="Select_View" action="View" namespace="/Select"/>
        <ww:a href="%{Select_View}">
            <ww:include value="/icon-view.jsp"/>
        </ww:a>
        <ww:a href="%{Select_View}">
            View Source
        </ww:a>

    </td>
</tr>

<tr valign="top">
    <td>Action as Page Controllers</td>
    <td>

        <ww:url id="ActionTag_Open" action="Open!input" namespace="/ActionTag"/>
        <ww:a href="%{ActionTag_Open}">
            <ww:include value="/icon-open.jsp"/>
        </ww:a>
        <ww:a href="%{ActionTag_Open}">Execute</ww:a>

    </td>
    <td>

        <ww:url id="ActionTag_View" action="View" namespace="/ActionTag"/>
        <ww:a href="%{ActionTag_View}">
            <ww:include value="/icon-view.jsp"/>
        </ww:a>
        <ww:a href="%{ActionTag_View}">
            View Source
        </ww:a>

    </td>
</tr>

<tr valign="top">
    <td>Detecting Buttons</td>
    <td>

        <ww:url id="Buttons_Open" action="Open!input" namespace="/Buttons"/>
        <ww:a href="%{Buttons_Open}">
            <ww:include value="/icon-open.jsp"/>
        </ww:a>
        <ww:a href="%{Buttons_Open}">Execute</ww:a>

    </td>
    <td>

        <ww:url id="Buttons_View" action="View" namespace="/Buttons"/>
        <ww:a href="%{Buttons_View}">
            <ww:include value="/icon-view.jsp"/>
        </ww:a>
        <ww:a href="%{Buttons_View}">
            View Source
        </ww:a>

    </td>
</tr>


</table>
<p><img src="<ww:url value="/images/valid-xhtml10.png"/>" alt="Valid XHTML 1.0!" height="31" width="88"/></p>
</body>

</html>