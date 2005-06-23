using System.Collections;
using Agility.Nexus.Validators;
using Nexus.Core;
using Nexus.Core.Validators;

namespace PhoneBook.Core
{
	/// <summary>
	/// Transform IDictionary entries into formatted AppContext properties.
	/// </summary>
	public class AppProcessor : Processor
	{
		public override bool ConvertInput (IProcessorContext incoming)
		{
			incoming.Target = incoming.Source;
			return true;
		}

		public override bool FormatOutput (IProcessorContext outgoing)
		{
			ProcessorCommand formatter = new FormatOutput ();
			IList source = outgoing.Source as IList;
			AppContextList target = new AppContextList ();
			foreach (IDictionary row in source)
			{
				IRequestContext context = new RequestContext (row);
				context.FieldTable = outgoing.FieldTable;
				ICollection keys = row.Keys;
				foreach (string key in keys)
				{
					IProcessorContext _context = new ProcessorContext (key, context);
					formatter.ExecuteProcess (_context);
				}
				target.AddEntry (context.Criteria);
			}
			outgoing.Target = target;
			return true;
		}
	}
}