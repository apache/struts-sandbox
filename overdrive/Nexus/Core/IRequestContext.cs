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
using Agility.Core;
using Nexus.Core.Profile;
using Nexus.Core.Tables;

namespace Nexus.Core
{
	/// <summary>
	/// Exchange data between business and presentation layers [OVR-7]. 
	/// </summary>
	/// <remarks><p>
	/// An IRequestContext can predefine whatever properties we need for 
	/// storing input, output, messages, and other common attributes, 
	/// including Locale (or Culture) and user credentials. 
	/// </p><p>
	/// A key member is the FieldTable. 
	/// The FieldTable uses XForms terminology for its members 
	/// and IRequestContext members follow suit. 
	/// For example, "errors" are called "Alerts" and generic 
	/// messages are called "Hints, 
	/// since these are terms used by the FieldTable and XForms.
	/// </p></remarks>
	/// 
	public interface IRequestContext : IContext
	{
		#region Processing 

		/// <summary>
		/// Identify the top-level Command (or Chain) processing 
		/// this Context.
		/// </summary>
		/// <remarks><P>
		/// The Command property corresponds to ID of INexusCommand 
		/// for the initial Command or Chain.
		/// </P></remarks>
		/// 
		string Command { get; set; }

		/// <summary>
		/// Provide the top-level Command (or Chain) processing this Context.
		/// </summary>
		/// <remarks><p>
		/// Command corresponds to ID of INexusCommand for the 
		/// initial Command or Chain.
		/// </p></remarks>
		/// 
		IRequestCommand CommandBin { get; set; }


		/// <summary>
		/// Provide the FieldTable for this Context.
		/// </summary>
		/// <remarks><p>
		/// The default implementation uses the Catalog to inject the global 
		/// Field Table reference. 
		/// The Context, and members with access to a Context, 
		/// can use the FieldTable to validate and format values, 
		/// and even to create controls that display values.
		/// </p></remarks>
		/// 
		IFieldTable FieldTable { get; set; }

		/// <summary>
		/// User profile, which includes user ID and Locale.
		/// </summary>
		IProfile Profile { get; set; }

		/// <summary>
		/// Return true if an Outcome object is present.
		/// </summary>
		/// <returns>True if an Outcome context is present.</returns>
		/// 
		bool HasOutcome { get; }

		/// <summary>
		/// Return a IList stored under the Command ID, if any.
		/// </summary>
		/// <remarks><p>
		/// Some Commands returns List of values.
		/// So that Commands can work together as part of a Chain, 
		/// list-based Commands are expected to store the 
		/// list under their own Command ID.
		/// Outcome is a convenience method to access the 
		/// initial or "outermost" Command or Chain ID. 
		/// </p>
		/// <p>
		/// To allow use as subcommands in a Chain, 
		/// IRequestCommand implementations should prefer the idiom 
		/// <code>Context[ID] = object</code>
		/// to using the Outcome directly. 
		/// Since they might not be the initial Command,
		/// but rather a subcommand, or link, in a Chain.
		/// </p><p>
		/// Outcome is more convenient to presentation layer clients, 
		/// who are looking for the top-level output, 
		/// rather than output of a particular subcommand.
		/// </p><p>
		/// As mentioned, both Outcome and the context[ID] idiom 
		/// can be used by Command that return lists of values. 
		/// Commands that return a single set of fields 
		/// can store the result directly in the main Context. 
		/// This strategy allows one Command to obtain field values 
		/// to be used by another Command 
		/// (like piping output between Unix shell commands.)
		/// </p><p>
		/// Note that "Outcome" is an "alias" to an entry in 
		/// this context. 
		/// Unlike FieldState, Outcome is not a subcontext 
		/// in its own right. 
		/// </p></remarks>
		///	
		object Outcome { get; set; }

		/// <summary>
		/// Indicate whether a Criteria is present.
		/// </summary>
		/// <returns>True if a Criteria is present.</returns>
		bool HasCriteria();

