using System.Collections;
using Nexus.Core;

namespace PhoneBook.Core
{
    /// <summary>
    /// Implement Nexus.Core.IEntryList for AppEntry objects.
    /// </summary>
    /// 
    public class AppEntryList : ArrayList, IEntryList
    {
        public object Insert(string key)
        {
            AppEntry entry = new AppEntry();
            entry.entry_key = key;
            Insert(0, entry);
            return entry;
        }

        public void AddEntry(IDictionary row)
        {
            AppEntry entry = new AppEntry();
            foreach (DictionaryEntry col in row)
                entry.Add(col.Key.ToString(), col.Value.ToString());
            Add(entry);
        }
        
        public AppEntry[] ToAppEntryArray()
        {
            return (AppEntry[])ToArray(typeof(AppEntry));
        }
    }
}