using System;
using System.Collections;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using PhoneBook.Core;
using PhoneBook.Core.Commands;

namespace PhoneBook.Web
{

	/// <summary>
	///  Maintain a list of employees with their telephone extension [OVR-5]. 
	/// </summary>
	public class Directory : Page
	{
	
		#region Messages

		private const string msg_FIND_CMD = "FIND";
		private const string msg_ADD_CMD = "ADD NEW";

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
		protected DropDownList lstSelect;
		protected TextBox txtFind;
		protected Button cmdFind;

		// pageload events - These methods populate controls to display

		private void Find_Init ()
		{
			this.cmdFind.Click += new EventHandler (this.Find_Submit);
			this.cmdFind.Text = msg_FIND_CMD;
			this.cmdAdd.Click += new EventHandler (this.Add_Submit);
			this.cmdAdd.Text = msg_ADD_CMD;
		}

		private void Find_Load ()
		{
			pnlFind.Visible = true;
		}

		// postback events - These events respond to user input (to controls displayed by pageload methods)

		protected void Find_Submit (object sender, EventArgs e)
		{
			List_Load ();
		}

		#endregion

		#region panel List

		protected Panel pnlList;
		protected DataGrid repList;
		protected Button cmdAdd;
		
		// pageload events

		private void List_Init ()
		{
			// Put user code to initialize the list here
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
			Find_Submit(null,null); 
			repList.CurrentPageIndex = e.NewPageIndex;
			repList.DataBind (); 
		}

		protected void Add_Submit (object sender, EventArgs e)
		{
			// TODO: ...
		}


		#endregion

		private void Page_Init()
		{
			Find_Init();
			List_Init();
		}

		private void Page_Load(object sender, System.EventArgs e)
		{
			if  (!IsPostBack)
			{
				Find_Load();
				List_Load();
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
			Page_Init();
		}
		
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{    
			this.Load += new System.EventHandler(this.Page_Load);
		}
		#endregion

	}

}
