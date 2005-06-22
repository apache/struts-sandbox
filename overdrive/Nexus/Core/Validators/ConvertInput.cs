using System.Collections;
using Nexus.Core.Tables;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// Convert all related fields from Criteria to the main context, 
	/// adding an Alert message to Errors if a conversion fails.
	/// </summary>
	public class ConvertInput : Validator
	{
		public override bool ProcessExecute (IValidatorContext incoming)
		{
			string key = incoming.FieldKey;
			IRequestContext context = incoming.Context;
			IDictionary criteria = incoming.Criteria;
			IFieldTable table = incoming.FieldTable;

			bool have = (criteria.Contains (key));
			if (have)
			{
				incoming.Source = criteria [key];
				bool okay = table.Convert_Execute (incoming);
				if (okay)
					// set to main context
					context [key] = incoming.Target;
				else context.AddAlertForField (key);
				return STOP;
			}

			return CONTINUE;
		}
	}
}