using System;
using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Concrete IRequestContext implementation.
	/// </summary>
	public class RequestContext : Context, IRequestContext
	{

		/// <summary>
		/// Convenience constructor to set Command on instantiation.
		/// </summary>
		/// <param name="command">Name of Command processing this Context.</param>
		public RequestContext (string command)
		{
			Command = command;
		}

		/// <summary>
		/// Default, no argument constructor.
		/// </summary>
		public RequestContext ()
		{
		}

		public string Command
		{
			get { return this [Tokens.COMMAND] as string; }
			set { this [Tokens.COMMAND] = value; }
		}

		public IRequestCommand CommandBin
		{
			get { return this [Tokens.COMMAND_BIN] as IRequestCommand; }
			set { this [Tokens.COMMAND_BIN] = value; }
		}


		public bool HasOutcome
		{
			get
			{
				// TODO:  Add RequestContext.HasOutcome getter implementation
				return false;
			}
		}

		public object Outcome
		{
			get
			{
				// TODO:  Add RequestContext.Outcome getter implementation
				return null;
			}
			set
			{
				// TODO:  Add RequestContext.Outcome setter implementation
			}
		}

		public Agility.Core.IContext Errors
		{
			get
			{
				// TODO:  Add RequestContext.Errors getter implementation
				return null;
			}
			set
			{
				// TODO:  Add RequestContext.Errors setter implementation
			}
		}

		public void AddError(string template)
		{
			// TODO:  Add RequestContext.AddError implementation
		}

		public bool HasErrors
		{
			get
			{
				// TODO:  Add RequestContext.HasErrors getter implementation
				return false;
			}
		}

		public Exception Fault
		{
			get
			{
				// TODO:  Add RequestContext.Fault getter implementation
				return null;
			}
			set
			{
				// TODO:  Add RequestContext.Fault setter implementation
			}
		}

		public bool HasFault
		{
			get
			{
				// TODO:  Add RequestContext.HasFault getter implementation
				return false;
			}
		}

		public bool IsNominal
		{
			get
			{
				// TODO:  Add RequestContext.IsNominal getter implementation
				return false;
			}
		}

	}
}
