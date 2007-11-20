<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
  <head>
      <title>
          <s:text name="user.title.update"/>
      </title>
  </head>
  <body>

    <s:include value="@form.jsp" />

    <div align="center">
        <h3>
            <s:text name="heading.subscriptions"/>
        </h3>
    </div>

    <table border="1" width="100%">

        <tr>
           <th align="center">
              	<s:text name="subscription.host"/>
           </th>
   	       <th align="center">
      	        <s:text name="subscription.username"/>
           </th>
  	        <th align="center">
      	        <s:text name="subscription.password"/>
           </th>
  	        <th align="center">
              	<s:text name="subscription.protocol"/>
           </th>
          	<th align="center">
               <s:text name="subscription.autoConnect"/>
          	</th>
          	<th align="center">
               <s:text name="heading.action"/>
  	        </th>
        </tr>

        <s:iterator value="user.subscriptions">
            <tr>
                <td align="left">
                    <s:property value="host"/>
                </td>
                <td align="left">
                    <s:property value="username"/>
                </td>
                <td align="left">
                    <s:property value="password"/>
                </td>
                <td align="center">
                    <s:property value="protocol.description"/>
                </td>
                <td align="center">
                    <s:property value="autoConnect"/>
                </td>
                <td align="center">

                    <a href="<s:url namespace="subscription" action="delete" method="input">
                    		<s:param name="subscription" value="host"/></s:url>">
                       <s:text name="heading.delete"/>
                    </a>
                    &nbsp;
                    <a href="<s:url namespace="subscription" action="update" method="input">
                    		<s:param name="subscription" value="host"/></s:url>">
                       <s:text name="heading.update"/>
                    </a>

                </td>
            </tr>
        </s:iterator>

    </table>

    <a href="<s:url namespace="subscription" action="create" method="input"/>">
      	<s:text name="heading.create"/>
    </a>

  </body>
    <script src="<s:url value="/assets/focus-first-input.js"/>" type="text/javascript"></script>
</html>
