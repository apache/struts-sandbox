using System.Collections;
using Agility.Nexus.Validators;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// Process the value of a KeyValue list 
	/// by setting the Key property to the Field's ID.
	/// </summary>
	public class KeyValueProcessor : Processor
	{
		private string _Key;

		public string Key
		{
			set { _Key = value; }
			get { return _Key; }
		}

		public override bool ConvertInput(IProcessorContext incoming)
		{
			incoming.Target = incoming.Source;
			return true;
		}

		public override bool FormatOutput(IProcessorContext outgoing)
		{
			ProcessorCommand formatter = new FormatOutput();
			IList source = outgoing.Source as IList;
			foreach (IKeyValue row in source)
			{
				string key = Key;
				IRequestContext context = new RequestContext();
				context[key] = row.Value;
				context.FieldTable = outgoing.FieldTable;
				IProcessorContext _context = new ProcessorContext(key, context);
				formatter.ExecuteProcess(_context);
				row.Value = _context.Target;
			}
			outgoing.Target = outgoing.Source;
			return true;
		}
	}
}