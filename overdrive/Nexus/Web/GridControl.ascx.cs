using System;
using System.Collections;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;
using WQD.Core.Controls;

namespace Nexus.Web
{
	/// <summary>
	/// List, edit, and select items using a DataGrid and IViewHelper commands. 
	/// </summary>
	public class GridControl : ViewControl
	{
		#region Runtime state Properties

		/// <summary>
		/// Provide an attribute token for List_Criteria.
		/// </summary>
		private string LIST_CRITERIA_KEY = "list_Criteria";

		/// <summary>
		/// Set the given criteria to the list_Critieria (creating a new one if null), and, 
		/// if AllowCustomPage is set, 
		/// calculate new Limit and Offset, based on pageIndex, and set to criteria.
		/// </summary>
		/// <remarks><p>
		/// This form is provided to be called by list_Criteria_Init. 
		/// The other form is provided to be called by other methods.
		/// </p></remarks>
		/// <param name="criteria">The criteria instance to store the attributes</param>
		/// <param name="pageIndex">The new page index</param>
		protected IDictionary list_Criteria_NewPageIndex(IDictionary criteria, int pageIndex)
		{
			if (Grid.AllowCustomPaging)
			{
				if (criteria == null) criteria = new Hashtable(); // FIXME: Spring?
				int page = pageIndex;
				int limit = Grid.PageSize;
				int offset = page*limit;
				criteria[ITEM_LIMIT] = limit;
				criteria[ITEM_OFFSET] = offset;
			}
			list_Criteria = criteria;
			return criteria;
		}

		protected IDictionary list_Criteria_NewPageIndex(IDictionary criteria, int pageIndex, bool allowCustomPaging)
		{
			Grid.AllowCustomPaging = allowCustomPaging;
			return list_Criteria_NewPageIndex(criteria, pageIndex);
		}

		protected IDictionary list_Criteria_NewPageIndex(int pageIndex)
		{
			IDictionary criteria = list_Criteria;
			return list_Criteria_NewPageIndex(criteria, pageIndex);
		}

		/// <summary>
		/// Provide values to use with a query statement, persisted across requests.
		/// </summary>
		protected IDictionary list_Criteria
		{
			get
			{
				IDictionary criteria = ViewState[LIST_CRITERIA_KEY] as IDictionary;
				return criteria;
			}
			set { ViewState[LIST_CRITERIA_KEY] = value; }
		}

		/// <summary>
		/// Merge values into list_Criteria.
		/// </summary>
		/// <param name="criteria">Values to append</param>
		public void Read(IDictionary criteria)
		{
			ICollection keys = criteria.Keys;
			foreach (string key in keys)
			{
				list_Criteria[key] = criteria[key];
			}
		}

		/// <summary>
		/// Provide attribute token for List_ItemIndex.
		/// </summary>
		private const string LIST_ITEM_INDEX = "list_ItemIndex";

		/// <summary>
		/// Current item index, used mainly to signal editing. 
		/// </summary>
		public virtual int list_ItemIndex
		{
			get
			{
				object value = ViewState[LIST_ITEM_INDEX];
				if (value == null) return -1;
				return (int) value;
			}
			set
			{
				ViewState[LIST_ITEM_INDEX] = value;
				if (Grid != null) Grid.EditItemIndex = value;
			}
		}

		/// <summary>
		/// Provide attribute token for List_ItemKey.
		/// </summary>
		private const string LIST_ITEM_KEY = "list_ItemKey";

		/// <summary>
		/// Provide data key for the selected item.
		/// </summary>
		public virtual string list_ItemKey
		{
			get { return ViewState[LIST_ITEM_KEY] as string; }
			set { ViewState[LIST_ITEM_KEY] = value; }
		}

		/// <summary>
		/// Provide attribute token for List_Insert.
		/// </summary>
		private const string LIST_INSERT_KEY = "list_Insert";

		/// <summary>
		/// Determine insert mode - are we adding or modifying?
		/// </summary>
		public virtual bool list_Insert
		{
			get
			{
				object value = ViewState[LIST_INSERT_KEY];
				if (value == null) return false;
				return (bool) value;
			}
			set { ViewState[LIST_INSERT_KEY] = value; }
		}

		/// <summary>
		/// Store whether command uses critiera.
		/// </summary>
		private bool _HasCriteria = true;

		/// <summary>
		/// Track whether a criteria is used.
		/// </summary>
		public virtual bool HasCriteria
		{
			get { return _HasCriteria; }
			set { _HasCriteria = value; }
		}

		#endregion

		#region Command properties to set

		/// <summary>
		/// Store the Find Command.
		/// </summary>
		private string _FindCommand;

		/// <summary>
		/// Provide the Find Command to prepare a new search.
		/// </summary>
		public virtual string FindCommand
		{
			get { return _FindCommand; }
			set { _FindCommand = value; }
		}

		/// <summary>
		/// Store the List Command.
		/// </summary>
		private string _ListCommand;

		/// <summary>
		/// Provide the List Command to filter items and populate the DataGrid.
		/// </summary>
		public virtual string ListCommand
		{
			get { return _ListCommand; }
			set { _ListCommand = value; }
		}

		/// <summary>
		/// Store the Save Command.
		/// </summary>
		private string _SaveCommand;

		/// <summary>
		/// Provide the Save Command to retain changes to an item.
		/// </summary>
		public virtual string SaveCommand
		{
			get { return _SaveCommand; }
			set { _SaveCommand = value; }
		}

		#endregion

		#region Column properties to set 

		/// <summary>
		/// Store the key field.
		/// </summary>
		private string _DataKeyField;

		/// <summary>
		/// Provide the key field for the DataGrid.
		/// </summary>
		public virtual string DataKeyField
		{
			get { return _DataKeyField; }
			set { _DataKeyField = value; }
		}

		/// <summary>
		/// Store the list of GridConfig items.
		/// </summary>
		private IList _Configs;

		/// <summary>
		/// Provide a list of the GridConfig items.
		/// </summary>
		/// <remarks><p>
		/// GridConfig is a local class that describes 
		/// the fields needed to program a DataGrid column.
		/// </p></remarks>
		public virtual IList Configs
		{
			get { return _Configs; }
			set { _Configs = value; }
		}

		#endregion

		#region Column properties with defaults

