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
using System.Text;
using Agility.Core;
using Agility.Extras.Spring;
using Nexus.Core.Helpers;
using NUnit.Framework;
using Spring.Context;

namespace Nexus.Core
{
	/// <summary>
	/// Provide base SetUp method and convenience methods 
	/// for tests that use a IRequestCatalog.
	/// </summary>
	/// 
	[TestFixture]
	public class CatalogBaseTest
	{
		/// <summary>
		/// Catalog instance that tests can use.
		/// </summary>
		protected IRequestCatalog catalog;

		/// <summary>
		/// Setup catalog between tests.
		/// </summary>
		/// 
		[SetUp]
		public virtual void SetUp()
		{
			IApplicationContext factory = Objects.Factory();
			catalog = factory.GetObject("Catalog") as IRequestCatalog;
		}

		/// <summary>
		/// Exercise Setup method.
		/// </summary>
		/// 
		[Test]
		public void AssertSetUp()
		{
			Assert.IsTrue(catalog != null, "Expected non-null catalog.");
		}

		#region IRequestContext tests

		/// <summary>
		/// Determine if the context contains each key in keys.
		/// </summary>
		/// <param name="context">Context to process</param>
		/// <param name="keys">Keys to verify</param>
		/// <returns>True if contact contains each key in keys</returns>
		protected bool ContainsKeys(IContext context, string[] keys)
		{
			bool found = true;
			foreach (string key in keys)
			{
				found = found && context.Contains(key);
			}
			return found;
		}

		/// <summary>
		/// Determine if the Criteria for context contains each key in keys.
		/// </summary>
		/// <param name="context">Context to process</param>
		/// <param name="keys">Keys to verify</param>
		/// <returns>True if Criteria for contact contains each key in keys</returns>
		public bool ContainsCriteriaKeys(IRequestContext context, string[] keys)
		{
			if (!context.HasCriteria()) return false;

			IDictionary criteria = context.Criteria;
			bool found = true;
			foreach (string v in keys)
			{
				found = found && criteria.Contains(v);
			}
			return found;
		}
		
		protected void FaultText(Exception fault)
		{
			StringBuilder text = new StringBuilder("[");
			text.Append(fault.Message);
			text.Append("] ");
			text.Append(fault.Source);
			text.Append(fault.StackTrace);
			Assert.Fail(text.ToString());			
		}

		/// <summary>
		/// Convenience method to confirm that no Exception was caught.
		/// </summary>
		/// <param name="context">Context under test</param>
		/// 
		public void AssertNoFault(IRequestContext context)
		{
			if (context.HasFault)
			{
				FaultText(context.Fault);
			}
		}
		
		/// <summary>
		/// Convenience method to confirm that no Exception was caught.
		/// </summary>
		/// <param name="helper">Helper under test</param>
		/// 
		public void AssertNoFault(IViewHelper helper )
		{
			if (helper.HasFault) FaultText(helper.Fault) ;
		}

		/// <summary>
		/// Convenience method to confirm 
		/// that there are no alerts or fault.
		/// </summary>
		/// <param name="context">Context under test</param>
		/// 
		public void AssertNominal(IRequestContext context)
		{
			AssertNoFault(context);
			bool hasAlerts = context.HasAlerts;
			if (hasAlerts)
			{
				// TODO: Use new TextOnly method here.
				StringBuilder outer = new StringBuilder();
				IDictionary store = context.Alerts;
				ICollection keys = store.Keys;
				foreach (string key in keys)
				{
					StringBuilder inner = new StringBuilder();
					inner.Append(key);
					inner.Append(": ");
					IList messages = store[key] as IList;
					foreach (string message in messages)
					{
						inner.Append(message);
						inner.Append(";");
					}
					outer.Append(inner.ToString());
					outer.Append("/n");
				}
				Assert.Fail(outer.ToString());
			}
		}

		/// <summary>
		/// Convenience method to confirm 
		/// that there are no alerts or fault.
		/// </summary>
		/// <param name="helper">Helper under test</param>
		/// 
		public void AssertNominal(IViewHelper helper)
		{
			AssertNoFault(helper); 

			bool hasAlerts = helper.HasAlerts;
			if (hasAlerts)
			{
				Assert.Fail(helper.AlertsText);
			}
		}

		/// <summary>
		/// Confirm that the value is stored in the context under the key.
		/// </summary>
		/// <param name="context">The context to check</param>
		/// <param name="key">The key</param>
		/// <param name="value">The value</param>
		protected void AssertKey(IDictionary context, string key, string value)
		{
			Assert.IsNotNull(value, "Value is null");
			Assert.IsNotNull(key, "Key is null");
			Assert.IsTrue(value.Equals(context[key]), "Key:Value mismatch: " + key + ":" + value);
		}

