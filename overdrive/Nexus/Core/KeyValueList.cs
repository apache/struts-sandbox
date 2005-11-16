using System;
using System.Collections;

namespace Nexus.Core
{
	/// <summary>
	/// Implement IKeyValueList.
	/// </summary>
	public class KeyValueList : IKeyValueList
	{

		private IList list;

		/// <summary>
		/// Construct instance without parameters.
		/// </summary>
		public KeyValueList()
		{
			list = new ArrayList();
		}

		public KeyValueList(IList _list)
		{
			list = _list;
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
					list.Add(o);
				}
			}
		}

		public string ValueFor(string key)
		{
			if ((key == null) || (key.Equals(String.Empty))) return key;
			foreach (IKeyValue kv in list)
			{
				if (key.Equals(kv.Key)) return kv.Value as string;
			}
			return null;
		}

		public int Add(object value)
		{
			return list.Add(value);
		}

		public bool Contains(object value)
		{
			return list.Contains(value);
		}

		public void Clear()
		{
			list.Clear();
		}

		public int IndexOf(object value)
		{
			return list.IndexOf(value);
		}

		public void Insert(int index, object value)
		{
			list.Insert(index,value);
		}

		public void Remove(object value)
		{
			list.Remove(value);
		}

		public void RemoveAt(int index)
		{
			list.RemoveAt(index);
		}

		public bool IsReadOnly
		{
			get { return list.IsReadOnly; }
		}

		public bool IsFixedSize
		{
			get { return list.IsFixedSize; }
		}

		public object this[int index]
		{
			get { return list[index]; }
			set { list[index] = value; }
		}

		public void CopyTo(Array array, int index)
		{
			this.CopyTo(array,index);
		}

		public int Count
		{
			get { return list.Count; }
		}

		public object SyncRoot
		{
			get { return list.SyncRoot; }
		}

		public bool IsSynchronized
		{
			get { return list.IsSynchronized; }
		}

		public IEnumerator GetEnumerator()
		{
			return list.GetEnumerator();
		}
	}
}