		/// <summary>
		/// Provide default text for the EDIT control.
		/// </summary>
		public const string msg_EDIT_TEXT = "EDIT";
		
		/// <summary>
		/// Provide default text for the CANCEL control.
		/// </summary>
		public const string msg_QUIT_TEXT = "CANCEL";

		/// <summary>
		/// Provide default text for the SAVE control.
		/// </summary>
		public const string msg_SAVE_TEXT = "SAVE";

		/// <summary>
		/// Provide default text for the ITEM control.
		/// </summary>		
		public const string msg_ITEM_TEXT = "#";
		
		/// <summary>
		/// Store the text for the EDIT control.
		/// </summary>
		private string _EditText = msg_EDIT_TEXT;

		/// <summary>
		/// Provide the text for the EDIT control.
		/// </summary>
		public virtual string EditText
		{
			get { return _EditText; }
			set { _EditText = value; }
		}

		/// <summary>
		/// Store the text for the QUIT control.
		/// </summary>
		private string _QuitText = msg_QUIT_TEXT;

		/// <summary>
		/// Provide the text for the QUIT control.
		/// </summary>
		public virtual string QuitText
		{
			get { return _QuitText; }
			set { _QuitText = value; }
		}

		/// <summary>
		/// Store the text for the SAVE control.
		/// </summary>
		private string _SaveText = msg_SAVE_TEXT;

		/// <summary>
		/// Provide the text for the SAVE control.
		/// </summary>
		public virtual string SaveText
		{
			get { return _SaveText; }
			set { _SaveText = value; }
		}

		/// <summary>
		/// Store the text for the ITEM control.
		/// </summary>
		private string _ItemText = msg_ITEM_TEXT;

		/// <summary>
		/// Provide the text for the ITEM control.
		/// </summary>
		public virtual string ItemText
		{
			get { return _ItemText; }
			set { _ItemText = value; }
		}

		/// <summary>
		/// Provide default token to signal an item select command.
		/// </summary>		
		public const string msg_ITEM_COMMAND = "Item";

		/// <summary>
		/// Store the token to signal an item select command. 
		/// </summary>
		private string _ItemCommand = msg_ITEM_COMMAND;

		/// <summary>
		/// Provide the token to signal an item select command. 
		/// </summary>
		public virtual string ItemCommandName
		{
			get { return _ItemCommand; }
			set { _ItemCommand = value; }
		}

		/// <summary>
		/// Store whether an item column is presented [false].
		/// </summary>
		private bool _HasItemColumn = false;

		/// <summary>
		/// Provide whether an item column is presented [false].
		/// </summary>
		public virtual bool HasItemColumn
		{
			get { return _HasItemColumn; }
			set { _HasItemColumn = value; }
		}

		/// <summary>
		/// Store whether an edit column is presented [false].
		/// </summary>
		private bool _HasEditColumn = false;

		/// <summary>
		/// Provide whether an edit column is presented [false].
		/// </summary>
		public virtual bool HasEditColumn
		{
			get { return _HasEditColumn; }
			set { _HasEditColumn = value; }
		}

		/// <summary>
		/// Store whether dataset is being accessed page by page. 
		/// </summary>
		private bool _AllowCustomPaging = true;

		/// <summary>
		/// Provide whether dataset is being accessed page by page. 
		/// </summary>
		/// <remarks><p>
		/// Custom paging refers to whether the entire dataset is retrieved all at once
		/// or whether the items to fill the current page are retrieved.
		/// A DataGrid may be configured to display pages with AllowCusteomPaging set to false, 
		/// but the underlying query should retrieve the entire dataset up front.
		/// Whether page numbers are used is set in the DataGrid markup, not here.
		/// </p></remarks>
		public virtual bool AllowCustomPaging
		{
			get { return _AllowCustomPaging; }
			set { _AllowCustomPaging = value; }
		}

		/// <summary>
		/// Store the ASP.NET default for a DataGrid page size.
		/// </summary>
		const int DEFAULT_DATAGRID_PAGESIZE = 10;
		
		/// <summary>
		/// Store the DataGrid default page size.
		/// </summary>
		private int _PageSize = DEFAULT_DATAGRID_PAGESIZE;

		/// <summary>
		/// Provide the DataGrid default page size.
		/// </summary>
		public virtual int PageSize
		{
			get { return _PageSize; }
			set { _PageSize = value; }
		}

		#endregion		

		#region Binding methods 

		/// <summary>
		/// Set the Helper's outcome to the DataGrid datasource, 
		/// and update the virtual item count if AllowCustomPaging.
		/// </summary>
		/// <param name="helper"></param>
		protected virtual void DataSource(IViewHelper helper)
		{
			IList list = helper.Outcome;
			DataGrid grid = Grid;
			grid.DataSource = list;
			if (grid.AllowCustomPaging)
			{
				grid.VirtualItemCount = GetItemCount(helper);
			}
		}

		/// <summary>
		/// Bind the current datasource to the base control and the DataGrid control.
		/// </summary>
		public override void DataBind()
		{
			base.DataBind();
			Grid.DataBind();
		}

		/// <summary>
		/// Configure a control column to select a DataGrid item (or row).
		/// </summary>
		/// <param name="pos">The column position</param>
		/// <returns>The next column position</returns>
		protected virtual int BindItemColumn(int pos)
		{
			ButtonColumn column = new ButtonColumn();
			column.ButtonType = ButtonColumnType.PushButton;
			column.Text = ItemText;
			column.CommandName = ItemCommandName;
			Grid.Columns.AddAt(pos, column);
			return ++pos;
		}

		/// <summary>
		/// Configure a control column to edit a DataGrid item (or row).
		/// </summary>
		/// <param name="pos">The column position</param>
		/// <returns>The next column position</returns>
		protected virtual int BindEditColumn(int pos)
		{
			EditCommandColumn column = new EditCommandColumn();
			column.ButtonType = ButtonColumnType.PushButton;
			column.EditText = EditText;
			column.CancelText = QuitText;
			column.UpdateText = SaveText;
			Grid.Columns.AddAt(pos, column);
			return ++pos;
		}

