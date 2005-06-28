using System;
using System.Collections;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;
using Nexus.Core.Tables;
using Nexus.Web;
using Nexus.Web.Helpers;
using Spring.Context;

namespace Nexus
{
	/// <summary>
	/// Summary description for GridViewHelper.
	/// </summary>
	public abstract class GridViewHelper : WebViewHelper, IGridViewHelper
	{

		#region IViewHelper 

		/// <remarks><p>
		/// Check to see if the Helpers are all good; 
		/// though, most often an individual Helper is checked instead. 
		/// </p></remarks>
		public override bool IsNominal
		{
			get { return FindHelper.IsNominal && ListHelper.IsNominal && SaveHelper.IsNominal;  }
		}

		#endregion

		#region IListViewHelper


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

		private string _DataKeyField;
		public virtual string DataKeyField
		{
			get { return _DataKeyField; }
			set { _DataKeyField = value; }
		}

		#region text properties 

		private IMessageSource _MessageSource;
		/// <summary>
		/// Identify the message source for this FieldContext.
		/// </summary>
		/// <exception cref="System.InvalidOperationException">
		/// If the context has not been initialized yet.
		/// </exception>
		public IMessageSource MessageSource
		{
			get{ return _MessageSource; }
			set{ _MessageSource = value; }
		}

		/// <summary>
		/// Resolve the message.
		/// </summary>
		/// <param name="name">The name of the resource to get.</param>
		/// <returns>
		/// The resolved message if the lookup was successful. Otherwise, it either throws
		/// an exception or returns the resource name, depending on the implementation.
		/// </returns>
		private string GetMessage(string name)
		{
			return MessageSource.GetMessage(name);
		}

		public virtual string EditText
		{
			get { return GetMessage(Tokens.ENTRY_EDIT_COMMAND); }
			set { throw new NotSupportedException(); }
		}

		public virtual string QuitText
		{
			get { return GetMessage(Tokens.ENTRY_QUIT_COMMAND); }
			set { throw new NotSupportedException(); }
		}

		public virtual string SaveText
		{
			get { return GetMessage(Tokens.ENTRY_SAVE_COMMAND); }
			set { throw new NotSupportedException(); }
		}

		public virtual string ItemText
		{
			get { return GetMessage(Tokens.ENTRY_ITEM_COMMAND); }
			set { throw new NotSupportedException(); }
		}

		public virtual string ItemCommandName
		{
			get { return GetMessage(Tokens.ENTRY_ITEM_COMMAND_NAME); }
			set { throw new NotSupportedException(); }
		}

		#endregion 

		#endregion 

		#region IGridViewHelper 

		public virtual bool Load (DataGrid grid, IDictionary criteria)
		{
			if (HasCriteria)
				ExecuteList (grid, criteria);
			else
				ExecuteList (grid);
			return ListHelper.IsNominal;
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
			bool okay = ListHelper.IsNominal ;
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
				SaveHelper.Execute();
			}
			return SaveHelper.IsNominal ;
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
			return ListHelper.IsNominal ;
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
			IDictionary target = ListHelper.Criteria;
			foreach (DictionaryEntry e in criteria)
			{
				target[e.Key] = e.Value;
			}
			return ExecuteList(grid);
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
