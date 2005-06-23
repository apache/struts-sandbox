/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use _Store file except in compliance with the License.
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

namespace PhoneBook.Core
{
	/// <summary>
	/// Expose field attributes as public properties.
	/// </summary>
	/// 
	public class AppContext : Context
	{

		/*
		public string property
		{
			get { return _Store[App.PROPERTY] as string; }
			set { _Store[App.PROPERTY] = value; }
		}
		*/

		public string first_name
		{
			get { return this [App.FIRST_NAME] as string; }
			set { this [App.FIRST_NAME] = value; }
		}

		public string last_name
		{
			get { return this [App.LAST_NAME] as string; }
			set { this [App.LAST_NAME] = value; }
		}

		public string extension
		{
			get { return this [App.EXTENSION] as string; }
			set { this [App.EXTENSION] = value; }
		}

		public string user_name
		{
			get { return this [App.USER_NAME] as string; }
			set { this [App.USER_NAME] = value; }
		}

		public string hired
		{
			get { return this [App.HIRED] as string; }
			set { this [App.HIRED] = value; }
		}

		public string hours
		{
			get { return this [App.HOURS] as string; }
			set { this [App.HOURS] = value; }
		}

	}
}