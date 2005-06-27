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

		public HtmlGenericControl title;
		public HtmlGenericControl heading;
		protected Label profile_label;
		protected Panel error_panel;
		protected Label error_label;

		private AppUserProfile _Profile;
		protected AppUserProfile Profile
		{
			set
			{
				if (value == null)
					_Profile = NewProfile ();
				else
					_Profile = value;

			}
			get { return _Profile; }
		}

		protected AppUserProfile NewProfile ()
		{
			WindowsIdentity id = WindowsIdentity.GetCurrent ();
			AppUserProfile profile = new AppUserProfile (id);
			Session [UserProfile.USER_PROFILE] = profile;

			UserHelper.Criteria [App.USER_NAME] = profile.UserId;
			UserHelper.Execute ();
			if (UserHelper.IsNominal)
			{
				string editor = UserHelper.Criteria [App.EDITOR] as string;
				bool isEditor = ((editor != null) && (editor.Equals ("1")));
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

		private Label[] FilterLabels ()
		{
			Label[] labels = {last_name_label,first_name_label,extension_label,user_name_label,hired_label,hours_label};
			return labels;
		}

		private DropDownList[] FilterList ()
		{
			DropDownList[] lists = {last_name_list, first_name_list, extension_list, user_name_list, hired_list, hours_list};
			return lists;
		}

		private void ListAll_Click (object sender, EventArgs e)
		{
			Filter_Reset (null);
			List_Load ();
		}

		protected override void Find_Init ()
		{
			list_all_command.Click += new EventHandler (ListAll_Click);
			list_all_command.Text = GetMessage(list_all_command.ID);

			foreach (Label label in FilterLabels())
			{ 
				label.Text = GetMessage(label.ID);
			}

			foreach (DropDownList filter in FilterList ())
			{
				filter.AutoPostBack = true;
				filter.SelectedIndexChanged += new EventHandler (Find_Submit);
			}
		}

		private void Filter_Reset (DropDownList except)
		{
			int exceptIndex = 0;
			if (except != null) exceptIndex = except.SelectedIndex;
			foreach (DropDownList filter in FilterList ())
			{
				filter.SelectedIndex = 0;
			}
			if (except != null) except.SelectedIndex = exceptIndex;
			// Update other members
			List_ResetIndex ();
			Page_Prompt = GetMessage(App.DIRECTORY_PROMPT);
		}

		protected override void Find_Submit (object sender, EventArgs e)
		{
			IGridViewHelper h = GridHelper;
			DropDownList list = sender as DropDownList;
			string id = list.ID;
			int v = id.LastIndexOf (h.FindHelper.ListSuffix);
			string key = id.Substring (0, v);
			h.FindHelper.Criteria [key] = list.SelectedValue;
			List_Criteria = h.FindHelper.Criteria;
			Filter_Reset (list);
			List_Load ();
		}

		protected override void Find_Load ()
		{
			IViewHelper h = GridHelper.FindHelper;
			h.ExecuteBind (find_panel.Controls);
			bool ok = (h.IsNominal);
			if (!ok)
				Page_Error = h;
		}

		#endregion

		#region Page Events

		protected override void Page_Init ()
		{
			base.Page_Init ();
			list_panel.Visible = true;
			error_panel.Visible = false;
			Profile = Session [UserProfile.USER_PROFILE] as AppUserProfile;
			GridHelper.HasEditColumn = Profile.IsEditor;
			if (!IsPostBack)
			{
				Page_Prompt = GetMessage(App.DIRECTORY_PROMPT);
				profile_label.Text = Profile.UserId;
				// UserLocale = Profile.Locale;
			}
		}

		protected override void Page_PreRender(object sender, EventArgs e)
		{
			base.Page_PreRender(sender,e);
			title.InnerText = GetMessage(App.DIRECTORY_TITLE);
			heading.InnerText = GetMessage(App.DIRECTORY_HEADING);
		}

		#endregion

	}
}