		/// <summary>
		/// Configure the list of custom columns, starting from a given column position.
		/// </summary>
		/// <remarks><p>
		/// If the edit or item columns have already been configured, 
		/// then starting position may be 1 or 2.
		/// </p></remarks>
		/// <param name="pos">Starting column position</param>
		/// <returns>Next column position</returns>
		protected virtual int BindColumns(int pos)
		{
			DataGrid grid = Grid;
			grid.DataKeyField = DataKeyField;
			IList configs = Configs;
			int colCount = configs.Count;
			for (int c = 0; c < colCount; c++)
			{
				IGridConfig config = configs[c] as IGridConfig;
				if (config.HasTemplate)
				{
					pos = BindTemplateColumn(pos, config);
				}
				else pos = BindColumn(pos, config);
			}
			return pos;
		}

		/// <summary>
		/// Configure a DataGrid column at the given position, 
		/// using the IGridConfig settings.
		/// </summary>
		/// <param name="pos">Position to insert column</param>
		/// <param name="config">Column settings</param>
		/// <returns>The next column pos</returns>
		protected int BindColumn(int pos, IGridConfig config)
		{
			BoundColumn column = new BoundColumn();
			column.HeaderText = config.HeaderText;
			column.DataField = config.DataField;
			// column.SortExpression = config.sortExpression; // See DataGridColumn.SortExpression Property
			// column.DataFormatString = config.dataFormat; // See Formatting Types in .NET Dev Guide
			Grid.Columns.AddAt(pos, column);
			return pos + 1;
		}

		/// <summary>
		/// Configure a template column at the given position, 
		/// using the IGtridConfig settings.
		/// </summary>
		/// <param name="pos">Position to insert column</param>
		/// <param name="config">Column settings</param>
		/// <returns>The next column pos</returns>
		protected int BindTemplateColumn(int pos, IGridConfig config)
		{
			TemplateColumn column = new TemplateColumn();
			column.HeaderText = config.HeaderText;
			column.ItemTemplate = config.ItemTemplate;
			column.EditItemTemplate = config.EditItemTemplate;
			// column.SortExpression = config.sortExpression; // See DataGridColumn.SortExpression Property
			// column.DataFormatString = config.dataFormat; // See Formatting Types in .NET Dev Guide
			Grid.Columns.AddAt(pos, column);
			return pos + 1;
		}

		/// <summary>
		/// Store whether the DataGrid has bee configured.
		/// </summary>
		private bool bind = true;

		/// <summary>
		/// Initialize the DataGrid with any custom columns.
		/// </summary>
		/// <remarks>
		/// This method is meant to be overriden by subclasses 
		/// to program a custom set of DataGrid columns.
		/// </remarks>
		protected virtual void InitGrid()
		{
			bind = true;
		}

		/// <summary>
		/// Obtain the item count from Helper.
		/// </summary>
		/// <param name="helper">The helper to examine</param>
		/// <returns>Total count of items for all pages</returns>
		/// 	
		protected int GetItemCount(IViewHelper helper)
		{
			return Convert.ToInt32(helper.Criteria[ITEM_COUNT]);
		}

		/// <summary>
		/// Obtain the item page from Helper, or zero if no page set.
		/// </summary>
		/// <param name="helper">The helper to examine</param>
		/// <returns>Current page number within data set</returns>
		/// 	
		protected int GetItemPage(IViewHelper helper)
		{
			object page = helper.Criteria[ITEM_PAGE];
			if (page==null) return 0;			
			return Convert.ToInt32(page);
		}

		/// <summary>
		/// Update the item page from Helper.
		/// </summary>
		/// <param name="helper">The helper to examine</param>
		/// <returns>Current page number for current item</returns>
		/// 	
		protected void SetItemPage(IViewHelper helper, int page)
		{
			helper.Criteria[ITEM_PAGE] = Convert.ToString(page);
		}

		
		/// <summary>
		/// Update the item offset from Helper.
		/// </summary>
		/// <param name="helper">The helper to examine</param>
		/// <returns>Current page number for current item</returns>
		/// 	
		protected void SetItemOffset(IViewHelper helper, int ofs)
		{
			helper.Criteria[ITEM_OFFSET] = Convert.ToString(ofs);
		}


		/// <summary>
		/// Obtain the item key field name.
		/// </summary>
		/// <param name="context">The context to examine</param>
		/// <returns>Name of key field</returns>
		/// 	
		protected object GetItemKey(IDictionary context)
		{
			if (context==null) return null;
			return context[ITEM_KEY];
		}
		
		/// <summary>
		/// Store the item key field name.
		/// </summary>
		/// <param name="context">The context to examine</param>
		/// <param name="key">The name of the item key field</param>
		/// 	
		protected void SetItemKey(IDictionary context, string key)
		{
			if (context!=null) context[ITEM_KEY] = key;
		}
		
		/// <summary>
		/// Obtain the item key value for Helper.
		/// </summary>
		/// <param name="context">The context to examine</param>
		/// <returns>Current page number within data set</returns>
		/// 	
		protected object GetItemKeyValue(IDictionary context)
		{
			if (context==null) return null;
			object key = context[ITEM_KEY];
			if (key==null) return null;
			return context[key];
		}
		
		/// <summary>
		/// Update the item key value for Helper, especially to clear on Add.
		/// </summary>
		/// <param name="context">The context to examine</param>
		/// <returns>Current page number within data set</returns>
		/// 	
		protected void SetItemKeyValue(IDictionary context, string val)
		{
			if (context==null) return;
			object key = context[ITEM_KEY];
			if (key!=null) context[key] = val;
		}

		/// <summary>
		/// Configure the DataGrid for initial display.
		/// </summary>
		/// <param name="helper">The Helper with an outcome to bind as a DataSource</param>
		protected virtual void BindGrid(IViewHelper helper)
		{

			// Only bind columns once
			// WARNING: Won't work with a singleton
			DataGrid grid = Grid;
			int count = (helper.Outcome).Count;
			if (bind)
			{
				bind = false;
				int i = 0;
				if (HasEditColumn) i = BindEditColumn(i);
				if (HasItemColumn) i = BindItemColumn(i);
				// Adopt any direct changes to Grid object
				if (grid.PageSize==DEFAULT_DATAGRID_PAGESIZE) grid.PageSize = PageSize;
				AllowCustomPaging = AllowCustomPaging || grid.AllowCustomPaging;
				// Check custom page settings
				if (AllowCustomPaging)
				{					
					grid.AllowCustomPaging = true;
					count = GetItemCount(helper);
					grid.VirtualItemCount = count;
					int page = GetItemPage(helper);
					if (page!=0) grid.CurrentPageIndex = page;
				}
				BindColumns(i);
			}
			ListPageIndexChanged_Raise(this,
			                           grid.CurrentPageIndex,
			                           grid.PageSize,
			                           count);
			DataSource(helper);
			DataBind();
		}

