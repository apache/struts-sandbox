using System;
using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Summary description for RequestContext.
	/// </summary>
	public class RequestContext : Context, IRequestContext
	{

		public bool ContainsKeys(string[] keys)
		{
			// TODO:  Add RequestContext.ContainsKeys implementation
			return false;
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
