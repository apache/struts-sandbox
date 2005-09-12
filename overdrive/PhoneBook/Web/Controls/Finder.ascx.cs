using System;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Web.Controls;
using PhoneBook.Core;
using PhoneBook.Web.Forms;

namespace PhoneBook.Web.Controls
{
	public class Finder : ViewControl
	{
		protected DropDownList last_name_list;
		protected DropDownList first_name_list;
		protected DropDownList extension_list;
		protected DropDownList user_name_list;
		protected DropDownList hired_list;
		protected DropDownList hours_list;
		protected Button find;

		/// <summary>
		/// Fires when search criteria is input.
		/// </summary>
		public event EventHandler Click;

		private DropDownList[] FilterList()
		{
			DropDownList[] lists = {last_name_list, first_name_list, extension_list, user_name_list, hired_list, hours_list};
			return lists;
		}

		private void Filter_Reset(DropDownList except)
		{
			int exceptIndex = 0;
			if (except != null) exceptIndex = except.SelectedIndex;
			foreach (DropDownList filter in FilterList())
			{
				filter.SelectedIndex = 0;
			}
			if (except != null) except.SelectedIndex = exceptIndex;
		}

		private void Filter_Changed(object sender, EventArgs e)
		{
			IViewHelper helper = Catalog.GetHelperFor(App.ENTRY_LIST);
			DropDownList list = sender as DropDownList;
			string id = list.ID;
			int v = id.LastIndexOf(helper.ListSuffix);
			string key = id.Substring(0, v);
			helper.Criteria[key] = list.SelectedValue;
			Filter_Reset(list);
			Click(this, new ViewArgs(helper));
		}

		public void Open()
		{
			IViewHelper h = this.ExecuteBind(App.DIRECTORY_VIEW);
			bool ok = (h.IsNominal);
			if (!ok)
				Page_Error = h;
		}

		private void find_Click(object sender, EventArgs e)
		{
			if (Click == null) return;
			Filter_Reset(null);
			IViewHelper helper = Read(App.DIRECTORY_VIEW);
			Click(this, new ViewArgs(helper));
		}

		private void Page_Load(object sender, EventArgs e)
		{
			find.Text = Directory.msg_LIST_ALL_CMD;
			find.Click += new EventHandler(find_Click);

			foreach (DropDownList filter in FilterList())
			{
				filter.AutoPostBack = true;
				filter.SelectedIndexChanged += new EventHandler(Filter_Changed);
			}

			if (!IsPostBack) Open();
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