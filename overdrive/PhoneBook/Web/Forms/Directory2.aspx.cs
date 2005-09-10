using System;
using System.Security.Principal;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Core.Profile;
using Nexus.Web;
using PhoneBook.Core;

namespace PhoneBook.Web.Forms
{
	/// <summary>
	///  Maintain a list of employees with their telephone extension [OVR-5]. 
	/// </summary>
	/// 
	public class Directory2 : BaseGridPage
	{
		#region Helpers 

		private IViewHelper _UserHelper;

		/// <summary>
		/// Obtain entry for a user.
		/// </summary>
		///
		public virtual IViewHelper UserHelper
		{
			get { return _UserHelper; }
			set { _UserHelper = value; }
		}

		#endregion

		#region Page Properties 

		protected HtmlGenericControl title;
		protected HtmlGenericControl heading;
		protected Label greeting;
		protected Label profile_label;
		protected Panel error_panel;
		protected Label error_label;

		private AppUserProfile _Profile;

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

		protected AppUserProfile NewProfile()
		{
			WindowsIdentity id = WindowsIdentity.GetCurrent();
			AppUserProfile profile = new AppUserProfile(id);
			Session[UserProfile.USER_PROFILE] = profile;

			UserHelper.Criteria[App.USER_NAME] = profile.UserId;
			UserHelper.Execute();
			if (UserHelper.IsNominal)
			{
				string editor = UserHelper.Criteria[App.EDITOR] as string;
				bool isEditor = ((editor != null) && (editor.Equals("1")));
				profile.IsEditor = isEditor;
			}

			return profile;
		}

		/// <summary>
		/// Display a list of error mesasges.
		/// </summary>
		protected override IViewHelper Page_Error
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
		/// Display a Prompt mesasges.
		/// </summary>
		protected override string Page_Prompt
		{
			set { prompt_label.Text = value; }
		}

		#endregion

		#region Find -- Display Find controls

		protected Panel find_panel;
		public Label last_name_label;
		public Label first_name_label;
		public Label extension_label;
		public Label user_name_label;
		public Label hired_label;
		public Label hours_label;
		protected DropDownList last_name_list;
		protected DropDownList first_name_list;
		protected DropDownList extension_list;
		protected DropDownList user_name_list;
		protected DropDownList hired_list;
		protected DropDownList hours_list;
		// TODO: protected DropDownList editor_list;
		protected Button list_all_command;

		// pageload events - These methods populate controls to display

		private Label[] FilterLabels()
		{
			Label[] labels = {last_name_label, first_name_label, extension_label, user_name_label, hired_label, hours_label};
			return labels;
		}

		private DropDownList[] FilterList()
		{
			DropDownList[] lists = {last_name_list, first_name_list, extension_list, user_name_list, hired_list, hours_list};
			return lists;
		}

		private void ListAll_Click(object sender, EventArgs e)
		{
			Filter_Reset(null);
			List_Load();
		}

		protected override void Find_Init()
		{
			base.Find_Init();
			list_all_command.Click += new EventHandler(ListAll_Click);
			list_all_command.Text = GetMessage(list_all_command.ID);

			foreach (Label label in FilterLabels())
			{
				label.Text = GetMessage(label.ID);
			}

			foreach (DropDownList filter in FilterList())
			{
				filter.AutoPostBack = true;
				filter.SelectedIndexChanged += new EventHandler(Find_Submit);
			}
		}

		private string GetRootID(string id)
		{
			int v = id.LastIndexOf(GridHelper.FindHelper.ListSuffix);
			string key = id.Substring(0, v);
			return key;
		}

		private void Filter_Reset(DropDownList except)
		{
			// Reset filter controls
			int exceptIndex = 0;
			if (except != null) exceptIndex = except.SelectedIndex;
			foreach (DropDownList filter in FilterList())
			{
				filter.SelectedIndex = 0;
			}
			if (except != null) except.SelectedIndex = exceptIndex;
			// Tell everyone that we are starting over
			Page_Prompt = GetMessage(App.DIRECTORY_PROMPT);
			List_ResetIndex();
		}

		protected override void Find_Submit(object sender, EventArgs e)
		{
			// Don't call base: base.Find_Submit (); 
			DropDownList list = sender as DropDownList;
			Filter_Reset(list);
			string key = GetRootID(list.ID);
			IGridViewHelper h = GridHelper;
			h.FindHelper.Criteria.Clear();
			h.FindHelper.Criteria[key] = list.SelectedValue;
			List_Criteria = GridHelper.FindHelper.Criteria;
			List_Load();
		}

		protected override void Find_Load()
		{
			base.Find_Load();
			IViewHelper h = GridHelper.FindHelper;
			h.ExecuteBind(find_panel.Controls);
			bool ok = (h.IsNominal);
			if (!ok)
				Page_Error = h;
		}

		#endregion

		#region Page Events

		protected override void Page_Init()
		{
			base.Page_Init();
			list_panel.Visible = true; // base behavior hides
			Profile = Session[UserProfile.USER_PROFILE] as AppUserProfile;
			GridHelper.HasEditColumn = Profile.IsEditor;
			GridHelper.FindHelper.Profile = Profile;
			GridHelper.ListHelper.Profile = Profile;
			GridHelper.SaveHelper.Profile = Profile;
			if (!IsPostBack)
			{
				Page_Prompt = GetMessage(App.DIRECTORY_PROMPT);
				profile_label.Text = Profile.UserId;
				// UserLocale = Profile.Locale;
			}
		}

		protected override void Page_Load(object sender, EventArgs e)
		{
			base.Page_Load(sender, e);
			error_panel.Visible = false;
		}

		protected override void Page_PreRender(object sender, EventArgs e)
		{
			base.Page_PreRender(sender, e);
			greeting.Text = GetMessage(greeting.ID);
			title.InnerText = GetMessage(App.DIRECTORY_TITLE);
			heading.InnerText = GetMessage(App.DIRECTORY_HEADING);
		}

		#endregion
	}
}