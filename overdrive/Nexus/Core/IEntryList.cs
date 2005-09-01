using System.Collections;

namespace Nexus.Core
{
	/// <summary>
	/// A list of a set of Fields, such as displayed as the result of a query.
	/// </summary>
	public interface IEntryList : IList
	{
		/// <summary>
		/// Create and Insert a new entry object at index 0.
		/// </summary>
		object Insert(string key);

		/// <summary>
		/// Add a entry object based on an IDictionary.
		/// </summary>
		/// <param name="row"></param>
		void AddEntry(IDictionary row);

	}
}