<%@ Control Language="c#" AutoEventWireup="false" Codebehind="LetterFilter.ascx.cs" Inherits="Nexus.Web.Controls.LetterFilter" TargetSchema="http://schemas.microsoft.com/intellisense/ie5"%>
<asp:repeater id="letters" runat="server">
	<itemtemplate>
		<asp:linkbutton id="letter" runat="server" 
			commandname="filter" 
			commandargument='<%# DataBinder.Eval(Container, "DataItem.Letter")%>' >
			<%# DataBinder.Eval(Container, "DataItem.Letter")%>
		</asp:linkbutton>
	</itemtemplate>
</asp:repeater>