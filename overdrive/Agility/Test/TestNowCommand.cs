using System;

namespace Agility.Core
{
	/// <summary>
	/// A Command that sets a new output value to the DateTime for "Now".
	/// </summary>
	public class TestNowCommand : ICommand
	{
		public bool Execute(IContext _context)
		{
			TestContext context = _context as TestContext;
			context[context.OutputKey] = DateTime.Now;
			return false;
		}
	}
}