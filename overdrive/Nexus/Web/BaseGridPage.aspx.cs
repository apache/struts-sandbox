using System;
using System.Collections;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Web;
using Spring.Web.UI;

namespace Nexus.Web
{
	/// <summary>
	/// Provide common functionality for page that is configured to use a IGridViewHelper.
	/// </summary>
	/// <remarks><p>
	/// The implementing code-benhind needs to 
	/// * extend BaseGridPage, 
	/// * inject a instance of IGridViewHelper, and
	/// * override Page_Error and Page_Prompt (optional). 
	/// GridViewHelper is abstract, and you need to implement your own NewEntryList method. 
	/// </p><p>
	/// The ASPX page needs to provide 
	/// * pnlError
	/// * pnlList
	/// * repList
	/// * cmdAddList
	/// </p>
	/// <p>
	/// If a Find dialog is provided, the implementing code behind can override 
	/// * Find_Init, 
	/// * Find_Load, and 
	/// * Find_Submit.
	/// The ListHelper will use the criteria setup by Find_Submit to consumate the search.
	/// </p><p>
	/// The BasePage and Helper provide all the routine functionality need to display a 
	/// datagrid with an arbitrary set of columns for inline editing. 
	/// The remaining functionality is provided by configuring the GridViewHelper instance
	/// with the FindHelper, ListHelper, and SaveHelper needed for each respective task. 
	/// The Helpers do not have to be written specially, and may even be used elsewhere. 
	/// The GridViewHelper and BasePage manage the Helpers, which provide the custom 
	/// functionalty for the specified columns. 
	/// </p><p>
	/// The columns to edit are configured through the Helper's FieldSet. 
	/// Each column must be a FieldContext. 
	/// The FieldContext Label is used for the column heading.
	/// </p><p>
	/// For a working example, see Directory2 in the PhoneBook application.
	/// </p></remarks>
	public class BaseGridPage : Page
	{

		#region Helper

		private IGridViewHelper _GridHelper;
		/// <summary>
		/// Encapsulate three Helpers that work together
		/// to Find, List, and Save DataGrid entries.
		/// </summary>
		///
		public virtual IGridViewHelper GridHelper
		{
			get { return _GridHelper; }
			set { _GridHelper = value; }
		}

		#endregion

		#region Page Properties 

		private IViewHelper _Page_Error;
		///<summary>
		///Handle error messages.
		///</summary>
		/// <remarks><p>
		/// Set is called when an error occurs. 
		/// Override to provide functionality.
		/// </p></remarks>		
		protected virtual IViewHelper Page_Error
		{
			set {_Page_Error = value;}
			get {return _Page_Error;}
		}

		private string _Page_Prompt;
		///<summary>
		///Handle page prompts.
		///</summary>
		/// <remarks><p>
		/// Set is called when the prompt changes; override to provide functionality.
		/// </p></remarks>		
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
		/// Identify the attribute token for List_ItemIndex
		/// </summary>
		private const string LIST_ITEM_INDEX = "__LIST_ITEM_INDEX";

		/// <summary>
		/// Store the current item index, mainly to signal edit mode. 
		/// </summary>
		protected virtual int List_ItemIndex
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
		/// Identify the attribute token for List_ItemKey.
		/// </summary>
		private const string LIST_ITEM_KEY = "__LIST_ITEM_KEY";

		/// <summary>
		/// Store the data key for the selected item.
		/// </summary>
		protected virtual string List_ItemKey
		{
			get { return ViewState [LIST_ITEM_KEY] as string; }
			set { ViewState [LIST_ITEM_KEY] = value; }
		}

		/// <summary>
		/// Identify the attribute token for List_Insert.
		/// </summary>
		private const string LIST_INSERT_KEY = "__LIST_INSERT_KEY";

		/// <summary>
		/// Store insert mode (are we adding or modifying?).
		/// </summary>
		protected virtual bool List_Insert
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

		protected virtual void Find_Init ()
		{
			// override to provide functionality
		}

		protected virtual void Find_Load () 
		{
			// override to provide functionality
		}

		protected virtual void Find_Submit (object sender, EventArgs e)
		{
			IViewHelper h = GridHelper;
			h.Read (pnlFind.Controls);
			List_Load ();
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
			IGridViewHelper h = GridHelper;
			bool okay = h.Load (repList, h.FindHelper.Criteria);
			if (okay)
			{
				// Template_Load(h.TitleText,h.HeadingText,h.PromptText);
				cmdListAdd.Text = msg_ADD_COMMAND;
				pnlList.Visible = true;
			}
			else
			{
				pnlList.Visible = false;
				Page_Error = h.ListHelper;
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
			IGridViewHelper h = GridHelper; 
			bool okay = h.Save (key, controls);
			if (okay)
			{
				okay = h.List (repList);
				Page_Prompt = (List_Insert) ? msg_ADD_SUCCESS : msg_SAVE_SUCCESS;
				List_Insert = false;
				List_ItemIndex = -1;
				List_Refresh ();
			}
			if (!okay) Page_Error = h.SaveHelper; 
		}

		protected virtual void List_Refresh ()
		{
			IGridViewHelper h = GridHelper;
			h.DataBind (repList);
			pnlList.Visible = true;
		}

		protected virtual void List_Add_Load ()
		{
			IGridViewHelper h = GridHelper;
			bool okay = h.DataInsert (repList);
			if (okay)
			{
				Page_Prompt = msg_EDIT_HINT;
				List_Insert = true;
				List_ItemIndex = 0;
				pnlList.Visible = true;
			}
			else Page_Error = h.ListHelper;
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
			IGridViewHelper h = GridHelper;
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

		protected virtual void Page_Init ()
		{
			Find_Init ();
			List_Init ();

			if (!IsPostBack)
			{
				pnlList.Visible = false;				
			}
		}

		protected virtual void Page_Load (object sender, EventArgs e)
		{

			if (!IsPostBack)
			{
				Find_Load ();
			}

			if (pnlList.Visible)
				List_Load ();
		}

		#endregion
	}
}
