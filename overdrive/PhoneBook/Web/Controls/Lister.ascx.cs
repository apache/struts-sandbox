using System;
using System.Collections;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Web;
using PhoneBook.Core;

namespace PhoneBook.Web.Controls
{
	/// <summary>
	/// Present matching directory entries.
	/// </summary>
	/// 
	public class Lister : ViewControl
	{
		/// <summary>
		/// Populate the DataGrid with directory entries matching the filter settings.
		/// </summary>
		/// <param name="criteria">Filter settings</param>
		/// 
		public void Open(IDictionary criteria)
		{
			IViewHelper helper = ReadExecute(App.ENTRY_LIST, criteria);
			bool ok = helper.IsNominal;
			if (!ok) Page_Alert = helper;
			else
			{
				IList result = helper.Outcome;
				list.DataSource = result;
				list.DataBind();
			}
		}

		/// <summary>
		/// Provide reference to datagrid instance.
		/// </summary>
		/// 
		protected DataGrid list;

		/// <summary>
		/// Handle page's load event.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void Page_Load(object sender, EventArgs e)
		{
			if (IsPostBack) return;
			Open(null);
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
		}

		/// <summary>
		///		Required method for Designer support - do not modify
		///		the contents of this method with the code editor.
		/// </summary>
		/// 
		private void InitializeComponent()
		{
			this.Load += new EventHandler(this.Page_Load);
		}

		#endregion
	}
}