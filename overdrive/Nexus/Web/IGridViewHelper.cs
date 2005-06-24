using System.Collections;
using System.Web.UI.WebControls;
using Nexus.Core.Helpers;

namespace Nexus.Web
{
	/// <summary>
	/// Helper for controls that display a list, including an editable list.
	/// </summary>
	public interface IGridViewHelper : IViewHelper
	{
		#region IListViewHelper

		/// <summary>
		/// Collect search criteria to use with List command. 
		/// </summary>
		/// 
		IViewHelper FindHelper { get; set; }

		/// <summary>
		/// Filter matching entries for display.
		/// </summary>
		/// 
		IViewHelper ListHelper { get; set; }

		/// <summary>
		/// Update persistent store.
		/// </summary>
		/// 
		IViewHelper SaveHelper { get; set; }

		/// <summary>
		/// Label the EDIT control.
		/// </summary>
		/// 
		string EditText { get; set; }

		/// <summary>
		/// Label the QUIT control.
		/// </summary>
		/// 
		string QuitText { get; set; }

		/// <summary>
		/// Label the SAVE control.
		/// </summary>
		/// 
		string SaveText { get; set; }

		/// <summary>
		/// Label the custom ITEM control
		/// </summary>
		string ItemText { get; set; }

		/// <summary>
		/// Identify the command for the custom ITEM control.
		/// </summary>
		string ItemCommandName { get; set; }

		/// <summary>
		/// Indicate whether this instance has a standard item command [false].
		/// </summary>
		bool HasItemColumn { get; set; }

		/// <summary>
		/// Indicate whether this instance has standard edit commands. 
		/// </summary>
		/// <remarks><p>
		/// By default, this property can return true if a SaveCommandID is present. 
		/// Otherwise false. If the standard heuristic is followed, then the 
		/// implementation may throw a NotImplementedException on an attempt to set 
		/// this property manually.
		/// </p></remarks>
		bool HasEditColumn { get; set; }

		/// <summary>
		/// Indicate whether the business logic expects values to be passed in the context [true].
		/// </summary>
		/// <remarks>
		/// Commands that do not use search criteria can set this property to false.
		/// </remarks>
		bool HasCriteria { get; set; }

		string DataKeyField { get; set; }

		#endregion 

		#region IGridViewHelper 

		/// <summary>
		/// Ready grid for initial display of data.
		/// </summary>
		/// <param name="grid">The instant DataGrid.</param>
		/// <param name="criteria">Search criteria, if any.</param>
		/// <returns>True if no errors were detected.</returns>
		bool Load (DataGrid grid, IDictionary criteria);

		/// <summary>
		/// Execute find (what?) command (enter search criteria).
		/// </summary>
		/// <param name="controls">The list of controls to which to bind values.</param>
		bool Find (ICollection controls);

		/// <summary>
		/// Execute list command (filter records based on search criteria).
		/// </summary>
		/// <param name="controls">The list of controls from which to obtain the values to filter.</param>
		bool List (ICollection controls);

		/// <summary>
		/// Execute list command (filter records based on search criteria).
		/// </summary>
		/// <param name="grid">The instant datagrid.</param>
		/// <returns>True if no errors were detected.</returns>
		bool List (DataGrid grid);

		/// <summary>
		/// Execute save command for the given key, using the given control collection.
		/// </summary>
		/// <param name="key">The primary key for the entity being saved.</param>
		/// <param name="controls">The list of controls from which to obtain the values to save.</param>
		bool Save (string key, ICollection controls);

		/// <summary>
		/// Create a standard datagrid button column.
		/// </summary>
		/// <param name="grid">Our datagrid instance</param>
		/// <param name="i">The first column to use, usually 0</param>
		/// <returns>The next column to use, usually 1</returns>
		int BindItemColumn (DataGrid grid, int i);

		/// <summary>
		/// Create a standard datagrid edit column.
		/// </summary>
		/// <param name="grid">Our datagrid instance</param>
		/// <param name="i">The first column to use, usually 0</param>
		/// <returns>The next column to use, usually 1</returns>
		int BindEditColumn (DataGrid grid, int i);

		/// <summary>
		/// Bind whatever columns need to be displayed for this data type.
		/// </summary>
		/// <param name="grid">Our datagrid instance</param>
		/// <param name="i">The first column to use, usually 1</param>
		/// <returns>The next column to use</returns>
		int BindColumns (DataGrid grid, int i);

		/// <summary>
		/// Use the Listcommand to obtain a datasource. 
		/// </summary>
		/// <comment>
		/// Usually called just before DataBind.
		/// </comment>
		/// <param name="grid">Our datagrid instance</param>
		void DataSource (DataGrid grid);

		/// <summary>
		/// Ccreate a blank row in the datagrid.
		/// </summary>
		/// <param name="grid">Our datagrid instance</param>
		bool DataInsert (DataGrid grid);

		/// <summary>
		/// Bind our datagrid to the datasource.
		/// </summary>
		/// <remarks><p>
		/// Usually called just after DataSource.
		/// </p></remarks>
		/// <param name="grid">Our datagrid instance</param>
		void DataBind (DataGrid grid);

		/// <summary>
		/// Bind the columns and datasource to the DataGrid.
		/// </summary>
		/// <param name="grid">A DataGrid instance</param>
		void BindGrid (DataGrid grid);

		/// <summary>
		/// Run ExecuteContext using the ListHelper, and bind the result.
		/// </summary>
		/// <param name="grid">A DataGrid instance</param>
		/// <returns>True if the Context is nominal.</returns>
		bool ExecuteList (DataGrid grid);

		/// <summary>
		/// Run Execute using the ListHelper and supplied criteria, and bind the result.
		/// </summary>
		/// <param name="grid">A DataGrid instance</param>
		/// <param name="criteria">Criteria values</param>
		/// <returns>True if the Context is nominal.</returns>
		bool ExecuteList (DataGrid grid, IDictionary criteria);

		/// <summary>
		/// Obtain the DataKey value from a DataGrid.
		/// </summary>
		/// <param name="e">Current event</param>
		/// <param name="grid">The DataGrid</param>
		/// <returns>The DataKey Value as a string</returns>
		string GetDataKey (DataGridCommandEventArgs e, DataGrid grid);

		/// <summary>
		/// Create a collection of the controls based on the current DataGrid cell.
		/// </summary>
		/// <param name="e">Current event</param>
		/// <param name="grid">The DataGrid</param>
		/// <returns>Collection of controls in the current DataGrid cell</returns>
		ICollection GetControls (DataGridCommandEventArgs e, DataGrid grid);

		#endregion


	}
}
