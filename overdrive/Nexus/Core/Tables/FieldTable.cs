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
			return GetField (id).Alert;
		}

		// FIXME: This is begging for a Chain
		public virtual bool Convert_Execute (IValidatorContext context)
		{
			bool okay = false;
			string id = context.FieldKey;
			string source = context.Source as string;
			IFieldContext fieldContext = GetField (id); // enforces Strict

			#region Not registered

			if ((fieldContext == null))
			{
				context.Target = source;
				return true;
			}

			#endregion

			#region Registered

			bool processed = false;

			// Collection
			if (IsCollectionType (fieldContext.DataType))
			{
				processed = true;
				context.Target = source; // TODO: Recurse into collection
				okay = true;
			}

			// Date
			if (IsDateType (fieldContext.DataType))
			{
				processed = true;
				if (IsStringEmpty (source))
				{
					DateTime t = DateTime_Convert (fieldContext, source);
					bool isDateTimeEmpty = DateTime_Empty.Equals (t);
					okay = !isDateTimeEmpty;
					context.Target = t;
				}
				else
				{
					context.Target = null; // FIXME: We could use DateTime_Empty here,
					okay = true; //  but there was an issue with iBATIS (is there still?)
				}
			}

			// String
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

		// FIXME: This is begging for a Chain
		public virtual bool Format_Execute (IValidatorContext context)
		{
			bool okay = false;
			string id = context.FieldKey;
			object source = context.Source;
			IFieldContext fieldContext = GetField (id); // Enforces Strict

			#region Not Registered

			if ((fieldContext == null))
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

			#endregion

			#region Registered

			bool processed = false;

			// Collection
			if (IsCollectionType (fieldContext.DataType))
			{
				processed = true;
				context.Target = source; // TODO: Recurse into collection
				okay = true;
			}

			// Date
			if (IsDateType (fieldContext.DataType))
			{
				processed = true;
				if (IsDateEmpty (source))
				{
					context.Target = String.Empty;
					okay = true;
				}
				else
				{
					string target = DateTime_Format (fieldContext, source);
					context.Target = target;
					okay = IsStringEmpty (target);
				}
			}

			// String
			if (IsStringType (fieldContext.DataType))
			{
				processed = true;
				context.Target = String_Format (fieldContext, source);
				okay = true;
			}

			// TODO: Other types. 

			#endregion

			if (!processed)
				throw new ArgumentOutOfRangeException ("Agility.Nexus.FieldTable.DataType", id);

			return okay;
		}

		public virtual IFieldContext GetField (string id)
		{
			IFieldContext fieldContext = this [id] as IFieldContext;
			bool problem = ((fieldContext == null) && (Strict));
			if (problem)
				throw new ArgumentNullException ("Agility.Nexus.FieldTable", "GetField");
			return fieldContext;
		}

		#endregion

		// TODO: Create a IDataTrip interface with IsType, IsEmpty, Convert, and Format methods. 
		// The runtime instances could then be injected into FieldTable
		// or added to a list, for extensibility

		#region Collection Convert/Format 

		private bool IsCollectionType (Type dataType)
		{
			bool v = (typeof (ICollection)).IsAssignableFrom (dataType);
			return (v);
		}

		#region Notes

		/*
			/// <summary>
			/// Instantiate from an IDictionary, 
			/// formatting each entry using the FieldTable from a INexusContext, 
			/// and reporting any conversion or formatting errors in the INexusContext.
			/// </summary>
			/// <remarks><p>
			/// The result of a query will come back as a list of IDictionaries, 
			/// using native, unformatted data types. 
			/// This constructor can be used to loop through a list of IDictionaires, 
			/// create a AppContext for each entry, and formatting any values 
			/// along the way. (Dates being the best example.) 
			/// The result is a AppContextList that can be used as a DataGrid 
			/// DataSource (or whatever). 
			/// </p></remarks>
			/// <param name="dictionary">Values for new object</param>
			/// <param name="context">Context with FieldTable and error handler</param>
			public AppContext (IDictionary dictionary, IRequestContext context)
			{
			#region Assert parameters

				if (null == dictionary) throw new ArgumentNullException ("dictionary", "AppContext(IDictionary,INexusContext");
				if (null == context) throw new ArgumentNullException ("context", "AppContext(IDictionary,INexusContext");
				IFieldTable table = context.FieldTable;
				if (null == table) throw new ArgumentNullException ("FieldTable", "AppContext(IDictionary,INexusContext");

			#endregion

				IEnumerator keys = dictionary.Keys.GetEnumerator ();
				while (keys.MoveNext ())
				{
					string key = keys.Current as string;
					IValidatorContext input = new ValidatorContext (); // ISSUE: Spring? [WNE-63]
					input.FieldKey = key;
					input.Source = dictionary [key];
					bool okay = table.Format (input);
					if (!okay)
						// OR, do we just want to push convert/format(id) up?
						context.AddAlertForField (key);
					this.Add (key, input.Target);
				}
			}
			*/

		#endregion

		#endregion

		#region DateTime Convert/Format

		private DateTime DateTime_Empty = DateTime.MinValue;

		private bool IsDateEmpty (object source)
		{
			SByte dbNull = 0;
			return ((null == source) || (DBNull.Value.Equals (source)) || (dbNull.Equals (source) || String.Empty.Equals (source)));
		}

		private bool IsDateType (Type dataType)
		{
			bool v = (typeof (DateTime)).IsAssignableFrom (dataType);
			return (v);
		}

		/// <summary>
		/// Substitute default value (MinValue) if conversion fails. 
		/// </summary>
		/// <param name="fieldContext">Our field definition.</param>
		/// <param name="input">The string to convert.</param>
		/// <returns>A DateTime value, using MinValue to represent failure.</returns>
		private DateTime DateTime_Convert (IFieldContext fieldContext, string input)
		{
			DateTime t = DateTime_Empty;
			try
			{
				t = Convert.ToDateTime (input);
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
		private string DateTime_Format (IFieldContext fieldContext, object value)
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

		#region String Convert/Format

		private string String_Empty = String.Empty;

		/// <summary>
		/// Determine whether string is null or empty.
		/// </summary>
		/// <param name="v">String to test.</param>
		/// <returns>True if the string is valid input (not null and not empty).</returns>
		/// 
		private bool IsStringEmpty (string v)
		{
			return ((v != null) && (!String_Empty.Equals (v)));
		}

		private bool IsStringType (Type dataType)
		{
			bool v = (typeof (string).IsAssignableFrom (dataType));
			return v;
		}

		private string String_Convert (IFieldContext fieldContext, string input)
		{
			bool isNull = (input == null);
			return isNull ? String_Empty : input;
			// If null, return empty string rather than null
		}

		private string String_Format (IFieldContext fieldContext, object value)
		{
			string t = String_Empty;
			if (value == null) return t;
			string format = (fieldContext.DataFormat == null) ? t : fieldContext.DataFormat;
			if (format.Equals (t)) return value.ToString ();
			return String.Format (format, value);
		}

		#endregion
	}

}