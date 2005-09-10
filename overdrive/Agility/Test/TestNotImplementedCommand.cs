using System;

namespace Agility.Core
{
	/// <summary>
	/// A command that throws a NotImplementedException.
	/// </summary>
	public class TestNotImplementedCommand : ICommand
	{
		public bool Execute(IContext context)
		{
			throw new NotImplementedException();
		}
	}
}