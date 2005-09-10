using System;
using NUnit.Framework;

namespace Agility.Core
{
	/// <summary>
	/// Summary description for TestChain.
	/// </summary>
	[TestFixture]
	public class TestChain
	{
		private IChain chain;

		[SetUp]
		public void SetUp()
		{
			chain = new Chain();
		}


		// TODO: Test Constructors and AddCommands.

		/// <summary>
		/// Test adding commands and that chain "freezes" after execute.
		/// </summary>
		[Test]
		public void TestAddCommand()
		{
			Chain test = chain as Chain;
			ICommand[] before = test.GetCommands();
			Assert.AreEqual(0, before.Length, "Expected an empty chain");

			ICommand c1 = new TestCommand();
			chain.AddCommand(c1);

			ICommand[] first = test.GetCommands();
			Assert.AreEqual(1, first.Length, "Expected one link in the chain");

			ICommand c2 = new TestNowCommand();
			chain.AddCommand(c2);

			ICommand[] second = test.GetCommands();
			Assert.AreEqual(2, second.Length, "Expected two links in the chain");

			TestContext context = new TestContext();
			chain.Execute(context);

			DateTime output = (DateTime) context[context.OutputKey];
			Assert.IsNotNull(output, "Expected output");

			try
			{
				chain.AddCommand(c1);
				Assert.Fail("Expected exception when adding command to frozen chain.");
			}
			catch (Exception expected)
			{
				// FIXME: Exception has to be specific
				Assert.IsNotNull(expected, "Expected exception");
			}
		}

		[Test]
		public void TestExecute()
		{
			chain.AddCommand(new TestInputCommand());
			chain.AddCommand(new TestModifyCommand());
			TestContext context = new TestContext();
			chain.Execute(context);
			string output = context[context.OutputKey] as string;
			Assert.IsNotNull(output, "Expected output");
			string expected = TestInputCommand.VALUE + TestModifyCommand.SUFFIX;
			Assert.IsTrue(expected.Equals(output), "Expected modified output");
		}

		[Test]
		public void TextExecuteReverse()
		{
			chain.AddCommand(new TestModifyCommand());
			chain.AddCommand(new TestInputCommand());
			TestContext context = new TestContext();
			chain.Execute(context);
			string output = context[context.OutputKey] as string;
			string expected = TestModifyCommand.SUFFIX;
			Assert.IsTrue(expected.Equals(output), "Expected modified suffix only");
		}

		[Test]
		public void TestFilterCommand()
		{
			string KEY = "FILTER"; // TestFilterCommand.FILTER_KEY;
			chain.AddCommand(new TestFilterCommand());
			chain.AddCommand(new TestNotImplementedCommand());
			IContext context = new TestContext();
			try
			{
				chain.Execute(context);
				Assert.Fail("Expected NotImplementedException");
			}
			catch (NotImplementedException expected)
			{
				Assert.IsNotNull(expected, "Expected exception");
			}
			ICommand filter = context[KEY] as ICommand;
			Assert.IsNull(filter);
		}

		[Test]
		public void TestFilterHandler()
		{
			string KEY = "FILTER"; // TestFilterCommand.FILTER_KEY;
			chain.AddCommand(new TestFilterHandler());
			chain.AddCommand(new TestNotImplementedCommand());
			IContext context = new TestContext();
			chain.Execute(context);
			NotImplementedException filter = context[KEY] as NotImplementedException;
			Assert.IsNotNull(filter, "Expected Exception to be stored.");
		}

	}
}