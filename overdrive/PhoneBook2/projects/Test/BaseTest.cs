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
using NUnit.Framework;

namespace PhoneBook.Core
{
	/// <summary>
	/// Provide base class so unit tests can share utility.
	/// </summary>
	/// 
	[TestFixture]
	public class BaseTest : CatalogBaseTest
	{
		/// <summary>
		/// Confirm that the outcome is a non-null, non-empty list.
		/// </summary>
		/// <param name="context">Context to confirm</param>
		/// <returns>The non-null, non-empty list</returns>
		/// 
		protected IList AssertListOutcome(IRequestContext context)
		{
			AssertNominal(context);
			Assert.IsTrue(context.HasOutcome, "Expected command to set an Outcome.");
			IList list = context.Outcome as IList;
			bool notEmpty = ((list != null) && (list.Count > 0));
			Assert.IsTrue(notEmpty, "Expected outcome to be a not-empty list");
			return list;
		}

		/// <summary>
		/// Exercise the testing infrastructure.
		/// </summary>
		/// 
		[Test]
		public void Pass()
		{
		}

		/// <summary>
		/// Exercise GUID creation, 
		/// and provide a device for generating GUIDs if needed.
		/// </summary>
		/// 
		[Test]
		public void GuidString()
		{
			IDictionary test = new Hashtable();
			for (int i = 0; i < 10; i++)
			{
				string key = Guid.NewGuid().ToString();
				Assert.IsNotNull(key);
				Assert.IsTrue(36 == key.Length);
				test.Add(key, key); // Add throws an exception on duplicate keys
			}
		}
	}
}