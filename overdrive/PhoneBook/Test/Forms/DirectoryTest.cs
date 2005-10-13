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
	[TestFixture]
	public class DirectoryTest : WebFormTestCase
	{
		private PanelTester pnlFind;
		private DropDownListTester last_name_list;
		private DropDownListTester first_name_list;
		private DropDownListTester extension_list;
		private DropDownListTester user_name_list;
		private DropDownListTester hired_list;
		private DropDownListTester hours_list;
		// TODO: private DropDownListTester editor_list;
		private ButtonTester cmdListAll;

		private PanelTester pnlList;
		private DataGridTester repList;
		private ButtonTester cmdAdd;

		/// <summary>
		/// Provide an array of the DropDownListTesters.
		/// </summary>
		/// <returns>An array of the DropDownListTesters</returns>
		/// 
		private DropDownListTester[] GetLists()
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

			pnlFind = new PanelTester("pnlFind", CurrentWebForm);
			last_name_list = new DropDownListTester(App.LAST_NAME_LIST, CurrentWebForm);
			first_name_list = new DropDownListTester(App.FIRST_NAME_LIST, CurrentWebForm);
			extension_list = new DropDownListTester(App.EXTENSION_LIST, CurrentWebForm);
			user_name_list = new DropDownListTester(App.USER_NAME_LIST, CurrentWebForm);
			hired_list = new DropDownListTester(App.HIRED_LIST, CurrentWebForm);
			hours_list = new DropDownListTester(App.HOURS_LIST, CurrentWebForm);
			// TODO: editor_list = new DropDownListTester (App.EDITOR_LIST, CurrentWebForm);
			cmdListAll = new ButtonTester("cmdListAll", CurrentWebForm);

			pnlList = new PanelTester("pnlList", CurrentWebForm);
			repList = new DataGridTester("repList", CurrentWebForm);
			cmdAdd = new ButtonTester("cmdAdd", CurrentWebForm);

			Browser.GetPage("http://localhost/PhoneBook/Forms/Directory.aspx");
		}


		/// <summary>
		/// Confirm whether Controls are visible or not visible.
		/// </summary>
		/// 
		[Test]
		public void FindControls()
		{
			AssertVisibility(pnlFind, true);
			foreach (DropDownListTester list in GetLists())
			{
				AssertVisibility(list, true);
			}
			AssertVisibility(cmdListAll, true);
			AssertVisibility(pnlList, true);
			AssertVisibility(repList, true);
			AssertVisibility(cmdAdd, true); // Visible if Editor
		}

		/// <summary>
		/// Confirm that filter lists have items.
		/// </summary>
		/// 
		[Test]
		public void ListControls()
		{
			foreach (DropDownListTester list in GetLists())
			{
				bool ok = (list.Items.Count > 0);
				AssertEquals(list.HtmlId + ": Expected all filter lists to have items", true, ok);
			}
		}

	}
}