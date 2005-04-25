using System;
using System.Web.UI;
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

		protected Panel pnlError;
		protected Label lblError;

		protected IViewHelper Page_Error 
		{
			set
			{
				lblError.Text = ViewHelper.HtmlErrorBuilder (value);
				pnlError.Visible = true;
			}
		}

		#endregion

		#region Messages

		private const string msg_FIND_CMD = "FIND";
		private const string msg_ADD_CMD = "ADD NEW";
		private const string msg_FIND_HELPER_NULL = "Directory.aspx.cs.View_Load: myFindHelper failed. Is it serializable?";
		private const string msg_LIST_HELPER_NULL = "Directory.aspx.cs.View_Load: myListHelper failed. Is it serializable?";

		#endregion

		#region Find

		protected Panel pnlFind;
		protected DropDownList lstSelect;
		protected TextBox txtFind;
		protected Button cmdFind;
		protected IViewHelper myFindHelper;

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
			pnlFind.Visible = true;
		}

		// postback events - These events respond to user input (to controls displayed by pageload methods)

		protected void Find_Submit (object sender, EventArgs e)
		{
			// TODO: myListHelper.Execute(pnlFind.Controls);
			List_Load ();
		}

		#endregion

		#region panel List

		protected Panel pnlList;
		protected DataGrid repList;
		protected Button cmdAdd;
		protected IListHelper myListHelper;
		
		// pageload events

		private void List_Init ()
		{
			// Put user code to initialize the list here
		}

		private bool List_Load ()
		{
			bool okay = true ; // TODO: myListHelper.IsNominal;
			if (okay)
			{
				// TODO: repList.DataSource = myListHelper.List;
				repList.DataBind ();
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
			Find_Submit(null,null); // ISSUE: Is this a kludge?
			repList.CurrentPageIndex = e.NewPageIndex;
			repList.DataBind (); // ISSUE:  myListHelper.DataBind (repList);
		}

		protected void Add_Submit (object sender, EventArgs e)
		{
			// call business logic
			// TODO: myListHelper.DataInsert (repList);
			// act on outcome
			if (true) // TODO: (myListHelper.IsNominal)
			{
				// TODO: myListHelper.ItemIndex = 0;
				// TODO: myListHelper.DataBind (repList);
				// TODO: myListHelper.Insert = true;
				pnlList.Visible = true;
			}
			else Page_Error = myListHelper;
		}


		#endregion

		private void View_Init()
		{
			// TODO: myFindHelper = Controller.GetFindHelper(App.FIND_HELPER);
			// TODO: ViewState[App.FIND_HELPER] = myFindHelper;
			// TODO: myListHelper = Controller.GetListHelper(App.LIST_HELPER);
			// TODO: ViewState[App.LIST_HELPER] = myListHelper;
		}

		private void View_Load()
		{
			// TODO: myFindHelper = ViewState[App.FIND_HELPER] as IFindHelper;
			// if (null==myFindHelper) throw new ApplicationException(msg_FIND_HELPER_NULL);			
			// TODO: myListHelper = ViewState[App.LIST_HELPER] as IListHelper;
			// if (null==myListHelper) throw new ApplicationException(msg_LIST_HELPER_NULL);			
		}

		private void Page_Load(object sender, System.EventArgs e)
		{
			Find_Init();
			List_Init();
			bool isFirstView = (!IsPostBack);
			if  (isFirstView)
			{	
				View_Init();
				Find_Load();
			} 
			View_Load();
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

		#region Draft APIs

		/*

draft APIs

IFindHelper 

static void HtmlErrorBuilder (IViewHelper);
bool Execute(pnlFind.Controls);
bool IsNominal;

IListHelper 

IList myListHelper.List();
void DataInsert (repList);
int ItemIndex;
DataBind (DataGrid);
bool Insert;

IViewController

IFindHelper GetFindHelper(string);
IListHelper GetListHelper(string);
				
		*/

		#endregion

	}
}
