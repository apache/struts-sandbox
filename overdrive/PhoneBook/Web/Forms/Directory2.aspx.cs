using System;
using System.Security.Principal;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;
using Nexus.Core.Profile;
using Nexus.Web;
using PhoneBook.Core;
using PhoneBook.Web.Controls;
using Spring.Web.UI;
using WQD.Core.Controls;

namespace PhoneBook.Web.Forms
{
	/// <summary>
	///  Maintain a list of employees with their telephone extension [OVR-5]. 
	/// </summary>
	/// <remarks><p>
	/// This version of the directory page supports paging and editing 
	/// through use of the Nexus GridControl.
	/// </p></remarks>
	/// 
	public class Directory2 : Page
	{
		#region Base Page members

		/// <summary>
		/// Provide field for AppUserProfile property.
		/// </summary>
		/// 
		private AppUserProfile _Profile;

		/// <summary>
		/// Expose the user's profile. 
		/// </summary>
		/// 
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
		/// 
		protected AppUserProfile NewProfile()
		{
			WindowsIdentity id = WindowsIdentity.GetCurrent();
			AppUserProfile profile = new AppUserProfile(id);
			Session[UserProfile.USER_PROFILE] = profile;

			IViewHelper helper = Catalog.GetHelperFor(App.ENTRY);
			helper.Criteria[App.USER_NAME] = profile.UserId;
			helper.Execute();
			if (helper.IsNominal)
			{
				string editor = helper.Criteria[App.EDITOR] as string;
				// ISSUE: Need constant for "1" (true)
				bool isEditor = ((editor != null) && (editor.Equals("1")));
				profile.IsEditor = isEditor;
				if (editor != null)
				{
					AppEntry entry = new AppEntry();
					entry.AddAll(helper.Criteria);
					profile.Entry = entry;
				}
			}
			return profile;
		}

		/// <summary>
		/// Present a list of error messages.
		/// </summary>
		/// 
		protected IViewHelper Page_Error
		{
			set
			{
				error_label.Text = value.AlertsText;
				error_panel.Visible = true;
			}
		}

		protected Panel prompt_panel;
		protected Label prompt_label;

		/// <summary>
		/// Display a Prompt message.
		/// </summary>
		/// 
		protected string Page_Prompt
		{
			set { prompt_label.Text = value; }
		}

		/// <summary>
		/// Provide a field for Catalog property.
		/// </summary>
		/// 
		private IRequestCatalog _Catalog;

		/// <summary>
		/// Expose the Catalog (object factory) for this application. 
		/// </summary>
		/// <remarks><p>
		/// Subclasses adding EventHandlers 
		/// should pass a reference to themselves with a ViewArgs instance, 
		/// encapsulating the Helper.
		/// </p></remarks>
		/// 
		public virtual IRequestCatalog Catalog
		{
			get { return _Catalog; }
			set { _Catalog = value; }
		}

		/// <summary>
		/// Handle View Error
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void View_Error(object sender, EventArgs e)
		{
			ViewArgs v = e as ViewArgs;
			if (v == null) throw new ArgumentException("View_Error: !(e is ViewArgs)");
			IViewHelper helper = v.Helper;
			if (helper != null) Page_Error = helper;
			else throw new ArgumentException("View_Error: (e.helper==null)");
		}

		/// <summary>
		/// Initialize User Controls by handling View Error events 
		/// and passing through our Catalog reference.
		/// </summary>
		/// <param name="c">Control to initialize</param>
		/// 
		private void View_Init(ViewControl c)
		{
			c.View_Alert += new EventHandler(View_Error);
			c.Catalog = Catalog; // ISSUE: Why isn't control injection working?
		}

		private void Page_PreRender(object sender, EventArgs e)
		{
			greeting.Text = GetMessage(greeting.ID);
			title.InnerText = GetMessage(App.DIRECTORY_TITLE);
			heading.InnerText = GetMessage(App.DIRECTORY_HEADING);
		}

		#endregion

		#region Event handlers

		/// <summary>
		/// Filter Lister for Directory
		/// </summary>
		/// 
		protected InitialFilter letter_filter;

		/// <summary>
		/// Apply letter filter to WNE Facilty List.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime arguments</param>
		/// 
		private void letter_filter_View_Filter(object sender, EventArgs e)
		{
			FindArgs a = e as FindArgs;
			lister.Read(a.Criteria);
			lister.Reset();
		}

		/// <summary>
		/// List matching directory entries.
		/// </summary>
		/// 
		protected Lister2 lister;

		/// <summary>
		/// Capture input values to filter a list of directory entries.
		/// </summary>
		/// 
		protected Finder2 finder;

		/// <summary>
		/// Handle Filter Changed event by opening the Lister control 
		/// and passing through the search criteria 
		/// provided by the event args, 
		/// so that the Lister control can present the matching entities.
		/// </summary>
		/// <remarks>
		/// </remarks>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime arguements</param>
		/// 
		protected void finder_Filter_Changed(object sender, EventArgs e)
		{
			ViewArgs a = e as ViewArgs;
			IViewHelper helper = a.Helper;
			lister.Reset(helper.Criteria); // Runs the list command with new criteria
		}

		#endregion

		#region Page Properties 

		protected HtmlGenericControl title;
		protected HtmlGenericControl heading;
		protected Label greeting;
		protected Label profile_label;
		protected Panel error_panel;
		protected Label error_label;

		#endregion

		#region Page Events

		/// <summary>
		/// Handle Page Init event by obtaining the user profile 
		/// and initalizing the controls.
		/// </summary>
		/// 
		private void Page_Init()
		{
			Profile = Session[UserProfile.USER_PROFILE] as AppUserProfile;
			PreRender += new EventHandler(Page_PreRender);

			View_Init(finder);

			View_Init(letter_filter);
			letter_filter.View_Filter += new EventHandler(letter_filter_View_Filter);
			IViewHelper helper = Catalog.GetHelperFor(App.ENTRY_INITIAL);

			helper.Execute();
			letter_filter.Open(helper.Outcome);

			View_Init(lister);
			finder.Filter_Changed += new EventHandler(finder_Filter_Changed);
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
			if (!IsPostBack)
			{
				Page_Prompt = GetMessage(App.DIRECTORY_PROMPT);
				string name = Profile.FullName;
				if (name == null)
					profile_label.Text = Profile.UserId;
				else
					profile_label.Text = name;
				// UserLocale = Profile.Locale;
				finder.Open();
			}
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