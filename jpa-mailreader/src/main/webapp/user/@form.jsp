<%@ taglib prefix="s" uri="/struts-tags" %>
<s:actionerror/>
<s:form action="index" validate="true">
    <s:hidden name="input"/>
    <s:if test="input == 'create'">
        <s:textfield key="user.username"/>
    </s:if>
    <s:else>
        <s:label key="user.username"/>
	    <s:hidden name="user"/>
    </s:else>

    <s:password key="user.password1"/>

    <s:password key="user.password2"/>

    <s:textfield key="user.fullName"/>

    <s:textfield key="user.fromAddress"/>

    <s:textfield key="user.replyToAddress"/>

    <s:if test="input == 'create'">
        <s:submit key="button.update" action="create" />     
        <s:submit key="button.cancel" action="create" method="cancel" onclick="form.onsubmit=null"/>
        <s:submit action="create" >Label</s:submit>     
    </s:if>
    <s:else>
        <s:submit key="button.update" action="update"/>
        <s:submit key="button.cancel" action="update" method="cancel" onclick="form.onsubmit=null"/>                
    </s:else>
    <s:reset key="button.reset"/>
	<script src="/assets/focus-first-input.js" type="text/javascript"></script>
</s:form>
