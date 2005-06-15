namespace Nexus.Core.Validators
{
	/// <summary>
	/// Format all related fields from the main context to Criteria, 
	/// adding an error message to Alerts if formatting fails.
	/// </summary>
	public class FormatOutput : Validator
	{
		public FormatOutput ()
		{
			Mode = MODE_OUTPUT;
		}
	}
}