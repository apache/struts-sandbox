namespace Nexus.Core.Validators
{
	/// <summary>
	/// Convert all related fields from Criteria to the main context, 
	/// adding an Alert message to Errors if a conversion fails.
	/// </summary>
	public class ConvertInput : Validator
	{
		public ConvertInput ()
		{
			Mode = MODE_INPUT;
		}

	}
}