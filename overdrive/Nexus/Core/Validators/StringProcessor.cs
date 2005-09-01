using System;

namespace Nexus.Core.Validators
{
	public class StringProcessor : Processor
	{
		#region IProcessor 

		public override bool ConvertInput(IProcessorContext incoming)
		{
			string source = incoming.Source as string;
			incoming.Target = String_Convert(source);
			return true;
		}

		public override bool FormatOutput(IProcessorContext outgoing)
		{
			object source = outgoing.Source;
			outgoing.Target = String_Format(source);
			return true;
		}

		#endregion

		private string String_Empty = String.Empty;

		private string String_Convert(string source)
		{
			bool isNull = (source == null);
			return isNull ? String_Empty : source;
			// If null, return empty string rather than null			
		}

		private string String_Format(object source)
		{
			string t = String_Empty;
			if (source == null) return t;
			string format = (DataFormat == null) ? t : DataFormat;
			if (format.Equals(t)) return source.ToString();
			return String.Format(format, source);
		}

		/*
		private bool IsMyType (Type dataType)
		{
			bool v = (typeof (string).IsAssignableFrom (dataType));
			return v;
		}
		*/


	}
}