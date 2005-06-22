/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
using System;
using System.Collections;
using System.Text;
using System.Web.UI;
using System.Web.UI.WebControls;
using Nexus.Core;
using Nexus.Core.Helpers;

namespace Nexus.Web.Helpers
{
	/// <summary>
	/// Implement IWebViewHelper [OVR-14].
	/// </summary>
	/// 
	public class WebViewHelper : ViewHelper
	{
		#region IViewHelper

		public override void ExecuteBind (ICollection controls)
		{
			Execute ();
			if (IsNominal) Bind (controls);
		}

		public override void ReadExecute (ICollection controls)
		{
			Read (controls);
			Execute ();
		}

		public override void Bind (ICollection controls)
		{
			ControlCollection cc = controls as ControlCollection;
			BindControls (cc, Context.Criteria, Prefix, ListSuffix);
		}

		public override void Read (ICollection controls)
		{
			ControlCollection cc = controls as ControlCollection;
			ReadControls (cc, Context.Criteria, Prefix, ListSuffix, NullIfEmpty);
		}

		public override string ErrorsText
		{
			get { return HtmlMessageBuilder (Alerts); }
		}

		public override string HintsText
		{
			get { return HtmlMessageBuilder (Hints); }
		}

		#endregion

		#region Bind methods

		private void BindControls (ControlCollection controls, IDictionary dictionary, string prefix, string list_suffix)
		{
			foreach (Control t in controls)
			{
				if (IsCheckBox (t))
				{
					CheckBox x = (CheckBox) t;
					string v = dictionary [ToColumn (x.ID, prefix)] as string;
					x.Checked = (v != null);
				}
				if (IsLabel (t))
				{
					Label x = (Label) t;
					object v = dictionary [ToColumn (x.ID, prefix)];
					if (v != null) x.Text = v.ToString ();
				}
				if (IsListControl (t))
				{
					ListControl x = (ListControl) t;
					string root = RootId (x.ID, prefix, list_suffix);
					IList s = dictionary [x.ID] as IList; // this_key_list
					string r = dictionary [root] as string; // this_key
					if ((null == r) || (0 == r.Length))
						BindListControl (x, s);
					else
						BindListControl (x, s, r);
				}
				if (IsRadioButton (t))
				{
					RadioButton x = (RadioButton) t;
					string v = dictionary [ToColumn (x.ID, prefix)] as string;
					x.Checked = (v != null);
				}
				if (IsTextBox (t))
				{
					TextBox x = (TextBox) t;
					object v = dictionary [ToColumn (x.ID, prefix)];
					if (v != null) x.Text = v.ToString ();
				}
			}
		}

		private void BindListControl (ListControl control, IList list)
		{
			bool insertKey = ((list != null) && (!list.Contains (String.Empty)) && (!list.Contains (SelectItemPrompt)));
			if (insertKey) list.Insert (0, new KeyValue (String.Empty, SelectItemPrompt));
			BindListControl (control, list, null);
		}

		/// <summary>
		/// Bind a list of KeyValue objects to a ListControl, 
		/// select any item matching value.
		/// </summary>
		/// <param name="control">ListControl to process</param>
		/// <param name="list">List of TextKey objects.</param>
		/// <param name="value">Value to select, or null if nothing is selected.</param>
		/// 
		private void BindListControl (ListControl control, IList list, string value)
		{
			control.DataTextField = "Value";
			control.DataValueField = "Key";
			control.DataSource = list;
			control.DataBind ();
			SelectListItem (control, value);
		}

		#endregion

		#region Read method

		private void ReadControls (ControlCollection controls, IDictionary dictionary, string prefix, string list_suffix, bool nullIfEmpty)
		{
			foreach (Control t in controls)
			{
				if (IsCheckBox (t))
				{
					CheckBox x = (CheckBox) t;
					string key = ToColumn (x.ID, prefix);
					string value = (x.Checked) ? key : null;
					dictionary.Add (key, value);
				}
				if (IsLabel (t))
				{
					Label x = (Label) t;
					string value = (nullIfEmpty) ? DoNullIfEmpty (x.Text) : x.Text;
					dictionary.Add (ToColumn (x.ID, prefix), value);
				}
				if (IsListControl (t))
				{
					ListControl x = (ListControl) t;
					string root = RootId (x.ID, prefix, list_suffix);
					string value = (nullIfEmpty) ? DoNullIfEmpty (x.SelectedValue) : x.SelectedValue;
					dictionary.Add (root, value);
				}
				if (IsRadioButton (t))
				{
					RadioButton x = (RadioButton) t;
					string key = ToColumn (x.ID, prefix);
					string value = (x.Checked) ? key : null;
					dictionary.Add (key, value);
				}
				if (IsTextBox (t))
				{
					TextBox x = (TextBox) t;
					string value = (nullIfEmpty) ? DoNullIfEmpty (x.Text) : x.Text;
					dictionary.Add (ToColumn (x.ID, prefix), value);
				}
			}
		}

