using System;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace PhoneBook.Web
{

	public class Directory : Page
	{

		protected Panel pnlList;
		protected DataGrid repList;
		protected DropDownList lstSelect;
		protected TextBox txtInput;
		protected Button btnInput;
		protected Button btnAdd;
		
		protected void List_PageIndexChanged (object sender, DataGridPageChangedEventArgs e)
		{}
		
		private void Page_Load(object sender, System.EventArgs e)
		{
			// Put user code to initialize the page here
		}

		#region Web Form Designer generated code
		override protected void OnInit(EventArgs e)
		{
			//
			// CODEGEN: This call is required by the ASP.NET Web Form Designer.
			//
			InitializeComponent();
			base.OnInit(e);
		}
		
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{    
			this.Load += new System.EventHandler(this.Page_Load);
		}
		#endregion
	}
}
