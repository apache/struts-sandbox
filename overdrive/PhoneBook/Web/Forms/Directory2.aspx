<%@ Page language="c#" Codebehind="Directory2.aspx.cs" AutoEventWireup="true" Inherits="PhoneBook.Web.Forms.Directory2" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<HTML>
	<HEAD>
		<title>PhoneBook Directory (2)</title>		
		<meta name="GENERATOR" Content="Microsoft Visual Studio .NET 7.1">
		<meta name="CODE_LANGUAGE" Content="C#">
		<meta name="vs_defaultClientScript" content="JavaScript">
		<meta name="vs_targetSchema" content="http://schemas.microsoft.com/intellisense/ie5">
		<LINK href="default.css" type="text/css" rel="stylesheet">
	</HEAD>
	<body>
		<h1>PhoneBook Directory</h1>
			<form id="frmDirectory" method="post" runat="server">
			
			<!-- ERROR -->
			<asp:Panel ID="pnlError" Runat="server">
			<p><asp:Label ID="lblError" Runat="server"></asp:Label></p>
			<hr>
			</asp:Panel>
			
			<!-- PROMPT -->
			<p>Select a filter to display fewer entries.</p>	

			<!-- FIND -->
			<asp:Panel ID="pnlFind" Runat="server">
			  <table><tr>
			  <td colspan="6" >
				  <asp:Button ID="cmdListAll" Runat="server"></asp:Button>
				  <INPUT onclick="javascript:window.print();" type="button" value="PRINT" name="cmd_print" id="cmd_print">
			  </td>
			  <tr>
			  <td>Last Name</td>
			  <td>First Name</td>
			  <td>Extension</td>
			  <td>User ID</td>
			  <td>Hire Date</td>
			  <td>Hours</td>			  
			  </tr><tr>
			  <td><asp:DropDownList ID="last_name_list" Runat=server></asp:DropDownList></td>
			  <td><asp:DropDownList ID="first_name_list" Runat=server></asp:DropDownList></td>
			  <td><asp:DropDownList ID="extension_list" Runat=server></asp:DropDownList></td>
			  <td><asp:DropDownList ID="user_name_list" Runat=server></asp:DropDownList></td>
			  <td><asp:DropDownList ID="hired_list" Runat=server></asp:DropDownList></td>
			  <td><asp:DropDownList ID="hours_list" Runat=server></asp:DropDownList></td>
			  </tr></table>
			</asp:panel>
			
			<!-- LIST -->			
			<asp:Panel ID="pnlList" Runat="server">
 � � � � � �	<asp:DataGrid id="repList" Runat="server" 
					PagerStyle-Mode="NumericPages" AllowPaging="true" PageSize="10" >
					<HeaderStyle CssClass="HeaderStyle" BackColor="#CCCC99"></HeaderStyle>
					<AlternatingItemStyle CssClass="AlternatingItemStyle" BackColor="#CCCC99"></AlternatingItemStyle>
					<EditItemStyle CssClass="EditItemStyle"></EditItemStyle>
�� � � � � �	</asp:DataGrid>
 � � � �		<p><asp:Button ID="cmdListAdd" Runat="server"></asp:Button></p>
			</asp:Panel>

		</form>
	</body>
</HTML>