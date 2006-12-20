using System;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;
using Nexus.Web;
using PhoneBook.Web.Controls;

namespace PhoneBook.Web.Forms
{
	/// <summary>
	///  Display a list of employees with their telephone extension [OVR-5]. 
	/// </summary>
	/// <remarks><p>
	/// This is a starter version of the Directory page 
	/// that presents the list 
	/// without offering more advanced features.
	/// </p></remarks>
	/// 
	public class Directory : Page
	{
		#region Messages

		/// <summary>
		/// Provide a message for the List All command.
		/// </summary>
		/// 
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
				error_label.Text = value.AlertsText;
				error_panel.Visible = true;
			}
		}

		/// <summary>
		/// Provide filed for Catalog property.
		/// </summary>
		private IRequestCatalog _Catalog;

		/// <summary>
		/// Provide reference to the Catalog (object factory) for this application. 
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

		/// <summary>
		/// Present matching directory entries.
		/// </summary>
		/// 
		protected Lister lister;

		/// <summary>
		/// Capture input values to filter a list of directory entries.
		/// </summary>
		/// 
		protected Finder finder;

		/// <summary>
		/// Handle Filter Changed event by opening the Lister control 
		/// and passing through the search criteria 
		/// provided by the event arts.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime arguements</param>
		/// 
		protected void finder_FilterChanged(object sender, EventArgs e)
		{
			ViewArgs a = e as ViewArgs;
			IViewHelper helper = a.Helper;
			lister.Open(helper.Criteria);
		}

		#endregion

		#region Page Events

		/// <summary>
		/// Handle View_Error event by presenting the error message
		/// provided by the Helper class.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void View_Error(object sender, EventArgs e)
		{
			ViewArgs v = e as ViewArgs;
			if (v == null) throw new ArgumentException("View_Error: !(e is ViewArgs)");
			IViewHelper helper = v.Helper;
			if (helper != null) Page_Error = helper;
			else throw new ArgumentException("View_Error: (e.helper==null)");
		}

		/// <summary>
		/// Initialize controls by registering for View Error events 
		/// and passing through our Catalog instance.
		/// </summary>
		/// <param name="c">Control to initialize</param>
		/// 
		private void View_Init(ViewControl c)
		{
			c.View_Alert += new EventHandler(View_Error);
			c.Catalog = Catalog; // ISSUE: Why isn't control injection working?
		}

		/// <summary>
		/// Handle Page Init event by initalizing the controls.
		/// </summary>
		/// 
		private void Page_Init()
		{
			View_Init(finder);
			View_Init(lister);
		}

		/// <summary>
		/// Handle page's load event.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void Page_Load(object sender, EventArgs e)
		{
			error_panel.Visible = false;
		}

		#endregion

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
		/// 
		private void InitializeComponent()
		{
			this.Load += new EventHandler(this.Page_Load);
		}

		#endregion
	}
}