using System.Collections;

namespace Nexus.Core
{
	/// <summary>
	/// List KeyValue objects.
	/// </summary>
	public interface IKeyValueList : IList
	{
		string ValueFor(string key);
	}
}