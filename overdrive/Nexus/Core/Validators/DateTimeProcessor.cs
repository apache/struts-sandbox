using System;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// Convert and format DateTime fields.
	/// </summary>
	public class DateTimeProcessor : Processor
	{
		#region IProcessor

		public override bool ConvertInput(IProcessorContext incoming)
		{
			bool okay = false;
			string source = incoming.Source as string;

			if (IsInput(source))
			{
				DateTime t = DateTime_Convert(source);
				bool isDateTimeEmpty = DateTime_Empty.Equals(t);
				okay = !isDateTimeEmpty;
				incoming.Target = t;
			}
			else
			{
				incoming.Target = null; // FIXME: We could use DateTime_Empty here,
				okay = true; //  but there was an issue with iBATIS (is there still?)
			}

			return okay;
		}

		public override bool FormatOutput(IProcessorContext outgoing)
		{
			bool okay = false;
			object source = outgoing.Source;

			if (IsEmpty(source))
			{
				outgoing.Target = String.Empty;
				okay = true;
			}
			else
			{
				string target = DateTime_Format(source);
				outgoing.Target = target;
				okay = IsInput(target);
			}
			return okay;
		}

		#endregion

		private bool IsInput(string v)
		{
			return ((v != null) && (!String.Empty.Equals(v)));
		}

		private DateTime DateTime_Empty = DateTime.MinValue;

		private bool IsEmpty(object source)
		{
			SByte dbNull = 0;
			return ((null == source) || (DBNull.Value.Equals(source)) || (dbNull.Equals(source) || String.Empty.Equals(source)));
		}

		private DateTime DateTime_Convert(string source)
		{
			DateTime t = DateTime_Empty;
			try
			{
				t = Convert.ToDateTime(source);
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

		private string DateTime_Format(object source)
		{
			DateTime t = DateTime_Empty;
			try
			{
				t = (DateTime) source;
			}
			catch (InvalidCastException e)
			{
				e = e;
			}
			if (DateTime_Empty.Equals(t)) return String.Empty;
			else return t.ToString(DataFormat);
		}


		/*
		public bool IsMyType (Type dataType)
		{
			bool v = (typeof (DateTime)).IsAssignableFrom (dataType);
			return v;
		}

		public bool IsMyField(IFieldContext field)
		{
			bool v = (field.ProcessorID.Equals(ID));
			return v;
		}
		*/

	}
}