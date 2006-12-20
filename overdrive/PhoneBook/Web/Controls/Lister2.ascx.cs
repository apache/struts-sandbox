using System;
using System.Collections;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;
using Nexus.Core.Profile;
using PhoneBook.Core;

namespace PhoneBook.Web.Controls
{
	/// <summary>
	/// Present matching directory entries.
	/// </summary>
	public class Lister2 : AppGridControl
	{
		/// <summary>
		/// Provide instance of DataGrid control
		/// </summary>
		/// 
		protected DataGrid list;

		/// <summary>
		/// Provide instance of Add button.
		/// </summary>
		/// 
		protected Button add;

		/// <summary>
		/// Toggle off the add button when adding.
		/// </summary>
		/// 
		protected override void list_Add()
		{
			base.list_Add();
			add.Visible = false;
		}

		/// <summary>
		/// Toggle off the add button when editing.
		/// </summary>
		/// 
		protected override void list_Edit(int index)
		{
			base.list_Edit(index);
			add.Visible = false;
		}

		protected override IViewHelper Save(string key, ControlCollection controls)
		{
			IViewHelper h = base.Save(key, controls);
			if (h.IsNominal)
			{
				bool needEditorValue = (null == h.Criteria[App.EDITOR]);
				// FIXME: [OVR-24] - Template columns not passed by DataGridCommandEventArgs
				if (needEditorValue)
				{
					h.Criteria[App.EDITOR] = FindControlValue(App.EDITOR);
				}
				h.Execute();
			}
			return h;
		}

		public override ControlCollection GetControls(DataGridCommandEventArgs e)
		{
			DataGrid grid = Grid;
			ControlCollection controls = new ControlCollection(grid);
			foreach (TableCell cell in e.Item.Cells)
			{
				for (int i = 0; i < cell.Controls.Count; i++)
					controls.Add(cell.Controls[i]);
			}

			/*
			// What the scripts usually do, but our EDITOR_CELL is null.
			const int EDITOR_CELL = 8;
			TableCell o = e.Item.Cells[EDITOR_CELL];
			DropDownList r = (DropDownList) o.FindControl(App.EDITOR);
			controls.Add(r);
			object o = e.Item.FindControl(App.EDITOR);

			// The template is in the DataGrid, just not in the event
			TableRow item = grid.Items[1];
			foreach (TableCell cell in item.Cells)
			{
				for (int i = 0; i < cell.Controls.Count; i++)
					controls.Add(cell.Controls[i]);				
			}	
			*/

			return controls;
		}

		/// <summary>
		/// Toggle add button on and present Grid.
		/// </summary>
		/// <returns>True if nominal</returns>
		/// 
		public override bool Open()
		{
			add.Visible = true;
			return base.Open ();
		}

		/// <summary>
		/// Complete loading Grid 
		/// after other members have initialized.
		/// </summary>
		/// 
		private void Grid_Load()
		{
			AppUserProfile profile = Session[UserProfile.USER_PROFILE] as AppUserProfile;
			HasEditColumn = profile.IsEditor;
		}

		private IKeyValueList _EditorKeys = null;

		private IKeyValueList EditorKeyList
		{
			get
			{
				if (_EditorKeys == null)
				{
					IKeyValueList data = new KeyValueList();
					// FIXME: Obtain from Spring?
					data.Add(new KeyValue(" ", "--v--"));
					data.Add(new KeyValue("0", "NO"));
					data.Add(new KeyValue("1", "YES"));
					_EditorKeys = data;
				}
				return _EditorKeys;
			}
		}

		/// <summary>
		/// ID Token to indicate a Label control.
		/// </summary>
		/// 
		private static string LABEL = "_label";

		/// <summary>
		/// Assemble an IGridConfig for an attribute.
		/// </summary>
		/// <param name="dataField">The attribute ID</param>
		/// <returns>An IGridConfig instance</returns>
		private IGridConfig GetConfig(string dataField)
		{
			string headerText = GetMessage(dataField + LABEL);
			IGridConfig config = new GridConfig(dataField, headerText);
			return config;
		}

		/// <summary>
		/// Initialize our Grid instance 
		/// by setting the columns, labels, 
		/// and other dynamic attributes.
		/// </summary>
		/// 
		private void Grid_Init(DataGrid grid)
		{
			Grid = grid;
			FindCommand = App.ENTRY_FIND;
			ListCommand = App.ENTRY_LIST;
			SaveCommand = App.ENTRY_SAVE;
			DataKeyField = App.ENTRY_KEY;
			AllowCustomPaging = true;
			PageSize = 2;
			// HasEditColumn = true; // Set from profile

			IList cols = new ArrayList(7);
			cols.Add(GetConfig(App.LAST_NAME));
			cols.Add(GetConfig(App.FIRST_NAME));
			cols.Add(GetConfig(App.EXTENSION));
			cols.Add(GetConfig(App.USER_NAME));
			cols.Add(GetConfig(App.HIRED));
			cols.Add(GetConfig(App.HOURS));
			IGridConfig c = GetConfig(App.EDITOR);
			c.ItemTemplate = new KeyValueTemplate(App.EDITOR, EditorKeyList);
			c.EditItemTemplate = new DropDownListTemplate(App.EDITOR, EditorKeyList);
			cols.Add(c);
			Configs = cols;
		}

		/// <summary>
		/// Provide a runtime instance of the label over the DataGrid.
		/// </summary>
		/// 
		public Label hint;

		/// <summary>
		/// Update the page index hint. 
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void this_ListPageIndexChanged(object sender, EventArgs e)
		{
			ListPageIndexChangedArgs a = e as ListPageIndexChangedArgs;
			hint.Text = ListPageIndexChanged_Message(a);
		}

		/// <summary>
		/// Handle Page Init event by obtaining the user profile 
		/// and initalizing the controls.
		/// </summary>
		/// 
		private void Page_Init()
		{
			Grid_Init(list);
			
			ListPageIndexChanged += new EventHandler(this_ListPageIndexChanged);
			add.Click += new EventHandler(list_Add);
		}

		/// <summary>
		/// Handle page's load event.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void Page_Load(object sender, EventArgs e)
		{
			Grid_Load();
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
			Page_Init();
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


}