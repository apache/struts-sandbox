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
using NUnit.Framework;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Exercise SelectAll Command.
	/// </summary>
	[TestFixture]
	public class SelectAllTest : BaseTest
	{
		/// <summary>
		/// SelectAll and succeed.
		/// </summary>
		/// <remarks>
		/// Nonfunctional work in progress.
		/// </remarks>
		[Test]
		public void SelectAll_Pass ()
		{
			// TODO: Write code to pass text [OVR-5]
			/*
			IRequestContext context = Controller.ExecuteContext (App.SELECT_ALL);
			AssertNominal (context);
			AssertOutcome (context.HasOutcome);
			IList list = context.Outcome as IList;
			AssertListNotEmpty(list);
			IDictionary row = list[0] as IDictionary;
			string[] fields = {App.FIRST_NAME, App.LAST_NAME, App.USER_NAME, App.EXTENSION, App.HIRED, App.HOURS, App.EDITOR};
			AssertFields (context,fields);
			*/
		}
	}
}