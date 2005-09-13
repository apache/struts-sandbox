using System;
using System.Collections;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;
using Nexus.Core.Profile;
using PhoneBook.Core;

namespace PhoneBook.Web.Controls
{
	public class Lister2 : AppGridControl
	{
		protected DataGrid list;
		protected Button add;

		public void Open(IDictionary criteria)
		{
			// list_ResetIndex(); -- what about saving the criteria?
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

		private static string LABEL = "_label";

		private void SetProperties()
		{
			FindCommand = App.ENTRY_FIND;
			ListCommand = App.ENTRY_LIST;
			SaveCommand = App.ENTRY_SAVE;
			DataKeyField = App.ENTRY_KEY;
			HasItemColumn = false;
			AppUserProfile profile = Session[UserProfile.USER_PROFILE] as AppUserProfile;
			HasEditColumn = profile.IsEditor;

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

		private void Page_Init()
		{
			Grid = list;
		}

		private void Page_Load(object sender, EventArgs e)
		{
			SetProperties();
			add.Click += new EventHandler(list_Add);
			add.Text = GetMessage(add.ID);
		}

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