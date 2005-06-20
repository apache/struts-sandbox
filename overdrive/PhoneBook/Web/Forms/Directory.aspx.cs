using System;
using System.Collections;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using PhoneBook.Core;
using PhoneBook.Core.Commands;

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
		private const string msg_PRINT_CMD = "PRINT";
		private const string msg_LIST_ALL_CMD = "SHOW ALL";

		#endregion

		public IList GetDataSource ()
		{
			BaseList command = new BaseList();
			command.ID = App.SELECT_ALL;
			IRequestContext context = command.NewContext ();
			command.Execute(context);
			IList result = context.Outcome as IList;
			return result;
		}


		#region Find

		protected Panel pnlFind;
		protected DropDownList lstLastName;
		protected DropDownList lstFirstName;
		protected DropDownList lstExtension;
		protected DropDownList lstUserName;
		protected DropDownList lstHireDate;
		protected DropDownList lstHours;
		protected DropDownList lstEditor;
		protected Button cmdListAll;
		protected Button cmdPrint;

		// pageload events - These methods populate controls to display

		private DropDownList[] GetLists ()
		{
			DropDownList[] lists = {lstLastName,lstFirstName,lstExtension,lstUserName,lstHireDate,lstHours,lstEditor};
			return lists;
		}

		private void Find_Init ()
		{
			cmdListAll.Text = msg_LIST_ALL_CMD;
			cmdPrint.Text = msg_PRINT_CMD;

			foreach (DropDownList filter in GetLists())
			{
				filter.AutoPostBack = true;				
			}
		}


		// postback events - These events respond to user input (to controls displayed by pageload methods)

		protected void Find_Filter (object sender, EventArgs e)
		{
			// TODO: See if filter changed.
			List_Load ();
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

		private void List_Load ()
		{
			repList.DataSource = GetDataSource();
			repList.DataBind ();
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
			Find_Filter(null,null); 
			repList.CurrentPageIndex = e.NewPageIndex;
			repList.DataBind (); 
		}

		#endregion

		protected void Page_Init()
		{
			Find_Init();
			List_Init();
		}

		protected void Page_Load(object sender, System.EventArgs e)
		{
			if  (IsPostBack)
			{
				Find_Filter(sender, e);	
			}
			{
				List_Load();
			}
		}
	}
}
