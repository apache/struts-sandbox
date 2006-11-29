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
	/// <remarks>
	/// ViewControl can read and bind controls, 
	/// and provides access methods for helper and message resources  
	/// </remarks>
	/// 
	/// 
	/// 
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
		protected IViewHelper Page_Alert
		{
			set { View_Alert_Handler(this, new ViewArgs(value)); }
		}

		#region String utilities 

		protected string NullOnEmpty(string input)
		{
			return (string.Empty.Equals(input)) ? null : input;
		}

		protected string Trim(string input, bool nullOnEmpty)
		{
			string output = (input==null) ? null : input.Trim();
			return (nullOnEmpty) ? NullOnEmpty(output) : output;
		}

		/// <summary>
		/// Extract the root name from the id, allowing for a prefix and suffix.
		/// </summary>
		/// <param name="id">The full id, including prefix and suffix.</param>
		/// <param name="prefix">The prefix to omit.</param>
		/// <param name="suffix">The suffix to omit.</param>
		/// <returns>ID for corresponding entry</returns>
		/// 
		private string RootId(string id, string prefix, string suffix)
		{
			int v = id.LastIndexOf(suffix);
			string fore = id.Substring(0, v);
			string root = ToColumn(fore, prefix);
			return root;
		}

		/// <summary>
		/// Trim any SQL wildcards that may have been added to a search string.
		/// </summary>
		/// <param name="input">String to trim</param>
		/// <returns>Input without SQL wildcards</returns>
		/// 
		protected string TrimWildCards(string input)
		{
			string trimmed = null;
			if (input != null) trimmed = input.Trim('%');
			return trimmed;
		}

		public const string NULL_TOKEN = "--v--";

		private static KeyValue _NullKey = new KeyValue(String.Empty, NULL_TOKEN);

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
		/// Return true if control is a MessageLabel 
		/// or MessageLabel subclass.
		/// </summary>
		/// <param name="control">Control to test.</param>
		/// <returns>True if control is a NameLabel</returns>
		/// 
		protected bool IsViewLabel(Control control)
		{
			return (control is ViewLabel);
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

		/// <summary>
		/// Kludgy method to read the value of a control 
		/// directly from the request under certain circumstances. 
		/// </summary>
		/// <remarks><p>
		/// This method is intended to workaround a problem 
		/// we are having with dymanci DataGrid templates. 
		/// The template column seems to be absent from the event args. 
		/// Other columns are there, but a template column is ignored. 
		/// </p><p>
		/// This method can retrieve the value directly from the request
		/// *if* the identifier is a unique string of characters that 
		/// won't be found as part of another parameter. 
		/// The DataGrid changes the identifer name, 
		/// but appends the control id given by the template. 
		/// So, we use key.IndexOf to find the control id within the
		/// manufactured parameter id. 
		/// Kludgy, but it's only meant as a workaround (see OVR-24).
		/// </p></remarks>
		/// 
		/// <param name="id">Control ID</param>
		/// <returns>Value of first paramter that has "id" 
		/// as any part of its name.</returns>
		protected string FindControlValue(string id)
		{
			string ctlKey = null;
			string[] keys = Request.Params.AllKeys;
			foreach (string key in keys)
			{
				bool found = (key.IndexOf(id) > -1);
				if (found) ctlKey = key;
				continue;
			}
			return Request.Params[ctlKey]; // FIXME
		}

		#endregion 

		#region ViewState methods 

		/// <summary>
		/// Token under which to store the array of primary keys. 
		/// </summary>
		/// 
		private const string KEYS = "Keys";

		/// <summary>
		/// Set an array of primary keys to the view state.
		/// </summary>
		/// <param name="keys">.</param>
		/// 
		protected void SetKeyIndex(string[] keys)
		{
			ViewState[KEYS] = keys;
		}

		/// <summary>
		/// Return the nth key from the array kept in view state.
		/// </summary>
		/// <param name="index">Array index for the primary key (corresponds to index of the DataGrid).</param>
		/// <returns>Nth key from primary key array kept in view state.</returns>
		protected string GetKeyIndex(int index)
		{
			string[] keys = (string[]) ViewState[KEYS];
			return keys[index];
		}

		/// <summary>
		/// Return the primary key for the current item index of the DataGrid.
		/// </summary>
		/// <param name="e">DataGrid event arguments.</param>
		/// <param name="offset">Number of items on prior pages, if any (page*pagesize).</param>
		/// <returns>The primary key for the current item index of the DataGrid.</returns>
		protected string GetKeyIndex(DataGridCommandEventArgs e, int offset)
		{
			return GetKeyIndex(e.Item.ItemIndex + offset);
		}

		/// <summary>
		/// Return the index for the given key, 
		/// usually so that it can be selected.
		/// </summary>
		/// <param name="list">List of KeyValue entries.</param>
		/// <param name="key">A key value from the list</param>
		/// <returns>-1 if not found</returns>
		/// 
		protected int IndexForKey(IList list, string key)
		{
			int i = 0;
			foreach (KeyValue row in list)
			{
				if (key.Equals(row.Key))
					return i;
				i++;
			}
			return -1;
		}

		#endregion

		#region IViewControl methods

		public void ResetControls()
		{
			ControlCollection controls = Controls;
			foreach (Control control in controls)
			{
				if (IsTextBox(control))
				{
					TextBox x = (TextBox) control;
					x.Text = String.Empty;
					continue;
				}
				if (IsListControl(control))
				{
					ListControl x = (ListControl) control;
					x.SelectedIndex = -1;
					continue;
				}

				if (IsRadioButton(control))
				{
					RadioButton x = (RadioButton) control;
					x.Checked = false;
					continue;
				}

				if (IsCheckBox(control))
				{
					CheckBox x = (CheckBox) control;
					x.Checked = false;
					continue;
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

		protected virtual void InitViewLabels()
		{
			foreach (Control t in Controls)
			{
				if (IsViewLabel(t))
				{
					ViewLabel x = (ViewLabel) t;
					if (x.View_Alert)
					{
						View_Alert += new EventHandler(x.View_Alert_Handler);
					}
					/* 
					if (x.View_Hint)
					{
						View_Hint += new EventHandler(x.View_Hint_Handler);
					}
					*/
					continue;
				}
			}
		}

		public void EnableControls(ControlCollection controls, bool enable)
		{
			foreach (Control t in controls)
			{
				if (IsTextBox(t))
				{
					TextBox x = (TextBox) t;
					x.Enabled = enable;
					continue;
				}
				if (IsViewLabel(t))
				{
					ViewLabel x = (ViewLabel) t;
					x.Enabled = enable;
					continue;
				}
				if (IsLabel(t))
				{
					Label x = (Label) t;
					x.Enabled = enable;
					continue;
				}
				if (IsListControl(t))
				{
					ListControl x = (ListControl) t;
					x.Enabled = enable;
					continue;
				}
				if (IsCheckBox(t))
				{
					CheckBox x = (CheckBox) t;
					x.Enabled = enable;
					continue;
				}
				if (IsRadioButton(t))
				{
					RadioButton x = (RadioButton) t;
					x.Enabled = enable;
					continue;
				}
				if (IsButton(t))
				{
					Button x = (Button) t;
					x.Enabled = enable;
					continue;
				}			
			}
		}

		public void EnableControls(ControlCollection controls)
		{
			EnableControls(controls,true);
		}

		public void EnableControls()
		{
			EnableControls(Controls,true);
		}

		public void DisableControls(ControlCollection controls)
		{
			EnableControls(controls,false);
		}

		public void DisableControls()
		{
			EnableControls(Controls,false);
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
					continue;
				}
				if (IsViewLabel(t))
				{
					ViewLabel x = (ViewLabel) t;
					object v = dictionary[ToColumn(x.ID, prefix)];
					if (v != null) x.Text = v.ToString();
					continue;
				}
				if (IsLabel(t))
				{
					Label x = (Label) t;
					object v = dictionary[ToColumn(x.ID, prefix)];
					if (v != null) x.Text = v.ToString();
					continue;
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
					continue;
				}
				if (IsCheckBox(t))
				{
					CheckBox x = (CheckBox) t;
					object v = dictionary[ToColumn(x.ID, prefix)];
					if (v != null) x.Checked = true;
					continue;
				}
				if (IsRadioButton(t))
				{
					RadioButton x = (RadioButton) t;
					object v = dictionary[ToColumn(x.ID, prefix)];
					if (v != null) x.Checked = true;
					continue;
				}
			}
		}

		public void Bind(ControlCollection controls, IDictionary dictionary)
		{
			BindControls(controls, dictionary, null, ListSuffix);
		}

		public void Bind(IDictionary dictionary)
		{
			BindControls(Controls, dictionary, null, ListSuffix);
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
			ExecuteBind(Controls, helper);
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
					string value = Trim(x.Text, nullOnEmpty);
					dictionary.Add(ToColumn(x.ID, prefix), value);
					continue;
				}
				if (IsLabel(t))
				{
					Label x = (Label) t;
					string value = Trim(x.Text, nullOnEmpty);
					dictionary.Add(ToColumn(x.ID, prefix), value);
					continue;
				}
				if (IsListControl(t))
				{
					ListControl x = (ListControl) t;
					string root = RootId(x.ID, prefix, list_suffix);
					string value = Trim(x.SelectedValue, nullOnEmpty);;
					dictionary.Add(root, value);
					continue;
				}
				if (IsCheckBox(t))
				{
					CheckBox x = (CheckBox) t;
					string key = ToColumn(x.ID, prefix);
					string value = (x.Checked) ? key : null;
					dictionary.Add(key, value);
					continue;
				}
				if (IsRadioButton(t))
				{
					RadioButton x = (RadioButton) t;
					string key = ToColumn(x.ID, prefix);
					string value = (x.Checked) ? key : null;
					dictionary.Add(key, value);
					continue;
				}
			}
		}

		public void Read(ControlCollection controls, IDictionary dictionary, bool nullIfEmpty)
		{
			ReadControls(controls, dictionary, null, ListSuffix, nullIfEmpty);
		}

		public void ReadExecute(ControlCollection controls, IViewHelper helper, bool nullIfEmpty)
		{
			Read(Controls, helper.Criteria, nullIfEmpty);
			helper.Execute();
		}

		public void ReadExecute(IViewHelper helper, bool nullIfEmpty)
		{
			ReadExecute(Controls, helper, nullIfEmpty);
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
			return Read(Controls, command, nullOnEmpty);
		}

		public IViewHelper Read(string command)
		{
			return Read(Controls, command, true);
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
			return ReadExecute(Controls, command, nullIfEmpty);
		}

		public IViewHelper ReadExecute(string command)
		{
			return ReadExecute(Controls, command, true);
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
				if (IsViewLabel(t))
				{
					ViewLabel x = (ViewLabel) t;
					if (x.Resource) try
					{
						x.Text = GetMessage(x.ID);
					}
					catch (Exception e)
					{
						if (x.Required) throw(e);
					}
					continue;
				}
				if (IsButton(t))
				{
					Button x = (Button) t;
					x.Text = GetMessage(x.ID);
					continue;
				}
				if (IsHyperLink(t))
				{
					HyperLink x = (HyperLink) t;
					x.Text = GetMessage(x.ID + TITLE);
					x.NavigateUrl = GetMessage(x.ID + LINK);
					continue;
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
			catch (NullReferenceException okay)
			{
				if (okay==null) throw okay; // Silly, but it placates the IDE
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
			bool insertKey = ((list != null) && (!list.Contains(NullKey)));
			if (insertKey) list.Insert(0, NullKey);
			control.DataTextField = KeyValue.VALUE;
			control.DataValueField = KeyValue.KEY;
			control.DataSource = list;
			control.DataBind();
			if (value!=null) SelectListItem(control, value);
		}

		protected void BindListControl(ListControl control, IList list)
		{
			BindListControl(control, list, null);
		}

		#endregion

		/// <summary>
		/// Signal when an error is exposed. 
		/// </summary>
		public event EventHandler View_Alert;

		/// <summary>
		/// Pass an error to another control registered to received it.
		/// </summary>
		/// <param name="sender">This object</param>
		/// <param name="e">A ViewArgs instance with the IViewHelper containing the error messages(s).</param>
		private void View_Alert_Handler(object sender, EventArgs e)
		{
			if (View_Alert != null)
			{
				View_Alert(sender, e);
			}
		}

		/// <summary>
		/// Initialize the control to use the standard View_Alert event handerl.
		/// </summary>
		/// <param name="c">Control to register</param>
		/// 
		protected void InitView(ViewControl c)
		{
			c.View_Alert += new EventHandler(View_Alert_Handler); // Bubble event
			c.Profile = Profile;
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
		/// Signal that input was reset.
		/// </summary>
		/// 
		public event EventHandler View_Reset;

		/// <summary>
		/// Reset control values.
		/// </summary>
		/// 
		protected void reset_Click(object sender, EventArgs e)
		{
			Page_Reset();
			if (View_Reset != null)
			{
				View_Reset(sender, e); // bubble it
			}			
		}

		/// <summary>
		/// Automatically lookup messages for Buttons, HyperLinks, and Labels.
		/// </summary>
		/// <remarks><p>
		/// Called during page Init cycle; override to change behavior or disable.
		/// </p></remarks>
		protected virtual void Page_Init()
		{
			InitViewLabels();
			if (IsPostBack) return;
			GetMessages();
		}

		/// <summary>
		/// Handle the page's Load event.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime arguments</param>
		/// 
		private void Page_Load(object sender, EventArgs e)
		{
			// Put user code to initialize the page here
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