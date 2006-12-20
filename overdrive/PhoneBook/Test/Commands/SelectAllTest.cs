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
using System;
using System.Collections;
using Nexus.Core;
using Nexus.Core.Helpers;
using NUnit.Framework;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Exercise SelectAll Command per [OVR-5].
	/// </summary>
	/// 
	[TestFixture]
	public class SelectAllTest : BaseTest
	{
		/// <summary>
		/// Assert result of SelectAll, after another method runs the command.
		/// </summary>
		/// <param name="context">Context with result to assert.</param>	
		/// 	
		private void SelectAll_Result(IRequestContext context)
		{
			IList list = AssertListOutcome(context);
			IDictionary row = list[0] as IDictionary;
			Assert.IsNotNull(row, "Expected list entry to be an IDictionary.");
			string[] KEYS = {App.FIRST_NAME, App.LAST_NAME, App.USER_NAME, App.EXTENSION, App.HIRED, App.HOURS, App.EDITOR};
			bool valid = true;
			foreach (string key in KEYS)
			{
				valid = valid && row.Contains(key);
			}
			Assert.IsTrue(valid, "Expected row to contain all keys.");
		}


		/// <summary>
		/// Filter all and succeed, using catalog.
		/// </summary>
		/// 
		[Test]
		public void SelectAll_Pass()
		{
			IRequestContext context = catalog.ExecuteRequest(App.ENTRY_LIST);
			SelectAll_Result(context);
		}

		/// <summary>
		/// Exercise Entry List and validate hired and extension string formatting.
		/// </summary>
		/// 
		[Test]
		public void FilterHelper_Format()
		{
			IViewHelper helper = catalog.GetHelperFor(App.ENTRY_LIST);
			helper.Execute();
			AssertNominal(helper);
			AppEntryList list = helper.Outcome as AppEntryList;
			Assert.IsNotNull(list, "Expected list to be AppEntryList");
			AppEntry row = list[0] as AppEntry;
			Assert.IsNotNull(row, "Expected rows to be AppEntries");

			string hired = row.hired;
			Assert.IsNotNull(hired, "Expected each row to have a hired date.");
			Assert.IsTrue(hired.Length < "##/##/#### ".Length, hired + ": Expected short date format.");

			string extension = row.extension;
			Assert.IsNotNull(extension, "Expected each row to have an extension.");
			Assert.IsTrue(extension.Length > "1234567890".Length, extension + ": Expected formatted extension.");
		}

		/// <summary>
		/// Exercise custom paging 
		/// (retrieve only visible section of the result se).
		/// </summary>
		/// 
		[Test]
		public void SelectAll_Limit()
		{
			IViewHelper helper = catalog.GetHelperFor(App.ENTRY_LIST);
			helper.Criteria[App.ITEM_LIMIT] = 2;
			helper.Criteria[App.ITEM_OFFSET] = 0;
			helper.Execute();
			if (!helper.IsNominal) Assert.Fail(helper.AlertsText);
			IList list = helper.Outcome;
			Assert.IsTrue(list.Count == 2, "Expected result set to be limited to two entries.");
			AppEntry entry = list[0] as AppEntry;
			helper.Criteria[App.ITEM_LIMIT] = 2;
			helper.Criteria[App.ITEM_OFFSET] = 3;
			helper.Execute();
			IList list2 = helper.Outcome;
			AppEntry entry2 = list2[0] as AppEntry;
			Assert.IsFalse(entry.entry_key.Equals(entry2.entry_key), "Expected result sets to be different");
			int count = Convert.ToInt32(helper.Criteria[App.ITEM_COUNT]);
			Assert.IsTrue(count > 2, "Expected the overall count to be higher");
		}

	}
}