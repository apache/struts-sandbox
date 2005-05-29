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
	/// Exercise SelectAll Command per [OVR-5].
	/// </summary>
	[TestFixture]
	public class SelectAllTest : BaseTest
	{

		/// <summary>
		/// Assert result of SelectAll, after another method runs the command.
		/// </summary>
		/// <param name="context">Context with result to assert.</param>		
		private void SelectAll_Result(IRequestContext context)
		{
			AssertNominal(context);
			Assert.IsTrue (context.HasOutcome,"Expected command to set an Outcome.");
			IList list = context.Outcome as IList;
			bool notEmpty = ((list!=null) && (list.Count>0));
			Assert.IsTrue (notEmpty,"Expected outcome to be a not-empty list");
			IDictionary row = list[0] as IDictionary;
			string[] KEYS = {App.FIRST_NAME, App.LAST_NAME, App.USER_NAME, App.EXTENSION, App.HIRED, App.HOURS, App.EDITOR};
			bool valid = true;
			foreach (string key in KEYS)
			{
				valid = valid && row.Contains (key);
			}
			Assert.IsTrue (valid,"Expected row to contain all keys.");
		}

		/// <summary>
		/// SelectAll and succeed, without using Catalog.
		/// </summary>
		[Test]
		public void SelectAll_Pass_Without_Catalog ()
		{
			BaseList command = new BaseList();
			command.ID = App.SELECT_ALL;
			IRequestContext context = command.NewContext ();
			command.Execute(context);
			SelectAll_Result(context);
		}

		/// <summary>
		/// SelectAll and succeed, using catalog.
		/// </summary>
		[Test]
		public void SelectAll_Pass ()
		{
			IRequestContext context = controller.ExecuteContext (App.SELECT_ALL);
			SelectAll_Result(context);
		}
	}
}