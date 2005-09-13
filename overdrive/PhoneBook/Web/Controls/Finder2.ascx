<%@ Control Language="c#" AutoEventWireup="false" Codebehind="Finder2.ascx.cs" Inherits="PhoneBook.Web.Controls.Finder2" TargetSchema="http://schemas.microsoft.com/intellisense/ie5"%>
<table>
	<tr>
		<td colspan="6" >
			<asp:Button ID="find" Runat="server"></asp:Button>
			<INPUT onclick="javascript:window.print();" type="button" value="PRINT" name="cmd_print" id="cmd_print">
		</td>
	</tr>
	<tr>
		<td><asp:Label runat="server" id="last_name_label"></asp:label></td>
		<td><asp:Label runat="server" id="first_name_label"></asp:label></td>
		<td><asp:Label runat="server" id="extension_label"></asp:Label></td>
		<td><asp:Label runat="server" id="user_name_label"></asp:Label></td>
		<td><asp:Label runat="server" id="hired_label"></asp:Label></td>
		<td><asp:Label runat="server" id="hours_label"></asp:Label></td>			  
	</tr>
	<tr>
		<td><asp:DropDownList ID="last_name_list" Runat=server></asp:DropDownList></td>
		<td><asp:DropDownList ID="first_name_list" Runat=server></asp:DropDownList></td>
		<td><asp:DropDownList ID="extension_list" Runat=server></asp:DropDownList></td>
		<td><asp:DropDownList ID="user_name_list" Runat=server></asp:DropDownList></td>
		<td><asp:DropDownList ID="hired_list" Runat=server></asp:DropDownList></td>
		<td><asp:DropDownList ID="hours_list" Runat=server></asp:DropDownList></td>
	</tr>
</table>
