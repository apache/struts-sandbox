using System.Text;
using Nexus.Core.Validators;

namespace PhoneBook.Core
{
	/// <summary>
	/// Remove punctuation on input and insert punctuation on output.
	/// </summary>
	public class TelephoneProcessor : Processor
	{
		public override bool ConvertInput(IProcessorContext incoming)
		{
			string source = incoming.Source as string;
			if (source == null) return false;

			char[] marks = {'-'};
			string[] splits = source.Split(marks);
			StringBuilder sb = new StringBuilder(source.Length);
			foreach (string s in splits)
			{
				sb.Append(s);
			}
			incoming.Target = sb.ToString();
			return true;
		}

		public override bool FormatOutput(IProcessorContext outgoing)
		{
			string mark = "-";
			string source = outgoing.Source as string;
			if (source == null) return false;
			string buffer = null;

			if (source.Length == 10)
			{
				// 012-345-6789
				string buffer1 = source.Insert(6, mark);
				buffer = buffer1.Insert(3, mark);
			}
			else if (source.Length == 7)
			{
				// 012-3456
				buffer = source.Insert(3, mark);
			}
			else buffer = source;

			outgoing.Target = buffer;
			return true;
		}
	}
}