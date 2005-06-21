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
		/// Obtains a IViewHelper for helper ID.
		/// </summary>
		/// <param name="name">Our helper ID</param>
		/// <returns>IViewHelper or null</returns>
		IViewHelper GetHelper (string name);

		/// <summary>
		/// Obtain a IRequestContext for command ID, 
		/// including embedded resources.
		/// </summary>
		/// <param name="name">Our command ID</param>
		/// <returns>IRequestContext with embedded resources.</returns>
		/// 
		IRequestContext GetRequest (string name);

		/// <summary>
		/// Obtain a IRequestContext for command ID, 
		/// including embedded resources, 
		/// and process string-based input. 
		/// </summary>
		/// <param name="name">Our command ID</param>
		/// <param name="input">Our input values</param>
		/// <returns>IRequestContext with embedded resources.</returns>
		/// 
		IRequestContext GetRequest (string name, IDictionary input);

		/// <summary>
		/// Obtain a IRequestContext for the command, 
		/// including embedded resources.
		/// </summary>
		/// <param name="command">Our command</param>
		/// <returns>IRequestContext with embedded resources.</returns>
		/// 
		IRequestContext GetRequest (IRequestCommand command);

		/// <summary>
		/// Obtain and execute a IRequestContext.
		/// </summary>
		/// <param name="name">Our command ID</param>
		/// <returns>Context after execution</returns>
		/// 
		IRequestContext ExecuteRequest (string name);

		/// <summary>
		/// Execute a IRequestContext.
		/// </summary>
		/// <param name="context">Context to execute</param>
		/// 
		void ExecuteRequest (IRequestContext context);

		/// <summary>
		/// Execute a IRequestContext as part of a View layer chain.
		/// </summary>
		/// <remarks><p>
		/// Among other things, the View layer chain may transfer 
		/// data between the FieldState and the root Context. 
		/// The View layer chain acts as a Front Controller.
		/// </p></remarks>
		/// <param name="context">Context to execute</param>
		/// 
		void ExecuteView (IRequestContext context);

	}
}