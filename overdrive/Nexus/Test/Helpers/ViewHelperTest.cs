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
using Agility.Core;
using Nexus.Web;
using NUnit.Framework;

namespace Nexus.Core.Helpers
{
	/// <summary>
	/// Exercise methods of standard IViewHelper implementation.
	/// </summary>
	[TestFixture]
	public class ViewHelperTest
	{
		private IViewHelper helper;

		/// <summary>
		/// Create a command in between test runs. 
		/// </summary>
		[SetUp]
		public void SetUp ()
		{
			helper = new ViewHelper ();
		}

		/// <summary>
		/// An error message will be returned surrounded by markup.
		/// </summary>
		[Test]
		public void HtmlErrorBuilder ()
		{
			string ERROR = "Global error!";
			helper.Context.AddError (ERROR);
			string errors = ViewHelper.HtmlErrorBuilder (helper);
			Assert.IsNotNull (errors, "Expected markup string.");
			Assert.IsTrue (errors.Length > ERROR.Length);
			Assert.IsTrue (errors.IndexOf (ERROR) > 0, "Expected error within markup.");
		}


		/// <summary>
		/// A ViewHelper will have a non-null Context.
		/// </summary>
		[Test]
		public void Context ()
		{
			Assert.IsNotNull (helper.Context, "Expected default Context instance.");
		}

		/// <summary>
		/// Errors added to the underlying Context pass through to ViewHelper Errors.
		/// </summary>
		[Test]
		public void Errors ()
		{
			Assert.IsNull (helper.Errors, "Expected no errors yet.");
			IContext errors = new Context ();
			errors.Add ("ERROR", "Error Message");
			helper.Context.Errors = errors;
			IContext _errors = helper.Errors;
			Assert.IsNotNull (_errors, "Expected errors to pass through.");
			Assert.IsTrue (_errors.Count == 1, "Expeced one error.");
			Assert.AreEqual (errors [0], _errors [0]);
		}

		/// <summary>
		/// Errors added to the underlying Context register with HasErrors.
		/// </summary>
		[Test]
		public void HasErrors ()
		{
			Assert.IsFalse (helper.HasErrors, "Expected no errors.");
			string ERROR = "I have an error!";
			helper.Context.AddError (ERROR);
			Assert.IsTrue (helper.HasErrors, "Expected to have errors.");
		}

		/// <summary>
		/// An Exception added to the underlying Context passes through to the ViewHelper Fault.
		/// </summary>
		public void Fault ()
		{
			Assert.IsNull (helper.Fault, "Expected null fault.");
			Exception fault = new Exception ();
			helper.Context.Fault = fault;
			Assert.IsNotNull (helper.Fault, "Expected non-null fault.");
		}

		/// <summary>
		/// An Exception added to the underlying Context registers with ViewContext HasFault.
		/// </summary>
		[Test]
		public void HasFault ()
		{
			Assert.IsFalse (helper.HasFault, "Expected no fault.");
			helper.Context.Fault = new Exception ();
			Assert.IsTrue (helper.HasFault, "Expected a fault");
		}

		/// <summary>
		/// An Exception or error added to the underlying Context registers with IsNominal.
		/// </summary>
		[Test]
		public void IsNominal ()
		{
			Assert.IsTrue (helper.IsNominal, "Expected nominal state.");
			helper.Context.AddError ("Yet another error message.");
			Assert.IsFalse (helper.IsNominal, "Expected error state");
			helper.Context.Fault = new Exception ();
			Assert.IsTrue (helper.HasFault, "Expected a fault");
			Assert.IsFalse (helper.IsNominal, "Expected error state");
		}

	}
}