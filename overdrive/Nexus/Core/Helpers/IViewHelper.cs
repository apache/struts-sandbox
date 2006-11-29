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
using Nexus.Core.Profile;
using Nexus.Core.Tables;

namespace Nexus.Core.Helpers
{
	/// <summary>
	/// Provide methods for running a business command and handling the result.
	/// </summary>
	/// <remarks><p>
	/// IViewHelper is a facade for use by a code-behind to simplify access 
	/// to the IRequestContext and IRequestCommand. 
	/// </p><p>
	/// The helper may also work with the catalog to act as a 
	/// "front controller" by ensuring routine tasks are carried out.
	/// These tasks can include input validation, data conversion, 
	/// text formatting, command logging, and so forth.
	/// </p></remarks>
	/// 
	public interface IViewHelper
	{
		/// <summary>
		/// Perform the Command associated with this Helper.
		/// </summary>
		/// 
		void Execute();

		/// <summary>
		/// Read input into the Criteria from a given Dictionary.
		/// </summary>
		/// <param name="criteria">Attributes to add to Critiera</param>
		/// <param name="nullIfEmpty">Set attributes for empty strings to null</param>
		/// 
		void Read(IDictionary criteria, bool nullIfEmpty);

		/// <summary>
		/// Store input and output values.
		/// </summary>
		/// 
		IDictionary Criteria { get; }

		/// <summary>
		/// User profile, which includes user ID and Locale.
		/// </summary>
		IProfile Profile { get; set; }

		/// <summary>
		/// Access result of operation as an IList.
		/// </summary>
		/// <remarks><p>
		/// If the Helper is designed to return a List result, 
		/// this method saves casting the outcome.
		/// If the Helper is not designed to return the result as a IList, 
		/// this method returns a single-value result as a one-entry list.
		/// </p><p>
		/// Note this since this is the Helper Outcome, 
		/// the result is relative to the Criteria, 
		/// rather than the main Context.
		/// </p></remarks>
		/// 
		IList Outcome { get; }

		// ----

		/// <summary>
		/// Record a list of alert (or error) messages, 
		/// keyed by the field causing the message, 
		/// or to a magic global key.
		/// </summary>
		/// <remarks>
		/// When recalling Alerts, by default include the Fault.
		/// </remarks>
		/// 
		IDictionary Alerts { get; }

		/// <summary>
		/// Return the Alerts for the specifiied ID, 
		/// formatted for display by a UI control.
		/// </summary>
		/// <remarks>
		/// If messages are localized or customized, 
		/// the helper will return correct version for the user.
		/// UI specific implementation may markup the errors as needed.
		/// </remarks>
		/// 
		string AlertsFor(string id);

		/// <summary>
		/// Return the Alerts, including any Fault, formatted for display by a UI control.
		/// </summary>
		/// <remarks>
		/// If messages are localized or customized, 
		/// the helper will return correct version for the user.
		/// UI specific implementation may markup the errors as needed.
		/// </remarks>
		/// 
		string AlertsText { get; }

		/// <summary>
		/// Indicate if alerts are queued.
		/// </summary>
		/// <returns>True if alerts are queued.</returns>
		/// 
		bool HasAlerts { get; }

		/// <summary>
		/// Record an Exception, if thrown.
		/// </summary>
		/// <remarks>
		/// By default, the Fault will be included in the list of Alerts.
		/// </remarks>
		/// 
		Exception Fault { get; }

		/// <summary>
		/// Indicate whether an Exception is caught.
		/// </summary>
		/// <returns>True if an Exception is caught.</returns>
		/// 
		bool HasFault { get; }

		/// <summary>
		/// Indicate if there are no alerts or fault pending.
		/// </summary>
		/// <returns>True if all is well.</returns>
		/// 
		bool IsNominal { get; }

		/// <summary>
		/// Record a list of hint (or advisory) messages, 
		/// keyed by a field or other identifier, 
		/// or to a magic global key.
		/// </summary>
		/// 
		IDictionary Hints { get; }

		/// <summary>
		/// Return the Hints for the specifiied ID, 
		/// formatted for display by a UI control.
		/// </summary>
		/// <remarks>
		/// If messages are localized or customized, 
		/// the helper will return correct version for the user.
		/// UI specific implementation may markup the errors as needed.
		/// </remarks>
		/// 
		string HintsFor(string id);

		/// <summary>
		/// Indicate if Hints are queued.
		/// </summary>
		/// <returns>True if Hints are queued.</returns>
		/// 
		bool HasHints { get; }

		/// <summary>
		/// Return Hints formatted for display by a UI control.
		/// </summary>
		/// <remarks>
		/// If messages are localized or customized, 
		/// the helper will return correct version for the user.
		/// UI specific implementatiosn may markup the messages as needed.
		/// </remarks>
		/// 
		string HintsText { get; }

		/// <summary>
		/// Provide the Field Table for this Helper.
		/// </summary>
		/// <remarks><p>
		/// The default implementation uses the Catalog to inject the global 
		/// Field Table reference. 
		/// The Context, and members with access to a Context, 
		/// can use the FieldTable to validate and format values, 
		/// and even to create controls that display values.
		/// </p></remarks>
		/// 
		IFieldTable FieldTable { get; }

		// ----

		/// <summary>
		/// Provide the command (or chain of commands) for this helper
		/// </summary>
		/// <remarks><p>
		/// Setting the Command also sets the internal Context for the command.
		/// </p></remarks>
		/// 
		IRequestCommand Command { get; set; }

		/// <summary>
		/// Provide a set of IFieldContext definitions to be used with this helper. 
		/// </summary>
		/// <remarks><p>
		/// The FieldSet is usually set by dependency injection.
		/// Some helpers generate DataGrids or DataForms based on the 
		/// FieldDefinitions 
		/// </p></remarks>
		/// 
		IList FieldSet { get; set; }

		/// <summary>
		/// Provide the catalog for this helper, 
		/// usually set by dependency injection.
		/// </summary>
		/// 
		IRequestCatalog Catalog { get; set; }

	}

}