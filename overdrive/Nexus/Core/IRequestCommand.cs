using Agility.Core;

namespace Nexus.Core
{
	/// <summary>
	/// Use an IRequestContext to process a Command [OVR-9]
	/// </summary>
	/// <remarks>
	/// <p>
	/// Rather than have each command cast its context to an IRequestContext, 
	/// provide a IRequestCommand with an alternative signature. 
	/// </p>
	/// </remarks>
	public interface IRequestCommand : ICommand
	{

		/// <summary>
		/// Factory method to provide an empty context that can be used with the Command instance.
		/// </summary>
		/// <returns>Context instance with Command ID set.</returns>
		IRequestContext NewContext ();

		/// <summary>
		/// Operations to perform with HelperContext.
		/// </summary>
		/// <remarks><p>
		/// Expected to be called from Execute as a casting convenience.
		/// </p></remarks>
		/// <param name="context">Context to process.</param>
		bool RequestExecute (IRequestContext context);
	
	}
}
