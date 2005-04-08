using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Abstract IRequestCommand; subclass must implement RequestExecute.
	/// </summary>
	public abstract class RequestCommand : IRequestCommand
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

		public abstract bool RequestExecute(IRequestContext context);

		public virtual bool Execute (IContext _context)
		{
			IRequestContext context = _context as IRequestContext;
			return RequestExecute (context);
		}
	}
}
