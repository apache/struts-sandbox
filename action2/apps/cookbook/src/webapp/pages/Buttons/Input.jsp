<%@ taglib prefix="ww" uri="/webwork" %>
<html>
<head>
    <title>Cookbook - Detecting Buttons</title>
    <ww:head/>
</head>

<body>

<ww:form method="POST">

    <ww:textfield
            label="Message"
            name="message"
            tooltip="Enter your text message here"/>

    <input type="submit" name="ford" value="Send Message to Ford">
    <input type="submit" name="marvin" value="Send Message to Marvin">
    <input type="submit" name="trillian" value="Send Message to Trillian">

</ww:form>

</body>
</html>
