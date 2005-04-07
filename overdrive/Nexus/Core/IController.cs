namespace Nexus.Core
{
	/// <summary>
	/// The IController interacts with the caller, 
	/// controlling and managing the processing of a request. 
	/// </summary>
	public interface IController
	{
		/// <summary>
		/// Obtain and execute the IRequestContext.
		/// </summary>
		/// <param name="command">Our command name</param>
		/// <returns>Context after execution</returns>
		/// 
		IRequestContext ExecuteContext (string command);
	}
}
