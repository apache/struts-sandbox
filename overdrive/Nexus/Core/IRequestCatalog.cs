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
using System.Collections;
using Agility.Core;
using Nexus.Core.Helpers;
using Nexus.Core.Tables;

namespace Nexus.Core
{
	/// <summary>
	///  Extend ICatalog to automatically set IFieldTable, 
	///  IMessageTable and provide convenience methods [OVR-8]. 
	/// </summary>
	/// 
	public interface IRequestCatalog : ICatalog
	{
		/// <summary>
		/// Provide the FieldTable for this Catalog.
		/// </summary>
		/// <remarks><p>
		/// The GetRequest methods "stamp" the Context 
		/// with a reference to the FieldTable, 
		/// among other things.
		/// </p></remarks>
		IFieldTable FieldTable { get; set; }

		/// <summary>
		/// Execute before a Command called via ExecuteView. 
		/// </summary>
		/// <remarks><p>
		/// Of course, a IRequestChain may be used here too.
		/// </p></remarks>
		/// 
		IRequestCommand PreOp { get; set; }

		/// <summary>
		/// Execute after a Command called via ExecuteView. 
		/// </summary>
		/// <remarks><p>
		/// Of course, a IRequestChain may be used here too.
		/// </p></remarks>
		/// 
		IRequestCommand PostOp { get; set; }

		/// <summary>
		/// Default IViewHelper instance for this Catalog.
		/// </summary>
		/// <remarks><p>
		/// Set in catalogs for applications that use ViewHelpers.
		/// The object should be a non-singleton instance ("protype").
		/// Used by GetHelperFor.
		/// </p></remarks>
		/// 
		IViewHelper ViewHelper { get; set; }

		/// <summary>
		/// Obtain an object for ID.
		/// </summary>
		/// <param name="name">Our object ID</param>
		/// <returns>object for name</returns>
		object GetObject(string name);

		/// <summary>
		/// Obtain a default IViewHelper instance, 
		/// configured for the specified command.
		/// </summary>
		/// <param name="command">The Command ID</param>
		/// <returns>Helper instance for command</returns>
		IViewHelper GetHelperFor(string command);

		/// <summary>
		/// Obtain Command and verify that instance is a IRequestCommand.
		/// </summary>
		/// <param name="command">Command ID</param>
		/// <returns>IRequestCommand instance for name</returns>
		/// <exception cref="Exception">
		/// Throws Exception if name is null, 
		/// name is not in catalog, 
		/// or if instance for name is not a IRequestCommand
		/// </exception>
		IRequestCommand GetRequestCommand(string command);

		/// <summary>
		/// Obtain a IRequestContext for command ID, 
		/// including embedded resources like the FieldTable,
		/// </summary>
		/// <param name="name">Our command ID</param>
		/// <returns>IRequestContext with embedded resources.</returns>
		/// 
		IRequestContext GetRequestContext(string name);

		/// <summary>
		/// Obtain a IRequestContext for command ID, 
		/// including embedded resources like the FieldTable,
		/// and process string-based input. 
		/// </summary>
		/// <param name="name">Our command ID</param>
		/// <param name="input">Our input values</param>
		/// <returns>IRequestContext with embedded resources.</returns>
		/// 
		IRequestContext GetRequestContext(string name, IDictionary input);

		/// <summary>
		/// Obtain a IRequestContext for the command, 
		/// including embedded resources.
		/// </summary>
		/// <param name="command">Our command</param>
		/// <returns>IRequestContext with embedded resources.</returns>
		/// 
		IRequestContext GetRequestContext(IRequestCommand command);

		/// <summary>
		/// Obtain and execute a IRequestContext.
		/// </summary>
		/// <param name="name">Our command ID</param>
		/// <returns>Context after execution</returns>
		/// 
		IRequestContext ExecuteRequest(string name);

		/// <summary>
		/// Execute a IRequestContext.
		/// </summary>
		/// <param name="context">Context to execute</param>
		/// 
		void ExecuteRequest(IRequestContext context);

		/// <summary>
		/// Execute a IRequestContext as part of a chain 
		/// created with the PreOp and PostOp commands (if any).
		/// </summary>
		/// <remarks><p>
		/// Among other things, the PreOp/PostOp chain may transfer 
		/// data between the Criteria and the root Context.
		/// </p><p>
		/// The PreOp/PostOp chain acts as a Front Controller 
		/// in that it ensures certain tasks are perform 
		/// upon every request. 
		/// </p><p>
		/// IViewHelper implementations are expected to 
		/// call ExecuteView to "invoke the Helper's command".
		/// </p></remarks>
		/// <param name="context">Context to execute</param>
		/// 
		void ExecuteView(IRequestContext context);

	}
}