		#endregion 

		#region Special ReadControls method 

		/// <summary>
		/// Inspect a collection of DataGrid controls and set control values to a dictionary.
		/// </summary>
		/// <param name="controls">DataGrid Control Collection</param>
		/// <param name="dictionary">Output object for control values</param>
		/// <param name="keys">List of control/attribute names to collect</param>
		/// <param name="nullIfEmpty">If value is an empty strong, set to null</param>
		protected void ReadGridControls(ControlCollection controls, IDictionary dictionary, string[] keys, bool nullIfEmpty)
		{
			int i = -1;
			foreach (Control t in controls)
			{
				i++;
				string key = keys[i];
				if (IsTextBox(t))
				{
					TextBox x = (TextBox) t;
					string value = (nullIfEmpty) ? NullOnEmpty(x.Text) : x.Text;
					dictionary.Add(key, value);
					continue;
				}
				if (IsLabel(t))
				{
					Label x = (Label) t;
					string value = (nullIfEmpty) ? NullOnEmpty(x.Text) : x.Text;
					dictionary.Add(key, value);
					continue;
				}
				if (IsListControl(t))
				{
					ListControl x = (ListControl) t;
					string value = (nullIfEmpty) ? NullOnEmpty(x.SelectedValue) : x.SelectedValue;
					dictionary.Add(key, value);
					continue;
				}
				if (IsCheckBox(t))
				{
					CheckBox x = (CheckBox) t;
					string value = (x.Checked) ? key : null;
					dictionary.Add(key, value);
					continue;
				}
				if (IsRadioButton(t))
				{
					RadioButton x = (RadioButton) t;
					string value = (x.Checked) ? key : null;
					dictionary.Add(key, value);
					continue;
				}
			}

			/// Workaround so that template columns can be utilized by a dynamic DataGrid.
			/// [OVR-24] - Template columns not passed by DataGridCommandEventArgs
			foreach (IGridConfig c in Configs)
			{
				bool isTemplateColumn = c.ItemTemplate!=null;
				if (isTemplateColumn)
				{
					string key = c.DataField;
					string value =  FindControlValue(key);
					dictionary.Add(key,value);
				}
			}
		}

		#endregion

		#region Command methods

		/// <summary>
		/// Provide an internal extension point 
		/// that can return an entry item of the appropriate type 
		/// to use when adding a new entry to the dataset.
		/// </summary>
		protected virtual IEntryList NewContextList
		{
			get { throw new NotImplementedException(); }
		}
		
		/// <summary>
		/// Create a blank for editing by creating an empty row 
		/// and temporarily changing the datasource.
		/// </summary>
		/// <returns></returns>
		protected virtual IViewHelper DataInsert()
		{
			DataGrid grid = Grid;
			IEntryList list = NewContextList;
			// Fake a blank row
			IViewHelper helper = GetHelperFor(ListCommand);
			list.Insert(String.Empty);
			helper.Criteria[ListCommand] = list;
			grid.DataSource = list;
			grid.CurrentPageIndex = 0;
			grid.EditItemIndex = 0;
			BindGrid(helper);
			return helper;
		}

		/// <summary>
		/// Invoke a Find command.
		/// </summary>
		/// <param name="key">Data index key for the entry, if any</param>
		/// <param name="controls">The set of controls</param>
		/// <returns>The executed helper</returns>
		protected virtual IViewHelper Find(string key, ControlCollection controls)
		{
			IViewHelper helper = ExecuteBind(FindCommand);
			return helper;
		}

		/// <summary>
		/// Invoke a Save command.
		/// </summary>
		/// <param name="key">Data index key for the entry, if any</param>
		/// <param name="controls">The set of controls</param>
		/// <returns>The executed helper</returns>
		protected virtual IViewHelper Save(string key, ControlCollection controls)
		{
			IViewHelper h = GetHelperFor(SaveCommand);
			if (h.IsNominal)
			{
				IList configs = Configs;
				h.Criteria[DataKeyField] = key;
				int cols = configs.Count;
				string[] keys = new string[2 + cols];
				// reconstruct the standard edit column keys
				// just as placeholders, really
				keys[0] = SaveText;
				keys[1] = QuitText;
				int index = 2;
				// append our field names to the array of keys
				for (int i = 0; i < cols; i++)
					keys[index++] = (configs[i] as IGridConfig).DataField;
				ReadGridControls(controls, h.Criteria, keys, true);

				h.Execute();
			}
			return h;
		}

		#endregion

		#region Loading methods

		/// <summary>
		/// Invoke a ListCommand that doesn't require a criteria.
		/// </summary>
		/// <returns>Executed helper</returns>
		public virtual IViewHelper ExecuteList()
		{
			IViewHelper helper = Execute(ListCommand);
			bool okay = helper.IsNominal;
			if (okay) BindGrid(helper); // DoBindGrid = helper;
			return helper;
		}
		
