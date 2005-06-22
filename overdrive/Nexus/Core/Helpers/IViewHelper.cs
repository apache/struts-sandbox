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
using Nexus.Core.Tables;

namespace Nexus.Core.Helpers
{
	/// <summary>
	/// Provide a facade for use by a code-behind to simplify access to the 
	/// IRequestContext and IRequestCommand. 
	/// </summary>
	/// <remarks><p>
	/// The helper may also work with the catalog to act as a 
	/// "front controller" by ensuring routine tasks are carried out.
	/// These tasks can include input validation, data conversion, 
	/// text formatting, command logging, and so forth.
	/// </p></remarks>
	/// 
	public interface IViewHelper
	{
		/// <summary>
		/// Invoke the helper's command and bind the output to 
		/// controls in the given collection.
		/// </summary>
		/// <remarks><p>
		/// Most code behinds will call either ExecuteBind or 
		/// ReadExecute by passing in the collection of controls 
		/// from a panel control. 
		/// </p></remarks>
		/// <param name="controls">Collection of controls to populate.</param>
		/// 
		void ExecuteBind (ICollection controls);


		/// <summary>
		/// Read input from the controls in the given collection, 
		/// and invoke the helper's command.
		/// </summary>
		/// <param name="controls">Collection of controls to 
		/// populate.</param>
		/// <remarks><p>
		/// Most code behinds will call either ExecuteBind or 
		/// ReadExecute by passing in the collection of controls 
		/// from a panel control. 
		/// </p></remarks>
		/// 
		void ReadExecute (ICollection controls);


		/// <summary>
		/// Bind the output of the helper's command to controls in the 
		/// given collection.
		/// </summary>
		/// <param name="controls">Collection of controls to 
		/// populate.</param>
		/// 
		void Bind (ICollection controls);


		/// <summary>
		/// Invoke the helper's command.
		/// </summary>
		/// 
		void Execute ();


		/// <summary>
		/// Read input from the controls in the given collection.
		/// </summary>
		/// <param name="controls">Collection of controls to populate.</param>
		/// 
		void Read (ICollection controls);


		/// <summary>
		/// Store input and output values.
		/// </summary>
		/// 
		IRequestContext Context { get; }


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
		/// Return the Alerts, including any Fault, formatted for display by a UI control.
		/// </summary>
		/// <remarks>
		/// If messages are localized or customized, 
		/// the helper will return correct version for the user.
		/// UI specific implementation may markup the errors as needed.
		/// </remarks>
		/// 
		string ErrorsText { get; }

		/// <summary>
		/// Record a list of hint (or advisory) messages, 
		/// keyed by a field or other identifier, 
		/// or to a magic global key.
		/// </summary>
		/// 
		IDictionary Hints { get; }


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

		// ----

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

		/* 
			// TODO: Messengers
			string Message(string key); 
			string MessageIndex {get;}
			IMessageTable MessageTable {get;}
		*/

		// ----

		/// <summary>
		/// Provide a prefix to trim from the id of a control during Read and 
		/// Bind.
		/// </summary>
		/// <remarks><p>
		/// The Prefix is needed when a single page uses a control 
		/// more than once often in separate panels.
		/// </p></remarks>
		/// 
		string Prefix { get; set; }


		/// <summary>
		/// Provide a suffix to truncate from a list control id 
		/// in order to set a corresponding value field ["_list"].
		/// </summary>
		/// <remark><p>
		/// When processing a single-value list control, if the id ends with 
		/// the list suffix, 
		/// the suffix is removed, and a field with the remaining name is set 
		/// to the selected item value.
		/// </p><P>
		/// So, the selected item from a list control with the id 
		/// "facility_key_list" will be set to a field named "facility_key".
		/// </P></remark>
		/// 
		string ListSuffix { get; set; }

		/* 
			// TODO: 
			string AlertSuffix {get; set}
			string HintSuffix {get; set}
			string LabelSuffix {get; set}
		*/


		/// <summary>
		/// Indicate whether to set the value read from control to null 
		/// if it is an empty string [TRUE].
		/// </summary>
		/// <remarks><p>
		/// If a control is blank, it may still return an empty string. 
		/// In a IDictionary, an empty string is a valid value, 
		/// so the entry for the control will still exist. 
		/// </p></remarks>
		/// 
		bool NullIfEmpty { get; set; }


		/// <summary>
		/// Provide a string token to insert as item 0 to a list controls ["--v--"].
		/// </summary>
		/// <remarks><p>
		/// To disable feature, set to a null string.
		/// </p></remarks>
		/// 
		string SelectItemPrompt { get; set; }


		// ----

		/// <summary>
		/// Provide the catalog for this helper, 
		/// usually set by dependency injection.
		/// </summary>
		/// 
		IRequestCatalog Catalog { get; set; }


		/// <summary>
		/// Provide the command (or chain of commands) for this helper, 
		/// usually set by dependency injection.
		/// </summary>
		/// 
		IRequestCommand Command { get; set; }

	}

}