using System;
using System.Collections;
using System.Text;

namespace Nexus.Core
{
	/// <summary>
	/// Expose field attributes as public properties.
	/// </summary>
	/// <remarks><p>
	/// The EntryDictionary is used for integration with libraries that 
	/// can use only public properties, such as DataGrid. 
	/// It is also used to pass properties as Event argument, 
	/// in which case it is used like a data transfer object.
	/// </p><p>
	/// The values are available both as an IDictionary and 
	/// (optionally) as Properties. 
	/// (The properties should use the IDictionary for storage.)
	/// To define properties, extend EntryDictionary.
	/// </p></remarks>
	public class EntryDictionary
	{
		private IDictionary _Value = new Hashtable();

		public EntryDictionary()
		{
			// Default contstructor	
		}

		public EntryDictionary(IDictionary sources)
		{
			AddAll(sources);
		}

		/// <summary>
		/// Provide a string representation of the field values 
		/// in the format "key=value\n".
		/// </summary>
		/// <returns>String representation</returns>
		public override String ToString()
		{
			StringBuilder builder = new StringBuilder(128);
			string tab = "=";
			string eol = "\n";
			ICollection keys = _Value.Keys;
			foreach (string key in keys)
			{
				builder.Append(key);
				builder.Append(tab);
				builder.Append(Get(key));
				builder.Append(eol);
			}
			return builder.ToString();
		}

		public void Add(string key, string value)
		{
			_Value.Add(key, value);
		}

		public void AddAll(IDictionary sources)
		{
			ICollection keys = sources.Keys;
			foreach (string key in keys)
			{
				object value = sources[key];
				if (value == null)
					Add(key, value as string);
				else
					Add(key, value.ToString());
			}
		}

		protected ICollection Keys
		{
			get { return _Value.Keys; }
		}

		public string Get(string key)
		{
			return _Value[key] as string;
		}

		public void Set(string key, string value)
		{
			if (value == null)
				_Value[key] = null;
			else _Value[key] = value.Trim();
		}

		public Boolean Contains(Object key)
		{
			return _Value.Contains(key);
		}

		public IDictionary Criteria
		{
			get { return _Value; }
		}

		/// <summary>
		/// Call calculated properties so that 
		/// they are cached as entries in the table.
		/// </summary>
		/// <remarks>
		/// <p>Override to provide functionality</p>
		/// </remarks>
		/// 
		public virtual void CacheText()
		{
		}

		/*
		public string Property
		{
			get { return return Get(App.PROPERTY); }
			set { Set(App.PROPERTY, value); }
		}
		*/
	}
}