		/// <summary>
		/// Provide an optional subcontext containing input or output 
		/// values, usually expressed as display strings.
		/// </summary>
		/// <remarks>
		/// <p>
		/// Criteria is provided for Commands that accept input 
		/// from other components which may need to be validated, 
		/// converted, or formatted before use.
		/// If the proposed FieldState is accepted, 
		/// the entries may be merged into the root Context, 
		/// perhaps after type conversion or formatting tasks.
		/// If the proposed FieldState is not accepted, 
		/// the entries are not merged into the root Context, 
		/// and there should be Errors or a Fault explaining 
		/// why the FieldState (e.g input) cannot be accepted.
		/// </p>
		/// <p>
		/// In practice, it is expected, but not required, that 
		/// all the FieldState entries will contain string values.
		/// </p>
		/// <p>
		/// Commands should only act on the Criteria in order 
		/// to transfer values between the FieldState and the 
		/// root Context. 
		/// Conventional Commands will look to the root Context 
		/// for the state and make any expected changes 
		/// or additions directly to the root context.
		/// FieldState is not expected to be used by a Commands 
		/// unless input is being submitted from an untrusted or 
		/// naive component, or needs to be transformed for use 
		/// by a display component.
		/// </p>
		/// </remarks>
		IDictionary Criteria { get; set; }

		#endregion 

		#region Messaging

		string FormatTemplate(string template, string value);

		/// <summary>
		/// Record a list of alert (or error) messages, 
		/// keyed by the field causing the message, 
		/// or to a magic global key.
		/// </summary>
		/// <remark><p>
		/// TODO: Refactor as NameValueCollection ?
		/// </p></remark>
		/// 
		IDictionary Alerts { get; set; }

		/// <summary>
		/// Add an alert message under the "global" key.
		/// </summary>
		/// <param name="template">Message template.</param>
		/// 
		void AddAlert(string template);

		/// <summary>
		/// Add an alert message, creating the context if needed. 
		/// </summary>
		/// <remarks>
		/// Multiple messages can be added for a key and retrieved as a List.
		/// </remarks>
		/// <param name="template">Message template.</param>
		/// <param name="message">Message key.</param>
		void AddAlert(string template, string message);

		/// <summary>
		/// Add a formatted "Alert" error message
		/// for the given field key via the FieldTable.
		/// </summary>
		/// <param name="key">Key from the FieldTable</param>
		void AddAlertForField(string key);

		/// <summary>
		/// Add a formatted "Required" error message
		/// for the given field key via the FieldTable.
		/// </summary>
		/// <param name="key">Key from the FieldTable</param>
		void AddAlertRequired(string key);

		/// <summary>
		/// Indicate whether alerts exist.
		/// </summary>
		/// <returns>True if there are alerts.</returns>
		/// 
		bool HasAlerts { get; }

		/// <summary>
		/// Record an Exception, if thrown.
		/// </summary>
		/// 
		Exception Fault { get; set; }

		/// <summary>
		/// Indicate whether an Exception was caught.
		/// </summary>
		/// <returns>True if an Exception was caught.</returns>
		/// 
		bool HasFault { get; }

		/// <summary>
		/// Indicate whether context is free of fault and alerts.
		/// </summary>
		/// <returns>True if there are no fault or alerts.</returns>
		/// 
		bool IsNominal { get; }

		/// <summary>
		/// Record hint (advisory or warning) messages (!errors), 
		/// keyed by the field causing the message, 
		/// or to a magic global key.
		/// </summary>
		/// 
		IDictionary Hints { get; set; }

		/// <summary>
		/// Add a hint, creating the context if needed.
		/// </summary>
		/// <remarks><p>
		/// Multiple hints can be added for a key and 
		/// retrieved as a List.
		/// </p></remarks>
		/// <param name="template">Message template.</param>
		/// <param name="message">Message key.</param>
		/// 
		void AddHint(string template, string message);

		/// <summary>
		/// Add a hint under the "global" key.
		/// </summary>
		/// <param name="template">Message template.</param>
		/// 
		void AddHint(string template);

		/// <summary>
		/// Indicate whether hints exist.
		/// </summary>
		/// <returns>True if there are hints.</returns>
		/// 
		bool HasHints { get; }

		#endregion
	}
}