		/// <summary>
		/// Look for a IssueEventKey, and scroll to it, if found.
		/// </summary>
		/// <param name="criteria">Input/outpout values</param>
		/// <returns>Helper after obtaining list</returns>
		public virtual IViewHelper ExecuteList(IDictionary criteria)
		{
			
			IViewHelper helper = ReadExecute(ListCommand, criteria);

			object issue_event_key = GetItemKeyValue(criteria);
			int count = GetItemCount(helper);
			bool okay = helper.IsNominal;
			bool skip_to_item = (issue_event_key == null) || (count==0) || !okay;
			if (skip_to_item)
			{
				if (okay) BindGrid(helper); // DoBindGrid = helper;
				return helper;
			}
			
			bool found = false; 
			int page = -1;
			int item = -1;
			object key = GetItemKey(criteria);
			while ((!found) && (count>item) && helper.IsNominal)
			{
				page++;
				if (helper.IsNominal)
				{
					IList outcome = helper.Outcome;
					foreach (EntryDictionary e in outcome)
					{
						found = found || (issue_event_key.Equals(e.Criteria[key]));
					}
					if (!found)
					{
						item = item + outcome.Count;
						SetItemOffset(helper,item+1);
						helper.Execute();
					}
				}
			}
			
			if (helper.IsNominal)
			{
				if (found) SetItemPage(helper,page);							
				BindGrid(helper); // DoBindGrid = helper;
			} 
			return helper;
		}
		
		
		
		
		/// <summary>
		/// Setup the DataGrid when the page is first initialized.
		/// </summary>
		/// <param name="criteria">Parameters for the comamnd</param>
		/// <returns></returns>
		public virtual IViewHelper LoadGrid(IDictionary criteria)
		{
			IViewHelper helper;

			if ((Grid.AllowCustomPaging) && (criteria == null))
			{
				list_Criteria_NewPageIndex(criteria, 0, true);
				HasCriteria = true;
			}

			if (HasCriteria) 
			{
				helper = ExecuteList(criteria);
			}
			else
				helper = ExecuteList();
			
			if (Grid.AllowCustomPaging)
			{
				int page = GetItemPage(helper);
				int count = GetItemCount(helper);
				ListPageIndexChanged_Raise(this, page, Grid.PageSize, count);								
			}
			
			return helper;
		}

		#endregion 

		#region List properties to set

		private DataGrid _Grid;

		public DataGrid Grid
		{
			get { return _Grid; }
			set { _Grid = value; }
		}

		#endregion

		#region List methods

		public virtual bool Open()
		{
			IViewHelper helper = LoadGrid(list_Criteria);
			bool okay = helper.IsNominal;
			if (!okay)
			{
				Page_Alert = helper;
			}
			return okay;
		}

		public virtual bool Open(IDictionary criteria)
		{
			Page_Reset();
			list_Criteria_NewPageIndex(criteria, 0, AllowCustomPaging);
			return Open();
		}

		public virtual void Reset(IDictionary criteria)
		{
			list_ResetIndex();
			Open(criteria);
		}

		public virtual void Reset()
		{
			Reset(list_Criteria);
		}

		/// <summary>
		/// Handle standard list Item events by opening item 
		/// or preparing to add a new item.
		/// </summary>
		/// <param name="commandName">The com</param>
		/// <param name="index"></param>
		protected virtual void list_Item(string commandName, int index)
		{
			switch (commandName)
			{
				case "Page":
					// Handled by StepList_PageIndexChanged
					break;
				case msg_ITEM_COMMAND:
					string key = Grid.DataKeys[index] as string;
					list_ItemKey = key;
					list_Item_Click(index);
					break;
				default:
					{
						if (list_Insert)
							// ISSUE: If insert fails, old input is not retained. [WNE-67]
							list_Add();
						else
							list_Refresh();
						break;
					}
			}
		}

		/// <summary>
		/// Handle standard Edit events by setting the index and refreshing display.
		/// </summary>
		/// <param name="index"></param>
		protected virtual void list_Edit(int index)
		{
			// ISSUE: Event? Page_Prompt = msg_EDIT_HINT;
			list_ItemIndex = index;
			list_Refresh();
		}

		/// <summary>
		/// Reset DataGrid state.
		/// </summary>
		protected virtual void list_Quit()
		{
			// ISSUE: Event? Page_Prompt = msg_QUIT_SUCCESS;
			list_Insert = false;
			list_ItemIndex = -1;
			list_Refresh();
		}

		/// <summary>
		/// Refresh DataGrid  by rebinding datasource.
		/// </summary>
		protected virtual void list_Refresh()
		{
			DataBind();
		}

		/// <summary>
		/// Set the selected index to 0. 
		/// </summary>
		/// <remarks><p>
		/// When changing the find set, also call List_ResetIndex;
		/// otherwise, the DataGrid may try to select an item 
		/// that is outside the new found set.
		/// </p></remarks>
		protected void list_ResetIndex()
		{
			Grid.SelectedIndex = 0;
			Grid.CurrentPageIndex = 0; // sic
		}

		/// <summary>
		/// Insert a new row for editing.
		/// </summary>
		protected virtual void list_Add()
		{
			IViewHelper helper = DataInsert();
			bool okay = helper.IsNominal;
			if (okay)
			{
				// ISSUE: Event? Page_Prompt = msg_EDIT_HINT;
				list_Insert = true;
				list_ItemIndex = 0;
				Grid.Visible = true;
			}
			else Page_Alert = helper;
		}

		#endregion

		#region List events

		/// <summary>
		/// Prepare an item for editing and return its key.
		/// </summary>
		/// <returns>Data key for item to edit</returns>
		private string GetDataKey()
		{
			DataGrid grid = Grid;
			int index = grid.EditItemIndex;
			string key = grid.DataKeys[index] as string;
			return key;
		}

		/// <summary>
		/// Harvest a collection of controls from DataGrid
		/// </summary>
		/// <param name="e"></param>
		/// <returns>A control collection for DataGrid</returns>
		public virtual ControlCollection GetControls(DataGridCommandEventArgs e)
		{
			DataGrid grid = Grid;
			ControlCollection controls = new ControlCollection(grid);
			foreach (TableCell t in e.Item.Cells)
			{
				for (int i = 0; i < t.Controls.Count; i++)
					controls.Add(t.Controls[i]);
			}
			return controls;
		}

		// postback events

		/// <summary>
		/// Handle event by presenting selected row in an editable form.
		/// </summary>
		/// <param name="source">Event source</param>
		/// <param name="e">Event parameters</param>
		private void list_Edit(object source, DataGridCommandEventArgs e)
		{
			list_Edit(e.Item.ItemIndex);
		}

		/// <summary>
		/// Handle event by retaining any changes to the selected DataGrid row.
		/// </summary>
		/// <param name="source">Event source</param>
		/// <param name="e">Event parameters</param>
		private void list_Save(object source, DataGridCommandEventArgs e)
		{
			string key = (list_Insert) ? null : GetDataKey();
			ControlCollection controls = GetControls(e);
			IViewHelper helper = Save(key, controls);
			bool okay = helper.IsNominal;
			if (okay)
			{				
				if (View_Save != null) View_Save(this, new ViewArgs(helper));
				list_Insert = false;
				list_ItemIndex = -1;
				okay = Open();
				// ISSUE: Event? Page_Prompt = (List_Insert) ? msg_ADD_SUCCESS : msg_SAVE_SUCCESS;
			}
			if (!okay) Page_Alert = helper;
		}

		
		/// <summary>
		/// Handle event by resetting DataGrid state.
		/// </summary>
		/// <param name="source">Event source</param>
		/// <param name="e">Event parameters</param>
		private void list_Quit(object source, DataGridCommandEventArgs e)
		{
			list_Quit();
		}

