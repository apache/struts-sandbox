<%@ taglib uri="/webwork" prefix="ww" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head><ww:if test="task=='Create'">
    <title><ww:text name="registration.title.create"/></title>
</ww:if>
    <ww:if test="task=='Edit'">
        <title><ww:text name="registration.title.edit"/></title>
    </ww:if>
</head>

<body>

<ww:form method="POST" validate="true">
    <ww:hidden name="task"/>
    <ww:if test="task == 'Create'">
        <ww:textfield label="%{getText('prompt.username')}" name="username"/>
    </ww:if>
    <ww:else>
        <ww:label label="%{getText('prompt.username')}" name="username"/>
        <ww:hidden name="username"/>
    </ww:else>

    <ww:textfield label="%{getText('prompt.password')}" name="password"/>

    <ww:textfield label="%{getText('prompt.password2')}" name="password2"/>

    <ww:textfield label="%{getText('prompt.fullName')}" name="fullName"/>

    <ww:textfield label="%{getText('prompt.fromAddress')}" name="fromAddress"/>

    <ww:textfield label="%{getText('prompt.replyToAddress')}" name="replyToAddress"/>

    <ww:submit/>

    <ww:reset/>

    <ww:submit action="Welcome" value="%{getText('button.cancel')}" onclick="form.onsubmit=null"/>

</ww:form>

<jsp:include page="Footer.jsp"/>

</body>
</html>
