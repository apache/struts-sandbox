using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Tables;
using Nexus.Web;
using PhoneBook.Core;

namespace PhoneBook.Web
{
	public class AppGridHelper : GridViewHelper
	{
		public override IEntryList NewEntryList ()
		{
			return new AppEntryList ();
		}

		private bool _HasEditColumn = true;
		public override bool HasEditColumn
		{
			get { return _HasEditColumn; }
			set { _HasEditColumn = value; }
		}

		public override int BindColumns (DataGrid grid, int i)
		{
			grid.DataKeyField = DataKeyField;
			int colCount = FieldSet.Count;
			for (int c = 0; c < colCount; c++)
			{
				IFieldContext fc = FieldSet [c] as IFieldContext;
				string column = fc.ID;
				string label = fc.Label;
				if ((label==null) || (label.Length==0)) label = column;
				
				if (fc.ControlTypeName.Equals ("CheckBox"))
					i = BindTemplateColumn (grid, GetCheckBoxColumn(), i, label);
				else 
					i = MyBindColumn (grid, i, label, column);
			}
			return i;
		}

		protected int MyBindColumn (DataGrid grid, int pos, string headerText, string dataField)
		{
			BoundColumn column = new BoundColumn ();
			column.DataField = dataField;
			column.HeaderText = headerText;
			grid.Columns.AddAt (pos, column);
			return pos + 1;
		}

		protected int BindTemplateColumn(DataGrid grid, DataGridColumn column, int pos, string headerText)
		{
				column.HeaderText = headerText;	
				grid.Columns.AddAt (pos, column);
				return pos + 1;
		}

		public TemplateColumn GetCheckBoxColumn()
		{
			TemplateColumn tm = new TemplateColumn();
			tm.ItemTemplate = new CheckBoxTemplate();
			return tm;
		}

	}

	public class CheckBoxTemplate : ITemplate 
	{ 
		public void InstantiateIn(Control container) 
		{ 
			container.Controls.Add(new CheckBox()); 
		} 
	}	
}