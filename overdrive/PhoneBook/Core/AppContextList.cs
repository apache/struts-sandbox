using System.Collections;
using Agility.Core;
using Nexus.Core;

namespace PhoneBook.Core
{
	/// <summary>
	/// List AppContext objects.
	/// </summary>
	/// 
	public class AppContextList : ArrayList, IContextList
	{

		public IContext Insert (string key)
		{
			AppContext entry = new AppContext();
			entry.last_name = key;
			this.Insert (0,entry);
			return entry;
		}

		public void AddEntry(IDictionary row)
		{
			AppContext entry = new AppContext();
			foreach (DictionaryEntry col in row) 
				entry.Add (col.Key,col.Value);
			Add(entry);
		}

	}
}