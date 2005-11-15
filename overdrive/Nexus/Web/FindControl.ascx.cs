using System;
using System.Collections;
using Nexus.Core.Helpers;
using WQD.Core.Controls;

namespace Nexus.Web
{
	/// <summary>
	/// Base class for find controls.
	/// </summary>
	/// <remarks><p>
	/// Typically, a FindControl will collect input 
	/// to filter a list of entries displayed by a GridControl.
	/// </p></remarks>
	public class FindControl : ViewControl
	{
		/// <summary>
		/// Provide a field for the FindCommand property.
		/// </summary>
		private string _FindCommand;

		/// <summary>
		/// Provide the command that will populate the data-entry controls.
		/// </summary>
		public string FindCommand
		{
			get { return _FindCommand; }
			set { _FindCommand = value; }
		}

		/// <summary>
		/// Expose values input by client 
		/// for use by another component.
		/// </summary>
		/// 
		public virtual IDictionary Criteria
		{
			get
			{
				IViewHelper helper = Read(FindCommand);
				if (!helper.IsNominal)
				{
					Page_Alert = helper;
				}
				return helper.Criteria;
			}
		}

		/// <summary>
		/// Signal that input is ready to submit.
		/// </summary>
		/// 
		public event EventHandler View_Find;

		/// <summary>
		/// Fire Click event when input is ready to submit.
		/// </summary>
		/// <param name="sender">Source of event [find Button]</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		protected void find_Click(object sender, EventArgs e)
		{
			if (View_Find != null)
			{
				FindArgs a = new FindArgs(e, Criteria);
				View_Find(sender, a);
			}
		}

		/// <summary>
		/// Prepare controls for data entry.
		/// </summary>
		/// <remarks><p>
		/// Preparation includes obtaining lists from the 
		/// databases. 
		/// Any errors are reported 
		/// through the standard page error handler.
		/// </p></remarks>
		/// 
		public virtual bool Open()
		{
			IViewHelper helper = ExecuteBind(FindCommand);
			Bind(Profile.Criteria);
			bool okay = helper.IsNominal;
			if (!okay) Page_Alert = helper;
			return okay;
		}

		public virtual bool Open(IDictionary criteria)
		{
			IViewHelper helper = GetHelperFor(FindCommand);
			helper.Read(criteria, true);
			ExecuteBind(helper);
			bool okay = helper.IsNominal;
			if (!okay) Page_Alert = helper;
			return okay;
		}

		/// <summary>
		/// Handle the page Load event. 
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
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