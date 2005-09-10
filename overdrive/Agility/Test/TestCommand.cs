namespace Agility.Core
{
	/// <summary>
	/// A do-nothing pass-through command.
	/// </summary>
	public class TestCommand : ICommand
	{
		public bool Execute(IContext context)
		{
			; // do nothing
			return false;
		}
	}
}