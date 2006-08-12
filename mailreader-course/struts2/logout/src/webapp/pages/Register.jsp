<%@ taglib uri="/webwork" prefix="ww" %>
<html>
<head>
    <title>MailReader - Register</title>
</head>

<body onLoad="self.focus();document.Register.username.focus()">

<ww:actionerror/>
<ww:form method="POST" validate="true" theme="simple">

    <ww:textfield label="Username" name="username"/>

    <ww:password label="Password" name="password"/>

    <ww:password label="(Repeat) Password" name="password2"/>

    <ww:textfield label="Full Name" name="fullName"/>

    <ww:textfield label="From Address" name="fromAddress"/>

    <ww:textfield label="Reply To Address" name="replyToAddress"/>

    <ww:submit value="Save" name="Save"/>

    <ww:submit action="Register!cancel" value="Cancel" name="Cancel"
               onclick="form.onsubmit=null"/>

</ww:form>


</body>
</html>
