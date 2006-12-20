<%@ Register TagPrefix="app" TagName="Finder" Src="../Controls/Finder2.ascx" %>
<%@ Register TagPrefix="app" TagName="Lister" Src="../Controls/Lister2.ascx" %>
<%@ Register TagPrefix="app" TagName="Filter" Src="../Controls/InitialFilter.ascx" %>
<%@ Page language="c#" Codebehind="Directory2.aspx.cs" AutoEventWireup="false" Inherits="PhoneBook.Web.Forms.Directory2" %>
<%@ Register TagPrefix="ovr" Namespace="Nexus.Web" Assembly="Nexus.Web" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<HTML>
	<HEAD>
		<title runat="server" id="title"></title>
		<meta name="GENERATOR" Content="Microsoft Visual Studio .NET 7.1">
		<meta name="CODE_LANGUAGE" Content="C#">
		<meta name="vs_defaultClientScript" content="JavaScript">
		<meta name="vs_targetSchema" content="http://schemas.microsoft.com/intellisense/ie5">
		<LINK href="default.css" type="text/css" rel="stylesheet">
	</HEAD>
	<body>
		<h1 id="heading" runat="server"></h1>
		<form id="form1" method="post" runat="server">
			<p><asp:Label ID="greeting" Runat="server"></asp:Label>
				<asp:Label ID="profile_label" Runat="server"></asp:Label></p>
			<!-- ERROR -->
			<asp:Panel ID="error_panel" Runat="server">
				<P>
					<asp:Label id="error_label" Runat="server"></asp:Label></P>
				<HR>
			</asp:Panel>
			<!-- PROMPT -->
			<asp:Panel ID="prompt_panel" Runat="server">
				<P>
					<asp:Label id="prompt_label" Runat="server"></asp:Label></P>
				<HR>
			</asp:Panel>
			<!-- FINDER -->
			<app:Finder id="finder" Runat="server"></app:Finder>
			<!-- LETTER FILTER -->
			<app:Filter id="letter_filter" Runat="server"></app:Filter>
			<!-- LISTER -->
			<app:Lister id="lister" runat="server"></app:Lister>
		</form>
	</body>
</HTML>
