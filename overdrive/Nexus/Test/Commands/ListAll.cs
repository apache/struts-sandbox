using System.Collections;

namespace Nexus.Core.Commands
{
	/// <summary>
	/// Return a list as the outcome.
	/// </summary>
	public class ListAll : RequestCommand
	{

		public override bool RequestExecute (IRequestContext context)
		{
			// IList list = Mapper.Get ().QueryForList (ID, context);
			// Fake it:
			IList list = new ArrayList();
			list.Add("data");
			context.Outcome = list;
			return CONTINUE;
		}
	}
}