using System;

namespace Agility.Core
{
	/// <summary>
	/// A Command that uses a Filter to clean up after itself.
	/// </summary>
	public class TestFilterCommand : IFilter
	{
		public const string FILTER_KEY = "FILTER";

		#region IFilter Members

		public virtual bool PostProcess(IContext context, Exception exception)
		{
			context[FILTER_KEY] = null;
			return false; // == I took care of my business, but someone 
			// still needs to handle the exception
		}

		#endregion

		#region ICommand Members

		public bool Execute(IContext context)
		{
			context.Add(FILTER_KEY, this);
			return false;
		}

		#endregion
	}
}