<?xml version="1.0"?>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<portlet:defineObjects/>

  <h1>Select Asset</h1>
  
            <table cellspacing="2" cellpadding="3">
              <tr>
                  <td class="property">
                      Asset search
                  </td>
                  <td>
                 <portlet:renderURL var="submitUrl"> 
                  <portlet:param name="contid" value="<%= String.valueOf(request.getAttribute("contid")) %>"/> 
                </portlet:renderURL>

                    <form method="GET" action="<%=submitUrl%>">
                        <input name="name" type="text" size="15" />&#160;
                        <input type="submit" value="Search" />
                    </form>    
                  </td>
              </tr>
              <tr>
                <td class="property">
                  Choose an asset
                </td>
                <td>
                  <form method="GET" action="<%=submitUrl%>" >
                      <select name="SystemKey">

                        <%
                        java.util.Map map = (java.util.Map)request.getAttribute("assets");
                        boolean any = false;
                        if (map != null) {
                        for (java.util.Iterator i = map.keySet().iterator(); i.hasNext(); any=true) {
                            String key = (String)i.next();
                        %> 
                            <option value="<%=map.get(key)%>"><%=key%></option>
                        <%}
                        } 
                        if (!any) {%>
                            <option value="">---- Assets ------</option>
                        <%}%>
                      </select>&#160;
                      <input type="submit" value="View" />
                  </form>
                </td>
              </tr>
            </table>

