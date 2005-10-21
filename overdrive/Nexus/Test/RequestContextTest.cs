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
using Nexus.Core.Commands;
using NUnit.Framework;

namespace Nexus.Core
{
	/// <summary>
	/// Exercise IRequestContext per [OVR-7].
	/// </summary>
	/// 
	[TestFixture]
	public class RequestContextTest
	{
		private IRequestContext context;
		private Exception fault;
		private IList list;

		/// <summary>
		/// Initialize private fields.
		/// </summary>
		/// 
		[SetUp]
		public void SetUp()
		{
			context = new RequestContext();
			context.Command = "list_all";
			context.CommandBin = new ListAll();
			Assert.IsTrue(context.IsNominal, "Expected nominal state for a new IRequestContext.");
			Assert.IsFalse(context.HasOutcome, "Expected no Outcome for a new IRequestContext.");

			fault = new ApplicationException("RequestContextTest");
			list = new ArrayList();
			list.Add("data");
		}

		/// <summary>
		/// A IRequestContext is not nominal if an alert is added. 
		/// </summary>
		/// 
		[Test]
		public void IsNominal_Alert()
		{
			context.AddAlert("Business logic alert");
			Assert.IsFalse(context.IsNominal, "Expected non-nominal state after adding alert message.");
		}

		/// <summary>
		/// A IRequestContext is not nominal if an Exception is set.
		/// </summary>
		/// 
		[Test]
		public void IsNominal_Fault()
		{
			context.Fault = fault;
			Assert.IsFalse(context.IsNominal,
			               "Expected non-nominal state after setting Exception.");
		}

		/// <summary>
		/// A IRequestContext is not nominal if multiple errors are added 
		/// and an Exception is set.
		/// </summary>
		/// 
		[Test]
		public void IsNominal_Alerts_and_Fault()
		{
			context.AddAlert("Business logic error");
			context.AddAlert("Business logic error 2");
			context.Fault = fault;
			Assert.IsFalse(context.IsNominal,
			               "Expected non-nominal state after adding errors and Exception.");
		}

		/// <summary>
		/// If data is set to the Outcome property, HasOutcome is true.
		/// </summary>
		/// 
		[Test]
		public void HasOutcome()
		{
			context.Outcome = list;
			Assert.IsTrue(context.HasOutcome);
		}

	}
}