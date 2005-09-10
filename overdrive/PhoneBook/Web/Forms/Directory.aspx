<%@ Page language="c#" Codebehind="Directory.aspx.cs" AutoEventWireup="true" Inherits="PhoneBook.Web.Forms.Directory" %>
<%@ Register TagPrefix="my" TagName="Finder" Src="../Controls/Finder.ascx" %>
<%@ Register TagPrefix="my" TagName="Lister" Src="../Controls/Lister.ascx" %>

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
			<form id="frmDirectory" method="post" runat="server">
			
			<!-- ERROR -->
			<asp:Panel ID="pnlError" Runat="server">
			<p><asp:Label ID="lblError" Runat="server"></asp:Label></p>
			<hr>
			</asp:Panel>
			
			<!-- PROMPT -->
			<p>Select a filter to display fewer entries.</p>	

			<my:Finder id="finder" Runat="server" OnClick="finder_Click"></my:Finder>
			
 			<my:Lister id="lister" runat="server" OnClick="lister_Click"></my:Lister>

		</form>
	</body>
</HTML>
