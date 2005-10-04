using System;
using System.Collections;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;
using Nexus.Core.Profile;
using UserControl = Spring.Web.UI.UserControl;

namespace Nexus.Web
{
	/// <summary>
	/// Base class for view controls (sub forms).
	/// </summary>
	public class ViewControl : UserControl, IViewControl
	{
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

		/// <summary>
		/// Psuedo property to generate an Error event encapsulating 
		/// our Helper property, containing the error.
		/// </summary>
		protected IViewHelper Page_Error
		{
			set { View_Error_Send(this, new ViewArgs(value)); }
		}

		#region String utilities 

		protected string NullOnEmpty(string input)
		{
			return (string.Empty.Equals(input)) ? null : input;
		}

		/// <summary>
		/// Extract the root name from the id, allowing for a prefix and suffix.
		/// </summary>
		/// <param name="id">The full id, including prefix and suffix.</param>
		/// <param name="prefix">The prefix to omit.</param>
		/// <param name="suffix">The suffix to omit.</param>
		/// <returns></returns>
		private string RootId(string id, string prefix, string suffix)
		{
			int v = id.LastIndexOf(suffix);
			string fore = id.Substring(0, v);
			string root = ToColumn(fore, prefix);
			return root;
		}

		/// <summary>
		/// Trim Sany QL wildcards that may have been added to a search string.
		/// </summary>
		/// <param name="input">String to trim</param>
		/// <returns>Input without SQL wildcards</returns>
		protected string TrimWildCards(string input)
		{
			string trimmed = null;
			if (input != null) trimmed = input.Trim('%');
			return trimmed;
		}

		private static KeyValue _NullKey = new KeyValue(String.Empty, "--v--");

		/// <summary>
		/// Default value for dropdown lists. 
		/// </summary>
		protected KeyValue NullKey
		{
			get { return _NullKey; }
		}

		/// <summary>
		/// The default list suffix.
		/// </summary>
		private string _ListSuffix = "_list";

		protected string ListSuffix
		{
			get { return _ListSuffix; }
			set { _ListSuffix = value; }
		}

		#endregion

		#region Control utilities

		/// <summary>
		/// Return true if control is a button.
		/// </summary>
		/// <param name="control">Control to test.</param>
		/// <returns>True if control is a Button</returns>
		/// 
		protected bool IsButton(Control control)
		{
			return (typeof (Button).Equals(control.GetType()));
		}

		/// <summary>
		/// Return true if control is a Checkbox.
		/// </summary>
		/// <param name="control">Control to test.</param>
		/// <returns>True if control is a Checkbox</returns>
		/// 
		protected bool IsCheckBox(Control control)
		{
			return (typeof (CheckBox).Equals(control.GetType()));
		}

		/// <summary>
		/// Return true if control is a HyperLink.
		/// </summary>
		/// <param name="control">Control to test.</param>
		/// <returns>True if control is a TextBox</returns>
		/// 
		protected bool IsHyperLink(Control control)
		{
			return (typeof (HyperLink).Equals(control.GetType()));
		}

		/// <summary>
		/// Return true if control is a Label.
		/// </summary>
		/// <param name="control">Control to test.</param>
		/// <returns>True if control is a Label</returns>
		/// 
		protected bool IsLabel(Control control)
		{
			return (typeof (Label).Equals(control.GetType()));
		}

		/// <summary>
		/// Return true if control is a List Control or one of the standard subclasses.
		/// </summary>
		/// <param name="control">Control to test.</param>
		/// <returns>True if control is a TextBox</returns>
		/// 
		protected bool IsListControl(Control control)
		{
			bool isList = false;
			Type type = control.GetType();
			isList = (isList) || typeof (ListControl).Equals(type);
			isList = (isList) || typeof (CheckBoxList).Equals(type);
			isList = (isList) || typeof (DropDownList).Equals(type);
			isList = (isList) || typeof (ListBox).Equals(type);
			isList = (isList) || typeof (RadioButtonList).Equals(type);
			return isList;
		}

