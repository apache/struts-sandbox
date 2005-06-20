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
using NUnit.Extensions.Asp;
using NUnit.Extensions.Asp.AspTester;
using NUnit.Framework;

namespace WNE.Core.Forms
{
	/// <summary>
	/// Exercise the Directory page controls.
	/// </summary>
	[TestFixture]
	public class DirectoryTest : WebFormTestCase
	{
		private PanelTester pnlFind;
		private DropDownListTester lstLastName;
		private DropDownListTester lstFirstName;
		private DropDownListTester lstExtension;
		private DropDownListTester lstUserName;
		private DropDownListTester lstHireDate;
		private DropDownListTester lstHours;
		private DropDownListTester lstEditor;
		private ButtonTester cmdListAll;
		private ButtonTester cmdPrint;

		private PanelTester pnlList;
		private DataGridTester repList;
		private ButtonTester cmdAdd;

		/// <summary>
		/// Provide an array of the DropDownListTesters.
		/// </summary>
		/// <returns>An array of the DropDownListTesters</returns>
		/// 
		private DropDownListTester[] GetLists ()
		{
			DropDownListTester[] lists = {lstLastName, lstFirstName, lstExtension, lstUserName, lstHireDate, lstHours, lstEditor};
			return lists;
		}

		/// <summary>
		/// Instantiate the control testers.
		/// </summary>
		/// 
		protected override void SetUp ()
		{
			base.SetUp ();

			pnlFind = new PanelTester ("pnlFind", CurrentWebForm);
			lstLastName = new DropDownListTester ("lstLastName", CurrentWebForm);
			lstFirstName = new DropDownListTester ("lstFirstName", CurrentWebForm);
			lstExtension = new DropDownListTester ("lstExtension", CurrentWebForm);
			lstUserName = new DropDownListTester ("lstUserName", CurrentWebForm);
			lstHireDate = new DropDownListTester ("lstHireDate", CurrentWebForm);
			lstHours = new DropDownListTester ("lstHours", CurrentWebForm);
			lstEditor = new DropDownListTester ("lstEditor", CurrentWebForm);
			cmdListAll = new ButtonTester ("cmdListAll", CurrentWebForm);
			cmdPrint = new ButtonTester ("cmdPrint", CurrentWebForm);

			pnlList = new PanelTester ("pnlList", CurrentWebForm);
			repList = new DataGridTester ("repList", CurrentWebForm);
			cmdAdd = new ButtonTester ("cmdAdd", CurrentWebForm);

			Browser.GetPage ("http://localhost/PhoneBook/Forms/Directory.aspx");
		}


		/// <summary>
		/// Confirm whether Controls are visible or not visible.
		/// </summary>
		/// 
		[Test]
		public void FindControls ()
		{
			WebAssert.Visible (pnlFind);
			foreach (DropDownListTester list in GetLists ())
			{
				WebAssert.Visible (list);
			}
			WebAssert.Visible (cmdListAll);
			WebAssert.Visible (cmdPrint);
			WebAssert.Visible (pnlList);
			WebAssert.Visible (repList);
			WebAssert.NotVisible (cmdAdd); // Visible if Editor
		}

		/// <summary>
		/// Confirm that filter lists have items.
		/// </summary>
		/// 
		[Test]
		public void ListControls ()
		{
			foreach (DropDownListTester list in GetLists ())
			{
				Assert.IsTrue (list.Items.Count > 0, list.HtmlId + ": Expected all filter lists to have items.");
			}
		}

	}
}