using System;

namespace Agility.Core
{
	/// <summary>
	/// A filter that handles the exception (somehow).
	/// </summary>
	public class TestFilterHandler : TestFilterCommand
	{
		public override bool PostProcess(IContext context, Exception exception)
		{
			context[FILTER_KEY] = exception;
			return true; // == OK, I handled it!
		}
	}
}