		/// <summary>
		/// Return true if control is a RadioButton.
		/// </summary>
		/// <param name="control">Control to test.</param>
		/// <returns>True if control is a RadioButton</returns>
		/// 
		protected bool IsRadioButton(Control control)
		{
			return (typeof (RadioButton).Equals(control.GetType()));
		}

		/// <summary>
		/// Return true if control is a TextBox.
		/// </summary>
		/// <param name="control">Control to test.</param>
		/// <returns>True if control is a TextBox</returns>
		/// 
		protected bool IsTextBox(Control control)
		{
			return (typeof (TextBox).Equals(control.GetType()));
		}

		/// <summary>
		/// Select only those items in control 
		/// whose Text property matches the given text.
		/// If the value is null, no action is taken.
		/// </summary>
		/// <param name="control">Control to set</param>
		/// <param name="text">Text to match</param>
		/// 
		protected void SelectItemText(ListControl control, string text)
		{
			if (text != null)
			{
				foreach (ListItem i in control.Items)
					i.Selected = false;

				foreach (ListItem i in control.Items)
				{
					if (text.Equals(i.Text))
						i.Selected = true;
				}
			}
		}

		#endregion 

		#region IViewControl methods

		public void ResetControls()
		{
			ControlCollection controls = this.Controls;
			foreach (Control control in controls)
			{
				if (IsTextBox(control))
				{
					TextBox x = (TextBox) control;
					x.Text = String.Empty;
				}
				if (IsListControl(control))
				{
					ListControl x = (ListControl) control;
					x.SelectedIndex = -1;
				}
			}
		}

		private IProfile _Profile; 

		public IProfile Profile
		{
			get { return _Profile; }
			set { _Profile = value; }
		}

		public IViewHelper GetHelperFor(string command)
		{
			IViewHelper helper = Catalog.GetHelperFor(command);
			helper.Profile = Profile;
			return helper;
		}

		public IViewHelper Execute(string command)
		{
			IViewHelper helper = GetHelperFor(command);
			helper.Execute();
			return helper;
		}

		private void BindControls(ControlCollection controls, IDictionary dictionary, string prefix, string list_suffix)
		{
			foreach (Control t in controls)
			{
				if (IsTextBox(t))
				{
					TextBox x = (TextBox) t;
					object v = dictionary[ToColumn(x.ID, prefix)];
					if (v != null) x.Text = v.ToString();
				}
				if (IsLabel(t))
				{
					Label x = (Label) t;
					object v = dictionary[ToColumn(x.ID, prefix)];
					if (v != null) x.Text = v.ToString();
				}
				if (IsListControl(t))
				{
					ListControl x = (ListControl) t;
					string root = RootId(x.ID, prefix, list_suffix);
					IList s = dictionary[root + list_suffix] as IList; // this_key_list
					string r = dictionary[root] as string; // this_key
					if ((null == r) || (0 == r.Length))
						BindListControl(x, s);
					else
						BindListControl(x, s, r);
				}
			}
		}

		public void Bind(ControlCollection controls, IDictionary dictionary)
		{
			BindControls(controls, dictionary, null, ListSuffix);
		}

		public void Bind(IDictionary dictionary)
		{
			BindControls(this.Controls, dictionary, null, ListSuffix);
		}

		public void ExecuteBind(ControlCollection controls, IViewHelper helper)
		{
			helper.Execute();
			Bind(controls, helper.Criteria);
		}

		public IViewHelper ExecuteBind(ControlCollection controls, string command)
		{
			IViewHelper helper = GetHelperFor(command);
			ExecuteBind(controls, helper);
			return helper;
		}

		public void ExecuteBind(IViewHelper helper)
		{
			ExecuteBind(this.Controls, helper);
		}

		public IViewHelper ExecuteBind(string command)
		{
			IViewHelper helper = GetHelperFor(command);
			ExecuteBind(helper);
			return helper;
		}

		/// <summary>
		/// Render a control id as a column name 
		/// by trimming a prefix from the id, if any.
		/// </summary>
		/// <param name="id">String to process.</param>
		/// <param name="prefix">Prefix to remove.</param>
		/// <returns>id without prefix.</returns>
		/// 
		private string ToColumn(string id, string prefix)
		{
			string trimmed;
			if (null == prefix) trimmed = id;
			else trimmed = id.Substring(prefix.Length);
			return trimmed;
		}

