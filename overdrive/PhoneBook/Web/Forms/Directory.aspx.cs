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

		protected Panel pnlError;
		protected Label lblError;

		/// <summary>
		/// Display a list of error messages.
		/// </summary>
		public IViewHelper Page_Error
		{
			set
			{
				lblError.Text = value.ErrorsText;
				pnlError.Visible = true;
			}
		}

		private IRequestCatalog _Catalog;

		/// <summary>
		/// Helper passed by an enclosing control (e.g. Page).
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

		#region Control Events

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

		protected void Page_Init()
		{
			pnlError.Visible = false;
			View_Init(finder);
			View_Init(lister);
		}

		protected void Page_Load(object sender, EventArgs e)
		{
			// Put user code to initialize the page here
		}

		#endregion
	}
}