using System;
using System.Collections;
using Nexus.Core.Commands;
using NUnit.Framework;

namespace Nexus.Core
{
	/// <summary>
	/// Exercise IRequestContext per [OVR-7].
	/// </summary>
	[TestFixture]
	public class RequestContextTest
	{

		IRequestContext context;
		Exception fault;
		IList list;

		/// <summary>
		/// Initialize private fields.
		/// </summary>
		[SetUp]
		public void SetUp()
		{
			context = new RequestContext();
			context.Command = "list_all";
			context.CommandBin = new ListAll();
			Assert.IsTrue (context.IsNominal,"Expected nominal state for a new IRequestContext.");			
			Assert.IsFalse(context.HasOutcome,"Expected no Outcome for a new IRequestContext.");			

			fault = new ApplicationException("RequestContextTest");
			list = new ArrayList();
			list.Add("data");
		}

		/// <summary>
		/// A IRequestContext is not nominal if an error is added. 
		/// </summary>
		[Test]
		public void IsNominal_Error()
		{
			context.AddError("Business logic error");
			Assert.IsFalse(context.IsNominal,"Expected non-nominal state after adding error message.");
		}

		/// <summary>
		/// A IRequestContext is not nominal is an Exception is set.
		/// </summary>
		[Test]
		public void IsNominal_Fault()
		{
			context.Fault = fault;
			Assert.IsFalse(context.IsNominal,"Expected non-nominal state after setting Exception.");
		}

		/// <summary>
		/// A IRequestContext is not nominal if multiple errors are added and an Exception is set.
		/// </summary>
		[Test]
		public void IsNominal_Errors_and_Fault()
		{
			context.AddError("Business logic error");
			context.AddError("Business logic error 2");
			context.Fault = fault;
			Assert.IsFalse(context.IsNominal,"Expected non-nominal state after adding errors and Exception.");
		}

		/// <summary>
		/// If data is set to the Outcome property, HasOutcome is true.
		/// </summary>
		[Test]
		public void HasOutcome()
		{
			context.Outcome = list;
			Assert.IsTrue (context.HasOutcome);
		}	

	}
}
