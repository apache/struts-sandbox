<%@ Page language="c#" Codebehind="Directory.aspx.cs" AutoEventWireup="false" Inherits="PhoneBook.Web.Forms.Directory" %>
<%@ Register TagPrefix="app" TagName="Finder" Src="../Controls/Finder.ascx" %>
<%@ Register TagPrefix="app" TagName="Lister" Src="../Controls/Lister.ascx" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" >
<HTML>
	<HEAD>
		<title>PhoneBook Directory</title>		
		<meta name="GENERATOR" Content="Microsoft Visual Studio .NET 7.1">
		<meta name="CODE_LANGUAGE" Content="C#">
		<meta name="vs_defaultClientScript" content="JavaScript">
		<meta name="vs_targetSchema" content="http://schemas.microsoft.com/intellisense/ie5">
		<LINK href="default.css" type="text/css" rel="stylesheet">
	</HEAD>
	<body>
		<h1>PhoneBook Directory</h1>
			<form id="form1" method="post" runat="server">
			
			<!-- ERROR -->
			<asp:Panel id="error_panel" runat="server">
			<p><asp:Label id="error_label" runat="server"></asp:Label></p>
			<hr>
			</asp:Panel>
			
			<!-- PROMPT -->
			<p>Select a filter to display fewer entries.</p>	

			<app:Finder id="finder" runat="server"></app:Finder>
			
 			<app:Lister id="lister" runat="server"></app:Lister>

		</form>
	</body>
</HTML>