		/// <summary>
		/// Handle list add event by inserting a new row, 
		/// and raising a View Add event.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Event parameters</param>
		protected void list_Add(object sender, EventArgs e)
		{
			list_Add();
			if (View_Add != null) View_Add(sender, e);
		}

		/// <summary>
		/// Handle list item event by opening item or preparing to add new item.
		/// </summary>
		/// <param name="source">Event source</param>
		/// <param name="e">Event parameters</param>
		private void List_Item(object source, DataGridCommandEventArgs e)
		{
			int index = e.Item.ItemIndex;
			list_Item(e.CommandName, index);
		}

		/// <summary>
		/// Provide key to store item limit in criteria.
		/// </summary>
		public const string ITEM_LIMIT = "item_limit";
		
		/// <summary>
		/// Provide key to store item offset in criteria.
		/// </summary>
		public const string ITEM_OFFSET = "item_offset";
		
		/// <summary>
		/// Provide key to store item count in criteria.
		/// </summary>
		public const string ITEM_COUNT = "item_count";

		/// <summary>
		/// Provide key to store item page in criteria.
		/// </summary>
		public const string ITEM_PAGE = "item_page";

		/// <summary>
		/// Provide key to store item key in criteria.
		/// </summary>
		public const string ITEM_KEY = "item_key";
		
		#endregion

		#region ListPageIndexChanged 

		/// <summary>
		/// Signal that the Grid page index has changed, 
		/// and provide values for a page index hint.
		/// </summary>
		/// 
		public event EventHandler ListPageIndexChanged;

		/// <summary>
		/// Provide a default key for message resources that set the hint label.
		/// </summary>
		/// 
		public const string PAGE_INDEX_HINT = "page_index_hint";

		/// <summary>
		/// Provide a default key for the "Not Found" hint.
		/// </summary>
		/// 
		public const string NOT_FOUND_HINT = "not_found_hint";

		/// <summary>
		/// Provide values for a page index message (items x thru x of x).
		/// </summary>
		/// 
		public class ListPageIndexChangedArgs : EventArgs
		{
			public int ItemFrom;
			public int ItemThru;
			public int ItemCount;
		}

		/// <summary>
		/// Optional extension point so that subclasses can make adjustments 
		/// based on whether there are items to display or not. 
		/// </summary>
		/// <remarks><p>
		/// The classic use case for this method is to turn off the Grid 
		/// if there are not any items to display in the Grid. 
		/// In this case, if this control is used more than once in an enclosing 
		/// page or control, then the Grid should be toggled on or off for 
		/// each instance (Visible = isItems);
		/// </p></remarks>
		/// <param name="isItems">True if there are 1 or more items to display</param>
		public virtual void ListPageIndexChanged_IsItems(bool isItems)
		{
			// Override to provide functionalilty	
		}
		
		/// <summary>
		/// Lookup the PAGE_INDEX_HINT or the NOT_FOUND_HINT in the application 
		/// message resources, and return as a formatted string. 
		/// </summary>
		/// <param name="args">Our ListPageIndexChangedArgs with the page index values</param>
		/// <returns>Formatted message string ready to markup and present</returns>
		/// 
		public virtual string ListPageIndexChanged_Message(ListPageIndexChangedArgs args)
		{
			bool isItems = (args.ItemCount > 0);
			ListPageIndexChanged_IsItems(isItems);

			string[] m_args = new string[3];
			m_args[0] = Convert.ToString(args.ItemFrom);
			m_args[1] = Convert.ToString(args.ItemThru);
			m_args[2] = Convert.ToString(args.ItemCount);

			string text = isItems ? GetMessage(PAGE_INDEX_HINT, m_args) : GetMessage(NOT_FOUND_HINT);
			return text;
		}

		/// <summary>
		/// Raise the ListPageIndexChanged event.  
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="page">Current page number</param>
		/// <param name="size">Items per page</param>
		/// <param name="count">Total number of items</param>
		/// 
		private void ListPageIndexChanged_Raise(object sender, int page, int size, int count)
		{
			if (ListPageIndexChanged != null)
			{
				int from = (page*size) + 1;
				int thru = (page*size) + size;
				if (thru > count) thru = count;
				ListPageIndexChangedArgs a = new ListPageIndexChangedArgs();
				a.ItemFrom = from;
				a.ItemThru = thru;
				a.ItemCount = count;
				ListPageIndexChanged(sender, a);
			}
		}

		/// <summary>
		/// Handle the PageIndexChanged raised by our DataGrid.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime arguements</param>
		/// 
		private void list_PageIndexChanged(object sender, DataGridPageChangedEventArgs e)
		{
			DataGrid grid = Grid;
			int count = (grid.DataSource as IList).Count;

			if (grid.AllowCustomPaging)
			{
				IDictionary criteria = list_Criteria_NewPageIndex(e.NewPageIndex);
				IViewHelper helper = GetHelperFor(ListCommand);
				helper.Read(criteria, true);
				helper.Execute();
				DataSource(helper);
				count = GetItemCount(helper);
			}
			grid.CurrentPageIndex = e.NewPageIndex;

			ListPageIndexChanged_Raise(sender, e.NewPageIndex, grid.PageSize, count);
			list_Refresh();
		}


		#endregion

		/// <summary>
		/// Signal when an item is being added.
		/// </summary>
		/// 
		public event EventHandler View_Add;

		/// <summary>
		/// Handle click event by raising a View Add event 
		/// and passing the list criteria.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime arguments</param>
		protected void add_Click(object sender, EventArgs e)
		{
			if (View_Add != null)
			{
				SetItemKeyValue(list_Criteria, null);
				FindArgs f = new FindArgs(e, list_Criteria);
				View_Add(sender, f);
			}
		}

