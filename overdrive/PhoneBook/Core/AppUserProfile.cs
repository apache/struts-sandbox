using System.Security.Principal;
using Nexus.Core.Profile;

namespace PhoneBook.Core
{
	/// <summary>
	/// Extend UserProfile to include an IsEditor property.
	/// </summary>
	public class AppUserProfile : UserProfile
	{
		private bool _IsEditor = false;
		public bool IsEditor
		{
			get { return _IsEditor; }
			set { _IsEditor = value; }
		}

		/// <summary>
		/// Instantiate from an IIdentity.
		/// </summary>
		/// <param name="id">Identity to copy for this profile.</param>
		public AppUserProfile (IIdentity id)
		{
			Principal = new UserPrincipal (id);
		}

	}
}