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

	}
}
