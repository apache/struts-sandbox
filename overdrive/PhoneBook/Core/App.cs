namespace PhoneBook.Core
{
	/// <summary>
	/// Tokens representing context keys.
	/// </summary>
	public class App
	{
		private App()
		{
			// No need to construct static helper class
		}

		/// <summary>
		/// Token for first_name property.
		/// </summary>
		public const string FIRST_NAME = "first_name";

		/// <summary>
		/// Token for last_name property.
		/// </summary>
		public const string LAST_NAME = "last_name";

		/// <summary>
		/// Token for user_name property.
		/// </summary>
		public const string USER_NAME = "user";

		/// <summary>
		/// Token for extension property.
		/// </summary>
		public const string EXTENSION = "extension";

		/// <summary>
		/// Token for hired property.
		/// </summary>
		public const string HIRED = "hired";

		/// <summary>
		/// Token for hours property.
		/// </summary>
		public const string HOURS = "hours";

		/// <summary>
		/// Token for editor property.
		/// </summary>
		public const string EDITOR = "editor";

		/// <summary>
		/// Token for seledct all command.
		/// </summary>
		public const string SELECT_ALL = "select_all";

	}
}
