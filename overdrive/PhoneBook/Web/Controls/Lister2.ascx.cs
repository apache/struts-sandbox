using System;
using System.Collections;
using System.Web.UI.WebControls;
using Nexus.Core.Profile;
using PhoneBook.Core;

namespace PhoneBook.Web.Controls
{
	/// <summary>
	/// Present matching directory entries.
	/// </summary>
	public class Lister2 : AppGridControl
	{

		/// <summary>
		/// Provide instance of DataGrid control
		/// </summary>
		/// 
		protected DataGrid list;

		/// <summary>
		/// Provide instance of Add button.
		/// </summary>
		/// 
		protected Button add;

		/// <summary>
		/// ID Token to indicate a Label control.
		/// </summary>
		/// 
		private static string LABEL = "_label";

		/// <summary>
		/// Complete loading Grid 
		/// after other members have initialized.
		/// </summary>
		/// 
		private void Grid_Load()
		{
			AppUserProfile profile = Session[UserProfile.USER_PROFILE] as AppUserProfile;
			HasEditColumn = profile.IsEditor;
		}

		/// <summary>
		/// Initialize our Grid instance 
		/// by setting the columns, labels, 
		/// and other dynamic attributes.
		/// </summary>
		/// 
		private void Grid_Init()
		{
			FindCommand = App.ENTRY_FIND;
			ListCommand = App.ENTRY_LIST;
			SaveCommand = App.ENTRY_SAVE;
			DataKeyField = App.ENTRY_KEY;
			AllowCustomPaging = true;

			IList f = new ArrayList(7);
			f.Add(App.LAST_NAME);
			f.Add(App.FIRST_NAME);
			f.Add(App.EXTENSION);
			f.Add(App.USER_NAME);
			f.Add(App.HIRED);
			f.Add(App.HOURS);
			f.Add(App.EDITOR);
			DataFields = f;

			IList k = new ArrayList(7);
			k.Add(GetMessage(App.LAST_NAME + LABEL));
			k.Add(GetMessage(App.FIRST_NAME + LABEL));
			k.Add(GetMessage(App.EXTENSION + LABEL));
			k.Add(GetMessage(App.USER_NAME + LABEL));
			k.Add(GetMessage(App.HIRED + LABEL));
			k.Add(GetMessage(App.HOURS + LABEL));
			k.Add(GetMessage(App.EDITOR + LABEL));
			DataLabels = k;
		}

		/// <summary>
		/// Handle Page Init event by obtaining the user profile 
		/// and initalizing the controls.
		/// </summary>
		/// 
		private void Page_Init()
		{
			Grid = list;
			Grid_Init();
		}

		/// <summary>
		/// Handle page's load event.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void Page_Load(object sender, EventArgs e)
		{
			add.Click += new EventHandler(add_Click);
			add.Text = GetMessage(add.ID);
			Grid_Load();
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