		/// <summary>
		/// Provide an internal extension point for handling a selected item.
		/// </summary>
		/// <param name="index">Page index of item being selected</param>
		protected virtual void list_Item_Click(int index)
		{
			// Override to provide implementation			
		}

		/// <summary>
		/// Signal when an item is being saved.
		/// </summary>
		public event EventHandler View_Save;

		/// <summary>
		/// Reset state for this control, including any ViewState attributes
		/// and the page indexes (@see(list_ResetIndex)), 
		/// usually on a new Open event or on a Quit event,
		/// </summary>
		public override void Page_Reset()
		{
			list_ResetIndex();
			base.Page_Reset();
		}

		/// <summary>
		/// Handle the page's Load event.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime arguments</param>
		private void Page_Load(object sender, EventArgs e)
		{
			DataGrid grid = Grid;
			grid.AutoGenerateColumns = false;
			grid.EditItemIndex = list_ItemIndex;
			grid.CancelCommand += new DataGridCommandEventHandler(list_Quit);
			grid.EditCommand += new DataGridCommandEventHandler(list_Edit);
			grid.UpdateCommand += new DataGridCommandEventHandler(list_Save);
			grid.ItemCommand += new DataGridCommandEventHandler(List_Item);
			grid.PageIndexChanged += new DataGridPageChangedEventHandler(list_PageIndexChanged);
			if (Visible) Open();
		}

		#region Web Form Designer generated code

		/// <summary>
		///		Initialize components.
		/// </summary>
		/// <param name="e">Runtime parameters</param>
		/// 
		protected override void OnInit(EventArgs e)
		{
			//
			// CODEGEN: This call is required by the ASP.NET Web Form Designer.
			//
			InitializeComponent();
			base.OnInit(e);
			InitGrid();
		}

		/// <summary>
		///		Required method for Designer support - do not modify
		///		the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.Load += new EventHandler(this.Page_Load);
		}

		#endregion

		#region Templates

		/// <summary>
		/// Describe the fields needed to program a DataGrid column.
		/// </summary>
		public interface IGridConfig
		{
			string DataField { get; }
			string HeaderText { get; }
			ITemplate ItemTemplate { get; set; }
			ITemplate EditItemTemplate { get; set; }
			bool HasTemplate { get; }
		}

		/// <summary>
		/// Implement IGridConfig.
		/// </summary>
		public class GridConfig : IGridConfig
		{
			/// <summary>
			/// Store attribute name for column (required).
			/// </summary>
			private string _DataField;

			/// <summary>
			/// Provide attribute name for column (required).
			/// </summary>
			public string DataField
			{
				get { return _DataField; }
			}

			/// <summary>
			/// Store heading for this column (optional).
			/// </summary>
			private string _HeaderText;

			/// <summary>
			/// Provide heading for this column (optional).
			/// </summary>
			public string HeaderText
			{
				get
				{
					if (_HeaderText == null) return DataField;
					return _HeaderText;
				}
			}

			/// <summary>
			/// Store item template for this column (optional).
			/// </summary>
			private ITemplate _ItemTemplate;

			/// <summary>
			/// Provide item template for this column (optional).
			/// </summary>
			public ITemplate ItemTemplate
			{
				get { return _ItemTemplate; }
				set { _ItemTemplate = value; }
			}

			/// <summary>
			/// Store edit template for this column (optional).
			/// </summary>
			private ITemplate _EditItemTemplate;

			/// <summary>
			/// Provide edit template for this column (optional).
			/// </summary>
			public ITemplate EditItemTemplate
			{
				get { return _EditItemTemplate; }
				set { _EditItemTemplate = value; }
			}

			// string DataFormat;
			// string SortFormat;
			// ITemplate ItemFormat;

			/// <summary>
			/// Store whether attribute has a template. 
			/// </summary>
			public bool HasTemplate
			{
				get { return (_ItemTemplate != null) || (_EditItemTemplate != null); }
			}

			/// <summary>
			/// Construct a GridConfig from a data field and header test.
			/// </summary>
			/// <param name="dataField">The attribute name for this column</param>
			/// <param name="headerText">The header text for this column</param>
			public GridConfig(string dataField, string headerText)
			{
				_DataField = dataField;
				_HeaderText = headerText;
			}

			/// <summary>
			/// Construct a GridConfig using all attributes.
			/// </summary>
			/// <param name="dataField">The attribute name for this column</param>
			/// <param name="headerText">The header text for this column</param>
			/// <param name="itemTemplate">The item template for this column</param>
			/// <param name="editItemTemplate">The edit template for this column</param>
			public GridConfig(string dataField, string headerText, ITemplate itemTemplate, ITemplate editItemTemplate)
			{
				_DataField = dataField;
				_HeaderText = headerText;
				_ItemTemplate = itemTemplate;
				_EditItemTemplate = editItemTemplate;
			}
		}

		/// <summary>
		/// Add literal text to a DataGrid column.
		/// </summary>
		public class LiteralTemplate : ITemplate
		{
			
			/// <summary>
			/// Store attribute name.
			/// </summary>
			private string _DataField;

			/// <summary>
			/// Handle data binding event by setting control text to data field.
			/// </summary>
			/// <param name="sender"></param>
			/// <param name="e"></param>
			private void OnDataBinding(object sender, EventArgs e)
			{
				Literal control;
				control = (Literal) sender;
				DataGridItem container = (DataGridItem) control.NamingContainer;
				control.Text = DataBinder.Eval(container.DataItem, _DataField) as string;
			}

			public void InstantiateIn(Control container)
			{
				Literal control = new Literal();
				control.ID = _DataField;
				control.DataBinding += new EventHandler(OnDataBinding);
				container.Controls.Add(control);
			}

			/// <summary>
			/// Construct instance from attribute name.
			/// </summary>
			/// <param name="dataField">Attribute name</param>
			public LiteralTemplate(string dataField)
			{
				_DataField = dataField;
			}
		}

		/// <summary>
		/// Configure a DataGrid column to display the selected value on the KeyValue list.
		/// </summary>
		public class KeyValueTemplate : ITemplate
		{
			private string _DataField;
			private IKeyValueList _Control;

