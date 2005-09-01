using System.Collections;
using Agility.Nexus.Validators;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// Transform IDictionary entries into formatted entries on a IEntryList instances.
	/// </summary>
	/// <remarks><p>
	/// To implement, override NewEntryList to provide an instances of the desired type. 
	/// This will usually be an IEntryList that creates the entry objects used by your application. 
	/// The entry objects can be conventional property objects, 
	/// or objects that expose properties backed by an IDictionary, as you prefer.
	/// </p></remarks>
	/// 
	public abstract class EntryListProcessor : Processor
	{
		public override bool ConvertInput(IProcessorContext incoming)
		{
			incoming.Target = incoming.Source;
			return true;
		}

		public override bool FormatOutput(IProcessorContext outgoing)
		{
			ProcessorCommand formatter = new FormatOutput();
			IList source = outgoing.Source as IList;

			IEntryList target = NewEntryList();

			foreach (IDictionary row in source)
			{
				IRequestContext context = new RequestContext(row);
				context.FieldTable = outgoing.FieldTable;
				ICollection keys = row.Keys;
				foreach (string key in keys)
				{
					IProcessorContext _context = new ProcessorContext(key, context);
					formatter.ExecuteProcess(_context);
				}
				target.AddEntry(context.Criteria);
			}
			outgoing.Target = target;
			return true;
		}

		/// <summary>
		/// Override to return an instance of the desired IEntryList type.
		/// </summary>
		/// <returns>An IEntryList instance</returns>
		public abstract IEntryList NewEntryList();
	}
}