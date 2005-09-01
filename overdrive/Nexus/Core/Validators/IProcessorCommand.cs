namespace Nexus.Core.Validators
{
	/// <summary>
	/// Transform values from one data type or format to another. [OVR-13].
	/// </summary>
	public interface IProcessorCommand : IRequestCommand
	{
		/// <summary>
		/// Provide a message template to use when a required field is missing.
		/// </summary>
		string Required { get; set; }

		/// <summary>
		/// Transform the value indicated by the context FieldKey, if present.
		/// </summary>
		/// <param name="context">The context we are processing</param>
		/// <returns>True if nominal</returns>
		bool ExecuteProcess(IProcessorContext context);

		/// <summary>
		/// Convert a field value, utlitizing the field table and processor.
		/// </summary>
		/// <param name="context">The context we are processing</param>
		/// <returns>True if nominal</returns>
		bool ExecuteConvert(IProcessorContext context);

		/// <summary>
		/// Format a field value, utlitizing the field table and processor.
		/// </summary>
		/// <param name="context">The context we are processing</param>
		/// <returns>True if nominal</returns>
		bool ExecuteFormat(IProcessorContext context);

	}
}