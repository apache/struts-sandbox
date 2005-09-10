namespace Agility.Core
{
	/// <summary>
	/// A Command that sets the input value as the output value, 
	/// and removes the input value.
	/// </summary>
	public class TestRemoveCommand : ICommand
	{
		public bool Execute(IContext _context)
		{
			TestContext context = _context as TestContext;
			string value = context[context.InputKey] as string;
			context[context.InputKey] = null;
			context[context.OutputKey] = value;
			return false;
		}
	}
}