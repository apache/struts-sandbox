using System;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;
using Nexus.Web.Controls;
using PhoneBook.Web.Controls;

namespace PhoneBook.Web.Forms
{
	/// <summary>
	///  Display a list of employees with their telephone extension [OVR-5]. 
	/// </summary>
	/// 
	public class Directory : Page
	{
		#region Messages

		public const string msg_LIST_ALL_CMD = "SHOW ALL";

		#endregion

		#region Page Properties 

		protected Panel error_panel;
		protected Label error_label;

		/// <summary>
		/// Display a list of error messages.
		/// </summary>
		public IViewHelper Page_Error
		{
			set
			{
				error_label.Text = value.ErrorsText;
				error_panel.Visible = true;
			}
		}

		private IRequestCatalog _Catalog;

		/// <summary>
		/// Provide reference ot the Catalog (object factory) for this application. 
		/// </summary>
		/// <remarks><p>
		/// Subclasses adding EventHandlers 
		/// should pass a reference to themselves with a ViewArgs instance, 
		/// encapsulating the Helper.
		/// </p></remarks>
		public virtual IRequestCatalog Catalog
		{
			get { return _Catalog; }
			set { _Catalog = value; }
		}

		#endregion

		#region Event handlers

		protected Lister lister;
		protected Finder finder;

		protected void finder_Click(object sender, EventArgs e)
		{
			ViewArgs a = e as ViewArgs;
			IViewHelper helper = a.Helper;
			lister.Open(helper.Criteria);
		}

		#endregion

		#region Page Events

		private void View_Error(object sender, EventArgs e)
		{
			ViewArgs v = e as ViewArgs;
			if (v == null) throw new ArgumentException("View_Error: !(e is ViewArgs)");
			IViewHelper helper = v.Helper;
			if (helper != null) Page_Error = helper;
			else throw new ArgumentException("View_Error: (e.helper==null)");
		}

		private void View_Init(ViewControl c)
		{
			c.View_Error += new EventHandler(View_Error);
			c.Catalog = this.Catalog; // ISSUE: Why isn't control injection working?
		}

		private void Page_Init()
		{
			View_Init(finder);
			View_Init(lister);
		}

		private void Page_Load(object sender, EventArgs e)
		{
			error_panel.Visible = false;
		}

		#endregion

		#region Web Form Designer generated code

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