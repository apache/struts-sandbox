<%@ taglib prefix="s" uri="/struts-tags" %>
<s:actionerror/>

<s:form validate="true">
    <s:hidden name="input"/>
    <s:hidden name="user"/>
    <s:label key="user.username"/>
    <s:if test="input == 'create'">
        <s:textfield key="subscription.host"/>
    </s:if>
    <s:else>
        <s:label key="subscription.host"/>
	    <s:hidden name="subscription"/>
    </s:else>

    <s:textfield key="subscription.username"/>

    <s:textfield key="subscription.password"/>

    <s:select key="subscription.protocol" list="protocols"/>

    <s:checkbox key="subscription.autoConnect"/>

   <!--  Struts tags disallow expressions as values, so we restort to JSTL for a hidden tag -->
    <s:if test="input == 'create'">
       <input type="hidden" name="subscription.user" value="${user.username}" id="create_subscription_user"/>
       <s:submit key="button.update" action="create" />
	   <s:submit key="button.cancel" action="create" method="cancel" onclick="form.onsubmit=null"/>
    </s:if>
    <s:else>
       <s:hidden name="subscription.user"/>
       <s:submit key="button.update" action="update" />
	   <s:submit key="button.cancel" action="update" method="cancel" onclick="form.onsubmit=null"/>
    </s:else>

   <s:reset key="button.reset"/>

</s:form>
