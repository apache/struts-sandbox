using System;
using System.Collections;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Web;
using Spring.Web.UI;

namespace Nexus.Web
{
	/// <summary>
	/// Base page for using a IGridViewHelper.
	/// </summary>
	public class BaseGridPage : Page
	{

		#region Helper

		private IGridViewHelper _Helper;
		/// <summary>
		/// Obtain dynamic data for the default view.
		/// </summary>
		///
		public virtual IGridViewHelper Helper
		{
			get { return _Helper; }
			set { _Helper = value; }
		}

		#endregion

		#region Page Properties 

		protected Panel pnlError;
		protected Label lblError;

		private IViewHelper _Page_Error;
		/// <summary>
		/// Set is called when an error occurs; override to provide functionality.
		/// </summary>		
		protected virtual IViewHelper Page_Error
		{
			set {_Page_Error = value;}
			get {return _Page_Error;}
		}

		private string _Page_Prompt;
		/// <summary>
		/// Set is called when the prompt changes; override to provide functionality.
		/// </summary>		
		protected virtual string Page_Prompt
		{
			set {_Page_Prompt = value;}
			get {return _Page_Prompt;}
		}
		#endregion


		#region Messages

		// ISSUE: Move messages to a default store (when available). [WNE-60]
		// FIXME: Make these text properties on DataGridHelper.

		protected const string msg_ADD_COMMAND = "ADD ITEM";
		protected const string msg_ADD_SUCCESS = "Item added.";
		protected const string msg_EDIT_HINT = "Edit description, press update. ";
		protected const string msg_QUIT_SUCCESS = "Change cancelled. ";
		protected const string msg_SAVE_SUCCESS = "Changes saved.";
		protected const string msg_TITLE = "NPDES Enforcement";

		#endregion

		#region Page Properties

		/// <summary>
		/// Attribute token for List_Criteria.
		/// </summary>
		private string LIST_CRITERIA_KEY = "__LIST_CRITERIA_KEY";

		/// <summary>
		/// Values to use with a query statement.
		/// </summary>
		public virtual IDictionary List_Criteria
		{
			get
			{
				IDictionary criteria = ViewState [LIST_CRITERIA_KEY] as IDictionary;
				return criteria;
			}
			set { ViewState [LIST_CRITERIA_KEY] = value; }
		}

		/// <summary>
		/// Attribute token for List_ItemIndex
		/// </summary>
		private const string LIST_ITEM_INDEX = "__LIST_ITEM_INDEX";

		/// <summary>
		/// Current item index, used mainly to signal editing. 
		/// </summary>
		public virtual int List_ItemIndex
		{
			get
			{
				object value = ViewState [LIST_ITEM_INDEX];
				if (value == null) return -1;
				return (int) value;
			}
			set
			{
				ViewState [LIST_ITEM_INDEX] = value;
				if (repList != null) repList.EditItemIndex = value;
			}
		}

		/// <summary>
		/// Attribute token for List_ItemKey.
		/// </summary>
		private const string LIST_ITEM_KEY = "__LIST_ITEM_KEY";

		/// <summary>
		/// The data key for the selected item.
		/// </summary>
		public virtual string List_ItemKey
		{
			get { return ViewState [LIST_ITEM_KEY] as string; }
			set { ViewState [LIST_ITEM_KEY] = value; }
		}

		/// <summary>
		/// Attribute token for List_Insert.
		/// </summary>
		private const string LIST_INSERT_KEY = "__LIST_INSERT_KEY";

		/// <summary>
		/// Insert mode - are we adding or modifying?
		/// </summary>
		public virtual bool List_Insert
		{
			get
			{
				object value = ViewState [LIST_INSERT_KEY];
				if (value == null) return false;
				return (bool) value;
			}
			set
			{
				ViewState [LIST_INSERT_KEY] = value;
				cmdListAdd.Visible = !value;
			}
		}

		#endregion

		#region Find methods

		protected Panel pnlFind;

		protected virtual bool Find_Submit (string prefix)
		{
			IViewHelper h = Helper;
			h.Read (pnlFind.Controls);
			List_Criteria = h.Criteria;
			return List_Load ();
		}

		#endregion

		#region panel: List

		#region List controls

		protected Panel pnlList;
		protected DataGrid repList;
		protected Button cmdListAdd;

