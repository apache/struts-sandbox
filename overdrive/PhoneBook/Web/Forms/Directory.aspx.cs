using System;
using System.Collections;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using System.Web.UI;

namespace PhoneBook.Web.Forms
{
	/// <summary>
	///  Maintain a list of employees with their telephone extension [OVR-5]. 
	/// </summary>
	/// 
	public class Directory : Page
	{
		#region Messages

		private const string msg_ADD_CMD = "ADD NEW";
		private const string msg_LIST_ALL_CMD = "SHOW ALL";

		#endregion

		#region Page Properties 

		protected Panel pnlError;
		protected Label lblError;

		/// <summary>
		/// Display a list of error mesasges.
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

		private IViewHelper _ViewHelper;
		/// <summary>
		/// Obtain dynamic data for the default view.
		/// </summary>
		///
		public virtual IViewHelper ViewHelper
		{
			get { return _ViewHelper; }
			set { _ViewHelper = value; }
		}

		private IViewHelper _FindHelper;
		public virtual IViewHelper FindHelper
		{
			get { return _FindHelper; }
			set { _FindHelper = value; }
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
		// TODO: protected DropDownList editor_list;
		protected Button cmdListAll;

		// pageload events - These methods populate controls to display

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
			int v = id.LastIndexOf (FindHelper.ListSuffix);
			string key = id.Substring (0, v);
			FindHelper.Criteria [key] = list.SelectedValue;
			Filter_Reset (list);
			List_Load (FindHelper);
		}

		private void Find_Load ()
		{
			IViewHelper h = ViewHelper;
			h.ExecuteBind (pnlFind.Controls);
			bool ok = (h.IsNominal);
			if (!ok)
				Page_Error = h;
		}

		// postback events - These events respond to user input (to controls displayed by pageload methods)

		private void ListAll_Click (object sender, EventArgs e)
		{
			Filter_Reset (null);
			List_Load (FindHelper);
		}

		#endregion

		#region List

		protected Panel pnlList;
		protected DataGrid repList;
		protected Button cmdAdd;

		// pageload events

		private void List_Init ()
		{
			this.cmdAdd.Text = msg_ADD_CMD;
			this.cmdAdd.Visible = false; // TODO: True if user is editor
		}

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

		// postback events 

		protected void List_ItemCommand (object source, DataGridCommandEventArgs e)
		{
			bool okay = false;
			switch (e.CommandName)
			{
				case "Page":
					// Handled by List_PageIndexChanged
					break;
				default:
					throw new NotImplementedException ();
			}

			if (okay) pnlList.Visible = false;
		}

		protected void List_PageIndexChanged (object sender, DataGridPageChangedEventArgs e)
		{
			repList.CurrentPageIndex = e.NewPageIndex;
			repList.DataBind ();
		}

		#endregion

		#region Page Events

		protected void Page_Init ()
		{
			pnlError.Visible = false;
			Find_Init ();
			List_Init ();
		}

		protected void Page_Load (object sender, EventArgs e)
		{
			if (!IsPostBack)
			{
				Find_Load ();
				List_Load (FindHelper);
			}
		}

		#endregion
	}
}