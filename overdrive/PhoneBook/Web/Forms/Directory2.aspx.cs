using System;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Web;

namespace PhoneBook.Web.Forms
{
	/// <summary>
	///  Maintain a list of employees with their telephone extension [OVR-5]. 
	/// </summary>
	/// 
	public class Directory2 : BaseGridPage
	{
		#region Messages

		private const string msg_FILTER = "Select a filter to display fewer entries";
		private const string msg_LIST_ALL_CMD = "SHOW ALL";

		#endregion

		#region Page Properties 

		protected Panel pnlError;
		protected Label lblError;

		/// <summary>
		/// Display a list of error mesasges.
		/// </summary>
		protected override IViewHelper Page_Error
		{
			set
			{
				lblError.Text = value.ErrorsText;
				pnlError.Visible = true;
			}
		}

		protected Panel pnlPrompt;
		protected Label lblPrompt;

		/// <summary>
		/// Display a Prompt mesasges.
		/// </summary>
		protected override string Page_Prompt
		{
			set { lblPrompt.Text = value; }
		}

		#endregion

		#region Find -- Display Find controls

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

		private void ListAll_Click (object sender, EventArgs e)
		{
			Filter_Reset (null);
			List_Load ();
		}

		protected override void Find_Init ()
		{
			cmdListAll.Text = msg_LIST_ALL_CMD;
			cmdListAll.Click += new EventHandler (ListAll_Click);

			foreach (DropDownList filter in FilterList ())
			{
				filter.AutoPostBack = true;
				filter.SelectedIndexChanged += new EventHandler (Find_Submit);
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
			// Update other members
			List_ResetIndex ();
			Page_Prompt = msg_FILTER;
		}

		protected override void Find_Submit (object sender, EventArgs e)
		{
			DropDownList list = sender as DropDownList;
			string id = list.ID;
			int v = id.LastIndexOf (GridHelper.FindHelper.ListSuffix);
			string key = id.Substring (0, v);
			GridHelper.FindHelper.Criteria [key] = list.SelectedValue;
			Filter_Reset (list);
			List_Load ();
		}

		protected override void Find_Load ()
		{
			IViewHelper h = GridHelper.FindHelper;
			h.ExecuteBind (pnlFind.Controls);
			bool ok = (h.IsNominal);
			if (!ok)
				Page_Error = h;
		}

		#endregion

		#region Page Events

		protected override void Page_Init ()
		{
			base.Page_Init ();
			pnlList.Visible = true;
			pnlError.Visible = false;
			if (!IsPostBack)
				Page_Prompt = msg_FILTER;
		}

		#endregion
	}
}