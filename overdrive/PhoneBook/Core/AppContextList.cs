using System.Collections;

namespace PhoneBook.Core
{
	/// <summary>
	/// List AppContext objects.
	/// </summary>
	/// 
	public class AppContextList : ArrayList
	{
		/// <summary>
		/// Create instance with zero parameters.
		/// </summary>
		/// 
		public AppContextList ()
		{
		}

		/// <summary>
		/// Create an AppContext object for each IDictionary on a IList.
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
		/// Wrap an IDictionary in an AppContext object and add it to the list.
		/// </summary>
		/// <param name="value">IDictionary to add as new entry</param>
		public void AddEntry (IDictionary value)
		{
			AppContext entry = new AppContext (value);
			Add (entry);
		}

	}
}