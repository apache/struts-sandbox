<!DOCTYPE html PUBLIC 
	"-//W3C//DTD XHTML 1.1 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	
<%@taglib prefix="s" uri="/struts-tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>Orders</title>
</head>
<body>
    <table>
        <tr>
            <th>ID</th>
            <th>Client</th>
            <th>Amount</th>
            <th>Actions</th>
        </tr>
        <s:iterator value="model">
        <tr>
            <td><s:property value="id" /></td>
            <td><s:property value="clientName" /></td>
            <td><s:property value="amount" /></td>
            <td><a href="<s:property value="id" />.xhtml">View</a> |
                <a href="<s:property value="id" />;edit.xhtml">Edit</a> |
                <a href="<s:property value="id" />.xhtml?_method=DELETE">Delete</a></td>
        </tr>
        </s:iterator>
    </table>    	
    <a href="new.xhtml">Create a new order</a>
</body>
</html>
	