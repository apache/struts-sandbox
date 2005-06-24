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

namespace PhoneBook.Core
{
	/// <summary>
	/// Tokens representing context keys.
	/// </summary>
	/// 
	public class App
	{
		private App ()
		{
			// No need to construct static helper class
		}

		/// <summary>
		/// Token for entry_key property.
		/// </summary>
		/// 
		public const string ENTRY_KEY = "entry_key";

		/// <summary>
		/// Token for first_name property.
		/// </summary>
		/// 
		public const string FIRST_NAME = "first_name";

		/// <summary>
		/// Token for last_name property.
		/// </summary>
		/// 
		public const string LAST_NAME = "last_name";

		/// <summary>
		/// Token for user_name property.
		/// </summary>
		/// 
		public const string USER_NAME = "user_name";

		/// <summary>
		/// Token for extension property.
		/// </summary>
		/// 
		public const string EXTENSION = "extension";

		/// <summary>
		/// Token for hired property.
		/// </summary>
		/// 
		public const string HIRED = "hired";

		/// <summary>
		/// Token for hours property.
		/// </summary>
		/// 
		public const string HOURS = "hours";

		/// <summary>
		/// Token for editor property.
		/// </summary>
		/// 
		public const string EDITOR = "editor";

		/// <summary>
		/// Token for select all command.
		/// </summary>
		/// 
		public const string FILTER = "filter";

		/// <summary>
		/// Token for List Last Names command.
		/// </summary>
		/// 
		public const string LAST_NAME_LIST = "last_name_list";

		/// <summary>
		/// Token for List Last Names command.
		/// </summary>
		/// 
		public const string FIRST_NAME_LIST = "first_name_list";

		/// <summary>
		/// Token for List Extensions command.
		/// </summary>
		/// 
		public const string EXTENSION_LIST = "extension_list";

		/// <summary>
		/// Token for List UserNames command.
		/// </summary>
		/// 
		public const string USER_NAME_LIST = "user_name_list";

		/// <summary>
		/// Token for List Hire Dates command.
		/// </summary>
		/// 
		public const string HIRED_LIST = "hired_list";

		/// <summary>
		/// Token for List Hours command.
		/// </summary>
		/// 
		public const string HOURS_LIST = "hours_list";

		/// <summary>
		/// Token for Directory View command.
		/// </summary>
		/// 
		public const string DIRECTORY_VIEW = "directory_view";

		/// <summary>
		/// Token for Directory Find helper.
		/// </summary>
		/// 
		public const string DIRECTORY_FIND_HELPER = "directory_find_helper";

		/// <summary>
		/// Token for Directory list helper.
		/// </summary>
		/// 
		public const string DIRECTORY_LIST_HELPER = "directory_list_helper";
	}
}