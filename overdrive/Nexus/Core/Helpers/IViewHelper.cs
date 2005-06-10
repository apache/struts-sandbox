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
	/// A facade for use by a code-behind to simplify access to the IRequestContext and IRequestCommand. 
	/// </summary>
	/// <remarks><p>
	/// The controller for a helper may also act as a "front controller" to ensure routine tasks are carried out.
	/// These tasks can include input validation, data conversion, text formatting, command logging, and so forth.
	/// </p></remarks>
	public interface IViewHelper
	{

		/// <summary>
		/// Invoke the helper's command and bind the output to controls in the given collection.
		/// </summary>
		/// <remarks><p>
		/// Most code behinds will call either ExecuteBind or ReadExecute by passing in the collection of controls from a panel control. 
		/// </p></remarks>
		/// <param name="controls">Collection of controls to populate.</param>
		/// 
		void ExecuteBind (ICollection controls);


		/// <summary>
		/// Read input from the controls in the given collection, and invoke the helper's command.
		/// </summary>
		/// <param name="controls">Collection of controls to populate.</param>
		/// <remarks><p>
		/// Most code behinds will call either ExecuteBind or ReadExecute by passing in the collection of controls from a panel control. 
		/// </p></remarks>
		/// 
		void ReadExecute (ICollection controls);


		/// <summary>
		/// Bind the output of the helper's command to controls in the given collection.
		/// </summary>
		/// <param name="controls">Collection of controls to populate.</param>
		/// 
		void Bind (ICollection controls);


		/// <summary>
		/// Invoke the helper's command.
		/// </summary>
		/// 
		void Execute();


		/// <summary>
		/// Read input from the controls in the given collection.
		/// </summary>
		/// <param name="controls">Collection of controls to populate.</param>
		/// 
		void Read (ICollection controls);


		// ----
		
		/// <summary>
		/// A list of error messages, keyed by the field causing the error, or to a magic global key.
		/// </summary>
		/// 
		IDictionary Errors { get; }


		/// <summary>
		/// Return true if errors are queued.
		/// </summary>
		/// <returns>True if errors are queued.</returns>
		bool HasErrors { get; }


		/// <summary>
		/// An Exception, if thrown.
		/// </summary>
		Exception Fault { get; }

		/// <summary>
		/// Return true if an exception is caught.
		/// </summary>
		/// <returns>True if an exception is caught.</returns>
		bool HasFault { get; }


		/// <summary>
		/// Return true if there are no errors or exception pending.
		/// </summary>
		/// <returns>True if all is well.</returns>
		bool IsNominal { get; }


		/// <summary>
		/// A list of text messages, keyed by a field or other identifier, or to a magic global key.
		/// </summary>
		/// 
		IDictionary Messages { get; }


		/// <summary>
		/// Return true if Messages are queued.
		/// </summary>
		/// <returns>True if Messages are queued.</returns>
		bool HasMessages { get; }


		// ----

		/// <summary>
		/// Set of IFieldContext definitions available to the application, usually set by the controller.
		/// </summary>
		/// <remarks><p>
		/// The FieldTable can be used to convert display strings to native types on input, 
		/// and from native types to display strings on output. 
		/// The FieldTable can also be used to generate UI controls. 
		/// </p></remarks>
		IFieldTable FieldTable { get;}


		/// <summary>
		/// Set of IFieldContext definitions to be used with this helper, usually set by dependency injection.
		/// </summary>
		/// <remarks><p>
		/// Some helpers generate DataGrids or DataForms based on the FieldDefinitions 
		/// </p></remarks>
		IList FieldSet {get;}
		
		/* 
			// TODO: 
			string Text(string key); 
			string TextIndex {get;}
			ITextTable TextTable {get;}
		*/

		
		// ----

		/// <summary>
		/// Prefix to trim from the id of a control during Read and Bind.
		/// </summary>
		/// <remarks><p>
		/// The Prefix is needed when a single page uses a control more than once  
		/// often in separate panels.
		/// </p></remarks>
		/// 
		string Prefix { get; set; }


		/// <summary>
		/// Suffix to truncate from a list control id in order to set a corresponding value field ["_list"].
		/// </summary>
		/// <remark><p>
		/// When processing a single-value list control, if the id ends with the list suffix, 
		/// the suffix is removed, and a field with the remaining name is set to the selected item value.
		/// </p><P>
		/// So, the selected item from a list control with the id "facility_key_list" will be 
		/// set to a field named "facility_key".
		/// </P></remark>
		/// 
		string ListSuffix {get; set;}

		/* 
			// TODO: 
			string AlertSuffix {get; set}
			string HintSuffix {get; set}
			string LabelSuffix {get; set}
		*/


		/// <summary>
		/// If a control value is an empty string, set the value to null instead [TRUE].
		/// </summary>
		/// 
		bool NullIfEmpty {get; set;}


		/// <summary>
		/// String token to insert as item 0 to a list controls ["--v---"].
		/// </summary>
		/// <remarks><p>
		/// To disable feature, set to a null string.
		/// </p></remarks>
		/// 
		string SelectItemPrompt {get; set;}


		// ----

		/// <summary>
		/// The controller for this helper, usually set by dependency injection.
		/// </summary>
		/// 
		IController Controller { get; set; }


		/// <summary>
		/// The command (or chain of commands) for this helper, usually set by dependency injection.
		/// </summary>
		/// 
		IRequestCommand Command { get; set; }

	}

}