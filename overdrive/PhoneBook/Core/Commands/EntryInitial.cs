using System.Collections;
using Nexus.Core;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Create list of initial letters for Facility Names.
	/// </summary>
	public class EntryInitial : BaseMapper
	{
		/// <summary>
		/// Document token representing match all entries.
		/// </summary>
		public const string ALL = "[*]";

		/// <summary>
		/// Document the wildcard character used by SQL queries.
		/// </summary>
		public const string WILDCARD = "%";

		public override bool RequestExecute(IRequestContext context)
		{
			const string ZERO = "0";

			string[] input = {
				"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
				"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z"
			};

			IList output = new ArrayList(26);

			foreach (string letter in input)
			{
				string initial = letter + WILDCARD;
				object result = Mapper.QueryForObject(QueryID, initial);
				if (ZERO.Equals(result)) continue;
				output.Add(letter);
			}

			output.Add(ALL);
			context.Outcome = output;
			return CONTINUE;
		}
	}
}