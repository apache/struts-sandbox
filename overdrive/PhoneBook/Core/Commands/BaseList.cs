using System.Collections;
using Nexus.Core;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Execute database statement for ID and set result as outcome.
	/// </summary>
	public class BaseList : AppCommand
	{
		public override bool RequestExecute (IRequestContext context)
		{
			IList rows = Mapper ().QueryForList (ID, null);
			context.Outcome = rows;
			return CONTINUE;
		}
	}
}