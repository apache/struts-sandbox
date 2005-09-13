using System.Security.Principal;
using System.Text;
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

		private AppEntry _Entry;

		public AppEntry Entry
		{
			get { return _Entry; }
			set
			{
				_Entry = value;
				if (_Entry!=null)
				{
					StringBuilder sb = new StringBuilder();
					sb.Append(_Entry.first_name);
					sb.Append(" ");
					sb.Append(Entry.last_name);
					FullName = sb.ToString().Trim();
				}
			}
		}

		private string _FullName;

		public string FullName
		{
			get { return _FullName; }
			set { _FullName = value; }
		}

		/// <summary>
		/// Instantiate from an IIdentity.
		/// </summary>
		/// <param name="id">Identity to copy for this profile.</param>
		public AppUserProfile(IIdentity id)
		{
			Principal = new UserPrincipal(id);
		}

	}
}