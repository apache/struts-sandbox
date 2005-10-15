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
using System.Net;
using NUnit.Extensions.Asp;
using NUnit.Extensions.Asp.AspTester;
using NUnit.Framework;
using PhoneBook.Core;

namespace WNE.Core.Forms
{
	/// <summary>
	/// Exercise the Directory page controls.
	/// </summary>
	/// 
	[TestFixture]
	public class DirectoryTest : WebFormTestCase
	{
		/// <summary>
		/// Provide finder instance for testing.
		/// </summary>
		/// 
		private UserControlTester finder;

		/// <summary>
		/// Provide last_name_list instance for testing.
		/// </summary>
		/// 
		private DropDownListTester last_name_list;

		/// <summary>
		/// Provide first_name_list instance for testing.
		/// </summary>
		/// 
		private DropDownListTester first_name_list;

		/// <summary>
		/// Provide extension_list instance for testing.
		/// </summary>
		/// 
		private DropDownListTester extension_list;

		/// <summary>
		/// Provide user_name_list instance for testing.
		/// </summary>
		/// 
		private DropDownListTester user_name_list;

		/// <summary>
		/// Provide finder instance for testing.
		/// </summary>
		/// 
		private DropDownListTester hired_list;

		/// <summary>
		/// Provide hours_list instance for testing.
		/// </summary>
		/// 
		private DropDownListTester hours_list;

		// TODO: private DropDownListTester editor_list;

		/// <summary>
		/// Provide find instance for testing.
		/// </summary>
		/// 
		private ButtonTester find;

		/// <summary>
		/// Provide finder lister for testing.
		/// </summary>
		/// 
		private UserControlTester lister;

		/// <summary>
		/// Provide list instance for testing.
		/// </summary>
		/// 
		private DataGridTester list;

		/// <summary>
		/// Provide add instance for testing.
		/// </summary>
		/// 
		private ButtonTester add;

		/// <summary>
		/// Provide an array of the DropDownListTesters.
		/// </summary>
		/// <returns>An array of the DropDownListTesters</returns>
		/// 
		private DropDownListTester[] GetFilters()
		{
			DropDownListTester[] lists = {last_name_list, first_name_list, extension_list, user_name_list, hired_list, hours_list};
			return lists;
		}

		/// <summary>
		/// Instantiate the control testers.
		/// </summary>
		/// 
		protected override void SetUp()
		{
			base.SetUp();
			string[] userLanguages = {"en-us"};
			Browser.UserLanguages = userLanguages;
			Browser.Credentials = CredentialCache.DefaultCredentials;

			finder = new UserControlTester("finder", CurrentWebForm);
			last_name_list = new DropDownListTester(App.LAST_NAME_LIST, finder);
			first_name_list = new DropDownListTester(App.FIRST_NAME_LIST, finder);
			extension_list = new DropDownListTester(App.EXTENSION_LIST, finder);
			user_name_list = new DropDownListTester(App.USER_NAME_LIST, finder);
			hired_list = new DropDownListTester(App.HIRED_LIST, finder);
			hours_list = new DropDownListTester(App.HOURS_LIST, finder);
			// TODO: editor_list = new DropDownListTester (App.EDITOR_LIST, CurrentWebForm);
			find = new ButtonTester("find", finder);

			lister = new UserControlTester("lister", CurrentWebForm);
			list = new DataGridTester("list", lister);
			add = new ButtonTester("add", lister);

			Browser.GetPage("http://localhost/PhoneBook/Forms/Directory2.aspx");
		}


		/// <summary>
		/// Confirm whether Controls are visible or not visible.
		/// </summary>
		/// 
		[Test]
		public void FindControls()
		{
			foreach (DropDownListTester filter in GetFilters())
			{
				AssertVisibility(filter, true);
			}
			AssertVisibility(find, true);

			AssertVisibility(list, true);
			AssertVisibility(add, true); // Visible if Editor
		}

		/// <summary>
		/// Confirm that filter lists have items.
		/// </summary>
		/// 
		[Test]
		public void ListControls()
		{
			foreach (DropDownListTester filter in GetFilters())
			{
				bool ok = (filter.Items.Count > 0);
				AssertEquals(filter.HtmlId + ": Expected all filter lists to have items", true, ok);
			}
		}

	}
}