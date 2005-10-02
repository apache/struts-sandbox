using System;
using System.Collections;

namespace WQD.Core.Controls
{
	/// <summary>
	/// Provide an EventArgs type to interace with FindControls
	/// that can be read or bound via an IDictionary object.
	/// </summary>
	/// 
	public class FindArgs : EventArgs
	{
		/// <summary>
		/// A reference to the original arguments for an event, if any.
		/// </summary>
		/// 
		public EventArgs OldArgs;

		/// <summary>
		/// Expose our dictionary instance.
		/// </summary>
		/// 
		public IDictionary Criteria;

		/// <summary>
		/// Create this instance using the given dictionary.
		/// </summary>
		/// 
		public FindArgs(IDictionary criteria)
		{
			Criteria = criteria;
			OldArgs = new EventArgs();
		}

		/// <summary>
		/// Create a new instance with empty fields.
		/// </summary>
		/// 
		public FindArgs()
		{
			Criteria = new Hashtable();
			OldArgs = new EventArgs();
		}

		/// <summary>
		/// Create a new instance, setting the old event arguments.
		/// </summary>
		/// <param name="oldArgs">Old Event arguments</param>
		/// 
		public FindArgs(EventArgs oldArgs)
		{
			this.OldArgs = oldArgs;
			Criteria = new Hashtable();
		}

		public FindArgs(EventArgs e, IDictionary c)
		{
			OldArgs = e;
			Criteria = c;
		}

	}
}