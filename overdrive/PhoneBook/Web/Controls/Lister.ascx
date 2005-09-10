<%@ Control Language="c#" AutoEventWireup="false" Codebehind="Lister.ascx.cs" Inherits="PhoneBook.Web.Controls.Lister" TargetSchema="http://schemas.microsoft.com/intellisense/ie5"%>
<asp:DataGrid id="list" Runat="server" AutoGenerateColumns=False>
	<HeaderStyle CssClass="HeaderStyle" BackColor="#CCCC99"></HeaderStyle>
	<AlternatingItemStyle CssClass="AlternatingItemStyle" BackColor="#CCCC99"></AlternatingItemStyle>
	<Columns>
		<asp:BoundColumn DataField="last_name" HeaderText="Last Name"></asp:BoundColumn>
		<asp:BoundColumn DataField="first_name" HeaderText="First Name"></asp:BoundColumn>
		<asp:BoundColumn DataField="extension" HeaderText="Extension"></asp:BoundColumn>
		<asp:BoundColumn DataField="user_name" HeaderText="User"></asp:BoundColumn>
		<asp:BoundColumn DataField="hired" HeaderText="Hire Date"></asp:BoundColumn>
		<asp:BoundColumn DataField="hours" HeaderText="Hours"></asp:BoundColumn>
		</Columns>
</asp:DataGrid>
