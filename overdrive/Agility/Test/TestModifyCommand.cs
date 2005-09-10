namespace Agility.Core
{
	/// <summary>
	/// A Command that modifies an input value by adding the 
	/// substring "_MODIFIED" and sets the new value as output, 
	/// without changing the input value.
	/// </summary>
	public class TestModifyCommand : ICommand
	{
		public const string SUFFIX = "_MODIFIED";

		public bool Execute(IContext _context)
		{
			TestContext context = _context as TestContext;
			string input = context[context.InputKey] as string;
			string output = input + SUFFIX;
			context[context.OutputKey] = output;
			return false;
		}
	}
}