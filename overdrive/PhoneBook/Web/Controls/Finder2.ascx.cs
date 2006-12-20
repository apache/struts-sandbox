using System;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Web;
using PhoneBook.Core;

namespace PhoneBook.Web.Controls
{
	/// <summary>
	/// Capture input values to filter a list of directory entries.
	/// </summary>
	/// 
	public class Finder2 : ViewControl
	{
		/// <summary>
		/// Signal update to input filters 
		/// by passing FindArgs with the search critiers.
		/// </summary>
		/// 
		public event EventHandler Filter_Changed;

		/// <summary>
		/// Populate the entry finder's own controls. 
		/// </summary>
		/// 
		public void Open()
		{
			IViewHelper h = GetHelperFor(App.ENTRY_FIND);
			ExecuteBind(h);
			bool ok = (h.IsNominal);
			if (!ok)
				Page_Alert = h;
		}

		/// <summary>
		/// Provide runtime instance of find Button.
		/// </summary>
		/// 
		protected Button find;

		/// <summary>
		/// Handle the Click event of the Find button 
		/// by resetting the filters 
		/// and raising the Filter Changed event
		/// so that the presentation will list all entries.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void find_Click(object sender, EventArgs e)
		{
			if (Filter_Changed == null) return;
			Filter_Reset(null);
			IViewHelper helper = Read(App.ENTRY_LIST);
			Filter_Changed(this, new ViewArgs(helper));
		}

		/// <summary>
		/// Unselect all but the active filter.
		/// </summary>
		/// <param name="except">The active filter</param>
		/// 
		private void Filter_Reset(DropDownList except)
		{
			// Reset filter controls
			int exceptIndex = 0;
			if (except != null) exceptIndex = except.SelectedIndex;
			foreach (Control c in Controls)
			{
				if (IsListControl(c))
				{
					DropDownList x = (DropDownList) c;
					x.SelectedIndex = 0;
				}
			}
			if (except != null) except.SelectedIndex = exceptIndex;
		}

		/// <summary>
		/// Handle the SelectIndexChanged event for any of the filters 
		/// by capturing its settings 
		/// and raising the Filter_Changed event.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void filter_SelectedIndexChanged(object sender, EventArgs e)
		{
			IViewHelper helper = Catalog.GetHelperFor(App.ENTRY_LIST);
			DropDownList list = sender as DropDownList;
			string id = list.ID;
			int v = id.LastIndexOf(ListSuffix);
			string key = id.Substring(0, v);
			helper.Criteria[key] = list.SelectedValue;
			Filter_Reset(list);
			Filter_Changed(this, new ViewArgs(helper));
		}

		private void Page_Load(object sender, EventArgs e)
		{
			find.Click += new EventHandler(find_Click);
			foreach (Control control in Controls)
			{
				if (IsListControl(control))
				{
					DropDownList filter = (DropDownList) control;
					filter.SelectedIndexChanged += new EventHandler(filter_SelectedIndexChanged);
					filter.AutoPostBack = true;
				}
			}
		}

		#region Web Form Designer generated code

		protected override void OnInit(EventArgs e)
		{
			//
			// CODEGEN: This call is required by the ASP.NET Web Form Designer.
			//
			InitializeComponent();
			base.OnInit(e);
		}

		/// <summary>
		///		Required method for Designer support - do not modify
		///		the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.Load += new EventHandler(this.Page_Load);
		}

		#endregion
	}
}