using System;
using System.Collections;
using Agility.Core;
using Nexus.Core.Validators;

namespace Nexus.Core.Tables
{
	/// <summary>
	/// Implement IFieldTable.
	/// </summary>
	/// <remarks><p>
	/// Validator
	/// * Needs configurable nullvalue support
	/// ** String, nullValue=""
	/// ** Double, nullValue=0.0"
	/// ** DataType, allowNull
	/// * If they give us something, and it fails conversion, then validation fails. 
	/// * If they give us null or an empty string, and the property has a nullValue, then the nullValue is used. 
	/// * If they give us null or an empty string, or the nullValue, and the property is required, then validation fails. 
	/// </p></remarks>
	[Serializable]
	public class FieldTable : Context, IFieldTable
	{
		#region IFieldTable

		#region Field

		private bool _Strict = false;
		public virtual bool Strict
		{
			get { return _Strict; }
			set { _Strict = value; }
		}

		public virtual IFieldContext AddFieldContext
		{
			set
			{
				if (value == null)
					throw new ArgumentNullException ("(value==null) || (Type!=IFieldContext)", "Nexus.Core.Tables.FieldTable.AddFieldContext");
				Field [value.ID] = value;
			}
		}

		public virtual IList AddFieldContexts
		{
			set
			{
				if (value == null)
					throw new ArgumentNullException ("(value==null) || (Type!=IFieldContext)", "Nexus.Core.Tables.FieldTable.AddFieldContexts");
				IEnumerator elements = value.GetEnumerator ();
				while (elements.MoveNext ()) AddFieldContext = elements.Current as IFieldContext;
			}
		}

		public virtual IFieldContext GetFieldContext (string id)
		{
			if (id==null) throw new ArgumentNullException ("id==null", "Nexus.Core.Tables.FieldTable.GetFieldContext");			
			IFieldContext fieldContext = Field [id] as IFieldContext;
			bool problem = ((fieldContext == null) && (Strict));
			if (problem)
				throw new ArgumentNullException (id, "Nexus.Core.Tables.FieldTable.GetFieldContext");
			return fieldContext;
		}

		#endregion 

		#region Processor

		public virtual IProcessor AddProcessor
		{
			set
			{
				if (value == null)
					throw new ArgumentNullException ("value==null", "Nexus.Core.Tables.FieldTable.AddProcessor");
				Processor [value.ID] = value;
			}
		}

		public virtual IList AddProcessors
		{
			set
			{
				if (value == null)
					throw new ArgumentNullException ("value==null", "Nexus.Core.Tables.FieldTable.AddProcessors");
				IEnumerator elements = value.GetEnumerator ();
				while (elements.MoveNext ()) AddProcessor = elements.Current as IProcessor;
			}
		}

		public virtual IProcessor GetProcessor (string id)
		{
			if (id == null)
				throw new ArgumentNullException ("id==null", "Nexus.Core.Tables.FieldTable.GetProcessor");
			IProcessor processor = Processor [id] as IProcessor;
			if (processor == null)
				throw new ArgumentNullException (id, "Nexus.Core.Tables.FieldTable.GetProcessor");
			return processor;
		}

		#endregion

		#region Text 

		public virtual string Alert (string id)
		{
			return GetFieldContext (id).Alert;
		}

		#endregion

		#endregion

		/// <summary>
		/// Create instance with zero paramters.
		/// </summary>
		public FieldTable ()
		{
			this [Tokens.Field] = new Hashtable ();
			this [Tokens.Processor] = new Hashtable ();
		}

		/// <summary>
		/// Internal storage for the FieldContexts.
		/// </summary>
		private IDictionary Field
		{
			get { return this [Tokens.Field] as IDictionary; }
		}

		/// <summary>
		/// Internal storage for the Processors.
		/// </summary>
		private IDictionary Processor
		{
			get { return this [Tokens.Processor] as IDictionary; }
		}

	}

}