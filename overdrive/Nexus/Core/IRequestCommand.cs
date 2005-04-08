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
using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Use an IRequestContext to process a Command [OVR-9]
	/// </summary>
	/// <remarks>
	/// <p>
	/// Rather than have each command cast its context to an IRequestContext, 
	/// provide a IRequestCommand with an alternative signature. 
	/// </p>
	/// </remarks>
	public interface IRequestCommand : ICommand
	{

		/// <summary>
		/// An identifier for this Command. 
		/// </summary>
		/// <remarks>
		/// Corresponds to the Command property of IHelperContext. 
		/// </remarks>
		/// <returns>An identifier for this Command.</returns>
		string ID {get; set;}

		/// <summary>
		/// Factory method to provide an empty context that can be used with the Command instance.
		/// </summary>
		/// <returns>Context instance with Command ID set.</returns>
		IRequestContext NewContext ();

		/// <summary>
		/// Operations to perform with HelperContext.
		/// </summary>
		/// <remarks><p>
		/// Expected to be called from Execute as a casting convenience.
		/// </p></remarks>
		/// <param name="context">Context to process.</param>
		bool RequestExecute (IRequestContext context);
	
	}
}
