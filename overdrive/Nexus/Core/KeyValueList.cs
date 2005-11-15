using System;
using System.Collections;

namespace Nexus.Core
{
	/// <summary>
	/// Implement IKeyValueList.
	/// </summary>
	public class KeyValueList : ArrayList, IKeyValueList
	{
		/// <summary>
		/// Construct instance without parameters.
		/// </summary>
		public KeyValueList()
		{
		}

		/// <summary>
		/// Add members of given list to this list.
		/// </summary>
		public virtual IList AddAll
		{
			set
			{
				foreach (object o in value)
				{
					this.Add(o);
				}
			}
		}

		public string ValueFor(string key)
		{
			if ((key==null) || (key.Equals(String.Empty))) return key;
			foreach (IKeyValue kv in this)
			{
				if (key.Equals(kv.Key)) return kv.Value as string;
			}
			return null;
		}
	}
}