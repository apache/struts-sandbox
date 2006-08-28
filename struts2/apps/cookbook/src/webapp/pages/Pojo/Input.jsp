<%@ taglib prefix="ww" uri="/webwork" %>
<html>
<head>
    <title>Cookbook - Input Form using a POJO</title>
    <ww:head/>
</head>

<body>

<ww:form method="POST">
    <ww:textfield
            label="First Name"
            name="firstname"
            tooltip="Enter your first name here"/>

    <ww:textfield
            label="Last Name"
            name="lastname"
            tooltip="Enter your last name here"/>

    <ww:textfield
            label="Telephone"
            name="extension"
            tooltip="Enter your office telephone number here"/>

    <ww:textfield
            label="Email Address"
            name="username"
            tooltip="Enter your email address here"/>

    <ww:datepicker
            tooltip="Select your hire date"
            label="Hire Date"
            name="hired"/>

    <ww:textfield
            label="Hours"
            name="hours"
            tooltip="Enter the number of hours you are scheduled to work"/>

    <ww:checkbox
            label="Editor?"
            name="editor"
            tooltip="Are you authorized to edit directory entries?"/>

    <ww:submit/>
</ww:form>

</body>
</html>
