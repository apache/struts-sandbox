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
		protected const string msg_EDIT_HINT = "Edit entry, press SAVE. ";
		protected const string msg_QUIT_SUCCESS = "Change cancelled. ";
		protected const string msg_SAVE_SUCCESS = "Changes saved.";

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
				if (criteria==null)
				{
					criteria = new Hashtable();
					ViewState [LIST_CRITERIA_KEY] = criteria;
				}
				return criteria;
			}
			set { ViewState [LIST_CRITERIA_KEY] = value; }
		}

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

		/// <summary>
		/// Group find controls.
		/// </summary>
		protected Panel pnlFind;

		/// <summary>
		/// Initialize controls within Find panel.
		/// </summary>
		protected virtual void Find_Init ()
		{
			// override to provide functionality
		}

		/// <summary>
		/// Load controls within Find panel.
		/// </summary>
		protected virtual void Find_Load () 
		{
			// override to provide functionality
		}

		/// <summary>
		/// Read current value of Find controls for load List.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Event</param>
		protected virtual void Find_Submit (object sender, EventArgs e)
		{
			IGridViewHelper h = GridHelper;
			h.Read (pnlFind.Controls);
			List_Criteria = h.FindHelper.Criteria;
			List_Load ();
		}

		#endregion

		#region panel: List

		#region List controls

		/// <summary>
		/// Group List controls.
		/// </summary>
		protected Panel pnlList;

		/// <summary>
		/// Render the list as a DataGrid.
		/// </summary>
		protected DataGrid repList;

		/// <summary>
		/// Invoke display for adding a new entry.
		/// </summary>
		protected Button cmdListAdd;

		#endregion

		#region List methods

		/// <summary>
		/// Filter entries and update display.
		/// </summary>
		/// <returns>True if nominal</returns>
		protected virtual bool List_Load ()
		{
			IGridViewHelper h = GridHelper;
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
				Page_Error = h.ListHelper;
			}
			return okay;
		}

		/// <summary>
		/// Handle custom Item events, such as adding a new entry.
		/// </summary>
		/// <remarks><p>
		/// The logic here, which seems kludgy, invokes our "insert" event, 
		/// if there are no other takers.
		/// </p></remarks>
		/// <remarks></remarks>
		/// <param name="commandName">Name of command for the Item event</param>
		/// <param name="index">Index of DataGrid entry causing the event</param>
		protected virtual void List_Item (string commandName, int index)
		{
			switch (commandName)
			{
				case "Page":
					// Handled by List_PageIndexChanged
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

		/// <summary>
		/// Invoke edit mode for the selected entry.
		/// </summary>
		/// <param name="index">Index of selected entry</param>
		protected virtual void List_Edit (int index)
		{
			Page_Prompt = msg_EDIT_HINT;
			List_ItemIndex = index;
			List_Refresh ();
		}

		/// <summary>
		/// Exit edit mode without changing the entry.
		/// </summary>
		protected virtual void List_Quit ()
		{
			Page_Prompt = msg_QUIT_SUCCESS;
			List_Insert = false;
			List_ItemIndex = -1;
			List_Refresh ();
		}

		/// <summary>
		/// Invoke the SaveHelper to update the persistent store
		/// for the entry being edited.
		/// </summary>
		/// <param name="key">Entry key</param>
		/// <param name="controls">Controls in the selected cell</param>
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

		/// <summary>
		/// Rebind the datasource to the grid.
		/// </summary>
		protected virtual void List_Refresh ()
		{
			IGridViewHelper h = GridHelper;
			h.DataBind (repList);
			pnlList.Visible = true;
		}

		/// <summary>
		/// Insert a blank entry in the datagrid 
		/// so that a new entry can be added inline.
		/// </summary>
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

		/// <summary>
		/// Set the selected index to 0. 
		/// </summary>
		/// <remarks><p>
		/// When changing the find set, also call List_ResetIndex;
		/// otherwise, the DataGrid may try to select an item 
		/// that is outside the new found set.
		/// </p></remarks>
		protected void List_ResetIndex ()
		{
			repList.SelectedIndex = 0;
			repList.CurrentPageIndex = 0; // sic
		}

		#endregion

		#region List events

		// init events

		/// <summary>
		/// Initialize controls in the List panel.
		/// </summary>
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

		/// <summary>
		/// Edit the selected entry (by invoking List_Edit(int)).
		/// </summary>
		/// <param name="source">Event source</param>
		/// <param name="e">Event</param>
		protected void List_Edit (object source, DataGridCommandEventArgs e)
		{
			List_Edit (e.Item.ItemIndex);
		}

		/// <summary>
		/// Save the selected entry (by invoking List_Save(string,ICollection)).
		/// </summary>
		/// <param name="source">Event</param>
		/// <param name="e"></param>
		protected void List_Save (object source, DataGridCommandEventArgs e)
		{
			IGridViewHelper h = GridHelper;
			string key = (List_Insert) ? null : h.GetDataKey (e, repList);
			ICollection controls = h.GetControls (e, repList);
			List_Save (key, controls);
		}

		/// <summary>
		/// Exit edit mode (by invoking List_Quit()).
		/// </summary>
		/// <param name="source"></param>
		/// <param name="e"></param>
		protected void List_Quit (object source, DataGridCommandEventArgs e)
		{
			List_Quit ();
		}

		/// <summary>
		/// Invoke insert mode (by invoking List_Add_Load()).
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		protected virtual void List_Add (object sender, EventArgs e)
		{
			List_Add_Load ();
		}

		/// <summary>
		/// Handle the custom Item event (by invoking List_Item(string,int)).
		/// </summary>
		/// <param name="source"></param>
		/// <param name="e"></param>
		protected void List_Item (object source, DataGridCommandEventArgs e)
		{
			int index = e.Item.ItemIndex;
			List_Item (e.CommandName, index);
		}

		/// <summary>
		/// Change the page index and refresh the display.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		protected void List_PageIndexChanged (object sender, DataGridPageChangedEventArgs e)
		{
			repList.CurrentPageIndex = e.NewPageIndex;
			List_Refresh ();
		}

		#endregion

		#endregion

		#region Page events

		/// <summary>
		/// Invoke other Init methods.
		/// </summary>
		/// <remarks><p>
		/// Call if overridden.
		/// </p></remarks>
		protected virtual void Page_Init ()
		{
			Find_Init ();
			List_Init ();

			if (!IsPostBack)
			{
				pnlList.Visible = false;				
			}
		}

		/// <summary>
		/// Invoke other Load methods.
		/// </summary>
		/// <remarks><p>
		/// Call if overridden.
		/// </p></remarks>
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
