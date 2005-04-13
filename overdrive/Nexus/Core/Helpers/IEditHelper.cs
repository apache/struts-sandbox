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

namespace Nexus.Core.Helpers
{
	/// <summary>
	/// Helper for editing items, including items on a list [OVR-11].
	/// </summary>
	public interface IEditHelper : IListHelper
	{
		/// <summary>
		/// Standard text to display for an Edit command (e.g. button).
		/// </summary>		
		string EditText { get; set; }

		/// <summary>
		/// Standard text to display for an Quit command (e.g. button).
		/// </summary>		
		string QuitText { get; set; }

		/// <summary>
		/// Standard text to display for an Save command (e.g. button).
		/// </summary>		
		string SaveText { get; set; }

		/// <summary>
		/// The ID of the command to execute for a Save operation. 
		/// </summary>		
		string SaveCommand { get; set; }
	}
}