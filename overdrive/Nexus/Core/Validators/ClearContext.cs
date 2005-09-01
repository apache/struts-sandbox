using System.Collections;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// Clear from the Context any keys present in the Criteria, 
	/// so that, if the context is re-used, values from a prior request do not linger.
	/// </summary>
	/// <remarks><p>
	/// Once the values are output from the Context to the Criteria, 
	/// they can be safely removed from the Context. 
	/// </p><p>This command can be used as part of a post-opt chain to ensure that 
	/// Context values are not retained if a Context object is used for multiple 
	/// business requests. 
	/// </p></remarks>
	public class ClearContext : RequestCommand
	{
		/// <summary>
		/// Clear from the Context any keys present in the Criteria.
		/// </summary>
		/// <param name="context">Context after attributes have been output to Criteria</param>
		/// <returns>CONTINUE</returns>
		public override bool RequestExecute(IRequestContext context)
		{
			ICollection keys = context.Criteria.Keys;
			foreach (string key in keys)
			{
				context.Remove(key);
			}

			return CONTINUE;
		}
	}
}