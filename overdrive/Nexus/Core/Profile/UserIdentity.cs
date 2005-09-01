using System;
using System.Security.Principal;

namespace Nexus.Core.Profile
{
	/// <summary>
	/// Implement IIdentity to capture user's login name.
	/// </summary>
	/// <remarks><p>
	/// An identity object represents the user on whose behalf the code is running).
	/// </p><p>
	/// For this to work, we must update the Web.config and the server settings. 
	/// </p><p>
	/// Web.config: &lt;authentication mode="Windows" /&gt; &lt;identity impersonate="true"/&gt;
	/// </p><p>
	/// IIS Admin: Disable Anon Access on the Directory Security tab.
	/// </p></remarks>
	[Serializable]
	public class UserIdentity : IIdentity
	{
		private string _Name;

		public string Name
		{
			get { return _Name; }
		}

		private string _AuthenticationType;

		public string AuthenticationType
		{
			get { return _AuthenticationType; }
			set { _AuthenticationType = value; }
		}

		private bool _IsAuthenticated = false;

		public bool IsAuthenticated
		{
			get { return _IsAuthenticated; }
			set { _IsAuthenticated = value; }
		}

		/// <summary>
		/// Instantiate with zero parameters.
		/// </summary>
		public UserIdentity()
		{
		}

		/// <summary>
		/// Instantiate with an IIdentity. 
		/// </summary>
		/// <remarks>
		/// Essentially, create a shallow copy of the given Identity.
		/// </remarks>
		/// <param name="id">Identity to copy</param>
		public UserIdentity(IIdentity id)
		{
			if (null != id)
			{
				_Name = id.Name;
				AuthenticationType = id.AuthenticationType;
				IsAuthenticated = id.IsAuthenticated;
			}
		}

		/// <summary>
		/// Instantiate from passed values. 
		/// </summary>
		/// <param name="name">Value for user's name</param>
		/// <param name="authenticationType">Value for AuthenticationType</param>
		/// <param name="isAuthenticated">Value for IsAuthenticated</param>
		public UserIdentity(string name, string authenticationType, bool isAuthenticated)
		{
			_Name = name;
			_AuthenticationType = authenticationType;
			_IsAuthenticated = isAuthenticated;
		}
	}
}