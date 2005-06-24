using System;
using System.Collections;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;
using Nexus.Core.Tables;
using Nexus.Web;
using Nexus.Web.Helpers;

namespace Nexus
{
	/// <summary>
	/// Summary description for GridViewHelper.
	/// </summary>
	public abstract class GridViewHelper : WebViewHelper, IGridViewHelper
	{

		#region IListViewHelper

		public const string msg_EDIT_TEXT = "EDIT";
		public const string msg_QUIT_TEXT = "CANCEL";
		public const string msg_SAVE_TEXT = "SAVE";
		public const string msg_ITEM_TEXT = "#";
		public const string msg_ITEM_COMMAND = "cmdItem";

		private IViewHelper _SaveHelper;
		public virtual IViewHelper SaveHelper
		{
			get { return _SaveHelper; }
			set { _SaveHelper = value; }
		}

		private IViewHelper _FindHelper;
		public virtual IViewHelper FindHelper
		{
			get { return _FindHelper; }
			set { _FindHelper = value; }
		}

		private IViewHelper _ListHelper;
		public virtual IViewHelper ListHelper
		{
			get { return _ListHelper; }
			set { _ListHelper = value; }
		}

		private bool _HasItemColumn = false;
		public virtual bool HasItemColumn
		{
			get { return _HasItemColumn; }
			set { _HasItemColumn = value; }
		}

		public virtual bool HasEditColumn
		{
			get { return (SaveHelper != null); }
			set { throw new NotImplementedException (); }
		}

		private bool _HasCriteria = true;
		public virtual bool HasCriteria
		{
			get { return _HasCriteria; }
			set { _HasCriteria = value; }
		}

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

		private string _ItemCommandName = msg_ITEM_COMMAND;
		public virtual string ItemCommandName
		{
			get { return _ItemCommandName as string; }
			set { _ItemCommandName = value; }
		}

		private string _DataKeyField;
		public virtual string DataKeyField
		{
			get { return _DataKeyField; }
			set { _DataKeyField = value; }
		}

		#endregion 

		#region IGridViewHelper 

		public virtual bool Load (DataGrid grid, IDictionary criteria)
		{
			if (HasCriteria)
				ExecuteList (grid, criteria);
			else
				ExecuteList (grid);
			return IsNominal;
		}

		public virtual bool Find (ICollection controls)
		{
			FindHelper.Execute ();
			FindHelper.Bind(controls);
			return FindHelper.IsNominal ;
		}

		public virtual bool List (ICollection controls)
		{
			ListHelper.ReadExecute (controls);
			return ListHelper.IsNominal ;
		}

		public virtual bool List (DataGrid grid)
		{
			ListHelper.Execute();
			bool okay = IsNominal ;
			if (okay)
			{
				DataSource (grid);
				DataBind (grid);
			}
			return okay;
		}

		public virtual bool Save (string key, ICollection controls)
		{
			if (SaveHelper.IsNominal)
			{
				SaveHelper.Criteria [DataKeyField] = key;
				int cols = FieldSet.Count;
				string[] keys = new string[2 + cols];
				// reconstruct the standard edit column keys
				// just as placeholders, really
				keys [0] = SaveText;
				keys [1] = QuitText;
				int index = 2;
				// append our field names to the array of keys
				for (int i = 0; i < cols; i++)
				{
					IFieldContext fc = FieldSet[i] as IFieldContext;	
					keys [index++] = fc.ID;
				}

				ReadGridControls (controls, SaveHelper.Criteria, keys, SaveHelper.NullIfEmpty);
			}
			return IsNominal ;
		}

		public virtual int BindItemColumn (DataGrid grid, int i)
		{
			ButtonColumn column = new ButtonColumn ();
			column.ButtonType = ButtonColumnType.PushButton;
			column.Text = ItemText;
			column.CommandName = ItemCommandName;
			grid.Columns.AddAt (i, column);
			return ++i;
		}

		public virtual int BindEditColumn (DataGrid grid, int i)
		{
			EditCommandColumn column = new EditCommandColumn ();
			column.ButtonType = ButtonColumnType.PushButton;
			column.EditText = EditText;
			column.CancelText = QuitText;
			column.UpdateText = SaveText;
			grid.Columns.AddAt (i, column);
			return ++i;
		}

		public virtual int BindColumns (DataGrid grid, int i)
		{
			grid.DataKeyField = DataKeyField;
			int colCount = FieldSet.Count;
			for (int c = 0; c < colCount; c++)
			{
				IFieldContext fc = FieldSet [c] as IFieldContext;
				string column = fc.ID;
				string label = fc.Label;
				if ((label==null) || (label.Length==0)) label = column;
				i = BindColumn (grid, i, label, column);
			}
			return i;
		}

		public virtual void DataSource (DataGrid grid)
		{
			IList list = ListHelper.Outcome;
			grid.DataSource = list;
		}

		public abstract IEntryList NewEntryList ();

		public virtual bool DataInsert (DataGrid grid)
		{
			// Fake a blank row
			IEntryList list = NewEntryList ();
			list.Insert (String.Empty);
			ListHelper.Criteria [ListHelper.Command.ID] = list;
			grid.DataSource = list;
			grid.CurrentPageIndex = 0;
			grid.EditItemIndex = 0;
			DataBind (grid);
			return IsNominal ;
		}

		public virtual void DataBind (DataGrid grid)
		{
			grid.DataBind ();
		}

		private bool bind = true;

		public virtual void BindGrid (DataGrid grid)
		{
			// Only bind columns once
			// WARNING: Won't work with a singleton
			if (bind)
			{
				bind = false;
				int i = 0;
				if (HasEditColumn) i = BindEditColumn (grid, i);
				if (HasItemColumn) i = BindItemColumn (grid, i);
				BindColumns (grid, i);

			}
			DataSource (grid);
			DataBind (grid);
		}

		public virtual bool ExecuteList (DataGrid grid)
		{
			ListHelper.Execute(); 
			bool okay = ListHelper.IsNominal ;
			if (okay) BindGrid (grid);
			return okay;
		}

		public virtual bool ExecuteList (DataGrid grid, IDictionary criteria)
		{
			ListHelper.ReadExecute(criteria);
			bool okay = ListHelper.IsNominal ;
			if (okay) BindGrid (grid);
			return okay;
		}

		public string GetDataKey (DataGridCommandEventArgs e, DataGrid grid)
		{
			int index = grid.EditItemIndex;
			string key = grid.DataKeys [index] as string;
			return key;
		}

		public ICollection GetControls (DataGridCommandEventArgs e, DataGrid grid)
		{
			ControlCollection controls = new ControlCollection (grid);
			foreach (TableCell t in e.Item.Cells)
			{
				for (int i = 0; i < t.Controls.Count; i++)
					controls.Add (t.Controls [i]);
			}
			return controls;
		}

		#endregion

	}
}