			/// <summary>
			/// Handle data binding events by extracting key and value, 
			/// and setting selected value to control text.
			/// </summary>
			/// <param name="sender">Event source</param>
			/// <param name="e">Runtime arguments</param>
			private void OnDataBinding(object sender, EventArgs e)
			{
				Literal control;
				control = (Literal) sender;
				DataGridItem container = (DataGridItem) control.NamingContainer;
				string key = DataBinder.Eval(container.DataItem, _DataField) as string;
				control.Text = _Control.ValueFor(key);
			}

			public void InstantiateIn(Control container)
			{
				Literal control = new Literal();
				control.ID = _DataField;
				control.DataBinding += new EventHandler(OnDataBinding);
				container.Controls.Add(control);
			}

			/// <summary>
			/// Construct instance from a datafield and the list.
			/// </summary>
			/// <param name="dataField">Attribute name</param>
			/// <param name="list">List of keyValue items</param>
			public KeyValueTemplate(string dataField, IKeyValueList list)
			{
				_DataField = dataField;
				_Control = list;				
			}	
		}

		/// <summary>
		/// Present a drop down list control when editing a column.
		/// </summary>
		public class DropDownListTemplate : ITemplate
		{
			/// <summary>
			/// Store attribute name.
			/// </summary>
			private string _DataField;
			
			/// <summary>
			/// Store reference to the DropDownList control.
			/// </summary>
			private DropDownList _Control;

			/// <summary>
			/// Scan list for an item matching value.
			/// </summary>
			/// <param name="control">The control to scan</param>
			/// <param name="value">The value to match</param>
			private void SelectItem(ListControl control, string value)
			{
				if (value != null)
				{
					int index = 0;
					foreach (ListItem i in control.Items)
					{
						if (value.Equals(i.Value))
						{
							control.SelectedIndex = index;
							continue;
						}
						index++;
					}
				}
			}

			/// <summary>
			/// Handle a data binding event by selecting the key row.
			/// </summary>
			/// <param name="sender"></param>
			/// <param name="e"></param>
			private void OnDataBinding(object sender, EventArgs e)
			{
				DropDownList control;
				control = (DropDownList) sender;
				DataGridItem container = (DataGridItem) control.NamingContainer;
				string key = DataBinder.Eval(container.DataItem, _DataField) as string;
				SelectItem(control, key);
				_SelectedIndex = control.SelectedIndex; // FIXME: [OVR-24]
			}

			/// <summary>
			/// Cache the selected index for OnPreRender.
			/// </summary>
			private int _SelectedIndex;

			/// <summary>
			/// Provide a kludge method to set Selected Index.
			/// </summary>
			/// <remarks><p>
			/// After setting the selected index on DataBinding, 
			/// it is somehow being reset to 0 before prerender. 
			/// This method restores the selected index st by 
			/// OnDataBinding. 
			/// </p></remarks>
			/// <param name="sender">Event source</param>
			/// <param name="e">Runtime parameters</param>
			private void OnPreRender(object sender, EventArgs e)
			{
				DropDownList control;
				control = (DropDownList) sender;
				control.SelectedIndex = _SelectedIndex;
			}

			public void InstantiateIn(Control container)
			{
				container.Controls.Add(_Control);
			}

			/// <summary>
			/// Construct a DropDownListTempate from an id and datasource. 
			/// </summary>
			/// <param name="id">Data Field Name</param>
			public DropDownListTemplate(string id, object dataSource)
			{
				_DataField = id;
				_Control = new DropDownList();
				_Control.ID = id;
				_Control.DataSource = dataSource;
				_Control.DataBind();
				_Control.DataBinding += new EventHandler(OnDataBinding);
				_Control.PreRender += new EventHandler(OnPreRender);
			}

			/// <summary>
			/// Construct a DropDownListTempate from an id and list. 
			/// </summary>
			/// <param name="id">Data Field Name</param>
			/// <param name="list">Items to list</param>
			public DropDownListTemplate(string id, IKeyValueList list) : this(id,list,false)
			{}

			/// <summary>
			/// Construct a DropDownListTempate from id, list, insertNullKey.
			/// </summary>
			/// <param name="id">Data Field Name</param>
			/// <param name="list">Items to list</param>
			/// <param name="insertNullKey">Whether to prepend a -v- item to the list</param>
			public DropDownListTemplate(string id, IKeyValueList list, bool insertNullKey)
			{
				if (insertNullKey) 
				{
					lock(list)
					{
						IKeyValue e = list[0] as KeyValue;
						if (!NULL_TOKEN.Equals(e.Text))
						{								
							list.Insert(0, new KeyValue(String.Empty, NULL_TOKEN));						
						}
					}
				}					

				_DataField = id;
				_Control = new DropDownList();
				_Control.ID = id;
				_Control.DataSource = list;
				_Control.DataTextField = "value";
				_Control.DataValueField = "key";
				_Control.DataBind();
				_Control.DataBinding += new EventHandler(OnDataBinding);
				_Control.PreRender += new EventHandler(OnPreRender);
			}
		}

		#endregion
	}

	/* 

	
	#region List Panel

		protected Panel pnlList;
		protected DataGridControl list_report;
		// from BaseGrid: Button cmdListAdd;

		private void List_Init()
		{
			list_report.Helper = this.Helper;
			list_report.List_Init();
			pnlList.Visible = false;
		}

		/// <summary>
		/// Select only those items in control 
		/// whose Value property matches the given value.
		/// If the value is null, no action is taken.
		/// </summary>
		/// <param name="control">ListControl to process</param>
		/// <param name="text">Text label to match</param>
		/// 
		static void SelectItemText (ListControl control, string text)
		{
			if (text != null)
			{
				foreach (ListItem i in control.Items)
					i.Selected = false;

				foreach (ListItem i in control.Items)
				{
					if (text.Equals (i.Text))
						i.Selected = true;
				}
			}
		}

		private void List_Edit_Submit(IDictionary context)
		{									
			Helper.BindControls(pnlEdit.Controls,context,null);
			string county_name = context[App.COUNTY_NAME] as string;
			SelectItemText(county_key_list,county_name);
			Template_Load (App.msg_ROUTING_HEADING, App.msg_ROUTING_EDIT_PROMPT);
			pnlEdit.Visible = true;
			pnlFind.Visible = false;
			pnlList.Visible = false;			
		}

	#endregion	
	
	
	
	 */
}
