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
using NUnit.Framework;

namespace PhoneBook.Core
{
	/// <summary>
	/// Base class for unit tests.
	/// </summary>
	[TestFixture]
	public class BaseTest
	{
		/// <summary>
		/// Dummy test to exercise infrastructure.
		/// </summary>
		[Test]
		public void Pass ()
		{
		}

		/// <summary>
		/// Demonstration GUIDs and provide a device for generating GUIDs if needed.
		/// </summary>
		[Test]
		public void GuidString ()
		{
			IDictionary test = new Hashtable ();
			for (int i = 0; i < 10; i++)
			{
				string key = Guid.NewGuid ().ToString ();
				Assert.IsNotNull (key);
				Assert.IsTrue (36 == key.Length);
				test.Add (key, key); // Add throws an exception on duplicate keys
			}
		}
	}
}