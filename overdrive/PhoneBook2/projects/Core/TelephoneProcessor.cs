using System.Text;
using Nexus.Core.Validators;

namespace PhoneBook.Core
{
	/// <summary>
	/// Remove punctuation on input and insert punctuation on output.
	/// </summary>
	/// <remarks><p>
	/// Null input or output is passed thorugh as null. 
	/// Non-null input or output must be strings.
	/// Output is formatted for local or long distane US telephone numbers. 
	/// Output strings that are too short or too long are passed through.
	/// </p></remarks>
	public class TelephoneProcessor : Processor
	{
		public override bool ConvertInput(IProcessorContext incoming)
		{
			object source = incoming.Source;
			if (source == null) return true;

			string input = source as string;
			if (input == null) return false;

			char[] marks = {'-'};
			string[] splits = input.Split(marks);
			StringBuilder sb = new StringBuilder(input.Length);
			foreach (string s in splits)
			{
				sb.Append(s);
			}
			incoming.Target = sb.ToString();
			return true;
		}

		public override bool FormatOutput(IProcessorContext outgoing)
		{
			object source = outgoing.Source;
			if (source == null) return true;

			string output = source as string;
			if (output == null) return false;

			string mark = "-";
			if (output == null) return false;
			string buffer;

			if (output.Length == 10)
			{
				// 012-345-6789
				string buffer1 = output.Insert(6, mark);
				buffer = buffer1.Insert(3, mark);
			}
			else if (output.Length == 7)
			{
				// 012-3456
				buffer = output.Insert(3, mark);
			}
			else buffer = output;

			outgoing.Target = buffer;
			return true;
		}
	}
}