		#endregion

		#region Control utilities

		private bool IsCheckBox (Control control)
		{
			return (typeof (CheckBox).Equals (control.GetType ()));
		}

		private bool IsLabel (Control control)
		{
			return (typeof (Label).Equals (control.GetType ()));
		}

		private bool IsListControl (Control control)
		{
			bool isList = false;
			Type type = control.GetType ();
			isList = (isList) || typeof (ListControl).Equals (type);
			isList = (isList) || typeof (CheckBoxList).Equals (type);
			isList = (isList) || typeof (DropDownList).Equals (type);
			isList = (isList) || typeof (ListBox).Equals (type);
			isList = (isList) || typeof (RadioButtonList).Equals (type);
			return isList;
		}

		private bool IsRadioButton (Control control)
		{
			return (typeof (RadioButton).Equals (control.GetType ()));
		}

		private bool IsTextBox (Control control)
		{
			return (typeof (TextBox).Equals (control.GetType ()));
		}

		#endregion

		#region String utilities

		/// <summary>
		/// If the input is an empty string, return null instead.
		/// </summary>
		/// <param name="input">Value</param>
		/// <returns>Null if value is empty, or the original value</returns>
		private string DoNullIfEmpty (string input)
		{
			return (string.Empty.Equals (input)) ? null : input;
		}

		/// <summary>
		/// Extract the root name from the id, allowing for a prefix and suffix.
		/// </summary>
		/// <param name="id">The full id, including prefix and suffix.</param>
		/// <param name="prefix">The prefix to omit.</param>
		/// <param name="suffix">The suffix to omit.</param>
		/// <returns></returns>
		private string RootId (string id, string prefix, string suffix)
		{
			int v = id.LastIndexOf (suffix);
			if (v < 1) return id;
			string fore = id.Substring (0, v);
			string root = ToColumn (fore, prefix);
			return root;
		}

		/// <summary>
		/// Render a control id as a column name 
		/// by trimming a prefix from the id, if any.
		/// </summary>
		/// <param name="id">String to process.</param>
		/// <param name="prefix">Prefix to remove.</param>
		/// <returns>id without prefix.</returns>
		/// 
		private string ToColumn (string id, string prefix)
		{
			string trimmed;
			if (null == prefix) trimmed = id;
			else trimmed = id.Substring (prefix.Length);
			return trimmed;
		}

		#endregion

		#region ListControl utilities

		/// <summary>
		/// Select only those items in control 
		/// whose Value property matches the given value.
		/// If the value is null, no action is taken.
		/// </summary>
		/// <param name="control"></param>
		/// <param name="value"></param>
		/// 
		private void SelectItem (ListControl control, string value)
		{
			if (value != null)
			{
				foreach (ListItem i in control.Items)
					i.Selected = false;

				foreach (ListItem i in control.Items)
				{
					if (value.Equals (i.Value))
						i.Selected = true;
				}
			}
		}

		/// <summary>
		/// Deactivate the selected item, and select any item matching value.
		/// </summary>
		/// <param name="control">Control to set</param>
		/// <param name="value">Default value</param>
		/// 
		private void SelectListItem (ListControl control, string value)
		{
			try
			{
				control.SelectedIndex = -1;
				SelectItem (control, value);
			}
			catch (NullReferenceException e1)
			{
				if (e1 == null) value = string.Empty; // placate the IDE
			}
		}

		#endregion

		#region Message utilities

		/// <summary>
		/// Build a set of messages using HTML markup.
		/// </summary>
		/// <param name="messages">A list of messages</param>
		/// <returns>HTML markup presenting the messages.</returns>
		/// 
		private string HtmlMessageList (IList messages)
		{
			StringBuilder sb = new StringBuilder ("<ul>");
			foreach (object o in messages)
			{
				sb.Append ("<li>");
				sb.Append (o.ToString ());
				sb.Append ("</li>");
			}
			sb.Append ("</ul>");

			return sb.ToString ();
		}

		/// <summary>
		/// Build a set error messages using HTML markup.
		/// </summary>
		/// <param name="store">A context listing errors, if any</param>
		/// <returns>HTML markup presenting the errors.</returns>
		/// 
		private string HtmlMessageBuilder (IDictionary store)
		{
			string messageMarkup = null;
			if (store != null)
			{
				IList messages = new ArrayList ();
				ICollection keys = store.Keys;
				foreach (string key in keys)
				{
					IList sublist = store [key] as IList;
					foreach (string message in sublist) messages.Add (message);
				}
				messageMarkup = HtmlMessageList (messages);
			}

			if (messageMarkup != null)
			{
				StringBuilder sb = new StringBuilder (messageMarkup);
				return sb.ToString ();
			}
			return null;
		}

		#endregion 
	}
}