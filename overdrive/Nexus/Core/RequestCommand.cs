using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Concrete IRequestCommand implementation.
	/// </summary>
	public class RequestCommand : IRequestCommand
	{

		/// <summary>
		/// Return STOP if a Command is part of a Chain.
		/// </summary>
		public const bool STOP = true;

		/// <summary>
		/// Return CONTINUE if another Command can run.
		/// </summary>
		public const bool CONTINUE = false;

		private string _ID = null;
		public virtual string ID
		{
			get { return _ID; }
			set { _ID = value; }
		}

		public virtual IRequestContext NewContext ()
		{
			// Return a new instance on each call.
			return new RequestContext (ID);
		}

		public virtual bool RequestExecute(IRequestContext context)
		{
			// TODO:  Add RequestCommand.NexusExecute implementation
			return STOP;
		}

		public virtual bool Execute (IContext context)
		{
			IRequestContext nexus = context as IRequestContext;
			return RequestExecute (nexus);
		}
	}
}
