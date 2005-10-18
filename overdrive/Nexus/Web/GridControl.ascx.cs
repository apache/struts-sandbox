using System;
using System.Collections;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;
using WQD.Core.Controls;

namespace Nexus.Web
{
	public class GridControl : ViewControl
	{
		#region Runtime state Properties

		/// <summary>
		/// Attribute token for List_Criteria.
		/// </summary>
		private string LIST_CRITERIA_KEY = "list_Criteria";

		/// <summary>
		/// Set the given criteria to the list_Critieria (creating a new one if null), and, 
		/// If AllowCustomPage is set, 
		/// calcuate new Limit and Offset, based on pageIndex, and set to criteria; 
		/// obtain Count from criteria (or set to 0).
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
				if (criteria==null) criteria = new Hashtable(); // FIXME: Spring?
				int page = pageIndex;
				int limit = Grid.PageSize;
				int offset = page * limit;
				criteria[ITEM_LIMIT] = limit;
				criteria[ITEM_OFFSET] = offset;
			}
			list_Criteria = criteria;
			return criteria;
		}
	
		/// <summary>
		/// If AllowCustomPage is set, 
		/// calculate new List and Offset, using the list_Context.
		/// </summary>
		/// <remarks><p>
		/// The other form is provided to be called by list_Criteria_Init. 
		/// This form is provided to be called by other methods.
		/// </p></remarks>
		/// <param name="pageIndex">The new page index</param>
		/// <returns>The updated list_Criteria instance</returns>
		protected IDictionary list_Criteria_NewPageIndex(int pageIndex)
		{
			IDictionary criteria = list_Criteria;
			return list_Criteria_NewPageIndex(criteria,pageIndex);
		}

		/// <summary>
		/// Values to use with a query statement.
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
		/// Attribute token for List_ItemIndex
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
		/// Attribute token for List_ItemKey.
		/// </summary>
		private const string LIST_ITEM_KEY = "list_ItemKey";

		/// <summary>
		/// The data key for the selected item.
		/// </summary>
		public virtual string list_ItemKey
		{
			get { return ViewState[LIST_ITEM_KEY] as string; }
			set { ViewState[LIST_ITEM_KEY] = value; }
		}

		/// <summary>
		/// Attribute token for List_Insert.
		/// </summary>
		private const string LIST_INSERT_KEY = "list_Insert";

		/// <summary>
		/// Insert mode - are we adding or modifying?
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

		private bool _HasCriteria = true;

		public virtual bool HasCriteria
		{
			get { return _HasCriteria; }
			set { _HasCriteria = value; }
		}

		#endregion

		#region Command properties to set

		private string _FindCommand;

		public virtual string FindCommand
		{
			get { return _FindCommand; }
			set { _FindCommand = value; }
		}

		private string _ListCommand;

		public virtual string ListCommand
		{
			get { return _ListCommand; }
			set { _ListCommand = value; }
		}

		private string _SaveCommand;

		public virtual string SaveCommand
		{
			get { return _SaveCommand; }
			set { _SaveCommand = value; }
		}

		#endregion

		#region Column properties to set 

		private string _DataKeyField;

		public virtual string DataKeyField
		{
			get { return _DataKeyField; }
			set { _DataKeyField = value; }
		}

		private IList _DataFields;

		public virtual IList DataFields
		{
			get { return _DataFields; }
			set { _DataFields = value; }
		}

		private IList _DataLabels;

		public virtual IList DataLabels
		{
			get { return _DataLabels; }
			set { _DataLabels = value; }
		}

		#endregion

		#region Column properties with defaults

		public const string msg_EDIT_TEXT = "EDIT";
		public const string msg_QUIT_TEXT = "CANCEL";
		public const string msg_SAVE_TEXT = "SAVE";
		public const string msg_ITEM_TEXT = "#";
		public const string msg_ITEM_COMMAND = "Item";

		private string _EditText = msg_EDIT_TEXT;

		public virtual string EditText
		{
			get { return _EditText; }
			set { _EditText = value; }
		}

		private string _QuitText = msg_QUIT_TEXT;

		public virtual string QuitText
		{
			get { return _QuitText; }
			set { _QuitText = value; }
		}

		private string _SaveText = msg_SAVE_TEXT;

		public virtual string SaveText
		{
			get { return _SaveText; }
			set { _SaveText = value; }
		}

		private string _ItemText = msg_ITEM_TEXT;

		public virtual string ItemText
		{
			get { return _ItemText; }
			set { _ItemText = value; }
		}

		private string _ItemCommand = msg_ITEM_COMMAND;

		public virtual string ItemCommandName
		{
			get { return _ItemCommand as string; }
			set { _ItemCommand = value; }
		}

		private bool _HasItemColumn = false;

		public virtual bool HasItemColumn
		{
			get { return _HasItemColumn; }
			set { _HasItemColumn = value; }
		}

		private bool _HasEditColumn = false;

		public virtual bool HasEditColumn
		{
			get { return _HasEditColumn; }
			set { _HasEditColumn = value; }
		}

		private bool _AllowCustomPaging = false;

		public virtual bool AllowCustomPaging
		{
			get { return _AllowCustomPaging; }
			set { _AllowCustomPaging = value; }
		}

		#endregion		

		#region Binding methods 

		protected virtual void DataSource(IViewHelper helper)
		{
			IList list = helper.Outcome as IList;
			DataGrid grid = Grid;
			grid.DataSource = list;


		}

		public override void DataBind()
		{
			base.DataBind();
			Grid.DataBind();
		}

		protected virtual int BindItemColumn(int i)
		{
			ButtonColumn column = new ButtonColumn();
			column.ButtonType = ButtonColumnType.PushButton;
			column.Text = ItemText;
			column.CommandName = ItemCommandName;
			Grid.Columns.AddAt(i, column);
			return ++i;
		}

		protected virtual int BindEditColumn(int i)
		{
			EditCommandColumn column = new EditCommandColumn();
			column.ButtonType = ButtonColumnType.PushButton;
			column.EditText = EditText;
			column.CancelText = QuitText;
			column.UpdateText = SaveText;
			Grid.Columns.AddAt(i, column);
			return ++i;
		}

		protected virtual int BindColumns(int i)
		{
			DataGrid grid = Grid;
			grid.DataKeyField = DataKeyField;
			int colCount = DataFields.Count;
			int lblCount = DataLabels.Count;
			for (int c = 0; c < colCount; c++)
			{
				string column = DataFields[c] as string;
				string label = (lblCount < c) ? column : DataLabels[c] as string;
				i = BindColumn(i, label, column);
			}
			return i;
		}

		protected int BindColumn(int pos, string headerText, string dataField, string sortExpression, string dataFormat)
		{
			BoundColumn column = new BoundColumn();
			column.HeaderText = headerText;
			column.DataField = dataField;
			column.SortExpression = sortExpression; // See DataGridColumn.SortExpression Property
			column.DataFormatString = dataFormat; // See Formatting Types in .NET Dev Guide
			Grid.Columns.AddAt(pos, column);
			return pos + 1;
		}

		public int BindColumn(int pos, string headerText, string dataField)
		{
			return BindColumn(pos, headerText, dataField, String.Empty, String.Empty);
		}

		private bool bind = true;

		protected virtual void InitGrid()
		{
			bind = true;
		}

		/// <summary>
		/// Obtain item count from Helper.
		/// </summary>
		/// <param name="helper">The helper to examine</param>
		/// <returns>Total count of items for all pages</returns>
		/// 	
		private int GetItemCount(IViewHelper helper)
		{
			return Convert.ToInt32(helper.Criteria[ITEM_COUNT]);
		}

		protected virtual void BindGrid(IViewHelper helper)
		{
			// Only bind columns once
			// WARNING: Won't work with a singleton
			if (bind)
			{
				bind = false;
				int i = 0;
				if (HasEditColumn) i = BindEditColumn(i);
				if (HasItemColumn) i = BindItemColumn(i);
				int count = (helper.Outcome).Count; 
				DataGrid grid = Grid;
				if (AllowCustomPaging)
				{
					count = GetItemCount(helper);
					grid.AllowCustomPaging = true;
					grid.VirtualItemCount = count;
				}
				BindColumns(i);
				ListPageIndexChanged_Raise(this, 
					grid.CurrentPageIndex,
					grid.PageSize,
					count);
			}
			DataSource(helper);
			DataBind();
		}

		#endregion 

		#region Special ReadControls method 

		private void ReadGridControls(ControlCollection controls, IDictionary dictionary, string[] keys, bool nullIfEmpty)
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
		}

		#endregion

		#region Command methods

		/// <summary>
		/// If "Add Row" feature is going to be used, 
		/// Override getter to return new instance of the Context list 
		/// for this application. 
		/// </summary>
		protected virtual IEntryList NewContextList
		{
			get { throw new NotImplementedException(); }
		}

		protected virtual IViewHelper DataInsert()
		{
			DataGrid grid = Grid;
			IEntryList list = NewContextList;
			// Fake a blank row
			IViewHelper helper = GetHelperFor(ListCommand);
			list.Insert(String.Empty);
			// ISSUE: FIXME: Do we need helper.Outcome = list;
			grid.DataSource = list;
			grid.CurrentPageIndex = 0;
			grid.EditItemIndex = 0;
			DataBind();
			return helper;
		}

		protected virtual IViewHelper Find(string key, ControlCollection controls)
		{
			IViewHelper helper = ExecuteBind(FindCommand);
			return helper;
		}

		protected virtual IViewHelper Save(string key, ControlCollection controls)
		{
			IViewHelper h = GetHelperFor(SaveCommand);
			if (h.IsNominal)
			{
				h.Criteria[DataKeyField] = key;
				int cols = DataFields.Count;
				string[] keys = new string[2 + cols];
				// reconstruct the standard edit column keys
				// just as placeholders, really
				keys[0] = SaveText;
				keys[1] = QuitText;
				int index = 2;
				// append our field names to the array of keys
				for (int i = 0; i < cols; i++)
					keys[index++] = DataFields[i] as string;
				ReadGridControls(controls, h.Criteria, keys, true);
				h.Execute();
			}
			return h;
		}

		#endregion

		#region Loading methods

		public virtual IViewHelper ExecuteList()
		{
			IViewHelper helper = Execute(ListCommand);
			bool okay = helper.IsNominal;
			if (okay) BindGrid(helper); // DoBindGrid = helper;
			return helper;
		}

		public virtual IViewHelper ExecuteList(IDictionary criteria)
		{
			IViewHelper helper = ReadExecute(ListCommand, criteria);
			bool okay = helper.IsNominal;
			if (okay) BindGrid(helper); // DoBindGrid = helper;
			return helper;
		}

		public virtual IViewHelper LoadGrid(IDictionary criteria)
		{
			IViewHelper helper;

			if ((Grid.AllowCustomPaging) && (criteria==null))
			{
				list_Criteria_NewPageIndex(criteria,0);
				HasCriteria = true;
			}

			if (HasCriteria)
				helper = ExecuteList(criteria);
			else
				helper = ExecuteList();
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
			IViewHelper helper = this.LoadGrid(list_Criteria);
			bool okay = helper.IsNominal;
			if (!okay)
			{
				Page_Error = helper;
			}
			return okay;
		}

		public virtual bool Open(IDictionary criteria)
		{
			Page_Reset();
			list_Criteria_NewPageIndex(criteria,0);
			return Open();
		}

		public virtual void Reset(IDictionary criteria)
		{
			list_ResetIndex();
			Open(criteria);
		}

		public virtual void Reset()
		{
			list_ResetIndex();
			list_Refresh();
		}

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
							list_Add_Load();
						else
							list_Refresh();
						break;
					}
			}
		}

		protected virtual void list_Edit(int index)
		{
			// ISSUE: Event? Page_Prompt = msg_EDIT_HINT;
			list_ItemIndex = index;
			list_Refresh();
		}

		protected virtual void list_Quit()
		{
			// ISSUE: Event? Page_Prompt = msg_QUIT_SUCCESS;
			list_Insert = false;
			list_ItemIndex = -1;
			list_Refresh();
		}

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

		protected virtual void list_Add_Load()
		{
			IViewHelper helper = DataInsert();
			bool okay = helper.IsNominal;
			if (okay)
			{
				// ISSUE: Event? Page_Prompt = msg_EDIT_HINT;
				list_Insert = true;
				list_ItemIndex = 0;
			}
			else Page_Error = helper;
		}

		#endregion

		#region List events

		private string GetDataKey()
		{
			DataGrid grid = Grid;
			int index = grid.EditItemIndex;
			string key = grid.DataKeys[index] as string;
			return key;
		}

		private ControlCollection GetControls(DataGridCommandEventArgs e)
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

		private void list_Edit(object source, DataGridCommandEventArgs e)
		{
			list_Edit(e.Item.ItemIndex);
		}

		private void list_Save(object source, DataGridCommandEventArgs e)
		{
			string key = (list_Insert) ? null : GetDataKey();
			ControlCollection controls = GetControls(e);
			IViewHelper helper = Save(key, controls);
			bool okay = helper.IsNominal;
			if (okay)
			{
				list_Insert = false;
				list_ItemIndex = -1;
				okay = this.Open();
				// ISSUE: Event? Page_Prompt = (List_Insert) ? msg_ADD_SUCCESS : msg_SAVE_SUCCESS;
			}
			if (!okay) Page_Error = helper;
		}

		private void list_Quit(object source, DataGridCommandEventArgs e)
		{
			list_Quit();
		}

		protected void list_Add(object sender, EventArgs e)
		{
			list_Add_Load();
			if (View_Add!=null) View_Add(sender,e);
		}

		private void List_Item(object source, DataGridCommandEventArgs e)
		{
			int index = e.Item.ItemIndex;
			list_Item(e.CommandName, index);
		}

		public const string ITEM_LIMIT = "item_limit";
		public const string ITEM_OFFSET = "item_offset";
		public const string ITEM_COUNT = "item_count";

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
		/// Lookup the PAGE_INDEX_HINT or the NOT_FOUND_HINT in the application 
		/// message resources, and return as a formatted string. 
		/// </summary>
		/// <param name="args">Our ListPageIndexChangedArgs with the page index values</param>
		/// <returns>Formatted message string ready to markup and present</returns>
		/// 
		public string ListPageIndexChanged_Message(ListPageIndexChangedArgs args)
		{			
			
			string[] m_args = new string[3];
			m_args[0] = Convert.ToString(args.ItemFrom);
			m_args[1] = Convert.ToString(args.ItemThru);
			m_args[2] = Convert.ToString(args.ItemCount);
		
			string text;
			if (args.ItemCount==0) 
			{
				text = GetMessage(NOT_FOUND_HINT);
			}
			else
			{
				text = GetMessage(PAGE_INDEX_HINT,m_args);				
			}			
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
			if (ListPageIndexChanged!=null)
			{
				int from = (page * size) + 1;
				int thru = (page * size) + size;
				if (thru>count) thru = count;				
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
				helper.Read(criteria,true);
				helper.Execute();
				DataSource(helper);
				count = GetItemCount(helper) ;
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

		protected void add_Click(object sender, EventArgs e)
		{
			if (View_Add!=null)
			{
				FindArgs f = new FindArgs(e,list_Criteria);
				View_Add(sender,f);
			}
		}

		protected virtual void list_Item_Click(int index)
		{
			// Override to provide implementation
		}

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
		/// 
		private void Page_Load(object sender, EventArgs e)
		{
			Grid.AutoGenerateColumns = false;
			Grid.EditItemIndex = list_ItemIndex;
			Grid.CancelCommand += new DataGridCommandEventHandler(list_Quit);
			Grid.EditCommand += new DataGridCommandEventHandler(list_Edit);
			Grid.UpdateCommand += new DataGridCommandEventHandler(list_Save);
			Grid.ItemCommand += new DataGridCommandEventHandler(List_Item);
			Grid.PageIndexChanged += new DataGridPageChangedEventHandler(list_PageIndexChanged);
			if (this.Visible) Open();
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