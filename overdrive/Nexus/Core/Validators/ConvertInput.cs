using System.Collections;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// Convert related fields from Criteria to the main context, 
	/// adding an Alert message to Errors if a conversion fails.
	/// </summary>
	public class ConvertInput : ProcessorCommand
	{
		public override bool ExecuteProcess(IProcessorContext incoming)
		{
			string key = incoming.FieldKey;
			IRequestContext context = incoming.Context;
			IDictionary criteria = incoming.Criteria;

			bool have = (criteria.Contains(key));
			if (have)
			{
				incoming.Source = criteria[key];
				bool okay = ExecuteConvert(incoming);
				if (okay)
					// set to main context
					context[key] = incoming.Target;
				else context.AddAlertForField(key);
				return STOP;
			}

			return CONTINUE;
		}
	}
}