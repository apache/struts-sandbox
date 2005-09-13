using System;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Web.Controls;
using PhoneBook.Core;

namespace PhoneBook.Web.Controls
{

	public class Finder2 : ViewControl
	{

		public Label last_name_label;
		public Label first_name_label;
		public Label extension_label;
		public Label user_name_label;
		public Label hired_label;
		public Label hours_label;

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

		private Label[] FilterLabels()
		{
			Label[] labels = {last_name_label, first_name_label, extension_label, user_name_label, hired_label, hours_label};
			return labels;
		}

		private DropDownList[] FilterList()
		{
			DropDownList[] lists = {last_name_list, first_name_list, extension_list, user_name_list, hired_list, hours_list};
			return lists;
		}

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
			foreach (DropDownList filter in FilterList())
			{
				filter.SelectedIndex = 0;
			}
			if (except != null) except.SelectedIndex = exceptIndex;
			// Tell everyone that we are starting over
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
			IViewHelper h = GetHelperFor(App.ENTRY_FIND);
			ExecuteBind(h);
			bool ok = (h.IsNominal);
			if (!ok)
				Page_Error = h;
		}

		private void Page_Load(object sender, System.EventArgs e)
		{
			find.Click += new EventHandler(find_Click);
			foreach (DropDownList filter in FilterList())
			{
				filter.SelectedIndexChanged += new EventHandler(Filter_Changed);
			}
			if (!IsPostBack)
			{
				find.Text = GetMessage(find.ID);
				foreach (Label label in FilterLabels())
				{
					label.Text = GetMessage(label.ID);
				}				
				foreach (DropDownList filter in FilterList())
				{
					filter.AutoPostBack = true;
				}
			}
		}

		#region Web Form Designer generated code
		override protected void OnInit(EventArgs e)
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
			this.Load += new System.EventHandler(this.Page_Load);
		}
		#endregion
	}
}
