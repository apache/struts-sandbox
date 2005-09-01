using System;
using System.Security.Principal;

namespace Nexus.Core.Profile
{
	/// <summary>
	/// Implement IPrincipal to capture the user's login name.
	/// </summary>
	/// <remarks><p>
	/// IPrincipal - A principal object represents the security context of the 
	/// user on whose behalf the code is running, including that user's identity 
	/// (IIdentity) and any roles to which they belong.
	/// </p></remarks>
	[Serializable]
	public class UserPrincipal : IPrincipal
	{
		private IIdentity _Identity;

		public IIdentity Identity
		{
			get { return _Identity; }
			set { _Identity = value; }
		}

		public bool IsInRole(string role)
		{
			if ((null == _Roles) || (0 == _Roles.Length)) return false;
			if ((null == role) || (0 == role.Length)) return false;
			bool found = false;
			for (int i = 0; i < _Roles.Length; i++)
				found = found || role.Equals(_Roles[i]);
			return found;
		}

		/// <summary>
		/// Field for Roles property.
		/// </summary>
		private string[] _Roles;

		/// <summary>
		/// The roles for this principal representated as an array.
		/// </summary>
		public string[] Roles
		{
			get { return _Roles; }
			set { _Roles = value; }
		}

		/// <summary>
		/// Instantiate default NexusPrincipal with empty NexusIdentity.
		/// </summary>
		public UserPrincipal()
		{
			Identity = new UserIdentity(); // FIXME: Spring?
		}

		/// <summary>
		/// Instantiate from an IIdentity.
		/// </summary>
		/// <remarks>
		/// The Roles for this principal will follow 
		/// those set by the Roles property, regardless of the 
		/// Identity or Authentication Type.
		/// </remarks>
		/// <param name="id">Value for user name</param>
		public UserPrincipal(IIdentity id)
		{
			Identity = new UserIdentity(id); // FIXME: Spring?
		}
	}
}