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
	/// </p></remarks>
	/// 
	public interface IRequestContext : IContext
	{
		/// <summary>
		/// Identifier for the top-level Command (or Chain) processing 
		/// this Context.
		/// </summary>
		/// <remarks><P>
		/// Corresponds to ID of INexusCommand for the initial Command 
		/// or Chain.
		/// </P></remarks>
		/// 
		string Command { get; set; }

		/// <summary>
		/// Instance of the top-level Command (or Chain) processing this 
		/// Context.
		/// </summary>
		/// <remarks><p>
		/// Corresponds to ID of INexusCommand for the initial Command 
		/// or Chain.
		/// </p></remarks>
		/// 
		IRequestCommand CommandBin { get; set; }


		/// <summary>
		/// Instance of the global Field Table for this application.
		/// </summary>
		/// <remarks><p>
		/// Corresponds to ID of INexusCommand for the initial Command or 
		/// Chain.
		/// </p></remarks>
		/// 
		IFieldTable FieldTable { get; set; }


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
		/// </p><p>
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
		/// A list of alert (or error) messages, 
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
		void AddAlert (string template);

		/// <summary>
		/// Indicate whether alerts exist.
		/// </summary>
		/// <returns>True if there are alerts.</returns>
		/// 
		bool HasAlerts { get; }

		/// <summary>
		/// An Exception, if thrown.
		/// </summary>
		/// <remark>
		/// A IViewContext is readonly, 
		/// but another interface (e.g. IHelperContext) may extend to add a 
		/// setter, if needed.
		/// </remark>
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
		/// A list of hint (advisory or warning) messages (!errors), 
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
		void AddHint (string template, string message);

		/// <summary>
		/// Add a hint under the "global" key.
		/// </summary>
		/// <param name="template">Message template.</param>
		/// 
		void AddHint (string template);

		/// <summary>
		/// Indicate whether hints exist.
		/// </summary>
		/// <returns>True if there are hints.</returns>
		/// 
		bool HasHints { get; }

	}
}