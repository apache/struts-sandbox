using System;
using System.Collections;
using Nexus.Core.Helpers;
using Nexus.Web.Controls;

namespace WQD.Web.Controls
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
			get {return _FindCommand;}
			set {_FindCommand = value;}
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
					Page_Error = helper;
				}
				return helper.Criteria;
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
		public virtual void Open()
		{
			if (IsPostBack) return;
			IViewHelper helper = ExecuteBind(FindCommand);
			if (!helper.IsNominal) Page_Error = helper;
		}

		/// <summary>
		/// Wire the event handlers and set defaults.
		/// </summary>
		/// <param name="sender">Source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void Page_Load(object sender, System.EventArgs e)
		{
			// Put user code to initialize the page here
		}

		#region Web Form Designer generated code

		/// <summary>
		///		Initialize components.
		/// </summary>
		/// <param name="e">Runtime parameters</param>
		/// 
		override protected void OnInit(EventArgs e)
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
			this.Load += new System.EventHandler(this.Page_Load);
		}
		#endregion
	}
}
