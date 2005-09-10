using System;
using NUnit.Framework;

namespace Agility.Core
{
	/// <summary>
	/// Test suite for the various Test*Commands.
	/// </summary>
	[TestFixture]
	public class CommandTest
	{
		[Test]
		public void ModifyCommand()
		{
			const string VALUE = TestInputCommand.VALUE;
			TestContext context = new TestContext();
			context.Add(context.InputKey, VALUE);
			ICommand command = new TestModifyCommand();
			command.Execute(context);
			string output = context[context.OutputKey] as string;
			Assert.IsNotNull(output, "Expected output");
			Assert.IsFalse(VALUE.Equals(output), "Expected modified output");
			string input = context[context.InputKey] as string;
			Assert.IsTrue(VALUE.Equals(input), "Expected " + VALUE + " but found " + input);
		}

		[Test]
		public void NotImplementedCommand()
		{
			TestContext context = new TestContext();
			ICommand command = new TestNotImplementedCommand();
			try
			{
				command.Execute(context);
				Assert.Fail("Expected exception");
			}
			catch (NotImplementedException expected)
			{
				Assert.IsNotNull(expected, "Expected exception");
			}
		}

		[Test]
		public void NowCommand()
		{
			TestContext context = new TestContext();
			ICommand command = new TestNowCommand();
			command.Execute(context);
			DateTime then = (DateTime) context[context.OutputKey];
			int greater = DateTime.Now.CompareTo(then);
			Assert.IsTrue(greater >= 0, "Expected now to be past");
		}

		[Test]
		public void RemoveCommand()
		{
			ICommand command = new TestRemoveCommand();
			const string VALUE = TestInputCommand.VALUE;
			TestContext context = new TestContext();
			context.Add(context.InputKey, VALUE);
			command.Execute(context);
			string input = context[context.InputKey] as string;
			Assert.IsNull(input, "Expected input to be removed");
			string output = context[context.OutputKey] as string;
			Assert.IsNotNull(output, "Expected non-null output");
			Assert.IsTrue(VALUE.Equals(output), "Expected " + VALUE + " but found " + output);
		}
	}
}