		private void ReadControls(ControlCollection controls, IDictionary dictionary, string prefix, string list_suffix, bool nullOnEmpty)
		{
			foreach (Control t in controls)
			{
				if (IsTextBox(t))
				{
					TextBox x = (TextBox) t;
					string value = (nullOnEmpty) ? NullOnEmpty(x.Text) : x.Text;
					dictionary.Add(ToColumn(x.ID, prefix), value);
				}
				if (IsLabel(t))
				{
					Label x = (Label) t;
					string value = (nullOnEmpty) ? NullOnEmpty(x.Text) : x.Text;
					dictionary.Add(ToColumn(x.ID, prefix), value);
				}
				if (IsListControl(t))
				{
					ListControl x = (ListControl) t;
					string root = RootId(x.ID, prefix, list_suffix);
					string value = (nullOnEmpty) ? NullOnEmpty(x.SelectedValue) : x.SelectedValue;
					dictionary.Add(root, value);
				}
				if (IsCheckBox(t))
				{
					CheckBox x = (CheckBox) t;
					string key = ToColumn(x.ID, prefix);
					string value = (x.Checked) ? key : null;
					dictionary.Add(key, value);
				}
				if (IsRadioButton(t))
				{
					RadioButton x = (RadioButton) t;
					string key = ToColumn(x.ID, prefix);
					string value = (x.Checked) ? key : null;
					dictionary.Add(key, value);
				}
			}
		}

		public void Read(ControlCollection controls, IDictionary dictionary, bool nullIfEmpty)
		{
			ReadControls(controls, dictionary, null, ListSuffix, nullIfEmpty);
		}

		public void ReadExecute(ControlCollection controls, IViewHelper helper, bool nullIfEmpty)
		{
			Read(this.Controls, helper.Criteria, nullIfEmpty);
			helper.Execute();
		}

		public void ReadExecute(IViewHelper helper, bool nullIfEmpty)
		{
			ReadExecute(this.Controls, helper, nullIfEmpty);
		}

		public void ReadExecute(IViewHelper helper)
		{
			ReadExecute(helper, true);
		}

		public IViewHelper Read(ControlCollection controls, string command, bool nullOnEmpty)
		{
			IViewHelper helper = GetHelperFor(command);
			ReadControls(controls, helper.Criteria, null, ListSuffix, nullOnEmpty);
			return helper;
		}

		public IViewHelper Read(string command, bool nullOnEmpty)
		{
			return Read(this.Controls, command, nullOnEmpty);
		}

		public IViewHelper Read(string command)
		{
			return Read(this.Controls, command, true);
		}

		public IViewHelper ReadExecute(ControlCollection collection, string command, bool nullOnEmpty)
		{
			IViewHelper helper = Read(collection, command, nullOnEmpty);
			helper.Execute();
			return helper;
		}

		public IViewHelper ReadExecute(ControlCollection collection, string command)
		{
			return ReadExecute(collection, command, true);
		}


		public IViewHelper ReadExecute(string command, bool nullIfEmpty)
		{
			return ReadExecute(this.Controls, command, nullIfEmpty);
		}

		public IViewHelper ReadExecute(string command)
		{
			return ReadExecute(this.Controls, command, true);
		}


		public IViewHelper Read(string command, IDictionary criteria, bool nullIfEmpty)
		{
			IViewHelper helper = GetHelperFor(command);
			helper.Read(criteria, nullIfEmpty);
			return helper;
		}

		public IViewHelper ReadExecute(string command, IDictionary criteria)
		{
			return ReadExecute(command, criteria, true);
		}

		public IViewHelper ReadExecute(string command, IDictionary criteria, bool nullIfEmpty)
		{
			IViewHelper helper = Read(command, criteria, nullIfEmpty);
			helper.Execute();
			return helper;
		}

		private static string TITLE = "_title";
		private static string LINK = "_link";

