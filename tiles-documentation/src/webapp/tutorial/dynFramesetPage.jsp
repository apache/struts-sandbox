<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<tiles:insert template="/tutorial/basic/myFramesetLayout.jsp" beanName="templateDefinition"  >
  <%--  Uncomment lines to overload parameters --%>
  <%-- <tiles:put name="title"  content="My first frameset page" direct="true" /> --%>
  <%-- <tiles:put name="header" content="/tutorial/common/header.jsp" direct="true"/> --%>
  <%-- <tiles:put name="footer" content="/tutorial/common/footer.jsp" direct="true"/> --%>
  <%-- <tiles:put name="menu"   content="/tutorial/basic/menu.jsp" direct="true"/> --%>
  <%-- <tiles:put name="body"   content="/tutorial/basic/helloBody.jsp" direct="true"/> --%>
</tiles:insert>
