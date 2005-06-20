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
			foreach (string key in list)
			{
				Assert.IsNotNull (key, "Expected each item to be non-null");
				Assert.IsTrue (key.Length > 0, "Expected each item to be non-empty");
			}
			IDictionary keys = new Hashtable (list.Count);
			foreach (string key in list)
			{
				if (keys.Contains (key)) Assert.Fail (key + ": Expected each item to be unique");
				keys.Add (key, key);
			}
		}

		/// <summary>
		/// Exercise the List Last Name command.
		/// </summary>
		/// 
		[Test]
		public void TestLastNameFilterList ()
		{
			IRequestContext context = catalog.ExecuteRequest (App.LIST_LAST_NAMES);
			FilterList_Result (context);
		}

	}
}