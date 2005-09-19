using System;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Web.Controls;
using PhoneBook.Core;

namespace PhoneBook.Web.Controls
{
	public class Finder2 : ViewControl
	{
		protected Button find;

		/// <summary>
		/// Fires when search criteria is input.
		/// </summary>
		public event EventHandler Click;

		private void find_Click(object sender, EventArgs e)
		{
			if (Click == null) return;
			Filter_Reset(null);
			IViewHelper helper = Read(App.ENTRY_FIND);
			Click(this, new ViewArgs(helper));
		}

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

		private void Filter_Changed(object sender, EventArgs e)
		{
			IViewHelper helper = Catalog.GetHelperFor(App.ENTRY_LIST);
			DropDownList list = sender as DropDownList;
			string id = list.ID;
			int v = id.LastIndexOf(ListSuffix);
			string key = id.Substring(0, v);
			helper.Criteria[key] = list.SelectedValue;
			Filter_Reset(list);
			Click(this, new ViewArgs(helper));
		}

		public void Open()
		{
			IViewHelper h = GetHelperFor(App.ENTRY_FIND);
			ExecuteBind(h);
			bool ok = (h.IsNominal);
			if (!ok)
				Page_Error = h;
		}

		private void Page_Load(object sender, EventArgs e)
		{
			find.Click += new EventHandler(find_Click);
			foreach (Control c in Controls)
			{
				if (IsListControl(c))
				{
					DropDownList x = (DropDownList) c;
					x.SelectedIndexChanged += new EventHandler(Filter_Changed);
					;
					x.AutoPostBack = true;
				}
			}

			if (IsPostBack) return;
			GetMessages();
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