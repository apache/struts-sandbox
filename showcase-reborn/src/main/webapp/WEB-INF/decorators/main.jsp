<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@taglib prefix="s" uri="/struts-tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><decorator:title default="${artifactId}"/></title>

    <s:url var="mainCss" value="/css/main.css"/>
    <link rel="stylesheet" href="${mainCss}"/>

    <s:url var="highlightCss" value="/css/highlight.css"/>
    <link rel="stylesheet" href="${highlightCss}"/>

    <s:url var="jqueryCss" value="/css/redmond/jquery-ui-1.7.2.custom.css"/>
    <link rel="stylesheet" href="${jqueryCss}"/>

    <s:url var="jquery" value="/javascript/jquery-1.3.2.min.js"/>
    <script type="text/javascript" src="${jquery}"></script>

    <s:url var="jqueryUI" value="/javascript/jquery-ui-1.7.2.custom.min.js"/>
    <script type="text/javascript" src="${jqueryUI}"></script>
    <decorator:head/>

    <script type="text/javascript">
        $(function() {
            $("#left-nav").accordion();
            $(".doc-tabs").tabs();
            $(".errorMessage").effect("pulsate", { times:3 }, 2000);
        });
    </script>
</head>

<body>
<div class="logo">
    <s:url var="logo" value="/images/struts-power.gif"/>
    <img src="${logo}" alt="Powered by Struts"/>
</div>

<div id="wrapper">
    <div class="left-nav">
        <jsp:include page="left-nav.jsp"/>
    </div>

    <div id="content">
        <decorator:body/>
    </div>
</div>
</body>
</html>

