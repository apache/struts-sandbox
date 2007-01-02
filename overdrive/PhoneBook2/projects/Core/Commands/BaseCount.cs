using Nexus.Core;

namespace PhoneBook.Core.Commands
{
	/// <summary>
	/// Invoke a query that returns the count of a result set.
	/// </summary>
	public class BaseCount : BaseMapper
	{
		public override bool RequestExecute(IRequestContext context)
		{
			object result = Mapper.QueryForObject(QueryID, context);
			context[App.ITEM_COUNT] = result;
			return CONTINUE;
		}
	}
}