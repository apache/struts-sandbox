using System;
using System.Collections;
using Nexus.Core.Commands;
using NUnit.Framework;

namespace Nexus.Core
{
	/// <summary>
	/// Prove that a single object can be returned by Execute, without error.
	/// </summary>
	[TestFixture]
	public class ObjectByKeyTest : CatalogBaseTest
	{
		public const string OBJECT_BY_KEY = "ObjectByKey";
		public const string OBJECT_BY_KEY_WITH_VALIDATE = "ObjectByKeyWithValidate";
		public const string OBJECT_BY_KEY_WITH_REQUIRED = "ObjectByKeyWithRequired";

		private string PK_SOMETHING = ObjectByKey.PK_SOMETHING;
		private string PK_SOMETHING_VALUE = ObjectByKey.PK_SOMETHING_VALUE;
		private const string PK_SOMETHING_RESULT = ObjectByKey.PK_SOMETHING_RESULT;
		private const string PK_SOME_DATE = ObjectByKey.PK_SOME_DATE;

		public void AssertSomething(IRequestContext context)
		{
			AssertNominal(context);
			Assert.IsNotNull(context[PK_SOMETHING_RESULT], "Expected result");
			Assert.IsFalse(context.HasOutcome, "Unexpected Outcome.");
		}

		public void AssertInvalid(IRequestContext context)
		{
			AssertNoFault(context);
			Assert.IsTrue(context.HasAlerts, "Expected error");
			Assert.IsNull(context[PK_SOMETHING_RESULT], "Unexpected result");
			Assert.IsFalse(context.HasOutcome, "Unexpected Outcome.");
		}

		[Test]
		public void ObjectByKey_Trusted()
		{
			IRequestContext context = catalog.GetRequestContext(OBJECT_BY_KEY);
			context[PK_SOMETHING] = PK_SOMETHING_VALUE;

			catalog.ExecuteRequest(context); // do the actual work

			AssertSomething(context);
		}

		[Test]
		public void ObjectByKey_UnTrusted()
		{
			IDictionary fields = new Hashtable();
			fields[PK_SOMETHING] = PK_SOMETHING_VALUE;
			IRequestContext context = catalog.GetRequestContext(OBJECT_BY_KEY_WITH_REQUIRED, fields);

			catalog.ExecuteRequest(context); // do the actual work

			AssertSomething(context);
		}

		[Test]
		public void ObjectByKey_UnTrusted_Fail()
		{
			IDictionary fields = new Hashtable();
			IRequestContext context = catalog.GetRequestContext(OBJECT_BY_KEY_WITH_REQUIRED, fields);

			catalog.ExecuteRequest(context); // do the actual work

			AssertInvalid(context);
		}


		[Test]
		public void ObjectByKey_Process()
		{
			IDictionary fields = new Hashtable();
			fields[PK_SOMETHING] = PK_SOMETHING_VALUE;
			// fields [PK_SOME_DATE] = DateTime.Now.ToShortDateString ();
			IRequestContext context = catalog.GetRequestContext(OBJECT_BY_KEY_WITH_VALIDATE, fields);

			catalog.ExecuteRequest(context); // do the actual work

			AssertSomething(context);

			try
			{
				DateTime output = (DateTime) context[PK_SOME_DATE];
				output = output;
			}
			catch (Exception e)
			{
				Assert.IsTrue(e != null);
				Assert.Fail("Expected date as binary in the main context");
			}

			string shortDate = context.Criteria[PK_SOME_DATE] as string;
			Assert.IsNotNull(shortDate, "Expected date as a string in criteria");

			DateTime now = DateTime.Now;
			// http://www.microsoft.com/globaldev/getWR/steps/wrg_date.mspx
			// Thread.CurrentThread.CurrentCulture = new CultureInfo("en-US");
			string expect = now.ToString("d");
			Assert.AreEqual(expect, shortDate);
		}

		[Test]
		public void ObjectByKey_Format_Date()
		{
			IDictionary fields = new Hashtable();
			fields[PK_SOMETHING] = PK_SOMETHING_VALUE;
			fields[PK_SOME_DATE] = DateTime.Now.ToShortDateString();
			IRequestContext context = catalog.GetRequestContext(OBJECT_BY_KEY_WITH_VALIDATE, fields);

			catalog.ExecuteRequest(context); // do the actual work

			AssertSomething(context);

			try
			{
				DateTime output = (DateTime) context[PK_SOME_DATE];
				output = output;
			}
			catch (Exception e)
			{
				Assert.IsTrue(e != null);
				Assert.Fail("Expected date as binary in the main context");
			}

			string formatted = context.Criteria[PK_SOME_DATE] as string;
			Assert.IsNotNull(formatted, "Expected date as a string in criteria");
		}


	}
}