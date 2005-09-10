namespace Agility.Core
{
	/// <summary>
	/// A Command that adds a value to a TestContext under its InputKey.
	/// </summary>
	public class TestInputCommand : ICommand
	{
		public const string VALUE = "INPUT";

		public bool Execute(IContext _context)
		{
			TestContext context = _context as TestContext;
			context.Add(context.InputKey, VALUE);
			return false;
		}

	}
}