		/// <summary>
		/// Confirm that the given context contains the given keys.
		/// </summary>
		/// <param name="context">The context to check</param>
		/// <param name="keys">The keys to check</param>
		protected void AssertKeys(IRequestContext context, string[] keys)
		{
			Assert.IsTrue(ContainsKeys(context, keys), "Missing keys.");
		}

		/// <summary>
		/// Confirm that the context contains the keys, 
		/// that each key represents an non-null IList, 
		/// and that each IList is not empty.
		/// </summary>
		/// <param name="context">The context to check</param>
		/// <param name="keys">The list keys</param>
		protected void AssertListKeys(IRequestContext context, string[] keys)
		{
			AssertKeys(context, keys);
			foreach (string key in keys)
			{
				IList list = context[key] as IList;
				Assert.IsNotNull(list, "List is null: " + key);
				Assert.IsTrue(list.Count > 0, "List is empty");
			}
		}

		/// <summary>
		/// Call AssertList(string,int) with no minimum.
		/// </summary>
		/// <param name="id"></param>
		protected IRequestContext AssertList(string id)
		{
			return AssertList(id, 0);
		}

		/// <summary>
		/// Execute the Command for the given id, 
		/// and confirm that the return state is Nominal, 
		/// has an Outcome, 
		/// that the Outcome is an non-null IList, 
		/// and that the IList containes at list minCount items.
		/// </summary>
		/// <param name="id">The List Command to check</param>
		/// <param name="minCount">The minimum number of items</param>
		protected IRequestContext AssertList(string id, int minCount)
		{
			IRequestContext context = catalog.GetRequestContext(id);
			catalog.ExecuteRequest(context);
			AssertNominal(context);
			Assert.IsTrue(context.HasOutcome, "Expected outcome");
			IList list = context.Outcome as IList;
			Assert.IsNotNull(list, "Expected outcome as IList");
			Assert.IsTrue(list.Count >= minCount, "Expected list entries");
			return context;
		}

		#endregion

		#region data access tests

		/// <summary>
		/// Virtual method for populating a context 
		/// for use with other routine tests.
		/// </summary>
		/// <param name="context"></param>
		protected virtual void Populate(IDictionary context)
		{
			// override to populate context
			throw new NotImplementedException("CatalogBaseTest.Populate must be overridden.");
		}

		/// <summary>
		/// Virtual method for populating a context 
		/// for an insert test. 
		/// </summary>
		/// <param name="context"></param>
		protected virtual void PopulateInsert(IDictionary context)
		{
			Populate(context);
		}

		/// <summary>
		/// Insert and then delete a new record, 
		/// calling the Populate method to fill the context with the appropriate values.
		/// </summary>
		/// <param name="insertId">The "save" command name</param>
		/// <param name="keyId">The name of the primary key field</param>
		/// <param name="keyValue">The primary key value initially set by Populate</param>
		/// <param name="deleteId">The "delete" command name</param>
		protected IRequestContext AssertInsertDelete(string insertId, string keyId, string keyValue, string deleteId)
		{
			IRequestContext context = catalog.GetRequestContext(insertId);
			PopulateInsert(context);
			context[keyId] = String.Empty;

			catalog.ExecuteRequest(context);
			AssertNominal(context);
			Assert.IsFalse(keyValue.Equals(context[keyId]), "Expected new primary key");

			ICommand delete = catalog.GetCommand(deleteId);
			delete.Execute(context);
			AssertNominal(context);
			return context;
		}

		protected IRequestContext AssertEdit(string editId, string keyId, string keyValue, string[] keys)
		{
			IRequestContext context = catalog.GetRequestContext(editId);
			context[keyId] = keyValue;
			catalog.ExecuteRequest(context);
			AssertNominal(context);
			Assert.IsTrue(ContainsKeys(context, keys), "Missing fields");
			return context;
		}

		/// <summary>
		/// Update the given record (usually to the same values).
		/// </summary>
		/// <param name="updateId">The "save" command</param>
		/// <param name="keyId">The name of the primary key</param>
		/// <param name="keyValue">The value of the primary key</param>
		protected IRequestContext AssertUpdate(string updateId, string keyId, string keyValue)
		{
			IRequestContext context = catalog.GetRequestContext(updateId);
			Populate(context);
			catalog.ExecuteRequest(context);
			AssertNominal(context);
			Assert.IsTrue(keyValue.Equals(context[keyId]));
			return context;
		}

		#endregion
	}
}