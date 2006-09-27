using System;
using System.Collections;
using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Implement IRequestChain.
	/// </summary>
	public class RequestChain : RequestCommand, IRequestChain
	{
		private Chain chain = new Chain();

		public void AddCommand(ICommand _command)
		{
			IRequestCommand command = _command as IRequestCommand;
			if (null == command)
				throw new ArgumentNullException("RequestChain.AddCommand", "_command");

			chain.AddCommand(command);

			// Composite Required and Related
			IList _RequiredIDs = command.RequiredIDs;
			if (null != _RequiredIDs) AddRequiredIDs = _RequiredIDs;

			IList _RelatedIDs = command.RelatedIDs;
			if (null != _RelatedIDs) AddRelatedIDs = _RelatedIDs;
		}

		public IList AddCommands
		{
			set
			{
				foreach (ICommand command in value)
				{
					AddCommand(command);
				}
			}
		}

		public override bool Execute(IContext context)
		{
			return chain.Execute(context);
		}

		public ICommand[] GetCommands()
		{
			return (chain.GetCommands());

		}

		public override bool RequestExecute(IRequestContext context)
		{
			return Execute(context);
		}

	}
}