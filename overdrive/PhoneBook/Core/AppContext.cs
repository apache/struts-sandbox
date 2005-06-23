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
using System.Collections;

namespace PhoneBook.Core
{
	/// <summary>
	/// Adapt a properties class to use an IDictionary for storage.
	/// </summary>
	/// 
	public class AppContext
	{
		#region Constructors

		/// <summary>
		/// Instantiate with zero parameters.
		/// </summary>
		public AppContext ()
		{
			_Value = new Hashtable ();
		}

		/// <summary>
		/// Instantiate from a IDictionary.
		/// </summary>
		/// <param name="dictionary">New values for properties</param>
		public AppContext (IDictionary dictionary)
		{
			_Value = dictionary;
		}

		#endregion

		private IDictionary _Value;
		public IDictionary Value
		{
			get { return _Value; }
		}

		/*
		public string property
		{
			get { return _Store[App.PROPERTY] as string; }
			set { _Store[App.PROPERTY] = value; }
		}
		*/

		public string first_name
		{
			get { return _Value [App.FIRST_NAME] as string; }
			set { _Value [App.FIRST_NAME] = value; }
		}

		public string last_name
		{
			get { return _Value [App.LAST_NAME] as string; }
			set { _Value [App.LAST_NAME] = value; }
		}

		public string extension
		{
			get { return _Value [App.EXTENSION] as string; }
			set { _Value [App.EXTENSION] = value; }
		}

		public string user_name
		{
			get { return _Value [App.USER_NAME] as string; }
			set { _Value [App.USER_NAME] = value; }
		}

		public string hired
		{
			get { return _Value [App.HIRED] as string; }
			set { _Value [App.HIRED] = value; }
		}

		public string hours
		{
			get { return _Value [App.HOURS] as string; }
			set { _Value [App.HOURS] = value; }
		}

	}
}