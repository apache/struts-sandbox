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
using System.Text;
using Agility.Extras.Spring;
using Nexus.Extras.Spring;
using NUnit.Framework;
using Spring.Context;

namespace Nexus.Core
{
	/// <summary>
	/// Provide base SetUp method and convenience methods.
	/// </summary>
	/// 
	[TestFixture]
	public class BaseNexusTest
	{
		protected IRequestCatalog catalog;

		/// <summary>
		/// Setup catalog between tests.
		/// </summary>
		/// 
		[SetUp]
		public virtual void SetUp ()
		{
			// TODO: Implement Objects.Factory [OVR-8]
			IApplicationContext factory = Objects.Factory ();
			catalog = new Catalog (factory); // sic
		}

		/// <summary>
		/// Exercise Setup method.
		/// </summary>
		/// 
		[Test]
		public void AssertSetUp ()
		{
			Assert.IsTrue (catalog != null, "Expected non-null catalog.");
		}

		/// <summary>
		/// Convenience method to confirm that no Exception was caught.
		/// </summary>
		/// <param name="context">Context under test</param>
		/// 
		public void AssertNoFault (IRequestContext context)
		{
			bool hasFault = context.HasFault;
			if (hasFault)
				Assert.Fail (context.Fault.Message);
		}

		/// <summary>
		/// Convenience method to confirm 
		/// that there are no alerts or fault.
		/// </summary>
		/// <param name="context">Context under test</param>
		/// 
		public void AssertNominal (IRequestContext context)
		{
			AssertNoFault (context);
			bool hasAlerts = context.HasAlerts;
			if (hasAlerts)
			{
				// TODO: Use new TextOnly method here.
				StringBuilder outer = new StringBuilder ();
				IDictionary store = context.Alerts;
				ICollection keys = store.Keys;
				foreach (string key in keys)
				{
					StringBuilder inner = new StringBuilder ();
					inner.Append (key);
					inner.Append (": ");
					IList messages = store [key] as IList;
					foreach (string message in messages)
					{
						inner.Append (message);
						inner.Append (";");
					}
					outer.Append (inner.ToString ());
					outer.Append ("/n");
				}
				Assert.Fail (outer.ToString ());
			}
		}
	}
}