using System;
using System.Collections;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Web;
using PhoneBook.Core;

namespace PhoneBook.Web.Controls
{
	public class Lister : ViewControl
	{
		protected DataGrid list;

		public void Open(IDictionary criteria)
		{
			IViewHelper helper = ReadExecute(App.ENTRY_LIST, criteria);
			bool ok = helper.IsNominal;
			if (!ok) Page_Error = helper;
			else
			{
				IList result = helper.Outcome;
				list.DataSource = result;
				list.DataBind();
			}
		}

		private void Page_Load(object sender, EventArgs e)
		{
			if (IsPostBack) return;
			Open(null);
		}

		#region Web Form Designer generated code

		protected override void OnInit(EventArgs e)
		{
			//
			// CODEGEN: This call is required by the ASP.NET Web Form Designer.
			//
			InitializeComponent();
			base.OnInit(e);
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