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

namespace Nexus.Core
{
	/// <summary>
	/// Interact with the caller, controlling and managing 
	/// the processing of a request [OVR-8]. 
	/// </summary>
	/// <remarks><p>
	/// The caller should only need to know the name of a Command 
	/// to be able to acquire the appropriate Context, and then execute the request. 
	/// </p></remarks>
	public interface IController
	{
		/// <summary>
		/// Obtain object instance for name.
		/// </summary>
		/// <param name="name"></param>
		/// <returns></returns>
		/// 
		object GetObject (string name);

		/// <summary>
		/// Obtain and execute the IRequestContext.
		/// </summary>
		/// <param name="command">Our command name</param>
		/// <returns>Context after execution</returns>
		/// 
		IRequestContext ExecuteContext (string command);
	}
}