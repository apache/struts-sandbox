using System;
using System.Collections;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;

namespace PhoneBook.Web.Forms
{
	/// <summary>
	///  Display a list of employees with their telephone extension [OVR-5]. 
	/// </summary>
	/// 
	public class Directory : Page
	{
		#region Messages

		private const string msg_LIST_ALL_CMD = "SHOW ALL";

		#endregion

		#region Page Properties 

		protected Panel pnlError;
		protected Label lblError;

		/// <summary>
		/// Display a list of error messages.
		/// </summary>
		public IViewHelper Page_Error
		{
			set
			{
				lblError.Text = value.ErrorsText;
				pnlError.Visible = true;
			}
		}

		#endregion

		#region Helpers

		private IViewHelper _FindHelper;
		/// <summary>
		/// Display the filter lists.
		/// </summary>
		///
		public virtual IViewHelper FindHelper
		{
			get { return _FindHelper; }
			set { _FindHelper = value; }
		}

		/// <summary>
		/// Apply filter and display matching entries.
		/// </summary>
		///
		private IViewHelper _ListHelper;
		public virtual IViewHelper ListHelper
		{
			get { return _ListHelper; }
			set { _ListHelper = value; }
		}

		#endregion

		#region Find

		protected Panel pnlFind;
		protected DropDownList last_name_list;
		protected DropDownList first_name_list;
		protected DropDownList extension_list;
		protected DropDownList user_name_list;
		protected DropDownList hired_list;
		protected DropDownList hours_list;
		protected Button cmdListAll;

		private DropDownList[] FilterList ()
		{
			DropDownList[] lists = {last_name_list, first_name_list, extension_list, user_name_list, hired_list, hours_list};
			return lists;
		}

		private void Find_Init ()
		{
			cmdListAll.Text = msg_LIST_ALL_CMD;
			cmdListAll.Click += new EventHandler (ListAll_Click);

			foreach (DropDownList filter in FilterList ())
			{
				filter.AutoPostBack = true;
				filter.SelectedIndexChanged += new EventHandler (Filter_Changed);
			}
		}

		private void Filter_Reset (DropDownList except)
		{
			int exceptIndex = 0;
			if (except != null) exceptIndex = except.SelectedIndex;
			foreach (DropDownList filter in FilterList ())
			{
				filter.SelectedIndex = 0;
			}
			if (except != null) except.SelectedIndex = exceptIndex;
		}

		private void Filter_Changed (object sender, EventArgs e)
		{
			DropDownList list = sender as DropDownList;
			string id = list.ID;
			int v = id.LastIndexOf (ListHelper.ListSuffix);
			string key = id.Substring (0, v);
			ListHelper.Criteria [key] = list.SelectedValue;
			Filter_Reset (list);
			List_Load (ListHelper);
		}

		private void Find_Load ()
		{
			IViewHelper h = FindHelper;
			h.ExecuteBind (pnlFind.Controls);
			bool ok = (h.IsNominal);
			if (!ok)
				Page_Error = h;
		}

		private void ListAll_Click (object sender, EventArgs e)
		{
			Filter_Reset (null);
			List_Load (ListHelper);
		}

		#endregion

		#region List

		protected Panel pnlList;
		protected DataGrid repList;

		private void List_Load (IViewHelper helper)
		{
			helper.Execute ();
			bool ok = helper.IsNominal;
			if (!ok) Page_Error = helper;
			else
			{
				IList result = helper.Outcome;
				repList.DataSource = result;
				repList.DataBind ();
			}
		}

		#endregion

		#region Page Events

		protected void Page_Init ()
		{
			pnlError.Visible = false;
			Find_Init ();
		}

		protected void Page_Load (object sender, EventArgs e)
		{
			if (!IsPostBack)
			{
				Find_Load ();
				List_Load (ListHelper);
			}
		}

		#endregion
	}
}