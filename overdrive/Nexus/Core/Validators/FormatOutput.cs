using System.Collections;
using Nexus.Core.Tables;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// Format all related fields from the main context to Criteria, 
	/// adding an error message to Alerts if formatting fails.
	/// </summary>
	public class FormatOutput : Validator
	{
		public override bool ProcessExecute (IValidatorContext outgoing)
		{
			string key = outgoing.FieldKey;
			IRequestContext context = outgoing.Context;
			IDictionary criteria = outgoing.Criteria;
			IFieldTable table = outgoing.FieldTable;

			bool have = (context.Contains (key));
			if (have)
			{
				outgoing.Source = context [key];
				bool okay = table.Format_Execute (outgoing);
				if (okay)
					// set to field buffer
					criteria [key] = outgoing.Target;
				else context.AddAlertForField (key);
				return STOP;
			}

			return CONTINUE;
		}
	}
}