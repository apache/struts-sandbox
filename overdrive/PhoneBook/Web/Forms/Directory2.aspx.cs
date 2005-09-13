using System;
using System.Security.Principal;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;
using Nexus.Core.Profile;
using Nexus.Web.Controls;
using PhoneBook.Core;
using PhoneBook.Web.Controls;
using Spring.Web.UI;

namespace PhoneBook.Web.Forms
{
	/// <summary>
	///  Maintain a list of employees with their telephone extension [OVR-5]. 
	/// </summary>
	/// 
	public class Directory2 : Page
	{
		#region Page Properties 

		protected HtmlGenericControl title;
		protected HtmlGenericControl heading;
		protected Label greeting;
		protected Label profile_label;
		protected Panel error_panel;
		protected Label error_label;

		private AppUserProfile _Profile;

		/// <summary>
		///  Obtain a profile for a user.
		/// </summary>
		protected AppUserProfile Profile
		{
			set
			{
				if (value == null)
					_Profile = NewProfile();
				else
					_Profile = value;

			}
			get { return _Profile; }
		}

		/// <summary>
		/// Create or retrieve an AppUserProfile 
		/// based on the client's WindowsIdentity.
		/// </summary>
		/// <returns>A new or prexisting AppUserProfile</returns>
		protected AppUserProfile NewProfile()
		{
			WindowsIdentity id = WindowsIdentity.GetCurrent();
			AppUserProfile profile = new AppUserProfile(id);
			Session[UserProfile.USER_PROFILE] = profile;

			IViewHelper helper = Catalog.GetHelperFor(App.ENTRY_LIST);
			helper.Criteria[App.USER_NAME] = profile.UserId;
			helper.Execute();
			if (helper.IsNominal)
			{
				string editor = helper.Criteria[App.EDITOR] as string;
				// ISSUE: Need constant for "1" (true)
				bool isEditor = ((editor != null) && (editor.Equals("1")));
				profile.IsEditor = isEditor;
			}

			return profile;
		}

		/// <summary>
		/// Display a list of error messagess.
		/// </summary>
		protected IViewHelper Page_Error
		{
			set
			{
				error_label.Text = value.ErrorsText;
				error_panel.Visible = true;
			}
		}

		protected Panel prompt_panel;
		protected Label prompt_label;

		/// <summary>
		/// Display a Prompt message.
		/// </summary>
		protected string Page_Prompt
		{
			set { prompt_label.Text = value; }
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

		protected Lister2 lister;
		protected Finder2 finder;

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
			Profile = Session[UserProfile.USER_PROFILE] as AppUserProfile;
			View_Init(finder);
			View_Init(lister);
		}

		private void Page_Load(object sender, EventArgs e)
		{
			error_panel.Visible = false;
			if (!IsPostBack)
			{
				Page_Prompt = GetMessage(App.DIRECTORY_PROMPT);
				profile_label.Text = Profile.UserId;
				// UserLocale = Profile.Locale;
				finder.Open();
			}
		}

		private void Page_PreRender(object sender, EventArgs e)
		{
			greeting.Text = GetMessage(greeting.ID);
			title.InnerText = GetMessage(App.DIRECTORY_TITLE);
			heading.InnerText = GetMessage(App.DIRECTORY_HEADING);
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
			this.PreRender += new EventHandler(this.Page_PreRender);
		}

		#endregion
	}
}