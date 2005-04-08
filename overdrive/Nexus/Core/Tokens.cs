namespace Nexus.Core
{
	/// <summary>
	/// Tokens representing context keys.
	/// </summary>
	public class Tokens
	{
		private Tokens()
		{
			// No need to construct static helper class
		}

		/// <summary>
		/// Token for Command property.
		/// </summary>
		public const string COMMAND = "__COMMAND";

		/// <summary>
		/// Token for CommandBin property.
		/// </summary>
		public const string COMMAND_BIN = "__COMMAND_BIN";

		/// <summary>
		/// Token for Errors property.
		/// </summary>
		public const string ERRORS = "__ERRORS";

		/// <summary>
		/// Token for a generic message.
		/// </summary>
		public const string GENERIC_MESSAGE = "__GENERIC_MESSAGE";

		/// <summary>
		/// Token for Fault property.
		/// </summary>
		public const string FAULT = "__FAULT";


	}
}
