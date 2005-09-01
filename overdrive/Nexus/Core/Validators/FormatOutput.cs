using System.Collections;

namespace Nexus.Core.Validators
{
	/// <summary>
	/// Format related fields from the main context to Criteria, 
	/// adding an error message to Alerts if formatting fails.
	/// </summary>
	public class FormatOutput : ProcessorCommand
	{
		public override bool ExecuteProcess(IProcessorContext outgoing)
		{
			string key = outgoing.FieldKey;
			IRequestContext context = outgoing.Context;
			IDictionary criteria = outgoing.Criteria;

			bool have = (context.Contains(key));
			if (have)
			{
				outgoing.Source = context[key];
				bool okay = ExecuteFormat(outgoing);
				if (okay)
					// set to field buffer
					criteria[key] = outgoing.Target;
				else context.AddAlertForField(key);
				return STOP;
			}

			return CONTINUE;
		}
	}
}