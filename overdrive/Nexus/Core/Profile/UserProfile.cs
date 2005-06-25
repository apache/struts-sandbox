using System;
using System.Globalization;
using System.Security.Principal;

namespace Nexus.Core.Profile
{
	/// <summary>
	/// Represent a user.
	/// </summary>
	/// <remarks><p>
	/// The UserProfile includes a standard Principal object for 
	/// authentification and authorization. 
	/// Any user-related properties may be added here, along with 
	/// convenience methods for determining roles, such as 
	/// IsEngineer, IsManager, et al.
	/// </p></remarks>
	[Serializable]
	public class UserProfile : IProfile
	{
		/// <summary>
		/// Identify attribute key for storing a user profile.
		/// </summary>
		public const string USER_PROFILE = "USER_PROFILE";

		private IPrincipal _Principal;
		/// <summary>
		/// Provide the Principal object for this user.
		/// </summary>
		/// <remarks><p>
		/// Usually, this is a UserPrincipal, 
		/// but any IPrincipal instance could be used.
		/// </p></remarks>
		public IPrincipal Principal
		{
			get { return _Principal; }
			set { _Principal = value; }
		}

		#region UserId

		/// <summary>
		/// Identify the character separating a "machine name" from a "user id".
		/// </summary>
		public static char[] USER_ID_SEPARATOR = {'\\'}; // Backslash is the quote character, so you need to escape it.

		/// <summary>
		/// Trim any machine name reference from Principal Name. 
		/// </summary>
		/// <param name="name">A Identity Name that may contain a machine name reference</param>
		/// <returns>Identity name with machine name removed</returns>
		protected string TrimMachineName (string name)
		{
			if (null == name) return String.Empty;
			string[] logon = name.Split (USER_ID_SEPARATOR);
			if (logon.Length > 1) return logon [1];
			return logon [0];
		}

		private string _UserId;
		/// <summary>
		/// Record the user id portion of the Identity Name.
		/// </summary>
		/// <remarks><p>
		/// The UserId can be used to related staff records to user logins.
		/// </p></remarks>
		public string UserId
		{
			get
			{
				if (null == _UserId)
					_UserId = TrimMachineName (Principal.Identity.Name);
				return (null == _UserId) ? String.Empty : _UserId;
			}
			set { _UserId = value; }
		}

		#endregion

		#region UserLocale 

		private CultureInfo _UserLocale;
		public CultureInfo UserLocale
		{
			get { return _UserLocale; }
			set { _UserLocale = value; }
		}

		#endregion

		#region Constructors 

		/// <summary>
		/// Instantiate a default profile.
		/// </summary>
		public UserProfile ()
		{
			Principal = new UserPrincipal (); // FIXME: Spring?
		}

		/// <summary>
		/// Instantiate from an IPrincipal.
		/// </summary>
		/// <param name="principal">Principal for this profile.</param>
		public UserProfile (IPrincipal principal)
		{
			Principal = principal;
		}

		/// <summary>
		/// Instantiate from an IIdentity.
		/// </summary>
		/// <param name="id">Identity to copy for this profile.</param>
		public UserProfile (IIdentity id)
		{
			Principal = new UserPrincipal (id);
		}

		#endregion
	}
}