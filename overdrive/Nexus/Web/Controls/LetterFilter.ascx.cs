using System;
using System.Collections;
using System.Data;
using System.Web.UI.WebControls;
using WQD.Core.Controls;

namespace Nexus.Web.Controls
{
	/// <summary>
	/// Display a list of letters, one of which can be selected at a time, 
	/// so a client control can filter a list of entries.
	/// </summary>
	/// <remarks><p>
	/// Adapted from http://www.codeproject.com/aspnet/LetterBasedPaging.asp
	/// </p></remarks>
	/// 
	public class LetterFilter : FindControl
	{
		/// <summary>
		/// Document token representing match all entries.
		/// </summary>
		public const string ALL = "[*]";

		/// <summary>
		/// Document the attribute name for the initial letter by which to filter.
		/// </summary>
		public const string ITEM_INITIAL = "initial";
		/// <summary>
		/// Provide a key under which to store the selected letter in ViewState.
		/// </summary>
		/// 
		public const string LETTER_KEY = "_Letter";

		/// <summary>
		/// Document the wildcard character used by SQL queries.
		/// </summary>
		public const string WILDCARD = "%";

		/// <summary>
		/// Signal that input is ready to submit, 
		/// passing the ITEM_INTIIAL value as FindArgs.
		/// </summary>
		/// 
		public event EventHandler View_Filter;

		/// <summary>
		/// List the letters that can selected.
		/// </summary>
		/// 
		protected Repeater letters;

		/// <summary>
		/// Provide a private field to cache the LetterFilter property.
		/// </summary>
		/// 
		private string _Letter;

		/// <summary>
		/// Store the current letter by which client is to filter.
		/// </summary>
		/// <remarks><p>
		/// The value is stored by ID in ViewState 
		/// so that more than one LetterFilter can be used by the same parent control.
		/// </p></remarks>
		/// 
		public string Letter
		{
			get
			{
				if (_Letter == null) _Letter = ViewState[ID + LETTER_KEY] as string;
				return _Letter;
			}
			set
			{
				_Letter = value;
				ViewState[ID + LETTER_KEY] = value;
				// Bind the datasource so that Letter change is reflected
				letters.DataBind();
			}
		}

		/// <summary>
		/// Provide a key under which to store the DataTable in ViewState.
		/// </summary>
		/// 		
		private string LETTER_TABLE_KEY = "_LetterData";

		/// <summary>
		/// Provide a private field to cache the LetterTable property.
		/// </summary>
		/// 		
		private DataTable _LetterTable;

		/// <summary>
		/// Store the DataTable containing our letters. 
		/// </summary>
		/// 		
		public DataTable LetterTable
		{
			get
			{
				if (_LetterTable == null) _LetterTable = ViewState[ID + LETTER_TABLE_KEY] as DataTable;
				return _LetterTable;
			}
			set
			{
				_LetterTable = value;
				ViewState[ID + LETTER_TABLE_KEY] = value;
			}

		}

		/// <summary>
		/// Provide the name of the item command to fire when the linkbutton for a letter is clicked.
		/// </summary>
		/// 		
		protected const string FILTER_CMD = "filter";

		/// <summary>
		/// Identify the linkbutton that will contain the letter in each repeater cell.
		/// </summary>
		/// 
		protected const string LETTER_ID = "letter";

		/// <summary>
		/// Identify the column in our DataTable that holds the letters. 
		/// </summary>
		/// 
		protected const string LETTER_COLUMN = "Letter";

		/// <summary>
		/// Handle the "Filter" by new letter command, 
		/// passing NULL if "ALL" is selected.
		/// </summary>
		/// 
		protected void letters_ItemCommand(object source, RepeaterCommandEventArgs e)
		{
			// Handle the "Filter" new record command
			if (e.CommandName == FILTER_CMD)
			{
				// Set the new Letter selection
				Letter = (string) e.CommandArgument;

				// Raise the "Filter" event so that client can update its list. 
				if (View_Filter != null)
				{
					string letter = Letter;
					if (ALL.Equals(letter)) letter = null;
					string letter2 = letter + WILDCARD;
					IDictionary criteria = new Hashtable(1);
					criteria.Add(ITEM_INITIAL, letter2);
					FindArgs a = new FindArgs(e, criteria);
					View_Filter(source, a);
				}
			}
		}

		/// <summary>
		/// Called when an item in the letters repeater control is data bound to a source.
		/// </summary>
		protected void letters_ItemDataBound(object source, RepeaterItemEventArgs e)
		{
			// Retrieve the row of data that is to be bound to the repeater
			DataRowView data = (DataRowView) e.Item.DataItem;

			// If the letter we are binding to the current repeater control item is
			//  the same as the one currently selected, than disable it so the user
			//  knows which one was selected.
			if ((string) data[0] == Letter)
			{
				LinkButton cmd = (LinkButton) e.Item.FindControl(LETTER_ID);
				cmd.Enabled = false;
			}
		}


		/// <summary>
		/// Create a new DataTable from the list of letters provided, 
		/// and store a referance to the newly create data table for use on post back.
		/// </summary>
		/// <param name="input">List of letters to present as commands</param>
		private void LetterTable_Init(IList input)
		{
			DataTable dt = new DataTable();
			dt.Columns.Add(new DataColumn(LETTER_COLUMN, typeof (string)));

			for (int i = 0; i < input.Count; i++)
			{
				DataRow dr = dt.NewRow();
				dr[0] = input[i];
				dt.Rows.Add(dr);
			}

			LetterTable = dt;
			Letter = null;
		}

		/// <summary>
		/// Bind the letters array to the repeater control, 
		/// set the default letter, 
		/// so that the letters are ready to present.
		/// </summary>
		/// 
		public override bool Open()
		{
			if (LetterTable == null)
			{
				// Default to A-Z
				string[] input = {
					"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
					"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
					"U", "V", "W", "X", "Y", "Z", ALL
				};

				LetterTable_Init(input);
			}

			letters.DataSource = LetterTable.DefaultView;
			string current = Letter;
			if (current == null) Letter = ALL;
			return true;
		}

		/// <summary>
		/// Bind the letters array to the repeater control, 
		/// set the default letter, 
		/// so that the letters are ready to present.
		/// </summary>
		/// 
		public bool Open(IList letters)
		{
			LetterTable_Init(letters);
			return Open();
		}


		/// <summary>
		/// Pass our DataGrid instance to base member.
		/// </summary>
		/// 
		private void Page_Init()
		{
			letters.ItemCommand += new RepeaterCommandEventHandler(this.letters_ItemCommand);
			letters.ItemDataBound += new RepeaterItemEventHandler(this.letters_ItemDataBound);
		}

		/// <summary>
		/// Handle the page Load event. 
		/// </summary>
		/// <param name="sender">Event source</param>
		/// <param name="e">Runtime parameters</param>
		/// 
		private void Page_Load(object sender, EventArgs e)
		{
			Open();
		}

		#region Web Form Designer generated code

		/// <summary>
		///		Generated method for Designer support.
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