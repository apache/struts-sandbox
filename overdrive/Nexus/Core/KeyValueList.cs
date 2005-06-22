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
		public KeyValueList ()
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
					this.Add (o);
				}
			}
		}

	}
}