		public virtual void GetMessages(ControlCollection controls)
		{
			foreach (Control t in controls)
			{
				if (IsButton(t))
				{
					Button x = (Button) t;
					x.Text = GetMessage(x.ID);
				}
				if (IsHyperLink(t))
				{
					HyperLink x = (HyperLink) t;
					x.Text = GetMessage(x.ID + TITLE);
					x.NavigateUrl = GetMessage(x.ID + LINK);
					continue;
				}
				if (IsLabel(t))
				{
					Label x = (Label) t;
					x.Text = GetMessage(x.ID);
				}
			}
		}

		public virtual void GetMessages()
		{
			GetMessages(Controls);
		}

		#endregion

		#region ListControl methods 

		/// <summary>
		/// Select only those items in control 
		/// whose Value property matches the given value.
		/// If the value is null, no action is taken.
		/// </summary>
		/// <param name="control"></param>
		/// <param name="value"></param>
		/// 
		protected void SelectItem(ListControl control, string value)
		{
			if (value != null)
			{
				foreach (ListItem i in control.Items)
					i.Selected = false;

				foreach (ListItem i in control.Items)
				{
					if (value.Equals(i.Value))
						i.Selected = true;
				}
			}
		}

		/// <summary>
		/// Deactivate the selected item, and select any item matching value.
		/// </summary>
		/// <param name="control"></param>
		/// <param name="value"></param>
		/// 
		protected void SelectListItem(ListControl control, string value)
		{
			try
			{
				control.SelectedIndex = -1;
				SelectItem(control, value);
			}
			catch (NullReferenceException e1)
			{
				if (e1 == null) value = string.Empty; // placate the IDE
			}
		}

		/// <summary>
		/// Bind a list of KeyValue objects to a ListControl, 
		/// select any item matching value.
		/// </summary>
		/// <param name="control">ListControl to process</param>
		/// <param name="list">List of TextKey objects.</param>
		/// <param name="value">Value to select, or null if nothing is selected.</param>
		/// 
		private void BindListControl(ListControl control, IList list, string value)
		{
			control.DataTextField = "Value";
			control.DataValueField = "Key";
			control.DataSource = list;
			control.DataBind();
			SelectListItem(control, value);
		}

		protected void BindListControl(ListControl control, IList list)
		{
			bool insertKey = ((list != null) && (!list.Contains(NullKey)));
			if (insertKey) list.Insert(0, NullKey);
			BindListControl(control, list, null);
		}

		#endregion

		/// <summary>
		/// Signal when an error is exposed. 
		/// </summary>
		public event EventHandler View_Error;

		/// <summary>
		/// Pass an error to another control registered to received it.
		/// </summary>
		/// <param name="sender">This object</param>
		/// <param name="e">A ViewArgs instance with the IViewHelper containing the error messages(s).</param>
		private void View_Error_Send(object sender, ViewArgs e)
		{
			if (View_Error != null)
			{
				View_Error(sender, e);
			}
		}

		/// <summary>
		/// Reset state for this control, including any ViewState attributes, 
		/// usually on a new Open event or on a Quit event.
		/// </summary>
		/// <remarks><p>
		/// Subclasses can override to clear any attributes manages through ViewState, 
		/// and to retain any default values that should survive a reset.
		/// If overridden, the best practice is to call base.Page_Reset 
		/// rather than call ResetControls directly.
		/// </p><p>
		/// Usually, Page_Reset should *not* be called on a Save event, 
		/// since the state at Save might be needed by another component. 
		/// The typical idiom is to call Page_Reset when an Open method is passed new parameters 
		/// or on a Quit event.
		/// </p></remarks>
		public virtual void Page_Reset()
		{
			ResetControls();
		}

		/// <summary>
		/// Automatically lookup messages for Buttons, HyperLinks, and Labels.
		/// </summary>
		/// <remarks><p>
		/// Called during page Init cycle; override to change behavior or disable.
		/// </p></remarks>
		protected virtual void Page_Init()
		{
			if (IsPostBack) return;
			GetMessages();
		}

		private void Page_Load(object sender, EventArgs e)
		{
			// Put user code to initialize the page here
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