using System;
using System.ComponentModel;
using System.Drawing;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace Nexus.Web
{
	public class NexusDataGrid : DataGrid
	{
		#region Public Properties

		[Bindable (true), Category ("Appearance"), TypeConverter (typeof (WebColorConverter)),
		Description ("Specifies the color a row is highlighted when the mouse is over it.")]
		public Color RowHighlightColor
		{
			get
			{
				object o = ViewState ["RowHighlightColor"];
				if (o == null)
					return Color.Empty;
				else
					return (Color) o;
			}
			set { ViewState ["RowHighlightColor"] = value; }
		}

		[DefaultValue (""),
		Description ("Specifies the CommandName used in the server-side DataGrid event when the row is clicked.")]
		public string RowClickEventCommandName
		{
			get
			{
				object o = ViewState ["RowClickEventCommandName"];
				if (o == null)
					return string.Empty;
				else
					return (string) o;
			}
			set { ViewState ["RowClickEventCommandName"] = value; }
		}

		[DefaultValue (true),
		Description ("Indicates whether or not rows are highlighted/clickable.")]
		public bool RowSelectionEnabled
		{
			get
			{
				object o = ViewState ["RowSelectionEnabled"];
				if (o == null)
					return true;
				else
					return (bool) o;
			}
			set { ViewState ["RowSelectionEnabled"] = value; }
		}

		#endregion

		#region Overridden DataGrid Methods

		protected override DataGridItem CreateItem (int itemIndex, int dataSourceIndex, ListItemType itemType)
		{
			// Create the NexusDataGridItem
			NexusDataGridItem item = new NexusDataGridItem (itemIndex, dataSourceIndex, itemType);

			// Set the client-side onmouseover and onmouseout if RowSelectionEnabled == true
			if (RowSelectionEnabled && itemType != ListItemType.Header && itemType != ListItemType.Footer
				&& itemType != ListItemType.Pager)
			{
				item.Attributes ["onmouseover"] = "javascript:nexusDG_changeBackColor(this, true);";
				item.Attributes ["onmouseout"] = "javascript:nexusDG_changeBackColor(this, false);";
			}

			// return the NexusDataGridItem
			return item;
		}

		protected override void OnPreRender (EventArgs e)
		{
			base.OnPreRender (e);

			if (!RowSelectionEnabled) return; // exit if not RowSelectionEnabled == true

			// add the client-side script to change the background color
			const string SCRIPT_KEY = "nexusDGscript";
			const string SCRIPT = @"<script language=""JavaScript"">
<!--
var lastColorUsed;
function nexusDG_changeBackColor(row, highlight)
{{
	if (row.className!=""EditItemStyle"")
	{{
		if (highlight)
		{{
			lastClassUsed = row.className;
			row.className = ""HighlightedItemStyle"";
		}}
		else
			row.className = lastClassUsed;
	}}
}}
// -->
</script>";

			if (RowHighlightColor != Color.Empty && !Page.IsClientScriptBlockRegistered (SCRIPT_KEY))
				Page.RegisterClientScriptBlock (SCRIPT_KEY, SCRIPT);
		}

		#endregion
	}

	public class NexusDataGridItem : DataGridItem, IPostBackEventHandler
	{
		public NexusDataGridItem (int itemIndex, int dataSetIndex, ListItemType itemType) : base (itemIndex, dataSetIndex, itemType)
		{
		}

		#region IPostBackEventHandler Members

		public void RaisePostBackEvent (string eventArgument)
		{
			CommandEventArgs commandArgs = new CommandEventArgs (eventArgument, null);
			DataGridCommandEventArgs args = new DataGridCommandEventArgs (this, this, commandArgs);
			base.RaiseBubbleEvent (this, args);
		}

		#endregion
	}
}