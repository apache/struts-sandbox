using System.Security.Principal;
using System.Text;
using Nexus.Core.Profile;

namespace PhoneBook.Core
{
	/// <summary>
	/// Extend UserProfile to include properties specific to this application, 
	/// such as IsEditor.
	/// </summary>
	/// 
	public class AppUserProfile : UserProfile
	{
		/// <summary>
		/// Provide a field for IsEditor property.
		/// </summary>
		private bool _IsEditor = false;

		/// <summary>
		/// Indicate whether user has editing priveleges. 
		/// </summary>
		/// 
		public bool IsEditor
		{
			get { return _IsEditor; }
			set { _IsEditor = value; }
		}

		/// <summary>
		/// Provide a field for Entry property.
		/// </summary>
		/// 
		private AppEntry _Entry;


		/// <summary>
		/// Record directory entry for user.
		/// </summary>
		/// 
		public AppEntry Entry
		{
			get { return _Entry; }
			set
			{
				_Entry = value;
				if (_Entry != null)
				{
					StringBuilder sb = new StringBuilder();
					sb.Append(_Entry.first_name);
					sb.Append(" ");
					sb.Append(Entry.last_name);
					FullName = sb.ToString().Trim();
				}
			}
		}

		/// <summary>
		/// Provide a field for FullName property.
		/// </summary>
		/// 
		private string _FullName;

		/// <summary>
		/// Record the user's full name (first and last names).
		/// </summary>
		/// 
		public string FullName
		{
			get { return _FullName; }
			set { _FullName = value; }
		}

		/// <summary>
		/// Instantiate from an IIdentity.
		/// </summary>
		/// <param name="id">Identity to copy for this profile.</param>
		/// 
		public AppUserProfile(IIdentity id)
		{
			Principal = new UserPrincipal(id);
		}

	}
}