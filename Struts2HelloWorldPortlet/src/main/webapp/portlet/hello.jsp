<%@ taglib prefix="s" uri="/struts-tags" %>

<h3><s:property value="message" /> </h3>

<h4>Create Your Own Message</h4>

<s:form action="index" method="post">

<s:textfield name="message" label="Enter your message" />

<s:submit />

</s:form>


