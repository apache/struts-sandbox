using System.Collections;
using System.Globalization;

namespace Nexus.Core.Profile
{
	/// <summary>
	/// Record user settings.
	/// </summary>
	/// 
	public interface IProfile
	{
		/// <summary>
		/// Record the User ID.
		/// </summary>
		/// 
		string UserId { get; set; }

		/// <summary>
		/// Record the User Locale.
		/// </summary>
		/// 
		CultureInfo UserLocale { get; set; }

		/// <summary>
		/// Record other default settings.
		/// </summary>
		/// 
		IDictionary Criteria { get; set; }
	}
}