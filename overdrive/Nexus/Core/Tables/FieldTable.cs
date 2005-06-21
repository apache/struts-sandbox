using System;
using System.Collections;
using Agility.Core;
using Nexus.Core.Validators;

namespace Nexus.Core.Tables
{
	/// <summary>
	/// Default implementation of IFieldTable.
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
		private bool _Strict = false;
		public virtual bool Strict
		{
			get { return _Strict; }
			set { _Strict = value; }
		}

		public virtual IFieldContext AddField
		{
			set
			{
				if (value == null)
					throw new ArgumentNullException ("value", "Agility.Nexus.FieldTable.AddField");
				this [value.ID] = value;
			}
		}

		public virtual IList AddFields
		{
			set
			{
				if (value == null)
					throw new ArgumentNullException ("value", "Agility.Nexus.FieldTable.AddFields");
				IEnumerator elements = value.GetEnumerator ();
				while (elements.MoveNext ()) AddField = elements.Current as IFieldContext;
			}
		}

		public virtual string Alert (string id)
		{
			return Get (id).Alert;
		}

		private bool IsStringType (Type dataType)
		{
			bool v = (typeof (string).IsAssignableFrom (dataType));
			return v;
		}

		private bool IsCollectionType (Type dataType)
		{
			bool v = (typeof (ICollection)).IsAssignableFrom (dataType);
			return (v);
		}

		public bool IsRichControl (string name)
		{
			return !(Tokens.INPUT_CONTROL.Equals (name));
		}

		public virtual bool Convert (IValidatorContext context)
		{
			bool okay = false;

			// FIXME: This is begging for a Chain

			#region non DataTypes

			string id = context.FieldKey;
			string source = context.Source as string;
			IFieldContext fieldContext = Get (id);
			if ((fieldContext == null))
			{
				if (Strict)
					throw new ArgumentNullException ("Nexus.Core.FieldTable.Convert", id);
				else
				{
					context.Target = source;
					return true;
				}
			}

			if (IsRichControl (fieldContext.ControlTypeName))
			{
				context.Target = source;
				return true;
			}

			#endregion

			#region DataTypes

			bool processed = false;

			if ((typeof (DateTime) == fieldContext.DataType))
			{
				processed = true;
				if (IsInput (source))
				{
					DateTime t = DateTime_Convert (fieldContext, source);
					bool isDateTimeEmpty = DateTime_Empty.Equals (t);
					okay = !isDateTimeEmpty;
					context.Target = t;
				}
				else
				{
					context.Target = null; // We could use DateTime_Empty here,
					okay = true; //  but there's an issue with iBATIS
				}
			}

			if (IsStringType (fieldContext.DataType))
			{
				processed = true;
				context.Target = String_Convert (fieldContext, source);
				okay = true;
			}

			// TODO: Other types. 

			#endregion

			if (!processed)
				throw new ArgumentOutOfRangeException ("Nexus.Core.FieldTable.DataType", id);

			return okay;
		}

		public virtual bool Format (IValidatorContext context)
		{
			bool okay = false;
			string id = context.FieldKey;
			object source = context.Source;
			IFieldContext fieldContext = Get (id);
			if ((fieldContext == null))
			{
				if (Strict)
					throw new ArgumentNullException ("Nexus.Core.FieldTable.Format", id);
				else
				{
					if (source == null)
						context.Target = null;
					else
					{
						Type sourceType = source.GetType ();
						if (IsCollectionType (sourceType)) context.Target = source;
						else context.Target = source.ToString ();
					}
					return true;
				}
			}

			if (IsRichControl (fieldContext.ControlTypeName))
			{
				context.Target = source;
				return true;
			}

			bool processed = false;
			if ((typeof (DateTime) == fieldContext.DataType))
			{
				processed = true;
				SByte dbNull = 0;
				bool isDateTimeEmpty = ((null == source) || (DBNull.Value.Equals (source)) || (dbNull.Equals (source) || String.Empty.Equals (source)));
				// We could use DateTime_Empty here, but there's an issue with iBATIS
				if (isDateTimeEmpty)
				{
					context.Target = String.Empty;
					okay = true;
				}
				else
				{
					string target = DateTime_Format (fieldContext, source);
					context.Target = target;
					okay = IsInput (target);
				}
			}

			if (IsStringType (fieldContext.DataType))
			{
				processed = true;
				context.Target = String_Format (fieldContext, source);
				okay = true;
			}

			// TODO: Other types. 

			if (!processed)
				throw new ArgumentOutOfRangeException ("Agility.Nexus.FieldTable.DataType", id);

			return okay;
		}

		public virtual IFieldContext Get (string id)
		{
			IFieldContext fieldContext = this [id] as IFieldContext;
			bool problem = ((fieldContext == null) && (Strict));
			if (problem)
				throw new ArgumentNullException ("Agility.Nexus.FieldTable", "Get");
			return fieldContext;
		}


		/// <summary>
		/// Determine whether string is null or empty.
		/// </summary>
		/// <param name="v">String to test.</param>
		/// <returns>True if the string is valid input (not null and not empty).</returns>
		/// 
		private bool IsInput (string v)
		{
			return ((v != null) && (!String.Empty.Equals (v)));
		}

		#region DateTime

		// TODO: Create a IDataTrip interface with Convert and Format methods. 
		// The runtime instances could then be injected into FieldTable
		// or added to a list, for extensibility

		private DateTime DateTime_Empty = DateTime.MinValue;

		/// <summary>
		/// Substitute default value (MinValue) if conversion fails. 
		/// </summary>
		/// <param name="fieldContext">Our field definition.</param>
		/// <param name="input">The string to convert.</param>
		/// <returns>A DateTime value, using MinValue to represent failure.</returns>
		public DateTime DateTime_Convert (IFieldContext fieldContext, string input)
		{
			DateTime t = DateTime_Empty;
			try
			{
				// t = System.Convert.ToDateTime (input);
				t = System.Convert.ToDateTime (input);
			}
			catch (InvalidCastException e)
			{
				e = e; // silly assignment
			}
			catch (FormatException e)
			{
				e = e; // silly assignment
			}
			return t;
		}

		/// <summary>
		/// Render DateTime value using the DataFormat string, 
		/// or an empty string if value is MinValue or an invalid type.
		/// </summary>
		/// <param name="fieldContext">Our field definition.</param>
		/// <param name="value">The value to format.</param>
		/// <returns>Formatted representation or an empty string.</returns>
		public string DateTime_Format (IFieldContext fieldContext, object value)
		{
			DateTime t = DateTime_Empty;
			try
			{
				t = (DateTime) value;
			}
			catch (InvalidCastException e)
			{
				e = e;
			}
			if (DateTime_Empty.Equals (t)) return String.Empty;
			else return t.ToString (fieldContext.DataFormat);
		}

		#endregion

		#region Utilities

		private string String_Empty = String.Empty;

		public string String_Convert (IFieldContext fieldContext, string input)
		{
			bool isNull = (input == null);
			return isNull ? String_Empty : input;
			// If null, return empty string rather than null

		}

		public string String_Format (IFieldContext fieldContext, object value)
		{
			string t = String_Empty;
			bool valid = (value != null);
			string format = (fieldContext.DataFormat != null) ? fieldContext.DataFormat : String.Empty;
			return valid ? String.Format (format, value) : t;
		}

		#endregion
	}
}