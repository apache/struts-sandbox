using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus;
using Nexus.Core;
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

		public virtual int BindEditorColumn (DataGrid grid, int i)
		{
			TemplateColumn master = new TemplateColumn();
			ColumnTemplate column = new ColumnTemplate();
			master.ItemTemplate = column; 
			grid.Columns.AddAt (i, master);
			return ++i;
		}
	}

	public class ColumnTemplate : ITemplate 
	{ 
		public void InstantiateIn(Control container) 
		{ 
			Label myLabel = new Label();
			myLabel.Text="Check to delete";
			CheckBox mycheckbox = new CheckBox();
			container.Controls.Add(myLabel); 
			container.Controls.Add(mycheckbox); 
		} 
	}
}