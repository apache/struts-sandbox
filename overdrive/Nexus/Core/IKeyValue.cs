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
	/// Represent a key/value pair, 
	/// as stored in an IDictionary or displayed 
	/// by a list in a user interface.
	/// </summary>
	/// <remarks>
	/// The Text method returns the string form of Value,
	/// which is useful for text-based controls.
	/// </remarks>
	/// 
	public interface IKeyValue
	{
		/// <summary>
		/// The Key property under which the Value is stored.
		/// </summary>
		/// 
		string Key { get; set; }

		/// <summary>
		/// The Value stored for the Key.
		/// </summary>
		/// 
		object Value { get; set; }


		/// <summary>
		/// The Value in its standard string format.
		/// </summary>
		/// 
		string Text { get; set; }

	}
}