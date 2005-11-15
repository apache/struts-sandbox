using System;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;

namespace Nexus.Web
{
	/// <summary>
	/// Present dynamic message by consulting with Message Resources 
	/// or handling an event.
	/// </summary>
	public class ViewLabel : Label
	{
		/// <summary>
		/// Field for EntryID property.
		/// </summary>
		/// 
		private string _EntryID;

		/// <summary>
		/// Name of corresponding entry.
		/// </summary>
		/// <remarks><p>
		/// If this is not set, 
		/// the EntryId is obtained by truncating the Suffix 
		/// from the ID.
		/// </p></remarks>
		public string EntryID
		{
			get
			{
				if ((_EntryID == null) || (_EntryID.Length == 0))
				{
					int v = ID.LastIndexOf(Suffix);
					_EntryID = ID.Substring(0, v);
				}
				return _EntryID;
			}
			set { _EntryID = value; }
		}

		public const string MESSAGE_SUFFIX = "_msg";

		/// <summary>
		/// Field for Suffix property ["_msg"].
		/// </summary>
		/// 
		private string _Suffix = MESSAGE_SUFFIX;

		/// <summary>
		/// Suffix to trim from ID to obtain EntryID [MESSAGE_SUFFIX].
		/// </summary>
		/// 
		public string Suffix
		{
			get { return _Suffix; }
			set { _Suffix = value; }
		}

		/// <summary>
		/// Field for Resource property [TRUE].
		/// </summary>
		/// 
		private bool _Resource = true;

		/// <summary>
		/// If true, populate from Message Resources.
		/// </summary>
		/// 
		public bool Resource
		{
			get { return _Resource; }
			set { _Resource = value; }
		}

		/// <summary>
		/// Field for Required property [FALSE].
		/// </summary>
		/// 
		private bool _Required = false;

		/// <summary>
		/// If true, throw exception if Resource==true 
		/// and message not in resource.
		/// </summary>
		/// 
		public bool Required
		{
			get { return _Required; }
			set { _Required = value; }
		}

		/// <summary>
		/// Field for View_Hint property [TRUE].
		/// </summary>
		/// 
		private bool _View_Hint = true;

		/// <summary>
		/// If true, register for View_Alert event 
		/// and display any alert for corresponding field.
		/// </summary>
		/// 
		public bool View_Hint
		{
			get { return _View_Hint; }
			set { _View_Hint = value; }
		}

		/// <summary>
		/// Field for View_Alert property [TRUE].
		/// </summary>
		/// 
		private bool _View_Alert = true;

		/// <summary>
		/// If true, register for View_Hint event 
		/// and display any hint for corresponding field.
		/// </summary>
		/// 
		public bool View_Alert
		{
			get { return _View_Alert; }
			set { _View_Alert = value; }
		}

		/// <summary>
		/// Present a list of error mesasges.
		/// </summary>
		/// 
		protected IViewHelper Alerts
		{
			set
			{
				if (value != null)
				{
					Text = value.AlertsFor(EntryID);
				}
			}
		}

		/// <summary>
		/// Provide a standard Event Handler for passing Alert messages.
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		public void View_Alert_Handler(object sender, EventArgs e)
		{
			ViewArgs v = e as ViewArgs;
			if (v == null) throw new ArgumentException("ViewLabel.View_Alert_Handler: !(e is ViewArgs)");
			IViewHelper helper = v.Helper;
			if (helper != null) Alerts = helper;
			else throw new ArgumentException("ViewLabel.View_Alert_Handler: (e.helper==null)");
		}
	}
}