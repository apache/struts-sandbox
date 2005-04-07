namespace Nexus.Core
{
	/// <summary>
	/// Interact with the caller, controlling and managing 
	/// the processing of a request [OVR-8]. 
	/// </summary>
	/// <remarks><p>
	/// The caller should only need to know the name of a Command 
	/// to be able to acquire the appropriate Context, and then execute the request. 
	/// </p></remarks>
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
