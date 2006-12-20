using System;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Web;
using PhoneBook.Core;
using PhoneBook.Web.Forms;
using WQD.Core.Controls;

namespace PhoneBook.Web.Controls
{
	/// <summary>
	/// Capture input values to filter a list of directory entries.
	/// </summary>
	/// 
	public class Finder : ViewControl
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
			IViewHelper h = ExecuteBind(App.ENTRY_FIND);
			bool ok = (h.IsNominal);
			if (!ok)
				Page_Alert = h;
		}

		/// <summary>
		/// Provide runtime instance of last_name_list filter.
		/// </summary>
		/// 
		protected DropDownList last_name_list;

		/// <summary>
		/// Provide runtime instance of first_name_list filter.
		/// </summary>
		/// 
		protected DropDownList first_name_list;

		/// <summary>
		/// Provide runtime instance of extension_list filter.
		/// </summary>
		/// 
		protected DropDownList extension_list;

		/// <summary>
		/// Provide runtime instance of user_name_list filter.
		/// </summary>
		/// 
		protected DropDownList user_name_list;

		/// <summary>
		/// Provide runtime instance of hired_list filter.
		/// </summary>
		/// 
		protected DropDownList hired_list;

		/// <summary>
		/// Provide runtime instance of hours_list filter.
		/// </summary>
		/// 
		protected DropDownList hours_list;

		/// <summary>
		/// Provide runtime instance of find filter.
		/// </summary>
		/// 
		protected Button find;

		/// <summary>
		/// Provide an array for filters so they can be handled as a group (or composite). 
		/// </summary>
		/// <returns>Array of filter instances</returns>
		/// 
		private DropDownList[] FilterList()
		{
			DropDownList[] lists = {last_name_list, first_name_list, extension_list, user_name_list, hired_list, hours_list};
			return lists;
		}

		/// <summary>
		/// Unselect all but the active filter.
		/// </summary>
		/// <param name="except">The active filter</param>
		/// 
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
			Filter_Changed(this, new FindArgs(e, helper.Criteria));
		}

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
			IViewHelper helper = Read(App.ENTRY_FIND);
			Filter_Changed(this, new ViewArgs(helper));
		}

		/// <summary>
		/// Handle page's load event.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void Page_Load(object sender, EventArgs e)
		{
			find.Text = Directory.msg_LIST_ALL_CMD;
			find.Click += new EventHandler(find_Click);

			foreach (DropDownList filter in FilterList())
			{
				filter.AutoPostBack = true;
				filter.SelectedIndexChanged += new EventHandler(filter_SelectedIndexChanged);
			}

			if (!IsPostBack) Open();
		}

		#region Web Form Designer generated code

		/// <summary>
		///		Initialize components.
		/// </summary>
		/// <param name="e">Runtime parameters</param>
		/// 
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
		/// 
		private void InitializeComponent()
		{
			this.Load += new EventHandler(this.Page_Load);
		}

		#endregion
	}
}