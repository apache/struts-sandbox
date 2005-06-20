using System.Collections;
using Nexus.Core;

namespace PhoneBook.Core
{
	/// <summary>
	/// A list of AppContext objects.
	/// </summary>
	/// 
	public class AppContextList : ArrayList
	{
		/// <summary>
		/// Instantiate with zero parameters.
		/// </summary>
		/// 
		public AppContextList ()
		{
		}

		/// <summary>
		/// Create a AppContext object for each IDictionary on a IList.
		/// </summary>
		/// <param name="dictionaries">A IList of IDictionaries with data values.</param>
		/// 
		public AppContextList (IList dictionaries)
		{
			foreach (IDictionary item in dictionaries)
			{
				Add (new AppContext (item));
			}
		}

		/// <summary>
		/// Create a AppContext object for each IDictionary on a IList, 
		/// using a FieldTable to format each entry.
		/// </summary>
		/// <param name="dictionaries">A IList of IDictionaries with data values.</param>
		/// 
		public AppContextList (IList dictionaries, IRequestContext context)
		{
			foreach (IDictionary item in dictionaries)
			{
				// TODO: Add (new AppContext (item, context));
			}
		}

	}
}