		#endregion

		#region List methods

		protected virtual bool List_Load ()
		{
			IGridViewHelper h = Helper;
			bool okay = h.Load (repList, List_Criteria);
			if (okay)
			{
				// Template_Load(h.TitleText,h.HeadingText,h.PromptText);
				cmdListAdd.Text = msg_ADD_COMMAND;
				pnlList.Visible = true;
			}
			else
			{
				pnlList.Visible = false;
				Page_Error = h;
			}
			return okay;
		}

		protected virtual void List_Item (string commandName, int index)
		{
			switch (commandName)
			{
				case "Page":
					// Handled by StepList_PageIndexChanged
					break;
				default:
				{
					if (List_Insert)
						// ISSUE: If insert fails, old input is not retained. [WNE-67]
						List_Add_Load ();
					else
						List_Refresh ();
					break;
				}
			}
		}

		protected virtual void List_Edit (int index)
		{
			Page_Prompt = msg_EDIT_HINT;
			List_ItemIndex = index;
			List_Refresh ();
		}

		protected virtual void List_Quit ()
		{
			Page_Prompt = msg_QUIT_SUCCESS;
			List_Insert = false;
			List_ItemIndex = -1;
			List_Refresh ();
		}

		protected virtual void List_Save (string key, ICollection controls)
		{
			IGridViewHelper h = Helper;
			bool okay = h.Save (key, controls);
			if (okay)
			{
				okay = h.List (repList);
				Page_Prompt = (List_Insert) ? msg_ADD_SUCCESS : msg_SAVE_SUCCESS;
				List_Insert = false;
				List_ItemIndex = -1;
				List_Refresh ();
			}
			if (!okay) Page_Error = h;
		}

		protected virtual void List_Refresh ()
		{
			IGridViewHelper h = Helper;
			h.DataBind (repList);
			pnlList.Visible = true;
		}

		protected virtual void List_Add_Load ()
		{
			IGridViewHelper h = Helper;
			bool okay = h.DataInsert (repList);
			if (okay)
			{
				Page_Prompt = msg_EDIT_HINT;
				List_Insert = true;
				List_ItemIndex = 0;
				pnlList.Visible = true;
			}
			else Page_Error = h;
		}

		#endregion

		#region List events

		// init events

		private void List_Init ()
		{
			repList.AutoGenerateColumns = false;
			repList.EditItemIndex = List_ItemIndex;
			repList.CancelCommand += new DataGridCommandEventHandler (this.List_Quit);
			repList.EditCommand += new DataGridCommandEventHandler (this.List_Edit);
			repList.UpdateCommand += new DataGridCommandEventHandler (this.List_Save);
			repList.ItemCommand += new DataGridCommandEventHandler (this.List_Item);
			repList.PageIndexChanged += new DataGridPageChangedEventHandler (this.List_PageIndexChanged);
			cmdListAdd.Click += new EventHandler (this.List_Add);
		}

		// postback events

		protected void List_Edit (object source, DataGridCommandEventArgs e)
		{
			List_Edit (e.Item.ItemIndex);
		}

		protected void List_Save (object source, DataGridCommandEventArgs e)
		{
			IGridViewHelper h = Helper;
			string key = (List_Insert) ? null : h.GetDataKey (e, repList);
			ICollection controls = h.GetControls (e, repList);
			List_Save (key, controls);
		}

		protected void List_Quit (object source, DataGridCommandEventArgs e)
		{
			List_Quit ();
		}

		protected virtual void List_Add (object sender, EventArgs e)
		{
			List_Add_Load ();
		}

		protected void List_Item (object source, DataGridCommandEventArgs e)
		{
			int index = e.Item.ItemIndex;
			List_Item (e.CommandName, index);
		}

		protected void List_PageIndexChanged (object sender, DataGridPageChangedEventArgs e)
		{
			repList.CurrentPageIndex = e.NewPageIndex;
			List_Refresh ();
		}

		#endregion

		#endregion

		#region Page events

		protected void Page_Init ()
		{
			List_Init ();
			bool isFirstView = !IsPostBack;
			if (isFirstView)
				pnlList.Visible = false;
		}

		protected void Page_Load (object sender, EventArgs e)
		{
			if (pnlList.Visible)
				List_Load ();
		}

		#endregion

	}
}
