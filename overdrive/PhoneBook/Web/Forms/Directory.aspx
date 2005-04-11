<%@ Page language="c#" Codebehind="Directory.aspx.cs" AutoEventWireup="false" Inherits="PhoneBook.Web.Directory" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<HTML>
	<HEAD>
		<title>PhoneBook</title>
		<meta name="GENERATOR" Content="Microsoft Visual Studio .NET 7.1">
		<meta name="CODE_LANGUAGE" Content="C#">
		<meta name="vs_defaultClientScript" content="JavaScript">
		<meta name="vs_targetSchema" content="http://schemas.microsoft.com/intellisense/ie5">
		<LINK href="default.css" type="text/css" rel="stylesheet">
	</HEAD>
	<body>
		<h1>PhoneBook</h1>
			<form id="frm" method="post" runat="server">
			<asp:Panel ID="pnlFind">
			  <table><tr>
			  <td>Sort by: </td><td><asp:DropDownList ID="lstSelect"></asp:DropDownList>
			  <td>Search for:<asp:TextBox ID="txtFind" Runat="server"></asp:TextBox> <asp:Button ID="cmdFind" Runat="server"></asp:Button></td>
			  <td><asp:Button ID="cmdAdd" Runat="server"></asp:Button></td>
			  </tr></table>
			</asp:panel>
			<asp:Panel ID="pnlList" Runat="server">
            	<asp:DataGrid id="repList" Runat="server" 
					PagerStyle-Mode="NumericPages" AllowPaging="true" PageSize="10" OnPageIndexChanged="List_PageIndexChanged">
					<HeaderStyle CssClass="HeaderStyle" BackColor="#CCCC99"></HeaderStyle>
					<AlternatingItemStyle CssClass="AlternatingItemStyle" BackColor="#CCCC99"></AlternatingItemStyle>
					<EditItemStyle CssClass="EditItemStyle"></EditItemStyle>
            	</asp:DataGrid>
			</asp:Panel>
			</asp:Panel>
		</form>
	</body>
</HTML>
