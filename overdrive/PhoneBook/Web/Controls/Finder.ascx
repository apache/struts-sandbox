<%@ Control Language="c#" AutoEventWireup="false" Codebehind="Finder.ascx.cs" Inherits="PhoneBook.Web.Controls.Finder" TargetSchema="http://schemas.microsoft.com/intellisense/ie5"%>
<table>
	<tr>
		<td colspan="6" >
			<asp:Button ID="find" Runat="server"></asp:Button>
			<INPUT onclick="javascript:window.print();" type="button" value="PRINT" name="print" id="print">
		</td>
	<tr>
		<td>Last Name</td>
		<td>First Name</td>
		<td>Extension</td>
		<td>User</td>
		<td>Hire Date</td>
		<td>Hours</td>			  
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
