<%@ Page language="c#" Codebehind="Directory2.aspx.cs" AutoEventWireup="true" Inherits="PhoneBook.Web.Forms.Directory2" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<HTML>
	<HEAD>
		<title id="title" runat="server"></title>		
		<meta name="GENERATOR" Content="Microsoft Visual Studio .NET 7.1">
		<meta name="CODE_LANGUAGE" Content="C#">
		<meta name="vs_defaultClientScript" content="JavaScript">
		<meta name="vs_targetSchema" content="http://schemas.microsoft.com/intellisense/ie5">
		<LINK href="default.css" type="text/css" rel="stylesheet">
	</HEAD>
	<body>
		<h1 id="heading" runat="server"></h1>
			<form id="form" method="post" runat="server">
			<p><asp:Label ID="greeting" Runat="server"></asp:Label> <asp:Label ID="profile_label" Runat="server"></asp:Label></p>
			
			<!-- ERROR -->
			<asp:Panel ID="error_panel" Runat="server">
			<p><asp:Label ID="error_label" Runat="server"></asp:Label></p>
			<hr>
			</asp:Panel>
			
			<!-- PROMPT -->
			<asp:Panel ID="prompt_panel" Runat="server">
			<p><asp:Label ID="prompt_label" Runat="server"></asp:Label></p>
			<hr>
			</asp:Panel>
						
			<!-- FIND -->
			<asp:Panel ID="find_panel" Runat="server">
			  <table><tr>
			  <td colspan="6" >
				  <asp:Button ID="list_all_command" Runat="server"></asp:Button>
				  <INPUT onclick="javascript:window.print();" type="button" value="PRINT" name="cmd_print" id="cmd_print">
			  </td>
			  <tr>
			  <td><asp:Label runat="server" id="last_name_label"></asp:label></td>
			  <td><asp:Label runat="server" id="first_name_label"></asp:label></td>
			  <td><asp:Label runat="server" id="extension_label"></asp:Label></td>
			  <td><asp:Label runat="server" id="user_name_label"></asp:Label></td>
			  <td><asp:Label runat="server" id="hired_label"></asp:Label></td>
			  <td><asp:Label runat="server" id="hours_label"></asp:Label></td>			  
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
			<asp:Panel ID="list_panel" Runat="server">
            	<asp:DataGrid id="list_report" Runat="server" 
					PagerStyle-Mode="NumericPages" AllowPaging="true" PageSize="2" >
					<HeaderStyle CssClass="HeaderStyle" BackColor="#CCCC99"></HeaderStyle>
					<AlternatingItemStyle CssClass="AlternatingItemStyle" BackColor="#CCCC99"></AlternatingItemStyle>
					<EditItemStyle CssClass="EditItemStyle"></EditItemStyle>
            	</asp:DataGrid>
        		<p><asp:Button ID="list_add_command" Runat="server"></asp:Button></p>
			</asp:Panel>

		</form>
	</body>
</HTML>
