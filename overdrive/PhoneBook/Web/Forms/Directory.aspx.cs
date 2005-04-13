using System;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;

namespace PhoneBook.Web
{

	/// <summary>
	///  Maintain a list of employees with their telephone extension [OVR-5]. 
	/// </summary>
	public class Directory : Page
	{
	
		#region Page Properties

		private IController _Controller;
		/// <summary>
		/// Instance of our application controller (injected by Spring).
		/// </summary>
		public IController Controller
		{
			get { return _Controller; }
			set { _Controller = value; }
		}

		protected HtmlGenericControl msgTitle;
		protected Panel pnlError;
		protected Label lblError;
		protected Panel pnlPrompt;
		protected Label lblPrompt;

		protected string Page_Title
		{
			set
			{
				msgTitle.InnerText = value;
			}
		}

		protected IViewHelper Page_Error 
		{
			set
			{
				lblError.Text = ViewHelper.HtmlErrorBuilder (value);
				pnlError.Visible = true;
			}
		}

		protected string Page_Prompt
		{
			set
			{
				lblPrompt.Text = value;
				pnlPrompt.Visible = true;
			}
		}
		
		private int List_ItemIndex
		{
			get
			{
				return (int) ViewState["ITEM_INDEX"];
			}
			set
			{
				ViewState["ITEM_INDEX"] = value;
				if (repList!=null) repList.EditItemIndex = value;
			}
		}

		private bool List_Insert
		{
			get
			{
				return (bool) ViewState["INSERT"];
			}
			set
			{
				ViewState["INSERT"] = value;
				cmdAdd.Visible = !value;
			}
		}
		
		#endregion

		#region Messages

		private const string msg_TITLE = "PhoneBook Application";
		private const string msg_PROMPT = "To select entries, choose a filter or search for a Name or Extension.";
		private const string msg_FIND_CMD = "FIND";
		private const string msg_ADD_CMD = "ADD NEW";

		#endregion

		#region Find

		protected Panel pnlFind;
		protected IViewHelper myFindHelper;
		protected DropDownList lstSelect;
		protected TextBox txtFind;
		protected Button cmdFind;
		protected Button cmdAdd;

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
			// TODO: myFindHelper.Execute(pnlFind.Controls);
			if (true) // TODO: (!myFindHelper.IsNominal )
			{
				// TODO: Page_Error = myFindHelper;
			} 
			else
			{
				// TODO: Page_Title = myFindHelper.Title;
				// TODO: Page_Prompt = myFindHelper.Prompt;
			}
			pnlFind.Visible = true;
		}

		// postback events - These events respond to user input (to controls displayed by pageload methods)

		protected void Find_Submit (object sender, EventArgs e)
		{
			// TODO: myListHelper.Execute(pnlFind.Controls);
			List_Load ();
		}

		protected void Add_Submit (object sender, EventArgs e)
		{
			// call business logic
			// TODO: myListHelper.DataInsert (repList);
			// act on outcome
			if (true) // TODO: (myListHelper.IsNominal)
			{
				List_ItemIndex = 0;
				// TODO: myListHelper.DataBind (repList);
				// TODO: Page_Prompt = myListHelper.Prompt;
				List_Insert = true;
				pnlList.Visible = true;
			}
			else Page_Error = myListHelper;
		}

		#endregion

		#region panel List

		protected Panel pnlList;
		protected IEditHelper myListHelper;
		protected DataGrid repList;
		
		// pageload events

		private void List_Init ()
		{
			// Put user code to initialize the list here
		}

		// Data access method
		private bool List_Load ()
		{
			bool okay = true ; // TODO: myListHelper.IsNominal;
			if (okay)
			{
				// TODO: repList.DataSource = myListHelper.List;
				repList.DataBind ();
				// TODO: Page_Title = myListHelper.Title;
				// TODO: Page_Prompt = myListHelper.Prompt;
				pnlList.Visible = true;
			}
			else
				Page_Error = myListHelper;
			return okay;
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
			Find_Submit(null,null); // ISSUE: Refine workflow ... [WNE-64]
			repList.CurrentPageIndex = e.NewPageIndex;
			repList.DataBind ();
		}

		#endregion

		private void Page_Load(object sender, System.EventArgs e)
		{
			Find_Init();
			List_Init();
			bool isFirstView = (!IsPostBack);
			if (isFirstView)
			{
				Page_Title = msg_TITLE;
				Page_Prompt = msg_PROMPT;
				// TODO: myFindHelper = Controller.GetObject(App.FIND_HELPER) as IViewHelper;
				// TODO: myListHelper = Controller.GetObject(App.LIST_HELPER) as IEditHelper;
				Find_Load(); 
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
