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
using Nexus.Core;
using Nexus.Core.Helpers;
using NUnit.Framework;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Exercise the various lists of distinct values 
	/// that are used to filter the directory.
	/// </summary>
	/// 
	[TestFixture]
	public class FilterListsTest : BaseTest
	{
		/// <summary>
		/// Confirm that a list is returned as the outcome, 
		/// and that each item on the list is not-empty and unique.
		/// </summary>
		/// <param name="context">Context returned by command</param>
		/// 
		private void FilterList_Result (IRequestContext context)
		{
			IList list = AssertListOutcome (context);
			foreach (IKeyValue item in list)
			{
				Assert.IsNotNull (item, "Expected each item to be non-null");
				object key = item.Value;
				Assert.IsNotNull (key, "Expected each key to be non-null");
				string keystring = key.ToString ();
				Assert.IsTrue (keystring.Length > 0, "Expected each key to be non-empty");
			}
			IDictionary keys = new Hashtable (list.Count);
			foreach (IKeyValue item in list)
			{
				string key = item.Value.ToString ();
				if (keys.Contains (key)) Assert.Fail (key + ": Expected each item to be unique");
				keys.Add (key, key);
			}
		}

		/// <summary>
		/// Exercise the filter commands.
		/// </summary>
		/// 
		[Test]
		public void TestFilterLists ()
		{
			string[] FILTERS = {App.LAST_NAME_LIST, App.FIRST_NAME_LIST, App.EXTENSION_LIST, App.USER_NAME_LIST, App.HIRED_LIST, App.HOURS_LIST};
			foreach (string filter in FILTERS)
			{
				IRequestContext context = catalog.ExecuteRequest (filter);
				FilterList_Result (context);
			}
		}

		private IKeyValueList FilterList (string key)
		{
			IViewHelper helper = catalog.GetHelper ("directory_view_helper");
			helper.Execute ();
			IKeyValueList list = helper.Criteria [key] as IKeyValueList;
			Assert.IsNotNull (list, "Expected KeyValueList");
			return list;
		}

		[Test]
		public void TestFilterFormat_extension ()
		{
			IKeyValueList list = FilterList (App.EXTENSION_LIST);
			foreach (IKeyValue item in list)
			{
				string key = item.Value as string;
				Assert.IsTrue (key.Length > "1234567890".Length, "Expected formatted extension, not: " + key);
			}
		}

		[Test]
		public void TestFilterFormat_hired ()
		{
			IKeyValueList list = FilterList (App.HIRED_LIST);
			foreach (IKeyValue item in list)
			{
				string key = item.Value as string;
				bool okay = (key.Length > 0) && (key.Length < "##/##/#### ".Length);
				Assert.IsTrue (okay, "Expected short date format, not: " + key);
			}
		}
	}
}